package org.rockhill.adoration.helper;

import com.sun.mail.smtp.SMTPTransport; //NOSONAR - need to be kept till it is working
import org.rockhill.adoration.configuration.EmailConfigurationAccess;
import org.rockhill.adoration.configuration.PropertyDto;
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

    private void sendProperMail(final String subject, final String text, final String to, final String cc, final String typeText) {
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
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // cc
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            // subject
            msg.setSubject(subject);
            // content
            msg.setContent(text, "text/plain; charset=UTF-8");
            msg.setSentDate(new Date());
            // Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            // connect
            t.connect(propertyDto.getSmtpServer(), propertyDto.getSmtpUserName(), propertyDto.getSmtpPassword());
            // send
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            logger.info(String.format("Send Email: %s, Response: %s", typeText, response));
            t.close();
        } catch (MessagingException e) {
            logger.warn(String.format("Send Email: %s FAILED, Response: %s", typeText, response), e);
        }
    }

    public void sendMailToAdministrator(final String subject, final String text) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        sendProperMail(subject, text, propertyDto.getEmailTo(), "", "to Administrator");
    }

    public void sendMailFromSocialLogin(String proviedEmail, String subject, String text) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        sendProperMail(subject, text, proviedEmail, propertyDto.getEmailTo(), "to person logged-in first time");
    }
}
