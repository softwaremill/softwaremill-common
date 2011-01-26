package pl.softwaremill.common.sqs.email;

import pl.softwaremill.common.sqs.task.Task;
import pl.softwaremill.common.sqs.task.TaskExecutor;
import pl.softwaremill.common.sqs.util.EmailDescription;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SendEmailTask implements Task<SendEmailTask> {
    private final EmailDescription email;

    public SendEmailTask(EmailDescription email) {
        this.email = email;
    }

    public EmailDescription getEmail() {
        return email;
    }

    @Override
    public Class<? extends TaskExecutor<SendEmailTask>> getExecutorBeanClass() {
        return SendEmailTaskExecutor.class;
    }
}
