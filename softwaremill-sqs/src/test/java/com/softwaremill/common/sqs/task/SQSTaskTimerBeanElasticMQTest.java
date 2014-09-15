package com.softwaremill.common.sqs.task;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.elasticmq.Node;
import org.elasticmq.NodeAddress;
import org.elasticmq.NodeBuilder;
import org.elasticmq.rest.RestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.elasticmq.storage.inmemory.InMemoryStorage;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.softwaremill.common.conf.Configuration;
import com.softwaremill.common.sqs.SQS;
import com.softwaremill.common.sqs.email.SendEmailTask;
import com.softwaremill.common.sqs.email.SendEmailTaskExecutor;
import com.softwaremill.common.sqs.util.EmailDescription;
import com.softwaremill.common.util.dependency.D;

import java.util.Iterator;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static com.softwaremill.common.sqs.SQSConfiguration.EMAIL_FROM;
import static com.softwaremill.common.sqs.SQSConfiguration.EMAIL_SMTP_PORT;

/**
 * @author Maciej Bilas
 * @since 15/10/12 17:23
 */
public class SQSTaskTimerBeanElasticMQTest {

    private static final int ELASTIMQ_PORT = 12365;
    private static final String TEST_QUEUE = Configuration.get("sqs").get("queue");
    private Node elasticNode;
    private RestServer sqsServer;
    private SimpleSmtpServer emailServer;


    private SQSTaskTimerBean taskTimerBean = new SQSTaskTimerBeanMock();

    @BeforeClass
    public void setupElasticMQ() {
        elasticNode = NodeBuilder.withStorage(new InMemoryStorage());
        sqsServer = new SQSRestServerBuilder(elasticNode.nativeClient(), ELASTIMQ_PORT, new NodeAddress("http", "localhost", ELASTIMQ_PORT, "")).start();

        AmazonSQSClient sqsClient = new AmazonSQSClient(new BasicAWSCredentials("1234", "1234"));
        sqsClient.setEndpoint("http://localhost:" + ELASTIMQ_PORT);
        sqsClient.createQueue(new CreateQueueRequest(TEST_QUEUE));
    }

    @BeforeClass
    public void startEmailServer() {
        emailServer = SimpleSmtpServer.start(Integer.valueOf(EMAIL_SMTP_PORT));
    }

    @Test
    public void shouldSendEmailMessage() throws Exception {
        // Given
        String to = "sqs-test@example.org";
        String subject = "TEST: testEmailDelivery()";
        String message = "Simple message from SQS email test";

        SQSTaskTimerBean.scheduleTask(new SendEmailTask(new EmailDescription(to, message, subject)),
                new SQS("http://localhost:" + ELASTIMQ_PORT, "1234", "1234").getQueueByName(TEST_QUEUE));
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

    private void sendEmail() throws Exception {
        D.withDependencies(new SendEmailTaskExecutor(), mock(BoundRequestContext.class), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Actually gets sent to SQS, might take some time to receive it
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    taskTimerBean.timeout(null);
                    if (emailServer.getReceivedEmailSize() > 0) {
                        break;
                    }
                }

                return null;
            }
        });
    }

    private SmtpMessage getEmail() {
        Iterator inbox = emailServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) inbox.next();
        // clear mock-inbox for other tests
        inbox.remove();
        return email;
    }

    @AfterClass(alwaysRun = true)
    public void stopElasticMQ() {
        sqsServer.stop();
        elasticNode.shutdown();
    }


    @AfterClass(alwaysRun = true)
    public void stopEmailServer() {
        if (emailServer != null) {
            emailServer.stop();
        }
    }

    private class SQSTaskTimerBeanMock extends SQSTaskTimerBean {

        @Override
        protected SQS getSQS() {
            return new SQS("http://localhost:" + ELASTIMQ_PORT, "1234", "1234");
        }
    }
}
