package pl.softwaremill.common.conf.encoding;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ConfigurationValueCoderTest {
    @Test
    public void shouldEncodeAndDecode() {
        // Given
        MasterPasswordStore.setMasterPassword("top-secret-passw0rd");
        String text = "ABCDE12345!@#$%";
        ConfigurationValueCoder coder = new ConfigurationValueCoder();

        // When
        String encoded = coder.encode(text);
        String decoded = coder.decode(encoded);

        // Then
        assertThat(decoded).isEqualTo(text);
    }

    @Test
    public void shouldCorrectlyVerifyThatAValueIsEncoded() {
        // Given
        MasterPasswordStore.setMasterPassword("top-secret-passw0rd");
        String text = "ABCDE12345!@#$%";
        ConfigurationValueCoder coder = new ConfigurationValueCoder();

        // When
        String encoded = coder.encode(text);

        // Then
        assertThat(coder.isEncoded(encoded)).isTrue();
        assertThat(coder.isEncoded(text)).isFalse();
    }
}
