package pl.softwaremill.common.sqs;

/**
 * A wrapper object around an SQS MessageID.
 *
 * @author Maciej Bilas
 * @since 11/10/12 16:23
 */
public class MessageId {

    private final String messageId;

    public MessageId(String messageId) {
        this.messageId = messageId;
    }

    public String get() {
        return messageId;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageId)) return false;

        MessageId messageId1 = (MessageId) o;

        if (messageId != null ? !messageId.equals(messageId1.messageId) : messageId1.messageId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return messageId != null ? messageId.hashCode() : 0;
    }
}
