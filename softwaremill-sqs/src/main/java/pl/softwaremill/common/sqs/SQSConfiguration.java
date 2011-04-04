package pl.softwaremill.common.sqs;

import pl.softwaremill.common.conf.Configuration;

import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SQSConfiguration {
    public static final String EMAIL_SMTP_HOST;
    public static final String EMAIL_SMTP_PORT;
    public static final String EMAIL_SMTP_USERNAME;
    public static final String EMAIL_SMTP_PASSWORD;
    public static final String TASK_SQS_QUEUE;
    public static final String EMAIL_FROM;
    public static final String AWS_ACCESS_KEY_ID;
    public static final String AWS_SECRET_ACCESS_KEY;
    public static final String SQS_SERVER;
    public static final String ENCODING;

    static {
        Map<String, String> props = Configuration.get("sqs");
        EMAIL_SMTP_HOST = props.get("smtpHost");
        EMAIL_SMTP_PORT = props.get("smtpPort");
        EMAIL_SMTP_USERNAME = props.get("smtpUsername");
        EMAIL_SMTP_PASSWORD = props.get("smtpPassword");
        TASK_SQS_QUEUE = props.get("queue");
        EMAIL_FROM = props.get("from");
        AWS_ACCESS_KEY_ID = props.get("AWSAccessKeyId");
        AWS_SECRET_ACCESS_KEY = props.get("SecretAccessKey");
        ENCODING = props.get("encoding");

        // read sqs server, use sqs. when no given
        String server = props.get("sqsServer");
        SQS_SERVER = (server == null) ? "queue.amazonaws.com" : server;
    }

    private static long discardMessagesSentBefore = 0;

    public static long getDiscardMessagesSentBefore() {
        return discardMessagesSentBefore;
    }

    public static void setDiscardMessagesSentBefore(long discardMessagesSentBefore) {
        SQSConfiguration.discardMessagesSentBefore = discardMessagesSentBefore;
    }
}
