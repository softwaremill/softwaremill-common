package com.softwaremill.common.util.templates;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maciek
 */
public class TemplateReaderTest {

    @Test
    public void shouldParseSimpleTemplate() throws Exception {
        // Given
        TemplateReader templateReader = new TemplateReader();

        // When
        TemplateContentData templateContent = templateReader.getContentData("simple.etemplate", null);

        //Then
        assertThat(templateContent).isNotNull();
        assertThat(templateContent.getSubject()).isEqualTo("Hello World!");
        assertThat(templateContent.getContent()).isEqualTo("This is a sample email.");
    }

    @Test
    public void shouldParseAdvancedTemplate() throws Exception {
        // Given
        TemplateReader templateReader = new TemplateReader();
        // {0} - username {1} - email {2} - organization name {3} - organization subdomain
        Object[] arguments = {"j.kowalski", "j.kowalski@example.org", "Kowalski Enterprises", "k-enterprises"};

        // When
        TemplateContentData templateContent = templateReader.getContentData("advanced.etemplate", arguments);

        //Then
        assertThat(templateContent).isNotNull();
        assertThat(templateContent.getSubject()).isEqualTo("Welcome to Company!");
        assertThat(templateContent.getContent()).contains("Welcome "+arguments[0]);
        assertThat(templateContent.getContent()).contains("your organization: "+arguments[2]);
        assertThat(templateContent.getContent()).contains("http://"+arguments[3]+".example.org");
        assertThat(templateContent.getContent()).contains("Username: "+arguments[0]);
        assertThat(templateContent.getContent()).contains("Email: "+arguments[1]);
    }
}
