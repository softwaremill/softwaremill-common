package pl.softwaremill.common.sqs;

import com.xerox.amazonws.sqs2.SQSException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pl.softwaremill.common.sqs.util.SQSAnswer;

import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * test for SQSManager
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 16, 2010
 */
public class SQSManagerTest {
    private static final String MESSAGE = "This is a simple String";

    @Test(enabled = true)
    public void testTwoMessagesSQSDelivery() throws InterruptedException {
        SQSManager.setQueueVisibilityTimeout("test", 0);

        //send a message to SQS
        SQSManager.sendMessage("test", MESSAGE);
        SQSManager.sendMessage("test", MESSAGE);

        //receive message from SQS
        SQSAnswer answer1 = receiveAndDeleteMessage();
        SQSAnswer answer2 = receiveAndDeleteMessage();

        //verify received message
        Assert.assertEquals(answer1.getMessage().toString(), MESSAGE);
        Assert.assertEquals(answer2.getMessage().toString(), MESSAGE);
    }

    private SQSAnswer receiveAndDeleteMessage() throws InterruptedException {
        for (int i=0; i<3; i++) {
            SQSAnswer answer = SQSManager.receiveMessage("test");
            if (answer != null) {
                SQSManager.deleteMessage("test", answer.getReceiptHandle());
                return answer;
            }

            Thread.sleep(1000);
        }

        throw new RuntimeException("No message received from queue test in 3 seconds");
    }

    @AfterClass(alwaysRun = true)
    public void emptyQueue() throws SQSException {
        new SQSEmptor(SQS_SERVER, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, "test").emptyQueue();
    }
}
