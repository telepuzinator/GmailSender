package ru.telepuzinator.gmaillibrary;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MessageBuilder {
    private MimeMessage message;
    private Multipart multipartContent;

    public MessageBuilder(Session session) {
        message = new MimeMessage(session);
        multipartContent = new MimeMultipart();
    }

    public MessageBuilder setSender(String sender) throws MessagingException {
        message.setSender(new InternetAddress(sender));
        return this;
    }

    public MessageBuilder setSubject(String subject) throws MessagingException {
        if(subject == null) subject = "";
        message.setSubject(subject);
        return this;
    }

    public MessageBuilder setBody(String body) throws MessagingException {
        if(body == null) body = "";
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setText(body);
        multipartContent.addBodyPart(mimeBodyPart);
        return this;
    }

    public MessageBuilder setFiles(File... files) throws MessagingException {
        if(files != null) {
            for(File f: files) {
                if(f != null && f.exists() && f.isFile()) {
                    addFile(f);
                }
            }
        }
        return this;
    }

    private void addFile(File file) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(file);
        bodyPart.setDataHandler(new DataHandler(fds));
        bodyPart.setFileName(fds.getName());
        multipartContent.addBodyPart(bodyPart);
    }

    public MessageBuilder setRecipients(String recipients) throws MessagingException {
        if (recipients.indexOf(',') > 0) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        } else {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        }
        return this;
    }

    public MimeMessage build() throws MessagingException {
        message.setContent(multipartContent);
        return message;
    }
}
