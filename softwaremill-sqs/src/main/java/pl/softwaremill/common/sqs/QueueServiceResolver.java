package pl.softwaremill.common.sqs;

import com.xerox.amazonws.sqs2.QueueService;

public class QueueServiceResolver {

    public QueueService resolveQueue(String sqsServer, String awsAccessKey, String awsSecretKey) {
        String[] splitServer = sqsServer.split(":");

        QueueService service;
        if (splitServer.length > 1) {
            String address = splitServer[0];
            String port = splitServer[1];

            service = new QueueService(awsAccessKey, awsSecretKey,
                    "443".equals(port), address,
                    Integer.parseInt(port));
        }
        else {
            service = new QueueService(awsAccessKey, awsSecretKey, true, sqsServer);
        }

        return service;
    }
}
