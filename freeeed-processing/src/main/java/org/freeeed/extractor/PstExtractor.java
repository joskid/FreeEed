package org.freeeed.extractor;

import com.pff.*;
import org.apache.commons.io.FileUtils;
import org.freeeed.Entity.Project;
import org.freeeed.Entity.ProjectFile;
import org.freeeed.ServiceDao.ProjectFileService;
import org.freeeed.mr.FreeEedMR;
import org.freeeed.services.ProcessingStats;
import org.freeeed.services.UniqueIdGenerator;
import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.*;

public class PstExtractor implements Runnable {

    private ProjectFile projectFile;
    private String tmpFolderEML, tmpFolderAttachment;
    private List<PSTMessage> emailList = new ArrayList<>();

    public PstExtractor(ProjectFile projectFile) {
        this.projectFile = projectFile;
        String pstId = UniqueIdGenerator.INSTANCE.getNextDocumentId();
        tmpFolderEML = projectFile.getFile().getParent() + System.getProperty("file.separator") + pstId + "_" + projectFile.getFile().getName() + System.getProperty("file.separator") + "eml" + System.getProperty("file.separator");
        new File(tmpFolderEML).mkdirs();

        tmpFolderAttachment = projectFile.getFile().getParent() + System.getProperty("file.separator") + pstId + "_" + projectFile.getFile().getName() + System.getProperty("file.separator") + "attachment" + System.getProperty("file.separator");
        new File(tmpFolderAttachment).mkdirs();

    }

    private void processFolder(final PSTFolder folder) throws PSTException, IOException {
        if (folder.hasSubfolders()) {
            final Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (final PSTFolder childFolder : childFolders) {
                this.processFolder(childFolder);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
            PSTMessage email = (PSTMessage) folder.getNextChild();
            while (email != null) {
                emailList.add(email);
                email = (PSTMessage) folder.getNextChild();
            }
        }
    }

    private void extractAttachment(PSTMessage email, String emlId, ProjectFile emlProjectFile) throws PSTException, IOException {
        for (int attachIndex = 0; attachIndex < email.getNumberOfAttachments(); attachIndex++) {
            PSTAttachment pstAttachment = email.getAttachment(attachIndex);
            InputStream inputStream = pstAttachment.getFileInputStream();
            File f = new File(tmpFolderAttachment + emlId + "_" + pstAttachment.getFilename().trim());
            f.createNewFile();
            FileUtils.copyInputStreamToFile(inputStream, f);
            ProjectFile file = new ProjectFile(f.getPath(), Project.getActiveProject());
            file.setSourceFile(projectFile);
            file.setParent(emlProjectFile);
            ProjectFileService.getInstance().createProjectFile(file);
        }
    }

    private ProjectFile saveToEML(PSTMessage message, String emlId) throws MessagingException, IOException, PSTException {
        Properties properties = System.getProperties();
        Session session = Session.getInstance(properties);
        MimeMessage mimeMessage = new MimeMessage(session);
        if (message.getTransportMessageHeaders() != null) {
            InternetHeaders headers = new InternetHeaders(new ByteArrayInputStream(message.getTransportMessageHeaders().getBytes()));
            headers.removeHeader("Content-Type");
            Enumeration<Header> allHeaders = headers.getAllHeaders();
            while (allHeaders.hasMoreElements()) {
                Header header = allHeaders.nextElement();
                mimeMessage.addHeader(header.getName(), header.getValue());
            }
        } else {
            mimeMessage.setSubject(message.getSubject());
            mimeMessage.setSentDate(message.getClientSubmitTime());
            InternetAddress fromMailbox = new InternetAddress();
            fromMailbox.setAddress(message.getSenderEmailAddress());
            if (message.getSenderName() != null && message.getSenderName().length() > 0) {
                fromMailbox.setPersonal(message.getSenderName());
            } else {
                fromMailbox.setPersonal(message.getSenderEmailAddress());
            }
            mimeMessage.setFrom(fromMailbox);
            for (int i = 0; i < message.getNumberOfRecipients(); i++) {
                PSTRecipient recipient = message.getRecipient(i);
                Message.RecipientType type = null;
                switch (recipient.getRecipientType()) {
                    case PSTRecipient.MAPI_TO:
                        type = MimeMessage.RecipientType.TO;
                        break;
                    case PSTRecipient.MAPI_CC:
                        type = MimeMessage.RecipientType.CC;
                        break;
                    case PSTRecipient.MAPI_BCC:
                        type = MimeMessage.RecipientType.BCC;
                        break;
                }
                mimeMessage.setRecipient(type, new InternetAddress(recipient.getEmailAddress(), recipient.getDisplayName()));
            }
        }
        MimeMultipart rootMultipart = new MimeMultipart();
        MimeMultipart contentMultipart = new MimeMultipart();
        MimeBodyPart contentBodyPart = new MimeBodyPart();
        contentBodyPart.setContent(contentMultipart);
        if (message.getBody() != null && message.getBody().length() > 0) {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(message.getBody());
            contentMultipart.addBodyPart(textBodyPart);
        }
        if (message.getBodyHTML() != null) {
            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            String htmlPart = message.getBodyHTML();
            htmlBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlPart, "text/html")));
            contentMultipart.addBodyPart(htmlBodyPart);
        }
        if (message.getBody() == null) {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText("<<Empty Body>>");
            textBodyPart.addHeaderLine("Content-Type: text/plain; charset=\"utf-8\"");
            textBodyPart.addHeaderLine("Content-Transfer-Encoding: quoted-printable");
            contentMultipart.addBodyPart(textBodyPart);
        }
        rootMultipart.addBodyPart(contentBodyPart);
        mimeMessage.setContent(rootMultipart);
        File emlFile = new File(tmpFolderEML + emlId + ".eml");
        emlFile.createNewFile();
        mimeMessage.writeTo(new FileOutputStream(emlFile));
        ProjectFile file = new ProjectFile(emlFile.getPath(), Project.getActiveProject());
        file.setSourceFile(projectFile);
        return ProjectFileService.getInstance().createProjectFile(file);
    }

    private void processEmailList() {
        emailList.forEach(email -> {
            String emlId = UniqueIdGenerator.INSTANCE.getNextDocumentId();
            ProjectFile emlProjectFile = null;
            try {
                emlProjectFile = saveToEML(email, emlId);

                if (email.hasAttachments()) {
                    extractAttachment(email, emlId, emlProjectFile);
                }

            } catch (MessagingException | IOException | PSTException e) {
                e.printStackTrace();
            }
            ProcessingStats.getInstance().addpstFilExtractedSize(email.getMessageSize());
        });
    }

    @Override
    public void run() {
        try {
            PSTFile pstFile = new PSTFile(projectFile.getFile());
            processFolder(pstFile.getRootFolder());
            processEmailList();
            pstFile.close();
            projectFile.setAsProcessed();
            ProjectFileService.getInstance().updateProjectFile(projectFile);
            FreeEedMR.getInstance().reducePSTFile();
        } catch (PSTException | IOException e) {
            e.printStackTrace();
        }
    }
}
