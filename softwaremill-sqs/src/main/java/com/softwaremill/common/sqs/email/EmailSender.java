package com.softwaremill.common.sqs.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.softwaremill.common.sqs.util.AttachmentDescription;
import com.softwaremill.common.sqs.util.EmailDescription;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    public static void send(String smtpHost, String smtpPort, String smtpUsername, String smtpPassword, String from,
                            String encoding, EmailDescription emailDescription,
                            AttachmentDescription... attachmentDescriptions) throws MessagingException {
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

        Address[] to = convertStringEmailsToAddresses(emailDescription.getEmails());
        Address[] replyTo = convertStringEmailsToAddresses(emailDescription.getReplyToEmails());
        Address[] cc = convertStringEmailsToAddresses(emailDescription.getCcEmails());
        Address[] bcc = convertStringEmailsToAddresses(emailDescription.getBccEmails());

        m.setRecipients(javax.mail.Message.RecipientType.TO, to);
        m.setRecipients(Message.RecipientType.CC, cc);
        m.setRecipients(Message.RecipientType.BCC, bcc);
        m.setReplyTo(replyTo);
        m.setSubject(emailDescription.getSubject(), encoding);
        m.setSentDate(new Date());

        if (attachmentDescriptions.length > 0) {
            addAttachments(m, emailDescription.getMessage(), encoding, attachmentDescriptions);
        } else {
            m.setText(emailDescription.getMessage(), encoding, "plain");
        }

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

    private static Address[] convertStringEmailsToAddresses(String[] emails) throws AddressException {
        Address[] addresses = new InternetAddress[emails.length];

        for (int i = 0; i < emails.length; i++) {
            addresses[i] = new InternetAddress(emails[i]);
        }

        return addresses;
    }

    private static void addAttachments(MimeMessage mimeMessage, String msg, String encoding,
                                       AttachmentDescription... attachmentDescriptions) throws MessagingException {
        MimeMultipart multiPart = new MimeMultipart();

        MimeBodyPart textPart = new MimeBodyPart();
        multiPart.addBodyPart(textPart);
        textPart.setText(msg, encoding, "plain");

        for (final AttachmentDescription attachmentDescription : attachmentDescriptions) {
            MimeBodyPart binaryPart = new MimeBodyPart();
            multiPart.addBodyPart(binaryPart);

            DataSource ds = new DataSource() {
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(attachmentDescription.getContent());
                }

                public OutputStream getOutputStream() throws IOException {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    byteStream.write(attachmentDescription.getContent());
                    return byteStream;
                }

                public String getContentType() {
                    return attachmentDescription.getContentType();
                }

                public String getName() {
                    return attachmentDescription.getFilename();
                }
            };
            binaryPart.setDataHandler(new DataHandler(ds));
            binaryPart.setFileName(attachmentDescription.getFilename());
            binaryPart.setDescription("");
        }

        mimeMessage.setContent(multiPart);
    }
}
