package pl.softwaremill.common.sqs.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.sqs.SQSManager;
import pl.softwaremill.common.sqs.exception.SQSRuntimeException;
import pl.softwaremill.common.sqs.timer.TimerManager;
import pl.softwaremill.common.sqs.util.SQSAnswer;
import pl.softwaremill.common.util.dependency.D;

import javax.ejb.Timeout;
import javax.ejb.Timer;

import static pl.softwaremill.common.sqs.SQSConfiguration.*;

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
    private static final Logger log = LoggerFactory.getLogger(SQSTaskTimerBean.class);

    public void startTimer(int interval){
        startTimer(EMAIL_SQS_QUEUE, interval);
    }

    public void destroyTimer(){
        destroyTimer(EMAIL_SQS_QUEUE);
    }

    @Timeout
    public void timeout(Timer timer) {
        SQSAnswer sqsAnswer = SQSManager.receiveMessage(EMAIL_SQS_QUEUE);

        if (sqsAnswer != null) {
            Object message = sqsAnswer.getMessage();

            if (message instanceof Task) {
                log.debug("Deserialized message: " + message);
                Task task = (Task) message;
                try {
                    executeTask(task);

                    SQSManager.deleteMessage(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle());
                }
                catch (RuntimeException e) {
                    log.warn("Something went wrong and the task has not been executed. Redelivery will occur.");
                    SQSManager.setMessageVisibilityTimeout(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle(), 10);
                    throw new SQSRuntimeException(e);
                }
            } else {
                log.warn("Trash in SQS: " + EMAIL_SQS_QUEUE);
                log.warn("Deserialized message: " + message + " -- removing message from queue!");
                SQSManager.deleteMessage(EMAIL_SQS_QUEUE, sqsAnswer.getReceiptHandle());
            }
        }

        log.debug("SQS task queue checked " + EMAIL_SQS_QUEUE);
    }

    private <T extends Task<T>> void executeTask(T task) {
        D.inject(task.getExecutorBeanClass()).execute(task);
    }

    /**
     * Asynchronously executes the given task.
     * @param task Task to execute.
     */
    public static void scheduleTask(Task<?> task) {
        SQSManager.sendMessage(EMAIL_SQS_QUEUE, task);
    }
}
