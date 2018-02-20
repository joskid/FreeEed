/*
 *
 * Copyright SHMsoft, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freeeed.ocr;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.freeeed.services.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * This class parses file with or without embedded documents
 *
 * @author nehaojha
 */
public class ImageTextParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTextParser.class);
    private static final Tika TIKA = new Tika();
    private static final Parser AUTO_DETECT_PARSER = new AutoDetectParser();
    private static final ParseContext PARSE_CONTEXT = new ParseContext();
    private static final String EMPTY = "";
    private static final Project CURRENT_PROJECT = Project.getCurrentProject();
    private static final String TMP = "/tmp/";
    private static final AtomicInteger COUNTER = new AtomicInteger();


    //config
    static {
        TesseractOCRConfig config = new TesseractOCRConfig();
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        PARSE_CONTEXT.set(TesseractOCRConfig.class, config);
        PARSE_CONTEXT.set(PDFParserConfig.class, pdfConfig);
        PARSE_CONTEXT.set(Parser.class, AUTO_DETECT_PARSER);
    }

    public static String parseContent(String file) {
        String simpleParse = parseText(file);
        if (!simpleParse.trim().isEmpty() || !CURRENT_PROJECT.isOcrEnabled()) {
            return simpleParse;
        }
        LOGGER.info("processing pdf with ocr");
        return parseImages(file);
    }

    private static String parseText(String filePath) {
        try {
            return TIKA.parseToString(new File(filePath));
        } catch (Exception ex) {
            LOGGER.error("Exception parsing pdf file ", ex);
        }
        return EMPTY;
    }

    private static String parseImages(String file) {
        //break file into pages, do ocr and then send parsed contents
        try {
            String path = splitPages(file);
            return ocrParallel(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY;
    }

    private static String ocrParallel(String path) throws IOException {
        File root = new File(path);
        File[] files = root.listFiles();
        int totalFiles = files.length;
        COUNTER.set(0);

        IntStream.range(0, totalFiles).parallel().forEach(i -> {
            File file = new File(path + i + ".pdf");
            parseAndPut(totalFiles, file);
        });

        return combineAll(path, totalFiles);
    }

    private static void parseAndPut(int totalFiles, File file) {
        try (InputStream stream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
            AUTO_DETECT_PARSER.parse(stream, handler, new Metadata(), PARSE_CONTEXT);
            Files.write(Paths.get(file.getPath().replace("pdf", "txt")), handler.toString().trim().getBytes());
            file.delete();
            int count = COUNTER.incrementAndGet();
            LOGGER.debug("progress " + count + " of " + totalFiles);
        } catch (Exception ex) {
            LOGGER.error("Problem parsing document {}", file, ex);
        }
    }

    private static String combineAll(String path, int totalFiles) throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < totalFiles; i++) {
            String filePath = path + i + ".txt";
            Files.readAllLines(Paths.get(filePath))
                    .forEach(line -> {
                        content.append(line).append("\n");
                    });
            new File(filePath).delete();
        }
        return content.toString();
    }

    private static String splitPages(String filePath) throws IOException {
        File file = new File(filePath);
        String pagePath;
        try (PDDocument document = PDDocument.load(file)) {
            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(document);
            Iterator<PDDocument> iterator = pages.listIterator();
            int i = 0;
            pagePath = createTempPath(file);
            LOGGER.debug("pagePath = " + pagePath);
            while (iterator.hasNext()) {
                PDDocument pd = iterator.next();
                pd.save(pagePath + i++ + ".pdf");
                pd.close();
            }
        }
        return pagePath;
    }

    private static String createTempPath(File file) {
        String pagePath = TMP + System.currentTimeMillis() + "/";
        File tempFile = new File(pagePath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        return pagePath;
    }

}
