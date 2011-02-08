package pl.softwaremill.common.faces.fileupload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class AbstractDumpTestCase {
	protected HttpServletRequest request;
	protected HttpMultipartRequest multiRequest;

	public void setUp(String fileName) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		DumpFile dumpFile = new DumpFile(in);
	
		request = new MockHttpServletRequest(
				dumpFile.getContent(),
				dumpFile.getContentType());

        multiRequest = new HttpMultipartRequest(request);
        request = multiRequest;
	}
}
