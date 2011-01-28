package pl.softwaremill.common.sqs;

import com.xerox.amazonws.sqs2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;
import pl.softwaremill.common.sqs.util.Base64Coder;
import pl.softwaremill.common.sqs.util.SQSAnswer;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * Class for sending messages to Amazon's Simple Queue Service
 * Configured via sqs.conf in jboss/server/profile/conf or classpath
 * AWSAccessKeyId= aws access key
 * SecretAccessKey= secret key
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 16, 2010
 * @author Adam Warski
 */
public class SQSManager {
    private static final Logger log = LoggerFactory.getLogger(SQSManager.class);

    private static final int REDELIVERY_LIMIT = 10;

    private static final Map<String, String> queueUrlCache = new ConcurrentHashMap<String, String>();

    /**
     * @param queue   the name of the SQS queue for which to set the timeout
     * @param timeout timeout in seconds for the whole queue (default is 30) - value is limited to 43200 seconds (12 hours)
     */
    public static void setQueueVisibilityTimeout(String queue, int timeout) {
        try {
            MessageQueue msgQueue = connectToQueue(queue, timeout);
            msgQueue.setVisibilityTimeout(timeout);
        } catch (SQSException e) {
            throw new SQSRuntimeException("Could not setup SQS queue: " + queue, e);
        }
    }


    /**
     * Sends a serializable message to an SQS queue using the Base64Coder util to encode it properly
     *
     * @param queue   the SQS queue the message is sent to
     * @param message a Serializable object
     */
    public static void sendMessage(String queue, Serializable message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            oos.close();
            baos.close();

        } catch (IOException e) {
            throw new SQSRuntimeException("Could not create stream, SQS message not sent: ", e);
        }

        String encodedMessage = new String(Base64Coder.encode(baos.toByteArray()));

        log.debug("Serialized Message: " + encodedMessage);

        for (int i = 0; i < REDELIVERY_LIMIT; i++) {
            try {
                MessageQueue msgQueue = connectToQueue(queue, -1);
                String msgId = msgQueue.sendMessage(encodedMessage);

                log.debug("Sent message with id " + msgId + " to queue " + queue);
                i = REDELIVERY_LIMIT;

            } catch (SQSException e) {
                log.error("Colud not sent message to SQS queue: " + queue, e);
                if (i == REDELIVERY_LIMIT) {
                    throw new SQSRuntimeException("Exceeded redelivery value: " + REDELIVERY_LIMIT + "; message not sent! ", e);
                }
                log.info("Retrying in 1 second");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Receives a message from a SQS queue and decodes it properly to an object using the Base64Coder util
     *
     * @param queue the SQS queue the message is received from
     * @return SQSAnswer holding an Object and the receipt handle for further processing
     *         or {@code null} if no message was available
     */
    public static SQSAnswer receiveMessage(String queue) {
        try {

            log.debug("Polling queue " + queue);

            MessageQueue msgQueue = connectToQueue(queue, -1);

            Message msg = msgQueue.receiveMessage();

            if (msg != null) {
                String data = msg.getMessageBody();

                ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decode(data));
                Object answer;

                try {
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    answer = ois.readObject();
                } catch (IOException e) {
                    log.error("I/O exception.", e);
                    deleteMessage(queue, msg.getReceiptHandle());
                    return null;
                } catch (ClassNotFoundException e) {
                    log.error("Class of the serialized object cannot be found.", e);
                    deleteMessage(queue, msg.getReceiptHandle());
                    return null;
                }

                log.debug("Got message from queue " + queue);

                return new SQSAnswer(answer, msg.getReceiptHandle());
            } else {
                return null;
            }

        } catch (SQSException e) {
            throw new SQSRuntimeException("Could not receive message from SQS queue: " + queue, e);
        }
    }


    /**
     * Deletes a message from a SQS queue
     *
     * @param receiptHandle handle of the message to be deleted
     * @param queue         SQS queue the message is held
     */
    public static void deleteMessage(String queue, String receiptHandle) {
        try {
            MessageQueue msgQueue = connectToQueue(queue, -1);
            msgQueue.deleteMessage(receiptHandle);
            log.debug("Deleted message in queue: " + queue);
        } catch (SQSException e) {
            throw new SQSRuntimeException("Could not delete message in queue " + queue + "! This will cause a redelivery.", e);
        }
    }


    /**
     * Resets a messages timeout in a SQS queue
     *
     * @param receiptHandle handle of the message to be reset
     * @param timeOut       new timeout to be set
     * @param queue         SQS queue the message is held
     */
    public static void setMessageVisibilityTimeout(String queue, String receiptHandle, int timeOut) {
        try {
            MessageQueue msgQueue = connectToQueue(queue, -1);
            msgQueue.setMessageVisibilityTimeout(receiptHandle, timeOut);
            log.debug("Set timeout to " + timeOut + " seconds in queue: " + queue);
        } catch (SQSException e) {
            throw new SQSRuntimeException("Could not reset timeout for message in queue " + queue + "! This will cause a delay in redelivery.", e);
        }
    }

    private static MessageQueue connectToQueue(String queue, int visibilityTimeout) throws SQSException {
        return connectToQueue(SQS_SERVER, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, queue, visibilityTimeout);
    }

    public static MessageQueue connectToQueue(String serverName, String accessKeyId, String secretAccessKey,
                                              String queue, int visibilityTimeout) throws SQSException {
        QueueService queueService = SQSUtils.getQueueService(accessKeyId, secretAccessKey, serverName);

        String queueUrl = queueUrlCache.get(queue);
        if (queueUrl == null) {
            MessageQueue messageQueue = queueService.getOrCreateMessageQueue(queue, visibilityTimeout);
            queueUrlCache.put(queue, messageQueue.getUrl().toString());
            return messageQueue;
        } else {
            return queueService.getMessageQueue(queueUrl);
        }
    }

}