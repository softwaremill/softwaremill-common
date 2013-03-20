package com.softwaremill.common.util.templates;

import com.google.common.io.Resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;

/**
 *  Reads a file with template message, filling the arguments with given data
 *
 * @author maciek
 */
public class TemplateReader {

    /** Key to find subject (e.g. for email) in template */
     public static final String KEY_SUBJECT = "%subject%";

    /**
     * Reads the template, merging with optional arguments
     * @param templateName Name of the template file to use
     * @param arguments Array to objects to use - must match the template's needs, can be {@code null}
     *
     * @return {@code TemplateContentData} object with subject and body based on passed arguments
     */
    public TemplateContentData getContentData(String templateName, Object[] arguments) {
        TemplateContentData templateContentData = getContentDataFromTemplate(templateName);

        if (arguments != null && arguments.length > 0) {
            templateContentData.setContent(MessageFormat.format(templateContentData.getContent(), arguments));
        }

        return templateContentData;
    }

    /**
     * Reads the template file, parsing all data to TemplateContentData object
     */
    private TemplateContentData getContentDataFromTemplate(String templateName) {
        TemplateContentData templateContentData = new TemplateContentData();
        StringBuilder contentPart = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Resources.newInputStreamSupplier(
                    Resources.getResource(templateName)).getInput(), Charset.defaultCharset()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith(KEY_SUBJECT)) {
                    templateContentData.setSubject(line.substring(KEY_SUBJECT.length()).trim());
                } else if (!line.startsWith("#")) {
                    contentPart.append(line);
                }
            }
            templateContentData.setContent(contentPart.toString());
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Couldn't find template: "+templateName);
        } catch (IOException e) {
            throw new RuntimeException("I/O exception while reading template: "+templateName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return templateContentData;
    }
}
