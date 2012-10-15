package pl.softwaremill.common.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * @author Maciej Bilas
 * @since 11/10/12 12:12
 */
public class SQS {

    private final AmazonSQS sqsClient;

    public SQS(AmazonSQS sqsClient) {
        this.sqsClient = sqsClient;
    }

    public SQS() {
        AmazonSQSClient sqsClient = new AmazonSQSClient(new BasicAWSCredentials(AWS_SECRET_ACCESS_KEY, AWS_ACCESS_KEY_ID),
                new ClientConfiguration().withProtocol(Util.determineProtocol(SQS_SERVER)));
        sqsClient.setEndpoint(SQS_SERVER);

        this.sqsClient = sqsClient;
    }

    public SQS(String server, String accessKey, String secretKey) {
        AmazonSQSClient sqsClient = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey),
                new ClientConfiguration().withProtocol(Util.determineProtocol(server)));
        sqsClient.setEndpoint(server);

        this.sqsClient = sqsClient;
    }

    public Queue getQueueByName(String queueName) {
        checkNotNull(queueName);


        try {
            GetQueueUrlResult url = sqsClient.getQueueUrl(new GetQueueUrlRequest(queueName));
            return new Queue(queueName, url.getQueueUrl(), sqsClient);
        } catch (QueueDoesNotExistException e) {
            throw new SQSRuntimeException(String.format("Queue \"%s\" does not exist.", queueName), e);
        } catch (AmazonServiceException e) {
            throw new SQSRuntimeException("AWS exception.",  e);
        }
    }
}
