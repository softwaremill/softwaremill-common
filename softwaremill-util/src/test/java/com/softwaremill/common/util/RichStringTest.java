package com.softwaremill.common.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichStringTest {
    @Test(dataProvider = "getReplacePolishCharactersStrings")
	public void shouldReplacePolishCharacters(String string, String expectedResult) throws Exception {
	    // When
		String replaced = new RichString(string).replacePolishChars();

		// Then
		assertThat(replaced).isEqualTo(expectedResult);
	}

	@DataProvider
	public Object[][] getReplacePolishCharactersStrings() {
	    return new Object[][] {
	        { "abcd", "abcd" },
	        { "ab cd", "ab cd" },
	        { "łąka", "laka" },
	        { "żubr ćma mrówka", "zubr cma mrowka" },
	        { "Żubr ćma MRÓWKA", "Zubr cma MROWKA" },
	    };
	}
}
