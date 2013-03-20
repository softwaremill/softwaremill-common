package com.softwaremill.common.sqs.email;

import com.softwaremill.common.sqs.util.EmailDescription;
import com.softwaremill.common.task.OneTimeTask;
import com.softwaremill.common.task.TaskExecutor;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SendEmailTask implements OneTimeTask<SendEmailTask> {
    private final EmailDescription email;

    public SendEmailTask(EmailDescription email) {
        this.email = email;
    }

    public EmailDescription getEmail() {
        return email;
    }

    @Override
    public Integer getTaskTimeout() {
        return null;
    }

    @Override
    public Class<? extends TaskExecutor<SendEmailTask>> getExecutorBeanClass() {
        return SendEmailTaskExecutor.class;
    }
}
