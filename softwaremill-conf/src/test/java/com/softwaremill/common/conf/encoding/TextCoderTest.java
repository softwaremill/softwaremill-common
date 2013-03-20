package pl.softwaremill.common.conf.encoding;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TextCoderTest {
    @Test
    public void shouldEncodeAndDecode() {
        // Given
        MasterPasswordStore.setMasterPassword("top-secret-passw0rd");
        String text = "ABCDE12345!@#$%";
        TextCoder coder = new TextCoder();

        // When
        String encoded = coder.encode(text);
        String decoded = coder.decode(encoded);

        // Then
        assertThat(decoded).isEqualTo(text);
    }

    @Test
    public void shouldReturnDifferentString() {
        // Given
        MasterPasswordStore.setMasterPassword("top-secret-passw0rd");
        String text = "ABCDE12345!@#$%";
        TextCoder coder = new TextCoder();

        // When
        String encoded = coder.encode(text);

        // Then
        assertThat(encoded).isNotEqualTo(text);
    }

    @Test
    public void shouldNotDecodeUsingDifferentPassword() {
        // Given
        String text = "ABCDE12345!@#$%";
        TextCoder coder = new TextCoder();

        // When
        MasterPasswordStore.setMasterPassword("top-secret-passw0rd1");
        String encoded = coder.encode(text);

        MasterPasswordStore.setMasterPassword("top-secret-passw0rd2");
        try {
            coder.decode(encoded);
            Assert.fail("Should not decode.");
        } catch (Exception e) {
            // Ok.
        }
    }
}
