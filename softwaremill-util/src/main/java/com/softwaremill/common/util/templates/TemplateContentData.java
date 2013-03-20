package com.softwaremill.common.util.templates;

/**
 * Object with String data: subject and content
 *
 * @author maciek
 */
public class TemplateContentData {

    private String subject;
    private String content;

    public TemplateContentData() {

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
