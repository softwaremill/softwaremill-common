package pl.softwaremill.common.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


/**
 * User: szimano
 */
public class RichHexStringTest {

    @Test(dataProvider = "hexTransformStrings")
    public void testHexToByteTransformations(String string) throws Exception {
        byte[] bytes = string.getBytes("utf-8");

        // make hex
        String hexString = new RichHexString(bytes).unwrap();

        // make bytes again
        byte[] result = new RichHexString(hexString).getBytes();

        assertEquals(result, bytes);

    }

    @Test (dataProvider = "hexStrings")
    public void testHexStringCreation(String hexString, boolean shouldFail, byte[] result) {
        try {
            RichHexString rh = new RichHexString(hexString);

            if (shouldFail) {
                fail("Creation of hex string with string "+hexString+" should fail.");
            }

            // assert equals expected
            assertEquals(rh.getBytes(), result);
        } catch (IllegalArgumentException e) {
            if (!shouldFail) {
                // rethrow
                throw e;
            }
        }
    }

    @DataProvider(name = "hexStrings")
    public Object[][] getHexStrings() {
        return new Object[][]{
            new Object[]{"0f430a", false, new byte[]{15, 67, 10}},
            new Object[]{"0F430A", false, new byte[]{15, 67, 10}},
            new Object[]{"Z", true, null},
            new Object[]{"", false, new byte[]{}},
        };
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
