package ru.telepuzinator.gmaillibrary;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

public class GmailOauthSender {
    private Session session;

    private SMTPTransport connectToSmtp(String host, int port, String userEmail,
                                       String oauthToken) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");
        session = Session.getInstance(props);
        session.setDebug(false);


        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        final String emptyPassword = null;
        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
                oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        transport.issueCommand("AUTH XOAUTH2 " + new String(response), 235);

        return transport;
    }

    public synchronized void sendMail(String subject, String body, String user,
                                      String oauthToken, String recipients) throws IOException,
            AuthenticationException, MessagingException {
        SMTPTransport smtpTransport = null;
        try {
            smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    oauthToken);
        } catch (Exception e) {
            String error;
            if((error = e.getMessage()) != null) {
                error = error.trim();
                if(error.contains("Unknown SMTP")) {
                    error = "No Internet";
                    throw new IOException(error);
                } else if(error.trim().startsWith("334")) {
                    throw new AuthenticationException(error);
                }
            }
        }
        if(smtpTransport == null) return;

            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            smtpTransport.sendMessage(message, message.getAllRecipients());
    }
}
