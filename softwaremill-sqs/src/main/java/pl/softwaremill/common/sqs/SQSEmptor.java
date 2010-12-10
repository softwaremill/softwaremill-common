package pl.softwaremill.common.sqs;

import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SQSEmptor {
    private final String serverName;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String queueName;

    public SQSEmptor(String serverName, String accessKeyId, String secretAccessKey, String queueName) {
        this.serverName = serverName;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.queueName = queueName;
    }

    public void emptyQueue() throws SQSException {
        MessageQueue msgQueue = SQSManager.connectToQueue(serverName, accessKeyId, secretAccessKey, queueName, -1);

        Message[] msgs;

        do {
            msgs = msgQueue.receiveMessages(10);
            for (Message msg : msgs) {
                System.out.println("Deleting message: " + msg.getMessageId() + " (" + msg.getReceiptHandle() + ")");
                msgQueue.deleteMessage(msg);
            }
        } while (msgs.length > 0);

        System.out.println("Queue emptied");
    }

    public static void main(String[] args) throws IOException, SQSException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Server name (return for queue.amazonaws.com):");
        String serverName = rdr.readLine().trim();
        if ("".equals(serverName)) {
            serverName = "queue.amazonaws.com";
        }

        System.out.println("Access key id:");
        String accessKeyId = rdr.readLine().trim();
        if ("".equals(accessKeyId)) {
            return;
        }

        System.out.println("Secret access key:");
        String secretAccessKey = rdr.readLine().trim();
        if ("".equals(secretAccessKey)) {
            return;
        }

        while (true) {
            System.out.println("Queue name to empty:");
            String queueName = rdr.readLine().trim();
            if ("".equals(queueName)) {
                return;
            }

            new SQSEmptor(serverName, accessKeyId, secretAccessKey, queueName).emptyQueue();
        }
    }
}
