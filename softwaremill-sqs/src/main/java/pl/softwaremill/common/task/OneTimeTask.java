package pl.softwaremill.common.task;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface OneTimeTask<T extends OneTimeTask<T>> extends Task<T> {
    /**
     * @return Number of seconds after which the task execution will be retried. Return {@code null} for the default
     * interval (30 seconds).
     */
    Integer getTaskTimeout();
}
