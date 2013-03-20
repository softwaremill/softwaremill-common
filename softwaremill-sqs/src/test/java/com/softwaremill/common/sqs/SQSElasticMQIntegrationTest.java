package com.softwaremill.common.sqs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.jayway.awaitility.Duration;
import org.elasticmq.Node;
import org.elasticmq.NodeAddress;
import org.elasticmq.NodeBuilder;
import org.elasticmq.rest.RestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.elasticmq.storage.inmemory.InMemoryStorage;
import org.hamcrest.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.softwaremill.common.util.Sleeper;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.String.format;
import static org.fest.assertions.Assertions.assertThat;
import static org.joda.time.Duration.standardMinutes;
import static org.joda.time.Duration.standardSeconds;

/**
 * @author Maciej Bilas
 * @since 15/10/12 11:53
 */
public class SQSElasticMQIntegrationTest {

    private static final int ELASTIMQ_PORT = 12365;
    private static final String TEST_QUEUE = "test-queue";
    private RestServer sqsServer;
    private Node elasticNode;

    private boolean queueCreated = false;

    @BeforeClass
    public void setupElasticMQ() {
        elasticNode = NodeBuilder.withStorage(new InMemoryStorage());
        sqsServer = new SQSRestServerBuilder(elasticNode.nativeClient(), ELASTIMQ_PORT,
                new NodeAddress("http", "localhost", ELASTIMQ_PORT, "")).start();
    }

    private AmazonSQS createDefaultSQSClient() {
        AmazonSQSClient sqsClient = new AmazonSQSClient(new BasicAWSCredentials("1234", "1234"));
        sqsClient.setEndpoint(format("http://localhost:%s", ELASTIMQ_PORT));
        return sqsClient;
    }


    @Test
    public void shouldCreateAndObtainAQueue() {
        // When
        Queue testQueue = obtainTestQueue();

        // Then
        assertThat(testQueue.getURL()).isNotNull();
    }

    private Queue obtainTestQueue() {
        AmazonSQS sqsClient = createDefaultSQSClient();
        SQS sqs = new SQS(sqsClient);

        if (!queueCreated) {
            sqsClient.createQueue(new CreateQueueRequest(TEST_QUEUE));
            queueCreated = true;
        }

        return sqs.getQueueByName(TEST_QUEUE);
    }

    @Test
    public void shouldSendAReceiveAMessage() throws Exception {
        // Given
        final Queue queue = obtainTestQueue();

        // When
        queue.sendSerializable("foo bar");

        // Then
        await().until(receivedMessageCallable(queue), receivedMessageMatcher(queue, "foo bar", true));
    }

    private Matcher<Optional<ReceivedMessage>> receivedMessageMatcher(
            final Queue queue, final Object expected, final boolean delete) {
        return new BaseMatcher<Optional<ReceivedMessage>>() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Optional))
                    return false;
                Optional<ReceivedMessage> msg = (Optional<ReceivedMessage>) o;
                if (msg.isPresent() && Objects.equal(msg.get().getMessage(), expected)) {
                    if (delete)
                        queue.deleteMessage(msg.get());
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expected.toString());
            }
        };
    }

    private Matcher<Optional<ReceivedMessage>> noMessageFound() {
        return new BaseMatcher<Optional<ReceivedMessage>>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof Optional && !((Optional) o).isPresent();

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Got a message");
            }
        };
    }

    @Test
    public void shouldSetTheVisibilityTimeoutOfAMessage() throws Exception {
        // Given
        final Queue queue = obtainTestQueue();
        String testMessage = "foo bar baz";

        // When
        queue.setQueueVisibilityTimeout(2);
        queue.sendSerializable(testMessage);

        long beforeReceivingTheMessage = System.currentTimeMillis();
        await().until(receivedMessageCallable(queue), receivedMessageMatcher(queue, testMessage, false));

        // Then

        // Assert that message is invisible for at least 1.5 seconds
        await().atMost(new Duration(1500 - (System.currentTimeMillis() - beforeReceivingTheMessage),
                TimeUnit.MILLISECONDS)).until(receivedMessageCallable(queue), noMessageFound()); // .5s tolerance

        // Finally delete the message
        await().until(receivedMessageCallable(queue), receivedMessageMatcher(queue, testMessage, true));
    }

    @Test/* Then */(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailIfDelayedMessageSentWithATimeoutGreaterThan15Minutes() throws Exception {
        // Given
        Queue queue = obtainTestQueue();
        String testMessage = "Bazinga!";

        // When
        queue.sendSerializableDelayed(testMessage, standardMinutes(16));
    }

    @Test/* Then */(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfDelayedMessageDelayIfNotProvided() {
        // Given
        Queue queue = obtainTestQueue();
        String testMessage = "It's not what it looks like";

        // When
        queue.sendSerializableDelayed(testMessage, null);
    }

    @Test
    public void shouldNotReceiveTheMessageFasterThanTheDelaySpecified() throws Exception {
        // Given
        final Queue queue = obtainTestQueue();
        String testMessage = "I'm not insane, my mother had me tested!";

        // When
        long beforeSendingTheMessage = System.currentTimeMillis();
        queue.sendSerializableDelayed(testMessage, standardSeconds(2));

        // Then

        // Assert that the message is not available in the queue for at least 1.5 seconds
        Thread.sleep(1500 - (System.currentTimeMillis() - beforeSendingTheMessage));
        MatcherAssert.assertThat(receivedMessageCallable(queue).call(), noMessageFound());

        // Try to obtain the message
        await().atMost(new Duration(1000, TimeUnit.MILLISECONDS)).until(receivedMessageCallable(queue),
                receivedMessageMatcher(queue, testMessage, true));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionOnTooLowReceiveMessageWaitTimeValue() {
        // Given
        int receiveMessageWaitTime = -1;

        // When
        Queue queue = obtainTestQueue();
        queue.setReceiveMessageWaitTime(receiveMessageWaitTime);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionOnTooHighReceiveMessageWaitTimeValue() {
        // Given
        int receiveMessageWaitTime = 21;

        // When
        Queue queue = obtainTestQueue();
        queue.setReceiveMessageWaitTime(receiveMessageWaitTime);
    }

    @Test(enabled = false) // TomekD: enable this once ElasticMQ starts supporting ReceiveMessageWaitTimeSeconds parameter
    public void shouldWaitUntilNewMessageAppears() {
        // Given
        Queue queue = obtainTestQueue();
        queue.setReceiveMessageWaitTime(20);
        String testMessage = "Message sent after queue was asked for new stuff";

        // When
        queue.sendSerializableDelayed(testMessage, standardSeconds(15));
        Optional<ReceivedMessage> messageOptional = queue.receiveSingleMessage();

        // Then

        // Even if we requested message before it was actually sent, we should receive it because of long (20secs) message polling
        assertThat(messageOptional.isPresent()).isTrue();
        assertThat(testMessage.equals(messageOptional.get().getMessage())).isTrue();

        queue.deleteMessage(messageOptional.get());
    }

    private Callable<Optional<ReceivedMessage>> receivedMessageCallable(final Queue queue) {
        return new Callable<Optional<ReceivedMessage>>() {
            @Override
            public Optional<ReceivedMessage> call() throws Exception {
                return queue.receiveSingleMessage();
            }
        };
    }


    @AfterClass(alwaysRun = true)
    public void stopElasticMQ() {
        sqsServer.stop();
        elasticNode.shutdown();
    }

}
