package pl.softwaremill.common.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;

import java.io.IOException;
import java.io.Serializable;

import static com.google.common.base.Preconditions.*;
import static java.lang.String.format;
import static pl.softwaremill.common.sqs.Util.deserializeFromBase64;

/**
 * @author Maciej Bilas
 * @since 11/10/12 12:43
 */
public class Queue {
    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);

    private static final int DELIVERY_RETRY_LIMIT = 10;
    private static final int DELIVERY_RETRY_SLEEP_TIME_IN_MILLIS = 1000;

    private final String name;
    private final String url;
    private final AmazonSQS sqsClient;

    @SuppressWarnings("UnusedDeclaration")
    public Queue() {
        LOG.error("Don't use this constructor. It's here only to make CDI happy.");
        this.name = null;
        this.url = null;
        this.sqsClient = null;
    }

    public Queue(String name, String url, AmazonSQS sqsClient) {
        this.name = name;
        this.url = checkNotNull(url);
        this.sqsClient = checkNotNull(sqsClient);
    }

    public String getURL() {
        return url;
    }

    private MessageId sendSerializableInternal(Serializable object, Optional<Duration> duration) {
        String encodedMessage;
        try {
            encodedMessage = Util.serializeToBase64(object);
        } catch (IOException e) {
            throw new SQSRuntimeException("Could not serialize message.", e);
        }

        checkState(encodedMessage.length() <= 64 * 1024);

        LOG.debug("Serialized Message: " + encodedMessage);

        SendMessageRequest request = new SendMessageRequest(url, encodedMessage);
        if (duration.isPresent()) {
            request.withDelaySeconds((int) duration.get().getStandardSeconds());
        }

        for (int i = 0; i < DELIVERY_RETRY_LIMIT; ++i) {
            try {
                /* No verification of message checksum is done at this point. It might get added in the future, though. */

                return new MessageId(sqsClient.sendMessage(request).getMessageId());
            } catch (AmazonServiceException e) {
                LOG.warn(format("Could not sent message to SQS queue: %s. Retrying.", url), e);
                try {
                    Thread.sleep(DELIVERY_RETRY_SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e1) {
                    // Ignore
                }
            }
        }
        throw new SQSRuntimeException("Exceeded redelivery value: " + DELIVERY_RETRY_LIMIT + "; message not sent!");
    }

    public MessageId sendSerializable(Serializable object) {
        checkNotNull(object);

        return sendSerializableInternal(object, Optional.<Duration>absent());
    }

    public MessageId sendSerializableDelayed(Serializable object, Duration duration) {
        checkNotNull(object);
        checkNotNull(duration);
        checkArgument(duration.getStandardSeconds() < 15 * 60,
                "SQS messages can only be delayed for a maximum of 15 minutes.");

        long droppedMillis = duration.getMillis() % 1000;
        if (droppedMillis != 0)
            LOG.warn(format("SQS delayed messages have a precision of 1s, milliseconds will be stripped. " +
                    "Object to send: %s Millis: %s", object.toString(), duration.getMillis()));

        if (duration.getMillis() < 1000) {
            return sendSerializable(object);
        } else {
            return sendSerializableInternal(object, Optional.of(duration));
        }
    }

    public Optional<ReceivedMessage> receiveSingleMessage() {
        LOG.debug(format("Polling queue %s", url));

        ReceiveMessageResult response = sqsClient.receiveMessage(new ReceiveMessageRequest(url)
                .withMaxNumberOfMessages(1).withAttributeNames("SentTimestamp"));

        switch (response.getMessages().size()) {
            case 0:
                return Optional.absent();
            case 1:
                return Optional.fromNullable(decodeMessage(response.getMessages().get(0)));
            default:
                /* This seems very unlikely.
                 * The API specified the following: SQS never returns more messages than this value but might return fewer.
                 * http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryReceiveMessage.html
                 */
                throw new IllegalStateException();
        }

    }

    private ReceivedMessage decodeMessage(Message message) {
        String receiptHandle = message.getReceiptHandle();
        try {
            /* Again no MD5 verification, yet */
            return new ReceivedMessage(deserializeFromBase64(message.getBody()),
                    new ReceiptHandle(receiptHandle),
                    new MessageId(message.getMessageId()));
        } catch (IOException e) {
            LOG.warn(format("Could not deserialize message from the queue %s.", name));
            LOG.debug(format("Message body of unrecognized message %s", message.getBody()));
            delete(receiptHandle);
            return null;
        } catch (ClassNotFoundException e) {
            LOG.warn(format("Could not deserialize message from the queue %s.", name));
            LOG.debug(format("Message body of unrecognized message %s", message.getBody()));
            delete(receiptHandle);
            return null;
        }
    }

    private void delete(String receiptHandle) {
        sqsClient.deleteMessage(new DeleteMessageRequest(url, receiptHandle));
    }

    public void deleteMessage(ReceivedMessage message) {
        checkNotNull(message);

        delete(message.getReceiptHandle().get());
    }

    /**
     * @param timeout timeout in seconds for the whole queue (default is 30) - value is limited to 43200 seconds (12 hours)
     */
    public void setQueueVisibilityTimeout(int timeout) {
        checkArgument(timeout >= 0);
        checkArgument(timeout <= 12 * 60 * 60);

        sqsClient.setQueueAttributes(new SetQueueAttributesRequest(url,
                ImmutableMap.of("VisibilityTimeout", Integer.toString(timeout))));
    }


    public void setMessageVisibilityTimeout(ReceivedMessage message, int timeout) {
        checkNotNull(message);
        checkArgument(timeout >= 0);
        checkArgument(timeout <= 12 * 60 * 60);

        sqsClient.changeMessageVisibility(new ChangeMessageVisibilityRequest(url,
                message.getReceiptHandle().get(), timeout));
    }
}
