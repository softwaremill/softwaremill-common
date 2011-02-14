package pl.softwaremill.common.servlet.multipart;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class DumpFile {
	private byte[] contents;
	private String contentType;
	
	public DumpFile(InputStream in) throws IOException {
//		InputStream in = DumpFile.class.getResourceAsStream("/dumps/"+fileName);
		if(in!=null) {
			contents = IOUtils.toByteArray(in);
			
			// i know this is not efficient, but do you see me caring!
			BufferedReader reader = new BufferedReader(new StringReader(new String(contents)));
			String boundary = reader.readLine();
			
			// where boundary is not found, its probably a normal upload.
			if(boundary==null || !boundary.startsWith("--")) {
				contentType = HttpMultipartRequest.URLENCODED_FORM_CONTENT_TYPE;
			} else {
				contentType = HttpMultipartRequest.MULTIPART_FORM_DATA +
							"; boundary="+boundary;
			}
		}
//        else {
//			throw new IllegalArgumentException("Dump file "+fileName+" not found");
//		}
	}
	
	public byte[] getContent() {
		return contents;
	}
	
	public String getContentType() {
		return contentType;
	}
}
