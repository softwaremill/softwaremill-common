package pl.softwaremill.common.sqs.email;

import pl.softwaremill.common.task.TaskExecutor;

import javax.mail.MessagingException;

import static pl.softwaremill.common.sqs.SQSConfiguration.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SendEmailTaskExecutor implements TaskExecutor<SendEmailTask> {
    @Override
    public void execute(SendEmailTask task) {
        try {
            EmailSender.send(EMAIL_SMTP_HOST, EMAIL_SMTP_PORT, EMAIL_SMTP_USERNAME, EMAIL_SMTP_PASSWORD,
                    EMAIL_FROM, ENCODING, task.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
