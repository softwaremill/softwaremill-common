package pl.softwaremill.common.servlet.multipart;

import org.apache.commons.fileupload.ParameterParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

/*
 MultipartRequest servlet library
 Copyright (C) 2001-2007 by Jason Pell

 Changed by Pawel Stawicki

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 A copy of the Lesser General Public License (lesser.txt) is included in
 this archive or goto the GNU website http://www.gnu.org/copyleft/lesser.html.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class HttpMultipartRequest extends HttpServletRequestWrapper {

    public static final int ABORT_ON_MAX_LENGTH = 100;
	public static final int IGNORE_ON_MAX_LENGTH = 101;

	public static final long MAX_CONTENT_MEMORY_THRESHOLD = 10 * 1024; // 10K

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String URLENCODED_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

	/**
	 * Default max content length if not specified
	 */
	public static final long MAX_CONTENT_LENGTH = 2 * (1024 * 1024); // 2MB!

	/**
	 * Defines the number of bytes to read per readLine call. 128K
	 */
	private static final int READ_LINE_BLOCK = 1024 * 128;

	private ParameterParser m_paramParser = new ParameterParser();

	private boolean m_isMultipart;

	private boolean m_isIgnoreOnMaxLength;

	private File m_tempDirectory;

	/**
	 * size of content that can be loaded into memory.
	 */
	private long m_maxContentMemoryThreshold;

	private boolean m_isLoadIntoMemory;

	private long m_maxContentLength;

	private String m_strBoundary;

	private long m_contentLength;

	private Map<String, String[]> m_htParameters;

	private Map<String, List<MultipartFile>> m_htFiles;

	private ProgressNotifier m_progressNotifier;

	/**
	 * This stores the actual total read including all multipart boundaries,
	 * etc.
	 */
	private long m_intTotalRead;

	/**
	 * Store a read from the input stream here. Global so we do not keep
	 * creating new arrays each read.
	 */
	private byte[] m_blockOfBytes = null;

	/**
	 * Define Character Encoding method here.
	 */
	private String m_charEncoding;

	/**
	 * Constructor which defaults max content length to MAX_CONTENT_LENGTH,
	 * maxContentMemoryThreshold to MAX_CONTENT_MEMORY_THRESHOLD,
	 * onMaxLengthMode is ABORT_ON_MAX_LENGTH and character encoding to
	 * DEFAULT_ENCODING.
	 *
	 * @param request
	 * @throws java.io.IOException
	 */
	public HttpMultipartRequest(HttpServletRequest request) throws IOException {
		this(request, MAX_CONTENT_LENGTH, MAX_CONTENT_MEMORY_THRESHOLD,
				ABORT_ON_MAX_LENGTH, DEFAULT_ENCODING, null);
	}

	/**
	 * All arguments constructor
	 *
	 * @param request -
	 *            request to be wrapped
	 * @param maxContentLength -
	 *            maximum number of bytes of content that can be handled
	 * @param maxContentMemoryThreshold -
	 *            The maximum content length before files will be automatically
	 *            saved to disk. If the threshold is exceeded all files will be
	 *            written to disk.
	 * @param onMaxLengthMode -
	 *            The following options are available for this option:
	 *            <ul>
	 *            <li>IGNORE_ON_MAX_LENGTH - If too much content is sent, this
	 *            class will not abort, but it means that no file content will
	 *            be saved.</li>
	 *            <li>ABORT_ON_MAX_LENGTH - If max content breached, this
	 *            setting will cause the parse process to throw an exception.</li>
	 *            </ul>
	 *
	 * @param charEncoding -
	 *            String encoding character set
	 *
	 * @param progressListener -
	 *            observer pattern listener for progress events. Events will
	 *            fire every 4096 bytes or whenever a new parameter (file or
	 *            otherwise) is encountered, whichever comes first.
	 *
	 * @throws IOException
	 */
	public HttpMultipartRequest(HttpServletRequest request,
			long maxContentLength, long maxContentMemoryThreshold,
			int onMaxLengthMode, String charEncoding,
			ProgressListener progressListener) throws IOException {
		super(request);

		initParser(request, maxContentLength, maxContentMemoryThreshold,
				onMaxLengthMode, charEncoding, progressListener);
	}

	/**
	 * @return true if current request is a multipart request.
	 */
	public boolean isMultipartRequest() {
		return m_isMultipart;
	}

	public long getMaxContentLengthMemoryThreshold() {
		return m_maxContentMemoryThreshold;
	}

	public long getMaxContentLength() {
		return m_maxContentLength;
	}

	/**
	 * If this class was constructed with a onMaxLengthMode mode of
	 * MultipartRequest.IGNORE_ON_MAX_LENGTH, this method will indicate whether
	 * the process is ignoring file content because the content-length was
	 * exceeded.
	 */
	public boolean isMaxLengthExceeded() {
		return (m_contentLength > m_maxContentLength);
	}

	/**
	 * This method returns the temporary directory configured via system
	 * properties, either multipartrequest specific parameters, or fallback to
	 * system default.
	 *
	 * This method will return a null value where load into memory is enabled.
	 */
	public File getTempDirectory() {
		return m_tempDirectory;
	}

	/**
	 * Are files being loaded into memory instead of the file system.
	 */
	public boolean isLoadIntoMemory() {
		return m_isLoadIntoMemory;
	}

	protected boolean isIgnoreOnMaxLength() {
		return m_isIgnoreOnMaxLength;
	}

	protected boolean isAbortOnMaxLength() {
		return !m_isIgnoreOnMaxLength;
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the
	 * parameter does not exist.
	 */
	public String getParameter(String name) {
		if (isMultipartRequest()) {
			String[] params = m_htParameters.get(name);
            if (params != null && params.length > 0) {
                return params[0];
            } else {
                return null;
            }
		} else {
			return super.getParameter(name);
		}
	}

	/**
	 * Returns an array of String objects containing all of the values the given
	 * request parameter has, or null if the parameter does not exist.
	 */
	public String[] getParameterValues(String name) {
		if (isMultipartRequest()) {
			return m_htParameters.get(name);
		} else {
			return super.getParameterValues(name);
		}
	}

	/**
	 * Returns an Enumeration of String objects containing the names of the
	 * parameters contained in this request. If the request has no parameters,
	 * the method returns an empty Enumeration.
	 */
	public Enumeration<String> getParameterNames() {
		if (isMultipartRequest()) {
			return new IteratorEnumeration(m_htParameters.keySet().iterator());
		} else {
			return super.getParameterNames();
		}
	}

	/**
	 * Returns a java.util.Map of the parameters of this request. This method
	 * does not include the file parameters, refer to the getFileParameterMap()
	 * method.
	 */
	public Map<String, String[]> getParameterMap() {
		if (isMultipartRequest()) {
			return m_htParameters;
		} else {
			return super.getParameterMap();
		}
	}

	/**
	 * Returns a instance of MultiPartFile for name
	 *
	 * @param name
	 * @return null if not a multipartrequest
	 */
	public MultipartFile getFileParameter(String name) {
		if (isMultipartRequest()) {
			MultipartFile fileObj = null;

			Object value = m_htFiles.get(name);
			if (value instanceof List)
				fileObj = (MultipartFile) ((List) value).get(0);
			else if (value instanceof MultipartFile)
				fileObj = (MultipartFile) m_htFiles.get(name);

			return fileObj;
		} else {
			return null;
		}
	}

	/**
	 * Returns an array of matching MultipartFile objects, or null if not a
	 * multipart request.
	 *
	 * @param name
	 * @return null if not a multipartrequest
	 */
	public List<MultipartFile> getFileParameterValues(String name) {
		if (isMultipartRequest()) {
			return m_htFiles.get(name);
		} else {
			return null;
		}
	}

	/**
	 * This enumeration will return all INPUT TYPE=FILE parameter NAMES as
	 * encountered during the upload.
	 *
	 * @return null if not a multipartrequest
	 */
	public Enumeration getFileParameterNames() {
		if (isMultipartRequest()) {
			return new IteratorEnumeration(m_htFiles.keySet().iterator());
		} else {
			return null;
		}
	}

	/**
	 * Returns a java.util.Enumeration of a MultiPartFile objects
	 *
	 * @return null if not a multipartrequest
	 */
	public Map getFileParameterMap() {
		if (isMultipartRequest()) {
			return m_htFiles;
		} else {
			return null;
		}
	}

	/**
	 * For debugging.
	 */
	public String toHtmlString() {
		StringBuffer sbReturn = new StringBuffer();

		sbReturn.append("<h2>Configuration</h2>");
		sbReturn.append("<ul>");
        sbReturn.append("<li>Max Length: ").append(getMaxContentLength()).append("</li>");
        sbReturn.append("<li>Content Length: ").append(getContentLength()).append("</li>");
        sbReturn.append("<li>Ignore on Maxlength: ").append(isIgnoreOnMaxLength()).append("</li>");

		if (isMultipartRequest()) {
            sbReturn.append("<li>Max Content Memory Threshold: ")
                    .append(getMaxContentLengthMemoryThreshold()).append("</li>");

            sbReturn.append("<li>Load into memory: ").append(isLoadIntoMemory()).append("</li>");
			if (!isLoadIntoMemory()) {
                sbReturn.append("Temporary Directory: ").append(getTempDirectory()).append("</li>");
			}
		}
		sbReturn.append("</ul>");

		sbReturn.append("<h2>Parameters</h2>");
		sbReturn
				.append("\n<table border=\"3\"><tr><td><b>Name</b></td><td><b>Value</b></td></tr>");
		for (Enumeration e = getParameterNames(); e.hasMoreElements();) {
			String strName = (String) e.nextElement();
            sbReturn.append("\n<tr>" + "<td>").append(strName).append("</td>");

			sbReturn.append("<td><table border=\"1\"><tr>");
			String[] values = getParameterValues(strName);
            for (String value : values) {
                sbReturn.append("<td>").append(value).append("</td>");
            }
			sbReturn.append("</tr></table></td></tr>");
		}
		sbReturn.append("</table>");

		if (isMultipartRequest()) {
			sbReturn.append("<h2>File Parameters</h2>");

			Enumeration fileEnumeration = getFileParameterNames();
			if (fileEnumeration.hasMoreElements()) {
				sbReturn.append("\n<table border=\"2\"><tr>"
						+ "<td><b>Name</b></td>" + "<td><b>Filename</b></td>"
						+ "<td><b>Content Type</b></td>"
						+ "<td><b>Size</b></td></tr>");

				for (; fileEnumeration.hasMoreElements();) {

					String strName = (String) fileEnumeration.nextElement();

					List<MultipartFile> multiFiles = getFileParameterValues(strName);
                    for (MultipartFile multiFile : multiFiles) {

                        sbReturn.append("\n<tr>" + "<td>")
                                .append(strName)
                                .append("</td>" + "<td>")
                                .append(multiFile.getName() != null ? multiFile.getName() : "")
                                .append("</td>");

                        sbReturn.append("<td>");
                        sbReturn.append(multiFile.getContentType() != null ? multiFile.getContentType() : "");
                        sbReturn.append("</td>" + "<td>");
                        sbReturn.append(multiFile.getSize() != -1 ? multiFile.getSize() + "" : "");
                        sbReturn.append("</td>" + "</tr>");
                    }
				}
				sbReturn.append("</table>");

			} else {
				sbReturn
						.append("<p style=\"{color: orange;}\">No valid files uploaded</p>");

				if (isMaxLengthExceeded()) {
                    sbReturn.append("<p style=\"{color: red;}\">Max Bytes exceeded (")
                            .append(m_contentLength)
                            .append(" > ")
                            .append(m_maxContentLength)
                            .append(") all file uploads ignored.</p>");
				}
			}
		}

		return sbReturn.toString();
	}

	private void initParser(HttpServletRequest request, long maxContentLength,
			long maxContentMemoryThreshold, int onMaxLengthMode,
			String charEncoding, ProgressListener progressListener)
			throws IllegalArgumentException, IOException {

		String contentType = request.getContentType();
		if (contentType != null && contentType.startsWith(MULTIPART_FORM_DATA)
				&& contentType.indexOf("boundary=") != -1) {

			m_isMultipart = true;

			m_htParameters = new HashMap<String, String[]>();
			m_htFiles = new HashMap<String, List<MultipartFile>>();

			if (onMaxLengthMode == IGNORE_ON_MAX_LENGTH) {
				m_isIgnoreOnMaxLength = true;
			} else if (onMaxLengthMode != ABORT_ON_MAX_LENGTH) {
				throw new IllegalArgumentException(
						"Illegal max length mode specified");
			}

			m_maxContentLength = maxContentLength;
			if (m_maxContentLength == -1) {
				m_maxContentLength = MAX_CONTENT_LENGTH;
			}

			m_contentLength = request.getContentLength();

			if (isAbortOnMaxLength() && isMaxLengthExceeded())// FIX: 1.15
			{
				throw new MaxContentLengthException(m_contentLength,
						m_maxContentLength);
			}

			// shamelessly stole this idea from Apache Commons File Upload
			m_maxContentMemoryThreshold = maxContentMemoryThreshold;

			if (m_maxContentMemoryThreshold >= m_contentLength) {
				m_isLoadIntoMemory = true;
			}

			if (!m_isLoadIntoMemory) {
				String dirValue = System
						.getProperty("net.iamvegan.multipartrequest.tmpdir");
				if (dirValue == null || dirValue.trim().length() == 0) {
					dirValue = System.getProperty("java.io.tmpdir");
				}

				if (dirValue != null && dirValue.length() > 0) {
					m_tempDirectory = new File(dirValue);
					if (!m_tempDirectory.isDirectory()
							|| !m_tempDirectory.canWrite()) {
						throw new IOException(
								"Invalid temporary directory specified");
					}
				}
			}

			if (charEncoding != null) {
				if (validateCharacterEncoding(charEncoding)) {
					m_charEncoding = charEncoding;
				}
			}

			// todo - should we use default servlet encoding?
			if (m_charEncoding == null) {
				m_charEncoding = DEFAULT_ENCODING;
			}

			if (progressListener != null) {
				m_progressNotifier = new ProgressNotifier(progressListener,
						m_contentLength);
			}

			m_strBoundary = contentType.substring(
					contentType.indexOf("boundary=") + "boundary=".length())
					.trim();

			m_blockOfBytes = new byte[READ_LINE_BLOCK];

			// Even though this method would never normally be called more than
			// once in the objects lifetime, we will initialise it here anyway.
			m_intTotalRead = 0; // <David Tuma> - fix: 1.20

			// Now parse the data.
			parse(new BufferedInputStream(request.getInputStream()));

			// No need for these once parse is complete.
			this.m_blockOfBytes = null;
		}
	}

	private void fireProgressEvent() {
		if (m_progressNotifier != null) {
			m_progressNotifier.notifyProgress(m_intTotalRead);
		}
	}

	private void fireNewItemProgressEvent() {
		if (m_progressNotifier != null) {
			m_progressNotifier.newItem();
		}
	}

	private boolean validateCharacterEncoding(String charsetName)
			throws UnsupportedEncodingException {
		new String(new byte[] { '0' }, charsetName);
		return true;
	}

	private void parse(InputStream in) throws IOException {

		// First run through, check that the first line is a boundary, otherwise
		// throw a exception as format incorrect.
		int read = readLine(in, m_blockOfBytes);
		String strLine = read > 0 ? new String(m_blockOfBytes, 0, read, m_charEncoding) : null;

		// Must be boundary at top of loop, otherwise we have finished.
		if (strLine == null || strLine.indexOf(this.m_strBoundary) == -1) {
			throw new IOException("Invalid Form Data, no boundary encountered.");
		}

		// TODO - this parsing process does not allow for the possibility of
		// newlines in the content-disposition, nor does it allow for nested
		// multipart parcels

		// At the top of loop, we assume that the Content-Disposition line is
		// next, otherwise we are at the end.
		while (true) {
			// Get Content-Disposition line.
			read = readLine(in, m_blockOfBytes);
			if (read <= 0)
				break; // Nothing to do.
			else {
				strLine = new String(m_blockOfBytes, 0, read, m_charEncoding);

				// Mac IE4 adds extra line after last boundary - 1.21
				if (strLine == null || strLine.length() == 0
						|| strLine.trim().length() == 0) {
					break;
				}

				fireNewItemProgressEvent();

				// use apache commons upload param parser
				Map parameters = m_paramParser.parse(strLine, ';');

				String strName = (String) parameters.get("name");

				// No filename specified at all - parameter
				if (!parameters.containsKey("filename")) {

					// Skip blank line.
					readLine(in, m_blockOfBytes);

					String param = readParameter(in);
					addParameter(strName, param);

				} else {

					String strFilename = (String) parameters.get("filename");

					// Fix: did not check whether filename was empty string
					// indicating a FILE was not passed.
					if (strFilename == null) {
						// FIX 1.14: IE problem with empty filename.
						read = readLine(in, m_blockOfBytes);
						strLine = read > 0 ? new String(m_blockOfBytes, 0,
								read, m_charEncoding) : null;

						// FIX 1.14 IE Problem: Check for content-type and
						// extra line even though no file specified.
						if (strLine != null
								&& strLine.toLowerCase().startsWith(
										"content-type:")) {
							readLine(in, m_blockOfBytes);
						}

						// Skip blank lines
						readLine(in, m_blockOfBytes);
						readLine(in, m_blockOfBytes);

					} else { // File uploaded.
						// Need to get the content type.
						read = readLine(in, m_blockOfBytes);
						strLine = read > 0 ? new String(m_blockOfBytes, 0,
								read, m_charEncoding) : null;

						// set default
						String strContentType = "application/octet-stream";

						if (strLine != null
								&& strLine.toLowerCase().startsWith(
										"content-type:")) {

							strContentType = strLine.substring(
									"content-type:".length()).trim();

							// Skip blank line, but only if a Content-Type was
							// specified.
							readLine(in, m_blockOfBytes);
						}

						if (isLoadIntoMemory()) {
							byte[] contentsOfFile = readFile(in);

							if (contentsOfFile != null) {
								long filesize = contentsOfFile.length;

								if (filesize > 0) {
									addFileParameter(strName,
											new MultipartFile(strName,
													strFilename,
													strContentType, filesize,
													contentsOfFile));
								}
							}
						} else { // Read the file onto file system.

							// todo - need to cleanup temporary directory
							// afterwards.
							File outFile = File.createTempFile("multPartReq",
									null, getTempDirectory());

							// delete file on exit - todo - how can we cause
							// File to be deleted on cleanup of File object instead
							outFile.deleteOnExit();

							long filesize = readAndWriteFile(in, outFile);

							if (filesize > 0) {
								addFileParameter(strName, new MultipartFile(
										strName, strFilename, strContentType,
										filesize, outFile));
							}
						}
					}
				}
			}
		}// while

		// last progress event
		fireProgressEvent();
	}

	/**
	 * So we can put the logic for supporting multiple parameters with the same
	 * form field name in the one location.
	 */
	private void addParameter(String strName, String value) {
		// Fix 1.16: for multiple parameter values.
		String[] oldParams = m_htParameters.get(strName);

        String[] newParams = new String[1];
        if (oldParams != null) {
            newParams = Arrays.copyOf(oldParams, oldParams.length + 1);
        }
        newParams[newParams.length - 1] = value;
		// Add an new entry to the param List.

		m_htParameters.put(strName, newParams);
	}

	private void addFileParameter(String strName, MultipartFile fileObj) {

		List<MultipartFile> objParms = m_htFiles.get(strName);

        if (objParms == null) {
            objParms = new ArrayList<MultipartFile>();
        }

        objParms.add(fileObj);
        m_htFiles.put(strName, objParms);
	}

	/**
	 * Read parameters, assume already passed Content-Disposition and blank
	 * line.
	 *
	 * @return the value read in.
	 */
	private String readParameter(InputStream in) throws IOException {
		StringBuffer buf = new StringBuffer();

		while (true) {
			int read = readLine(in, m_blockOfBytes);
			if (read < 0) {
				throw new IOException("Stream ended prematurely.");
			}

			// Change v1.18: Only instantiate string once for performance
			// reasons.
			String line = new String(m_blockOfBytes, 0, read, m_charEncoding);
			if (read < m_blockOfBytes.length
					&& line.indexOf(this.m_strBoundary) != -1) {
				break; // Boundary found, we need to finish up.
			} else {
				buf.append(line);
			}
		}

		if (buf.length() > 0)
			buf.setLength(getLengthMinusEnding(buf));
		return buf.toString();
	}

	/**
	 * Read from in, write to out, minus last two line ending bytes.
	 */
	private long readAndWrite(InputStream in, OutputStream out)
			throws IOException {
		long fileSize = 0;

		// This variable will be assigned the bytes actually read.
		byte[] secondLineOfBytes = new byte[m_blockOfBytes.length];
		// So we do not have to keep creating the second array.
		int sizeOfSecondArray = 0;

		while (true) {
			int read = readLine(in, m_blockOfBytes);
			if (read < 0) {
				throw new IOException("Stream ended prematurely.");
			}

			// Found boundary.
			if (read < m_blockOfBytes.length
					&& new String(m_blockOfBytes, 0, read, m_charEncoding)
							.indexOf(this.m_strBoundary) != -1) {
				// Write the line, minus any line ending bytes.
				// The secondLineOfBytes will NEVER BE NON-NULL if out==null, so
				// there is no need to included this in the test
				if (sizeOfSecondArray != 0) {
					// Only used once, so declare here.
					int actualLength = getLengthMinusEnding(secondLineOfBytes,
							sizeOfSecondArray);
					if (actualLength > 0 && out != null) {
						out.write(secondLineOfBytes, 0, actualLength);
						// Update file size.
						fileSize += actualLength;
					}
				}
				break;
			} else {
				// Write out previous line.
				// The sizeOfSecondArray will NEVER BE ZERO if out==null, so
				// there is no need to included this in the test
				if (sizeOfSecondArray != 0) {
					out.write(secondLineOfBytes, 0, sizeOfSecondArray);
					// Update file size.
					fileSize += sizeOfSecondArray;
				}

				// out will always be null, so there is no need to reset
				// sizeOfSecondArray to zero each time.
				if (out != null) {
					// Copy the read bytes into the array.
					System.arraycopy(m_blockOfBytes, 0, secondLineOfBytes, 0,
							read);
					// That is how many bytes to read from the secondLineOfBytes
					sizeOfSecondArray = read;
				}
			}
		}

		// Return the number of bytes written to outstream.
		return fileSize;
	}

	/**
	 * Read a Multipart section that is a file type. Assumes that the
	 * Content-Disposition/Content-Type and blank line have already been
	 * processed. So we read until we hit a boundary, then close file and
	 * return.
	 *
	 * @exception IOException
	 *                if an error occurs writing the file.
	 *
	 * @return the number of bytes read.
	 */
	private long readAndWriteFile(InputStream in, File outFile)
			throws IOException {
		BufferedOutputStream out = null;

		try {
			// 1.30rc1 - if max content is larger or equal to actual content
			// length, then we can continue, otherwise, all file content
			// should be silently ignored.
			if (!isMaxLengthExceeded()) {
				// Because the outFile should be a temporary file provided by
				// the TempFile class, it should already exist and should be
				// writable.
				if (outFile != null && outFile.exists() && outFile.canWrite()) {
					out = new BufferedOutputStream(
							new FileOutputStream(outFile));
				}
			}

			long count = readAndWrite(in, out);

			// Count would NOT be larger than zero if 'out' was null.
			if (count == 0) {
				// Delete file as empty.
				if (outFile != null)
					outFile.delete();
			}

			return count;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * Read file to memory
	 *
	 * @param in - File's InputStream
	 * @return contents of file, from which you can garner the size as well.
	 */
	private byte[] readFile(InputStream in) throws IOException {
		ByteArrayOutputStream out = null;

		// 1.30rc1 - if max content is larger or equal to actual content length,
		// then we can continue, otherwise, all file content should be silently
		// ignored.
		if (!isMaxLengthExceeded()) {
			// In this case, we do not need to worry about a outputdirectory.
			out = new ByteArrayOutputStream();
		}

		long count = readAndWrite(in, out);
		// Count would NOT be larger than zero if out was null.
		if (count > 0) {
			// Return contents of file to parse method for inclusion in
			// m_htFiles object.
			if (out != null)
				return out.toByteArray();
			else
				return null;
		} else
			return null;
	}

	/**
	 * Reads at most READ_BLOCK blocks of data, or a single line whichever is
	 * smaller. Returns -1, if nothing to read, or we have reached the specified
	 * content-length.
	 *
	 * Assumes that bytesToBeRead.length indicates the block size to read.
	 *
	 * @return -1 if stream has ended, before a newline encountered (should
	 *         never happen) OR we have read past the Content-Length specified.
	 *         (Should also not happen). Otherwise return the number of
	 *         characters read. You can test whether the number returned is less
	 *         than bytesToBeRead.length, which indicates that we have read the
	 *         last line of a file or parameter or a border line, or some other
	 *         formatting stuff.
	 */
	private int readLine(InputStream in, byte[] bytesToBeRead)
			throws IOException {
		// Ensure that there is still stuff to read...
		if (this.m_intTotalRead >= this.m_contentLength)
			return -1;

		// Get the length of what we are wanting to read.
		int length = bytesToBeRead.length;

		// End of content, but some servers (apparently) may not realise this
		// and end the InputStream, so we cover ourselves this way.
		// So we only read the data that is left.
		if (length > (this.m_contentLength - this.m_intTotalRead)) {
			length = (int) (this.m_contentLength - this.m_intTotalRead);
		}

		int result = readLine(in, bytesToBeRead, 0, length);
		// Only if we actually read something, otherwise something weird has
		// happened, such as the end of stream.
		if (result > 0) {
			this.m_intTotalRead += result;

			fireProgressEvent();
		}

		return result;
	}

	/**
	 * Returns the length of the line minus line ending.
	 *
	 * @param endOfArray
	 *            This is because in many cases the byteLine will have garbage
	 *            data at the end, so we act as though the actual end of the
	 *            array is this parameter. If you want to process the complete
	 *            byteLine, specify byteLine.length as the endOfArray parameter.
	 */
	private static int getLengthMinusEnding(byte byteLine[],
			int endOfArray) {
		if (byteLine == null)
			return 0;

		if (endOfArray >= 2 && byteLine[endOfArray - 2] == '\r'
				&& byteLine[endOfArray - 1] == '\n')
			return endOfArray - 2;
		else if (endOfArray >= 1 && byteLine[endOfArray - 1] == '\n'
				|| byteLine[endOfArray - 1] == '\r')
			return endOfArray - 1;
		else
			return endOfArray;
	}

	private static int getLengthMinusEnding(StringBuffer buf) {
		if (buf.length() >= 2 && buf.charAt(buf.length() - 2) == '\r'
				&& buf.charAt(buf.length() - 1) == '\n')
			return buf.length() - 2;
		else if (buf.length() >= 1 && buf.charAt(buf.length() - 1) == '\n'
				|| buf.charAt(buf.length() - 1) == '\r')
			return buf.length() - 1;
		else
			return buf.length();
	}

	/**
	 * <I>Tomcat's ServletInputStream.readLine(byte[],int,int) Slightly Modified
	 * to utilise in.read()</I> <BR>
	 * Reads the input stream, one line at a time. Starting at an offset, reads
	 * bytes into an array, until it reads a certain number of bytes or reaches
	 * a newline character, which it reads into the array as well.
	 *
	 * <p>
	 * This method <u><b>does not</b></u> returns -1 if it reaches the end of
	 * the input stream before reading the maximum number of bytes, it returns
	 * -1, if no bytes read.
	 *
	 * @param b
	 *            an array of bytes into which data is read
	 *
	 * @param off
	 *            an integer specifying the character at which this method
	 *            begins reading
	 *
	 * @param len
	 *            an integer specifying the maximum number of bytes to read
	 *
	 * @return an integer specifying the actual number of bytes read, or -1 if
	 *         the end of the stream is reached
	 *
	 * @exception IOException
	 *                if an input or output exception has occurred
	 *
	 *
	 * Note: We have a problem with Tomcat reporting an erroneous number of
	 * bytes, so we need to check this. This is the method where we get an
	 * infinite loop, but only with binary files.
	 */
	private int readLine(InputStream in, byte[] b, int off, int len)
			throws IOException {
		if (len <= 0)
			return 0;

		int count = 0, c;

		while ((c = in.read()) != -1) {
			b[off++] = (byte) c;
			count++;
			if (c == '\n' || count == len)
				break;
		}

		return count > 0 ? count : -1;
	}

    private class IteratorEnumeration implements Enumeration<String> {
        private Iterator<String> iterator;

        public IteratorEnumeration(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public String nextElement() {
            return iterator.next();
        }
    }
}
