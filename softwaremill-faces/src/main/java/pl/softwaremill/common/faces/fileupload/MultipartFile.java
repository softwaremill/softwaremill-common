package pl.softwaremill.common.faces.fileupload;

/*
	MultipartRequest servlet library
	Copyright (C) 2001-2007 by Jason Pell

	Changes by Pawel Stawicki
	
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

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author Jason Pell
 * @version 2.00b11
 */
public class MultipartFile {
    private String m_fieldname;
    private String m_rawFileName;
    private String m_fileName;
    private File m_tmpFile;
    private byte[] m_fileContents;
    private long m_fileSize;
    private String m_contentType;

    /**
     * @param fieldName - name of file field
     * @param fileName - Basename of file as provided from the browser.
     * @param contentType - Content type of file
     * @param fileSize - size of file
     * @param tmpFile - file contents
     */
    protected MultipartFile(String fieldName, String fileName, String contentType, long fileSize, File tmpFile) {
        m_fieldname = fieldName;
        m_rawFileName = fileName;
        m_fileName = getBasename(m_rawFileName);
        m_contentType = contentType;
        m_tmpFile = tmpFile;
        m_fileSize = fileSize;
    }

    /**
     * @param fieldName - name of file field
     * @param fileName - Basename of file as provided from the browser.
     * @param contentType - Content type of file
     * @param fileSize - size of file
     * @param fileContents - file contents
     */
    protected MultipartFile(String fieldName, String fileName, String contentType, long fileSize, byte[] fileContents) {
        m_fieldname = fieldName;
        m_rawFileName = fileName;
        m_fileName = getBasename(m_rawFileName);
        m_contentType = contentType;
        m_fileContents = fileContents;
        m_fileSize = fileSize;
    }

    public String getFieldName() {
        return m_fieldname;
    }

    public String getPathName() {
        return m_rawFileName;
    }

    public String getName() {
        return m_fileName;
    }

    public String getContentType() {
        return m_contentType;
    }

    public long getSize() {
        return m_fileSize;
    }

    public File getTmpFile() throws IOException {
        if (m_tmpFile == null) {
            m_tmpFile = createTmpFile();
        }

        return m_tmpFile;
    }

    private File createTmpFile() throws IOException {
        File file = File.createTempFile("multipart_file_", null);
        if (m_fileContents != null) {
            FileOutputStream fileStream = new FileOutputStream(file);
            try {
                IOUtils.write(m_fileContents, fileStream);
            } finally {
                fileStream.close();
            }
        }

        return file;
    }

    public InputStream getInputStream() throws IOException {
        if(m_tmpFile!=null) {
            return new FileInputStream(m_tmpFile);
        } else if(m_fileContents!=null){
            return new ByteArrayInputStream(m_fileContents);
        } else {
            return  new EmptyInputStream();
        }
    }

    public String toString() {
        return "fieldName="+getFieldName()+
                "; pathName="+getPathName()+
                "; name="+getName()+
                "; contentType="+getContentType()+
                "; size="+getSize();
    }

    /**
     * This needs to support the possibility of a / or a \ separator.
     *
     * Returns strFilename after removing all characters before the last
     * occurence of / or \.
     */
    private String getBasename(String strFilename) {
        if (strFilename == null)
            return strFilename;

        int intIndex = strFilename.lastIndexOf("/");
        if (intIndex == -1 || strFilename.lastIndexOf("\\") > intIndex)
            intIndex = strFilename.lastIndexOf("\\");

        if (intIndex != -1)
            return strFilename.substring(intIndex + 1);
        else
            return strFilename;
    }
}
