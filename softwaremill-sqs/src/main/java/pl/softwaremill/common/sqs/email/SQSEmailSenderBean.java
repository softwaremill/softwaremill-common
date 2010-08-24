package pl.softwaremill.common.sqs.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.SQSManager;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;
import pl.softwaremill.common.sqs.timer.TimerManager;
import pl.softwaremill.common.sqs.util.EmailDescription;
import pl.softwaremill.common.sqs.util.SQSAnswer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Receiver polling Amazon's SQS queue and sending emails
 * Host and port are configured via ejb-jar.xml
 * queue name and reply address are configured via system properties
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 24, 2010
 */
@Stateless
public class SQSEmailSenderBean extends TimerManager implements SQSEmailSender {

    @Resource
    TimerService timerService;

    @Resource(name = "smtpHost")
    private String smtpHost;

    @Resource(name = "smtpPort")
    private String smtpPort;

    private static final Logger log = LoggerFactory.getLogger(SQSEmailSenderBean.class);

    private final String queue = System.getProperty("project.prefix", "test_") + "Email";

    private final String reply = System.getProperty("project.reply", "do-not-reply@localhost.com");

    public void startTimer(int interval){
        startTimer(queue, interval);
    }

    public void destroyTimer(){
        destroyTimer(queue);
    }

    @Timeout
    public void timeout(Timer timer) {

        log.debug("Checking SQS queue " + queue);

        SQSAnswer sqsAnswer = SQSManager.receiveMessage(queue);

        if (sqsAnswer != null) {

            Object message = sqsAnswer.getMessage();

            if (message instanceof EmailDescription) {
                log.debug("Deserialized message: " + message);
                try {

                    EmailDescription emailDescription = (EmailDescription) message;

                    // Setup mail server
                    Properties props = System.getProperties();
                    props.put("mail.smtp.host", smtpHost);
                    props.put("mail.smtp.port", smtpPort);

                    // Get a mail session
                    Session session = Session.getDefaultInstance(props, null);

                    MimeMessage m = new MimeMessage(session);
                    m.setFrom(new InternetAddress(reply));
                    Address[] to = new InternetAddress[emailDescription.getEmails().length];

                    for (int i = 0; i < emailDescription.getEmails().length; i++) {
                        to[i] = new InternetAddress(emailDescription.getEmails()[i]);
                    }

                    m.setRecipients(javax.mail.Message.RecipientType.TO, to);
                    m.setSubject(emailDescription.getSubject());
                    m.setSentDate(new Date());
                    m.setContent(emailDescription.getMessage(), "text/plain");
                    Transport.send(m);

                    SQSManager.deleteMessage(queue, sqsAnswer.getReceiptHandle());

                }
                catch (javax.mail.MessagingException e) {
                    log.warn("Something went wrong and e-mail has not been sent. Redelivery will occur.");
                    SQSManager.setMessageVisibilityTimeout(queue, sqsAnswer.getReceiptHandle(), 0);
                    throw new SQSRuntimeException(e);
                }
            } else {
                log.warn("Trash in SQS: " + queue);
                log.warn("Deserialized message: " + message + " -- removing message from queue!");
                SQSManager.deleteMessage(queue, sqsAnswer.getReceiptHandle());
            }


        }

        log.debug("SQS queue checked " + queue);

    }
}
