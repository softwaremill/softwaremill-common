package pl.softwaremill.common.util.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class NotJustWhitespaceTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static class WithNotJustWhitespaceField {
        @NotJustWhitespace
        private final String data;

        public WithNotJustWhitespaceField(String data) {
            this.data = data;
        }
    }

    @Test(dataProvider="NotJustWhitespacePassData")
    public void testNotJustWhitespacePass(String text) {
        // Given
        WithNotJustWhitespaceField container = new WithNotJustWhitespaceField(text);

        // When
        Set<ConstraintViolation<WithNotJustWhitespaceField>> result = validator.validate(container);

        // Then
        assertThat(result).isEmpty();
    }

    @DataProvider(name = "NotJustWhitespacePassData")
    public Object[][] getNotJustWhitespacePassData() {
        return new Object[][] {
                new Object[] { "a" },
                new Object[] { " - " },
                new Object[] { "a\n" },
                new Object[] { " Multi line\ntext with\ttabs  and other stuff like\tą, *, ; and @" },
        };
    }

    @Test(dataProvider="NotJustWhitespaceFailData")
    public void testNotJustWhitespaceFail(String text) {
        // Given
        final WithNotJustWhitespaceField container = new WithNotJustWhitespaceField(text);

        // When
        Set<ConstraintViolation<WithNotJustWhitespaceField>> result = validator.validate(container);

        // Then
        assertThat(result).isNotEmpty();
    }

    @DataProvider(name = "NotJustWhitespaceFailData")
    public Object[][] getNotJustWhitespaceFailData() {
        return new Object[][] {
                new Object[] { "" },
                new Object[] { "   " },
                new Object[] { "\t" },
                new Object[] { "\n" },
        };
    }
}

