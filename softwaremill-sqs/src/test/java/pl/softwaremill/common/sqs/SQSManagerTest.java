package pl.softwaremill.common.sqs;

import org.testng.Assert;
import org.testng.annotations.Test;
import pl.softwaremill.common.sqs.util.SQSAnswer;

/**
 * test for SQSManager
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 16, 2010
 */
public class SQSManagerTest {

    private static final String MESSAGE = "This is a simple String";

    @Test(enabled = true)
    public void testSQSDelivery(){

        //send a message to SQS
        SQSManager.sendMessage("test", MESSAGE);

        //receive message from SQS
        SQSAnswer answer = SQSManager.receiveMessage("test");

        //verify received message
        Assert.assertEquals(answer.getMessage().toString(), MESSAGE);

        //delete message
        SQSManager.deleteMessage("test", answer.getReceiptHandle());

    }
}
