package com.softwaremill.common.conf.encoding;

/**
 * Encodes and decodes configuration values.
 * @author Adam Warski (adam at warski dot org)
 */
public class ConfigurationValueCoder {
    private final static String ENCODED_VALUE_PREFIX = "encoded.";

    private TextCoder coder = new TextCoder();

    public String encode(String value) {
        return ENCODED_VALUE_PREFIX + coder.encode(value);
    }

    public String decode(String value) {
        return coder.decode(value.substring(ENCODED_VALUE_PREFIX.length()));
    }

    public boolean isEncoded(String value) {
        return value.startsWith(ENCODED_VALUE_PREFIX);
    }
}
