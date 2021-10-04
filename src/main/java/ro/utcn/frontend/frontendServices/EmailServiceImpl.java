package ro.utcn.frontend.frontendServices;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.MailMessage;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static ro.utcn.exceptions.GeneralExceptions.MAIL_SUCCESS;


/**
 * This class is used for implementing a mail service in Java
 *
 * @author lucian.davidescu
 */
@Service
public class EmailServiceImpl {

    //LOGGER instantiation
    private static final Logger LOGGER = LogManager.getLogger(EmailServiceImpl.class);

    @Resource
    private Properties mailProperties;

    /**
     * This method will send a message to an user
     */
    public void sendMail(MailMessage mailMessage) throws GeneralExceptions {

        LOGGER.info("Start to send email to: " + mailMessage.getMailTo() + ", \nwith subject: " + mailMessage.getSubject() + ", \n content:" + mailMessage.getMessage());

        Session session = Session.getInstance(mailProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication((String) mailProperties.get("mail.username"), (String) mailProperties.get("mail.password"));
                    }
                });

        try {
            LOGGER.trace("Creates the message");
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress((String) mailProperties.get("mail.from")));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailMessage.getMailTo()));

            // Set Subject: header field
            message.setSubject(mailMessage.getSubject());

            // Send the actual HTML message, as big as you like
            message.setContent(mailMessage.getMessage(), "text/plain");

            LOGGER.debug("Sending the message");
            Transport.send(message);
            LOGGER.info("Mail sent");
        } catch (MessagingException mex) {
            LOGGER.error("Mail send failed", mex);
            throw new GeneralExceptions(mex.getMessage());
        }

    }

}
