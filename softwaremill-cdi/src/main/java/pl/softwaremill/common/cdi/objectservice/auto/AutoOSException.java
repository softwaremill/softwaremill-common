package pl.softwaremill.common.cdi.objectservice.auto;

public class AutoOSException extends RuntimeException {

    public AutoOSException() {
        super();
    }

    public AutoOSException(String message) {
        super(message);
    }

    public AutoOSException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoOSException(Throwable cause) {
        super(cause);
    }
}
