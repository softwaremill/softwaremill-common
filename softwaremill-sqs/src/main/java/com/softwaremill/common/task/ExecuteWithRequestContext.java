package com.softwaremill.common.task;

import org.jboss.weld.context.bound.BoundRequestContext;
import com.softwaremill.common.util.dependency.D;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ExecuteWithRequestContext {
    private final Task task;

    public ExecuteWithRequestContext(Task task) {
        this.task = task;
    }

    public void execute() {
        doExecute(task);
    }

    private <T extends Task<T>> void doExecute(T task) {
        BoundRequestContext requestContext = D.inject(BoundRequestContext.class);

        Map<String, Object> context = new HashMap<String, Object>();
        try {
            requestContext.associate(context);
            requestContext.activate();

            D.inject(task.getExecutorBeanClass()).execute(task);
        } finally {
            requestContext.invalidate();
            requestContext.deactivate();
            requestContext.dissociate(context);
        }
    }
}
