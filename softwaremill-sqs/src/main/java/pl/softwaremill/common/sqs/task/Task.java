package pl.softwaremill.common.sqs.task;

import java.io.Serializable;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface Task <T extends Task<T>> extends Serializable {
    Class<? extends TaskExecutor<T>> getExecutorBeanClass();

    /**
     * @return Number of seconds after which the task execution will be retried. Return {@code null} for the default
     * interval (30 seconds).
     */
    Integer getTaskTimeout();
}
