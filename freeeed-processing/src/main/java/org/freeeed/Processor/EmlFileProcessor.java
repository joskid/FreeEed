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
package org.freeeed.Processor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.freeeed.Entity.ProjectFile;
import org.freeeed.mail.EmailDataProvider;
import org.freeeed.mail.EmailUtil;
import org.freeeed.main.DiscoveryFile;
import org.freeeed.main.DocumentMetadata;
import org.freeeed.main.DocumentMetadataKeys;
import org.freeeed.services.ContentTypeMapping;
import org.freeeed.services.JsonParser;
import org.freeeed.util.Util;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;


/**
 * Process email files
 */
public class EmlFileProcessor extends FileProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmlFileProcessor.class);

    public EmlFileProcessor(ProjectFile projectFile) {
        this.projectFile = projectFile;
    }
    private static final ContentTypeMapping CONTENT_TYPE_MAPPING = new ContentTypeMapping();


    public static boolean isEml(DiscoveryFile discoveryFile) {
        boolean isEml = false;
        String ext = Util.getExtension(discoveryFile.getRealFileName());
        if ("eml".equalsIgnoreCase(ext)) {
            isEml = true;
        }
        //LOGGER.trace("{} is {}", discoveryFile.getRealFileName(), (isEml) ? "EML" : "NOT EML");
        return isEml;
    }


