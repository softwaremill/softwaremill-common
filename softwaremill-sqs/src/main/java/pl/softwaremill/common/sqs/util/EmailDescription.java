package pl.softwaremill.common.sqs.util;

import java.io.Serializable;

/**
 * Object sent to SQSEmailSenderBean to send emails
 *
 * @author szimano@szimano.org
 *         Date: Aug 24, 2010
 */
public class EmailDescription implements Serializable {

    private final String message;
    private final String[] emails;
    private final String subject;

    public EmailDescription(String email, String message, String subject) {
        this(new String[]{email}, message, subject);
    }

    public EmailDescription(String[] emails, String message, String subject) {
        this.emails = emails;
        this.message = message;
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public String[] getEmails() {
        return emails;
    }

    public String getSubject() {
        return subject;
    }
    
}
