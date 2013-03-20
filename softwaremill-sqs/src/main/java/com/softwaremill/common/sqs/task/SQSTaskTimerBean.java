package pl.softwaremill.common.sqs.task;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.Queue;
import pl.softwaremill.common.sqs.ReceivedMessage;
import pl.softwaremill.common.sqs.SQS;
import pl.softwaremill.common.sqs.timer.TimerManager;
import pl.softwaremill.common.task.ExecuteWithRequestContext;
import pl.softwaremill.common.task.OneTimeTask;

import javax.ejb.Timeout;
import javax.inject.Inject;

import static pl.softwaremill.common.sqs.SQSConfiguration.TASK_SQS_QUEUE;

/**
 * Receiver polling Amazon's SQS queue and executing tasks.
 *
 * Configured via sqs.conf in jboss/server/profile/conf or classpath
 * smtpHost= mailing host
 * smtpPort= mailing host's port
 * queue= SQS queue, where the email data is coming from
 * from= from address put into the eamil
 *
 * To use this bean write a @Stateless bean that extends SQSTaskTimer
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 24, 2010
 * @author Adam Warski
 */
public abstract class SQSTaskTimerBean extends TimerManager implements SQSTaskTimer {
    private static final Logger LOG = LoggerFactory.getLogger(SQSTaskTimerBean.class);

    public void startTimer(int interval){
        startTimer(TASK_SQS_QUEUE, interval);
    }

    public void destroyTimer(){
        destroyTimer(TASK_SQS_QUEUE);
    }

    @Inject
    private SQS sqs;

    /* That's just for legacy code, in case SQS doesn't get injected */
    protected SQS getSQS() {
        if (sqs ==null) {
            sqs = new SQS();
        }
        return sqs;
    }

    @Timeout
    public void timeout(javax.ejb.Timer timer) {
        Queue queue = getSQS().getQueueByName(TASK_SQS_QUEUE);
        Optional<ReceivedMessage> sqsAnswer = queue.receiveSingleMessage();

        if (sqsAnswer.isPresent()) {
            Object message = sqsAnswer.get().getMessage();

            if (message instanceof OneTimeTask) {
                LOG.debug("Deserialized message: " + message);
                OneTimeTask task = (OneTimeTask) message;
                try {
                    if (task.getTaskTimeout() != null) {
                        queue.setMessageVisibilityTimeout(sqsAnswer.get(), task.getTaskTimeout());
                    }

                    new ExecuteWithRequestContext(task).execute();

                    queue.deleteMessage(sqsAnswer.get());
                } catch (RuntimeException e) {
                    LOG.warn("Something went wrong and the task has not been executed. Redelivery will occur.", e);
                }
            } else {
                LOG.warn("Trash in SQS: " + TASK_SQS_QUEUE);
                LOG.warn("Deserialized message: " + message + " -- removing message from queue!");
                queue.deleteMessage(sqsAnswer.get());
            }
        }

        LOG.debug("SQS task queue checked " + TASK_SQS_QUEUE);
    }

    /**
     * Asynchronously executes the given task.
     * @param task Task to execute.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void scheduleTask(OneTimeTask<?> task) {
        scheduleTask(task, new SQS().getQueueByName(TASK_SQS_QUEUE));
    }

    public static void scheduleTask(OneTimeTask<?> task, Queue queue) {
        queue.sendSerializable(task);
    }
}
