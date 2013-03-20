package com.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecurityConditionException extends RuntimeException {
    public SecurityConditionException() {
    }

    public SecurityConditionException(String message) {
        super(message);
    }

    public SecurityConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityConditionException(Throwable cause) {
        super(cause);
    }
}
