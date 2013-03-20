package pl.softwaremill.common.test.web.email;

/**
 * Most common headers available in email
 *
 * @author maciek
 */
public enum EmailHeader {

    MIME_VERSION("MIME-Version"),

    MESSAGE_ID("Message-ID"),

    SUBJECT("Subject"),

    DATE("Date"),

    CONTENT_TRANSFER_ENCODING("Content-Transfer-Encoding"),

    TO("To"),

    CONTENT_TYPE("Content-Type"),

    FROM("From"),

    REPLY_TO("Reply-To");

    private String value;

    EmailHeader(String headerName){
        this.value = headerName;
    }

    public String getValue() {
        return value;
    }
}
