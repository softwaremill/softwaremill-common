package pl.softwaremill.common.sqs;

import org.testng.Assert;
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
        long oldDiscardMessagesSentBefore = getDiscardMessagesSentBefore();
        // Allowing a second time difference between amazon and our clocks
        setDiscardMessagesSentBefore(System.currentTimeMillis() - 1000);

        try {
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
        } finally {
            setDiscardMessagesSentBefore(oldDiscardMessagesSentBefore);
        }
    }

    private SQSAnswer receiveAndDeleteMessage() throws InterruptedException {
        for (int i=0; i<10; i++) {
            SQSAnswer answer = SQSManager.receiveMessage("test");
            if (answer != null) {
                SQSManager.deleteMessage("test", answer.getReceiptHandle());
                return answer;
            }

            Thread.sleep(1000);
        }

        throw new RuntimeException("No message received from queue test in 10 seconds");
    }
}
