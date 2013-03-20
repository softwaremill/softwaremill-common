package com.softwaremill.common.task;

import java.io.Serializable;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface Task<T extends Task<T>> extends Serializable {
    Class<? extends TaskExecutor<T>> getExecutorBeanClass();
}
