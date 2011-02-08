package pl.softwaremill.common.faces.fileupload;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class NoMultipartNormalParametersTest extends AbstractDumpTestCase {

    @BeforeMethod
	public void setUp() throws Exception {
		setUp("NoMultipart_NormalParameters_Firefox-2.0.0.3.dump");
	}

    @Test
	public void testIsMultipart() throws Exception {
		assertFalse(multiRequest.isMultipartRequest());
	}
    
}
