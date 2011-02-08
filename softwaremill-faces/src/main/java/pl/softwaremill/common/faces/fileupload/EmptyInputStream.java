package pl.softwaremill.common.faces.fileupload;

/*
 MultipartRequest servlet library
 Copyright (C) 2001-2007 by Jason Pell

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

import java.io.IOException;
import java.io.InputStream;

/**
 * A dummy input stream class.
 * 
 * @author Copyright (C) 2001-2006 Jason Pell
 * @version 2.00b3
 */
public class EmptyInputStream extends InputStream {
	public EmptyInputStream() {
	}

	public int read() throws IOException {
		return -1;
	}

	public int available() throws IOException {
		return 0;
	}
}