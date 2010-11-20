package pl.softwaremill.common.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


/**
 * User: szimano
 */
public class RichStringTest {

    @Test(dataProvider = "hexTransformStrings")
    public void testHexToByteTransformations(String string) throws Exception {
        byte[] bytes = string.getBytes("utf-8");

        // make hex
        String hexString = RichString.hexStringFromBytes(bytes);

        // make bytes again
        byte[] result = RichString.hexStringToByteArray(hexString);

        assertEquals(result, bytes);

    }

    @DataProvider(name = "hexTransformStrings")
    public Object[][] getCodingStrings() {
        return new Object[][]{
            new Object[]{"zupa"},
            new Object[]{""},
            new Object[]{"utf-8 zażółć gęślą jaźń"},
            new Object[]{"With" +
                    "new line"},
        };
    }
}
