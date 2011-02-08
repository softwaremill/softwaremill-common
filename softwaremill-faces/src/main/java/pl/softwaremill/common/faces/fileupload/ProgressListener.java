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

/**
*  Allows an object to be notified whenever a transfer is progressing.
*
*  <pre>
*          ProgressListener pl = new ProgressListener() {
*              public void update(long pBytesRead, long pContentLength, int pItems)
*              {
*					System.out.println("IN="+pBytesRead+", "+(int)(((float)pBytesRead/pContentLength)*100)+"%");
*              }
*          };
*
*          multi = new HttpServletMultipartRequest( req,
*                                                   Long.MAX_VALUE,
*                                                   HttpServletMultipartRequest.SAVE_TO_TMPDIR,
*                                                   HttpServletMultipartRequest.ABORT_ON_MAX_LENGTH,
*                                                   "UTF-8",
*                                                   pl );
*  </pre>
*/
public interface ProgressListener {
	/**
	 * Updates the listeners status information.
	 * @param pBytesRead The total number of bytes, which have been read so far.
	 * @param pContentLength The total number of bytes, which are being read. May be -1, if this number is unknown.
	 * @param pItems The number of the field, which is currently being read. (0 = no item so far, 1 = first item is being read, ...)
	 */
	void update(long pBytesRead, long pContentLength, int pItems);
}
