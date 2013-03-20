package com.softwaremill.common.cdi.autofactory.logger;

import org.slf4j.Logger;
import com.softwaremill.common.cdi.autofactory.CreatedWith;
import com.softwaremill.common.cdi.autofactory.FactoryParameter;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(InjectDataAndLoggerConstructor.Factory.class)
public class InjectDataAndLoggerConstructor {
    private final String data;
    private final Logger logger;

    @Inject
    public InjectDataAndLoggerConstructor(@FactoryParameter String data, Logger logger) {
        this.data = data;
        this.logger = logger;
    }

    public String getData() {
        return data;
    }

    public Logger getLogger() {
        return logger;
    }

    public static interface Factory {
        InjectDataAndLoggerConstructor create(String data);
    }
}
