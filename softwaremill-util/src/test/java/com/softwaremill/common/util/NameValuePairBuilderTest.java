package com.softwaremill.common.util;

import org.apache.http.NameValuePair;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.Assertions.assertThat;

public class NameValuePairBuilderTest {

    @Test
    public void shouldNotAllowDuplicateNames() {
        // Given
        NameValuePairBuilder builder = new NameValuePairBuilder();
        builder.addPair("unique", "value");

        // When
        catchException(builder).addPair("unique", "newValue");

        // Then
        assertThat(caughtException()).isExactlyInstanceOf(RuntimeException.class);
        assertThat(caughtException()).hasMessage(NameValuePairBuilder.NOT_UNIQUE_KEY_ERROR + "unique");
    }

    @Test
    public void shouldCreatePairsArray() {
        // Given
        NameValuePairBuilder builder = new NameValuePairBuilder();

        // When
        builder
                .addPair("name1", "value1")
                .addPair("name2", "value2");
        NameValuePair[] nameValuePairs = builder.build();

        // Then
        assertThat(nameValuePairs).hasSize(2);

        assertThat(nameValuePairs[0].getName()).isEqualTo("name1");
        assertThat(nameValuePairs[0].getValue()).isEqualTo("value1");

        assertThat(nameValuePairs[1].getName()).isEqualTo("name2");
        assertThat(nameValuePairs[1].getValue()).isEqualTo("value2");
    }
}
