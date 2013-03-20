package com.softwaremill.common.sqs;

/**
 * @author Maciej Bilas
 * @since 11/10/12 12:43
 */
public class ReceivedMessage {

    private final Object message;
    private final ReceiptHandle receiptHandle;
    private final MessageId messageId;

    public ReceivedMessage(Object message, ReceiptHandle receiptHandle,
                           MessageId messageId) {
        this.message = message;
        this.receiptHandle = receiptHandle;
        this.messageId = messageId;
    }

    public Object getMessage() {
        return message;
    }

    public ReceiptHandle getReceiptHandle() {
        return receiptHandle;
    }

    public MessageId getMessageId() {
        return messageId;
    }
}
