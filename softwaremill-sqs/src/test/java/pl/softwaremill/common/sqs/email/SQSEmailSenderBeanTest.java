package pl.softwaremill.common.sqs.email;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.softwaremill.common.sqs.util.EmailDescription;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * Tests the SQSEmailSenderBean that reads message from sqs-queue and sends an email with that message
 *
 * @author maciek
 */
public class SQSEmailSenderBeanTest {


    private SimpleSmtpServer emailServer;
    private int REDELIVERY_LIMIT = 10;

    private class SQSEmailSenderBeanMock extends SQSEmailSenderBean {
         // In test we just need to invoke the timer manually
    };
    private SQSEmailSenderBean emailSenderBean = new SQSEmailSenderBeanMock();

    @BeforeClass
    public void init() {
        emailServer = SimpleSmtpServer.start(Integer.valueOf(EMAIL_SMTP_PORT));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws SQSException {
        if (emailServer != null) {
            emailServer.stop();
        }
        // Make sure the sqs queue is empty - if not next test run by anyone-anywhere could fail
        MessageQueue msgQueue = SQSUtils.connectToQueue(EMAIL_SQS_QUEUE, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        Message msg;
        while((msg = msgQueue.receiveMessage()) != null){
            msgQueue.deleteMessage(msg);
        }
    }
    
    @Test(enabled = true)
    public void testEmailDelivery() throws Exception {
        // Given
        String to = "sqs-test@example.org";
        String subject = "TEST: testEmailDelivery()";
        String message = "Simple message from SQS email test";

        // When
        SQSEmailSenderBean.sendEmail(new EmailDescription(to, message, subject));
        sendEmail();

        // Then
        assertThat(emailServer.getReceivedEmailSize()).isEqualTo(1);

        SmtpMessage email = getEmail();
        assertThat(email.getBody()).contains(message);
        assertThat(email.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(email.getHeaderValue("To")).isEqualTo(to);
        assertThat(email.getHeaderValue("From")).isEqualTo(EMAIL_FROM);
        assertThat(email.getHeaderValue("Content-Type")).contains("UTF-8");
    }

    private void sendEmail() throws InterruptedException {
        // Actually gets sent to SQS, might take some time to receive it
        for (int i = 0; i < REDELIVERY_LIMIT; i++) {
            Thread.sleep(7000);
            emailSenderBean.timeout(null);
            if (emailServer.getReceivedEmailSize() > 0) {
                break;
            }
        }
    }

    private SmtpMessage getEmail() {
        Iterator inbox = emailServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) inbox.next();
        // clear mock-inbox for other tests
        inbox.remove();
        return email;
    }
}
