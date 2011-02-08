package pl.softwaremill.common.faces.fileupload;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 */
public class EmptyFileNormalParametersTest extends AbstractDumpTestCase {
    
    @BeforeMethod
	public void setUp() throws IOException {
		setUp("Multipart_EmptyFileUpload_NormalParameters_Firefox-2.0.0.3.dump");
	}

    @Test
	public void testIsMultipart() throws Exception {
		assertTrue(multiRequest.isMultipartRequest());
	}

    @Test
	public void testNormalParameters() {
		assertEquals(request.getParameter("name"), "Jason");
		
		String nameValues[] = request.getParameterValues("name");
		assertEquals(nameValues.length, 2);
		
		assertEquals(nameValues[0], "Jason");
		assertEquals(nameValues[1], "Clair");
	
		assertEquals(request.getParameter("surname"), "Pell");
		
		String surnameValues[] = request.getParameterValues("surname");
		assertEquals(surnameValues.length, 2);
		
		assertEquals(surnameValues[0], "Pell");
		assertEquals(surnameValues[1], "Harris");
		
		assertEquals(request.getParameter("stuff"), "Are you going to upload a file.\n");
	}

    @Test
	public void testFileParameters() {
		MultipartFile file = multiRequest.getFileParameter("file1");
		assertNull(file);
		
		MultipartFile file2 = multiRequest.getFileParameter("file2");
		assertNull(file2);
	}
}
