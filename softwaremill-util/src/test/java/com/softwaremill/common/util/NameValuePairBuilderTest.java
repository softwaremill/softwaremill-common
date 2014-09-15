package com.softwaremill.common.util;

import org.apache.http.NameValuePair;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(nameValuePairs).extracting("name").contains("name1", "name2");

        int indexOfName1 = 0;
        int indexOfName2 = 1;

        if (nameValuePairs[0].getName().equals("name2")) {
            indexOfName1 = 1;
            indexOfName2 = 0;
        }

        assertThat(nameValuePairs[indexOfName1].getName()).isEqualTo("name1");
        assertThat(nameValuePairs[indexOfName1].getValue()).isEqualTo("value1");

        assertThat(nameValuePairs[indexOfName2].getName()).isEqualTo("name2");
        assertThat(nameValuePairs[indexOfName2].getValue()).isEqualTo("value2");
    }
}
