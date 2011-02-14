package pl.softwaremill.common.servlet.multipart;

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

class ProgressNotifier {
	private ProgressListener m_progressListener;
	private long m_contentLength;

	/**
	 * Stores the count of the current item being process. Items include normal
	 * and file parameters
	 */
	private int m_itemCount = 0;

	private long m_bytesReadLastTime = 0;
	private long m_itemsLastTime = 0;

	private static final long NOTIFY_BUFFER_SIZE = 4096; // how many bytes to
															// read before init
															// a progress event.

	ProgressNotifier(ProgressListener listener, long contentLength) {
		m_progressListener = listener;
		m_contentLength = contentLength;
	}

	void notifyProgress(long pBytesRead) {
		if (m_itemsLastTime < m_itemCount
				|| (pBytesRead - m_bytesReadLastTime) >= NOTIFY_BUFFER_SIZE
				|| (pBytesRead > m_bytesReadLastTime && pBytesRead >= m_contentLength)) {

			m_progressListener.update(pBytesRead, m_contentLength, m_itemCount);
			m_bytesReadLastTime = pBytesRead;
			m_itemsLastTime = m_itemCount;
		}
	}

	void newItem() {
		m_itemCount++;
	}
}