package pl.softwaremill.common.faces.fileupload;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MultiPartMultifileUploadNormalParametersTest extends AbstractDumpTestCase {

    @BeforeMethod
	public void setUp() throws Exception {
		setUp("MultiPart_MultifileUpload_NormalParameters_Firefox-2.0.0.4.dump");
	}

    @Test
	public void testIsMultipart() throws Exception {
		assertTrue(multiRequest.isMultipartRequest());
	}
	
	
}
