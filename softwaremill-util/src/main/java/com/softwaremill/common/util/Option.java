package com.softwaremill.common.util;

import java.util.Arrays;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Option<T> {
    private final boolean error;
    private final boolean object;
    private final T result;
    private final String errorMessage;
    private final Object[] errorParams;

    private Option(T object){
        this.result = object;
        this.object = true;
        this.error = false;
        this.errorMessage = null;
        this.errorParams = null;
    }

    private Option(String errorMessage, Object... errorParams) {
        this.errorMessage = errorMessage;
        this.errorParams = errorParams;
        this.object = false;
        this.error = true;
        this.result = null;
    }

    /**
     * @param object The result.
     * @return An option representing a result (success).
     */
    public static <T> Option<T> object(T object){
        return new Option<T>(object);
    }

    /**
     * @param errorMessage The error message key.
     * @param errorParams Optional error message parameters.
     * @return An option representing an error.
     */
    public static <T> Option<T> error(String errorMessage, Object... errorParams) {
        return new Option<T>(errorMessage, errorParams);
    }

    public boolean isError() {
        return error;
    }

    public boolean isObject() {
        return object;
    }

    public T getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object[] getErrorParams() {
        return errorParams != null ? Arrays.copyOf(errorParams, errorParams.length) : null;
    }
}

