package com.softwaremill.common.cdi.autofactory.logger;

import org.slf4j.Logger;
import com.softwaremill.common.cdi.autofactory.CreatedWith;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(InjectDataAndLoggerField.Factory.class)
public class InjectDataAndLoggerField {
    private final String data;

    @Inject
    private Logger logger;

    public InjectDataAndLoggerField(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public Logger getLogger() {
        return logger;
    }

    public static interface Factory {
        InjectDataAndLoggerField create(String data);
    }
}
