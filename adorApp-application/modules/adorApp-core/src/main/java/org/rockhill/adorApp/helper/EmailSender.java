package org.rockhill.adorApp.helper;

import com.sun.mail.smtp.SMTPTransport;
import org.rockhill.adorApp.configuration.EmailConfigurationAccess;
import org.rockhill.adorApp.configuration.PropertyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component
public class EmailSender {
    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    EmailConfigurationAccess emailConfigurationAccess;

    public void sendMail(final String subject, final String text) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        Properties prop = new Properties();
        prop.put("mail.smtp.host", propertyDto.getSmtpServer()); //optional, defined in SMTPTransport
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", propertyDto.getSmtpPort()); // default port 25

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        String response = "failed";
        try {
            // from
            msg.setFrom(new InternetAddress(propertyDto.getEmailFrom()));
            // to
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(propertyDto.getEmailTo(), false));
            // cc
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse("", false));
            // subject
            msg.setSubject(subject);
            // content
            msg.setText(text);
            msg.setSentDate(new Date());
            // Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            // connect
            t.connect(propertyDto.getSmtpServer(), propertyDto.getSmtpUserName(), propertyDto.getSmtpPassword());
            // send
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            logger.info("Send Email Response: " + response);
            t.close();
        } catch (MessagingException e) {
            logger.warn("Send Email Failed - Response: " + response, e);
        }
    }
}