/*
    private void parseDateTimeSentFields(DocumentMetadata metadata, Date sentDate) {
        if (sentDate == null) {
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = df.format(sentDate);
        parseDateTimeFields(metadata, date);
    }

    private void parseDateTimeReceivedFields(DocumentMetadata metadata) {
        String date = metadata.getMessageDate();
        parseDateTimeFields(metadata, date);
    }

    private void parseDateTimeFields(DocumentMetadata metadata, String date) {
        if (date != null && date.length() > 0) {
            try {
                SimpleDateFormat df = null;
                if (date.startsWith("00")) {
                    df = new SimpleDateFormat("'00'yy-MM-dd'T'HH:mm:ss'Z'");
                } else {
                    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                }

                Date dateObj = df.parse(date);

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
                dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateOnly = dateFormatter.format(dateObj);

                metadata.setMessageDate(dateOnly);
                metadata.setMessageDateReceived(dateOnly);

                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String timeOnly = timeFormatter.format(dateObj);

                metadata.setMessageTimeReceived(timeOnly);
            } catch (Exception e) {
                LOGGER.error("Problem extracting date time fields" + e.toString());
            }
        }
    }

*/
    void parse(ProjectFile projectFile) {
        //LOGGER.debug("Parsing file: {}, original file name: {}", discoveryFile.getPath().getPath(), discoveryFile.getRealFileName());
        TikaInputStream inputStream = null;

        Metadata metadata= new Metadata();

        try {
            String extension = Util.getExtension(projectFile.getFile().getName());
            //LOGGER.debug("Detected extension: {}", extension);
            if ("eml".equalsIgnoreCase(extension)) {
                //EmlParser emlParser = new EmlParser(projectFile.getPath());
                //extractEmlFields(metadata, emlParser);
                //inputStream = TikaInputStream.get(projectFile.getPath().toPath());
                //String text = tika.parseToString(inputStream);
                //metadata.set(DocumentMetadataKeys.DOCUMENT_TEXT, text);
                //metadata.setContentType("message/rfc822");
                //parseDateTimeReceivedFields(metadata);
                //parseDateTimeSentFields(metadata, emlParser.getSentDate());
            }
/*
            DiscoveryFile f = new DiscoveryFile(projectFile.getFile().getAbsolutePath(),projectFile.getFile().getName());


       */


            //getting the list of all meta data elements




            // System.out.println(fileType);


/*
            if (Objects.isNull(metadata.getContentType())) {
                metadata.setContentType(extension);
            }

            if (!metadata.getContentType().equals("image/jpeg") && !metadata.getContentType().equals("tiff")) {
                String fileType = CONTENT_TYPE_MAPPING.getFileType(metadata.getContentType());
                metadata.setFiletype(fileType);
            }
            */
        } catch (Exception e) {
            // the show must still go on
            //metadata.set(DocumentMetadataKeys.PROCESSING_EXCEPTION, e.getMessage());
            //LOGGER.error("Problem parsing file", e);
        }
    }


    /**
     * This function is specifically Memex crawler. *jl means JSON lines.
     * Furthermore, each JSON line has the expected fields
     *
     * @param fileName input file in *jl format
     * @param metadata extracted metadata
     */
    // TODO make the code more elegant, try-with-exceptions
    private void extractJlFields(String fileName, DocumentMetadata metadata) {
        /*
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(new File(fileName), "UTF-8");
            while (it.hasNext()) {
                String jsonAsString = it.nextLine();
                String htmlText = JsonParser.getJsonField(jsonAsString, "extracted_text");
                String text = Jsoup.parse(htmlText).text();
                metadata.set(DocumentMetadataKeys.DOCUMENT_TEXT, text);
                metadata.setContentType("application/jl");
            }
        } catch (IOException e) {
            LOGGER.error("Problem with JSON line", e);
        } finally {
            assert it != null;
            try {
                it.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }


    /**
     * Parses JSON given as tech spec
     *
     * @param jsonLine
     * @param metadata
     */

    public void parseJsonFields(String jsonLine, DocumentMetadata metadata) {
        /*
        Map<String, String> fieldMap = JsonParser.getJsonAsMap(jsonLine);
        Iterator<String> keyIterator = fieldMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            metadata.addField(key, fieldMap.get(key));
        }
        metadata.setContentType("application/json");
        */
    }

    /*
    private void extractEmlFields(DocumentMetadata metadata, EmailDataProvider emlParser) {
        try {
            String text = prepareContent(emlParser.getContent());
            List<String> attachments = emlParser.getAttachmentNames();
            text = parseAttachment(text, attachments);

            metadata.set(DocumentMetadataKeys.DOCUMENT_TEXT, text);
            if (emlParser.getFrom() != null) {
                metadata.setMessageFrom(EmailUtil.getAddressLine(emlParser.getFrom()));
            }

            if (emlParser.getSubject() != null) {
                metadata.setMessageSubject(emlParser.getSubject());
            }

            if (emlParser.getMessageId() != null) {
                metadata.setMessageId(emlParser.getMessageId());
            }

            if (emlParser.getReferencedMessageIds() != null) {
                metadata.setReferencedMessageIds(StringUtils.join(emlParser.getReferencedMessageIds(), ", "));
            }

            if (emlParser.getTo() != null) {
                metadata.setMessageTo(EmailUtil.getAddressLine(emlParser.getTo()));
            }

            if (emlParser.getCC() != null) {
                metadata.setMessageCC(EmailUtil.getAddressLine(emlParser.getCC()));
            }

            if (emlParser.getDate() != null) {
                metadata.setMessageCreationDate(formatDate(emlParser.getDate()));
            }
        } catch (IOException | MessagingException e) {
            LOGGER.error("Problem parsing eml file ", e);
        }
    }

    public static String parseAttachment(String text, List<String> attachments) {
        if (attachments.size() > 0) {
            text += "<br/>=====================================<br/>Attachments:<br/><br/>";
            for (String att : attachments) {
                if (att != null) {
                    text += att + "<br/>";
                }
            }
        }
        return text;
    }

    private static String prepareContent(String content) {
        StringBuilder result = new StringBuilder();

        String[] lines = content.split("\n");
        for (String line : lines) {
            result.append(line.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
            result.append("<br/>");
        }

        return result.toString();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
*/

    @Override
    public String getSingleFileName() {
        return projectFile.getFile().getName();
    }

    @Override
    public void run() {



        String emailPath = getSingleFileName();
        String emailName = new File(emailPath).getName();
        // TODO this is a little more complex, there are attachments without extensions
        // if the file already has an extension - then it is an attachment
        String ext = Util.getExtension(emailPath);
        if (ext.isEmpty()) {
            emailPath += ".eml";
        }
        //LOGGER.debug("Processing eml file with path: " + emailPath + ", name: " + emailName);
        // update application log
        //LOGGER.trace("Processing file: {}", discoveryFile.getRealFileName());
        // set to true if file matches any query params
        boolean isResponsive = false;
        // exception message to place in output if error occurs
        String exceptionMessage = null;
        // ImageTextParser metadata, derived from Tika metadata class
        /*
        String extension = Util.getExtension(projectFile.getRealFileName());
        if ("jl".equalsIgnoreCase(extension)) {
            extractJlFields(projectFile);
        }
        try {
            extractMetadata();
            // search through Tika results using Lucene
            isResponsive = isResponsive(metadata);
        } catch (IOException | ParseException e) {
            LOGGER.warn("Exception processing file ", e);
            exceptionMessage = e.getMessage();
        }
        */

        // update exception message if error
        /*
        if (exceptionMessage != null) {
            metadata.set(DocumentMetadataKeys.PROCESSING_EXCEPTION, exceptionMessage);
        }
        if (isResponsive || exceptionMessage != null) {
            createImage(projectFile);
            if (isPreview()) {
                try {
                    createHtmlForDocument(projectFile);
                } catch (Exception e) {
                    metadata.set(DocumentMetadataKeys.PROCESSING_EXCEPTION, e.getMessage());
                }
            }
            writeMetadata();
        }
        */
    }
/*
    @Override
    public String getOriginalDocumentPath(DiscoveryFile discoveryFile) {
        //String pathToEmail = discoveryFile.getPath().getPath().substring(Settings.getSettings().getPSTDir().length() + 1);
        return new File(pathToEmail).getParent() + File.separator + discoveryFile.getRealFileName();
    }
    */
}