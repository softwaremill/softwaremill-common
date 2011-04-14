package pl.softwaremill.common.sqs.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.util.EmailDescription;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    public static void send(String smtpHost, String smtpPort, String smtpUsername, String smtpPassword, String from,
                            String encoding, EmailDescription emailDescription) throws MessagingException {
        boolean secured = smtpUsername != null;

        // Setup mail server
        Properties props = new Properties();
        if (secured) {
            props.put("mail.smtps.host", smtpHost);
            props.put("mail.smtps.port", smtpPort);
            props.put("mail.smtps.starttls.enable", "true");
            props.put("mail.smtps.auth", "true");
            props.put("mail.smtps.user", smtpUsername);
            props.put("mail.smtps.password", smtpPassword);
        } else {
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
        }

        // Get a mail session
        Session session = Session.getInstance(props);

        MimeMessage m = new MimeMessage(session);
        m.setFrom(new InternetAddress(from));
        Address[] to = new InternetAddress[emailDescription.getEmails().length];

        for (int i = 0; i < emailDescription.getEmails().length; i++) {
            to[i] = new InternetAddress(emailDescription.getEmails()[i]);
        }

        m.setRecipients(javax.mail.Message.RecipientType.TO, to);
        m.setSubject(emailDescription.getSubject(), encoding);
        m.setSentDate(new Date());
        m.setText(emailDescription.getMessage(), encoding, "plain");
        if (secured) {
            Transport transport = session.getTransport("smtps");
            try {
                transport.connect(smtpUsername, smtpPassword);
                transport.sendMessage(m, m.getAllRecipients());
            } finally {
                transport.close();
            }
        } else {
            Transport.send(m);
        }

        log.debug("Mail '" + emailDescription.getSubject() + "' sent to: " + Arrays.toString(to));
    }
}
