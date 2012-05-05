package pl.softwaremill.common.sqs.util;

import java.io.Serializable;

/**
 * @author szimano@szimano.org
 *         Date: Aug 24, 2010
 */
public class EmailDescription implements Serializable {

    private final String message;
    private final String[] emails;
    private final String subject;
    private final String[] replyToEmails;
    private final String[] ccEmails;
    private final String[] bccEmails;

    public EmailDescription(String email, String message, String subject) {
        this(new String[]{email}, message, subject, new String[]{});
    }

    public EmailDescription(String email, String message, String subject, String replyToEmail) {
        this(new String[]{email}, message, subject, new String[]{replyToEmail});
    }

    public EmailDescription(String email, String message, String subject, String replyToEmail, String ccEmail) {
        this(new String[]{email}, message, subject, new String[]{replyToEmail}, new String[]{ccEmail}, new String[]{});
    }

    public EmailDescription(String email, String message, String subject, String replyToEmail, String ccEmail,
                            String bccEmail) {
        this(new String[]{email}, message, subject, new String[]{replyToEmail}, new String[]{ccEmail},
                new String[]{bccEmail});
    }

    public EmailDescription(String[] emails, String message, String subject, String[] replyToEmails) {
        this(emails, message, subject, replyToEmails, new String[]{}, new String[]{});
    }

    public EmailDescription(String[] emails, String message, String subject, String[] replyToEmails, String[] ccEmails,
                            String[] bccEmails) {
        this.emails = emails;
        this.message = message;
        this.subject = subject;
        this.replyToEmails = replyToEmails;
        this.ccEmails = ccEmails;
        this.bccEmails = bccEmails;
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

    public String[] getReplyToEmails() {
        return replyToEmails;
    }

    public String[] getCcEmails() {
        return ccEmails;
    }

    public String[] getBccEmails() {
        return bccEmails;
    }
}
