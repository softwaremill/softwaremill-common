package pl.softwaremill.common.sqs;

import com.amazonaws.AmazonServiceException;
import com.google.common.base.Optional;

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

    public void emptyQueue() throws AmazonServiceException {
        Queue queue = new SQS(serverName, accessKeyId, secretAccessKey).getQueueByName(queueName);
        Optional<ReceivedMessage> message;
        do {
            message = queue.receiveSingleMessage();
            if (message.isPresent()) {
                System.out.println("Deleting message: " + message.get().getMessageId().get() + " (" + message.get().getReceiptHandle().get() + ")");
                queue.deleteMessage(message.get());
            }
        } while (message.isPresent());

        System.out.println("Queue emptied");
    }

    public static void main(String[] args) throws IOException, AmazonServiceException {
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
