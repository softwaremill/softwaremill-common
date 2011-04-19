package pl.softwaremill.common.sqs.util;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AttachmentDescription {
    private final byte[] content;
    private final String filename;
    private final String contentType;

    public AttachmentDescription(byte[] content, String filename, String contentType) {
        this.content = content;
        this.filename = filename;
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }
}
