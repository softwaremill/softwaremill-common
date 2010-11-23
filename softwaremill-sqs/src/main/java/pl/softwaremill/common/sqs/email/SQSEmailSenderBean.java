package pl.softwaremill.common.sqs.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.SQSManager;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;
import pl.softwaremill.common.sqs.timer.TimerManager;
import pl.softwaremill.common.sqs.util.EmailDescription;
import pl.softwaremill.common.sqs.util.SQSAnswer;

import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * Receiver polling Amazon's SQS queue and sending emails
 *
 * Configured via sqs.conf in jboss/server/profile/conf or classpath
 * smtpHost= mailing host
 * smtpPort= mailing host's port
 * queue= SQS queue, where the email data is coming from
 * from= from address put into the eamil
 *
 * To use this bean write a @Stateless bean that extends SQSEmailSender
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 24, 2010
 * @author Adam Warski
 */
public abstract class SQSEmailSenderBean extends TimerManager implements SQSEmailSender {
    private static final Logger log = LoggerFactory.getLogger(SQSEmailSenderBean.class);

    public void startTimer(int interval){
        startTimer(EMAIL_SQS_QUEUE, interval);
    }

    public void destroyTimer(){
        destroyTimer(EMAIL_SQS_QUEUE);
    }

    @Timeout
    public void timeout(Timer timer) {
        SQSAnswer sqsAnswer = SQSManager.receiveMessage(EMAIL_SQS_QUEUE);

        if (sqsAnswer != null) {

            Object message = sqsAnswer.getMessage();

            if (message instanceof EmailDescription) {
                log.debug("Deserialized message: " + message);
                try {

                    EmailDescription emailDescription = (EmailDescription) message;

                    boolean secured = EMAIL_SMTP_USERNAME != null;

                    // Setup mail server
                    Properties props = System.getProperties();
                    if (secured) {
                        props.put("mail.smtps.host", EMAIL_SMTP_HOST);
                        props.put("mail.smtps.port", EMAIL_SMTP_PORT);
                        props.put("mail.smtps.starttls.enable", "true");
                        props.put("mail.smtps.auth", "true");
                        props.put("mail.smtps.user", EMAIL_SMTP_USERNAME);
                        props.put("mail.smtps.password", EMAIL_SMTP_PASSWORD);
                    } else {
                        props.put("mail.smtp.host", EMAIL_SMTP_HOST);
                        props.put("mail.smtp.port", EMAIL_SMTP_PORT);
                    }

                    // Get a mail session
                    Session session = Session.getDefaultInstance(props, null);

                    MimeMessage m = new MimeMessage(session);
                    m.setFrom(new InternetAddress(EMAIL_FROM));
                    Address[] to = new InternetAddress[emailDescription.getEmails().length];

                    for (int i = 0; i < emailDescription.getEmails().length; i++) {
                        to[i] = new InternetAddress(emailDescription.getEmails()[i]);
                    }

                    m.setRecipients(javax.mail.Message.RecipientType.TO, to);
                    m.setSubject(emailDescription.getSubject(), ENCODING);
                    m.setSentDate(new Date());
                    m.setText(emailDescription.getMessage(), ENCODING, "plain");
                    if (secured) {
                        Transport transport = session.getTransport("smtps");
                        try {
                            transport.connect(EMAIL_SMTP_USERNAME, EMAIL_SMTP_PASSWORD);
                            transport.sendMessage(m, m.getAllRecipients());
                        } finally {
                            transport.close();
                        }
                    } else {
                        Transport.send(m);
                    }

                    SQSManager.deleteMessage(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle());

                    log.debug("Mail sent to: " + Arrays.toString(to));
                }
                catch (javax.mail.MessagingException e) {
                    log.warn("Something went wrong and e-mail has not been sent. Redelivery will occur.");
                    SQSManager.setMessageVisibilityTimeout(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle(), 10);
                    throw new SQSRuntimeException(e);
                }
            } else {
                log.warn("Trash in SQS: " + EMAIL_SQS_QUEUE);
                log.warn("Deserialized message: " + message + " -- removing message from queue!");
                SQSManager.deleteMessage(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle());
            }


        }

        log.debug("SQS email queue checked " + EMAIL_SQS_QUEUE);
    }

    /**
     * Asynchronously sends the given email.
     */
    public static void sendEmail(EmailDescription email) {
        SQSManager.sendMessage(EMAIL_SQS_QUEUE, email);
    }
}
