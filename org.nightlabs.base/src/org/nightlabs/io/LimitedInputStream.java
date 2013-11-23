/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to make sure, not more data is read from the socket as
 * the current block (e.g. body) contains. Thus, you can use this as a wrapper
 * for a SocketInputStream and give this to higher streams like GZipInputStream or
 * even a reader + deserializer.
 * 
 * @author Marco Schulze
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
public class LimitedInputStream extends InputStream
{
  protected InputStream in;
  protected int minLimit;
  protected int maxLimit;
  protected int readPos = 0;


  /**
   * This is the only constructor that makes sence for this stream.
   * @param in This is my input stream
   */
  public LimitedInputStream(InputStream in, int minLimit, int maxLimit)
  {
    this.in = in;
    this.minLimit = minLimit;
    this.maxLimit = maxLimit;
  }

  @Override
	public int available()
  throws IOException
  {
    int reallyAvailable = in.available();

    if (reallyAvailable < 0) {
      if (readPos < minLimit)
        throw new IOException("inputStream is closed; only "+readPos+" Bytes read, but minLimit = "+minLimit+" Bytes!");
    }

    if (reallyAvailable <= 0) // we are never blocking, but if in is blocking and returned 0, we should return 0 as well
      return reallyAvailable;

    int limitedAvailable = Math.min(
      reallyAvailable,
      maxLimit - readPos
    );

//    limitedAvailable = Math.max( // es geht darum, wieviel NONBLOCKING gelesen werden kann, also auskommentieren.
//      minLimit - readPos,
//      limitedAvailable
//    );

    if (limitedAvailable <= 0)
      limitedAvailable = -1;

    return limitedAvailable;
  }

/*
  public void close()
  throws IOException
  {

  }
*/

//  public void mark(int readlimit)
//  {
//    if (in.markSupported())
//      in.mark(readlimit);
//  }


  @Override
	public boolean markSupported()
  {
    return false;
  }

  @Override
	public int read()
  throws java.io.IOException
  {
    if (readPos < maxLimit) {
      int res = in.read();

      if (res >= 0)
        readPos++;
      else {
        if (readPos < minLimit)
          throw new IOException("inputStream is closed; only "+readPos+" Bytes read, but minLimit = "+minLimit+" Bytes!");
      }

      return res;
    }
    else
      return -1;
  }

  @Override
	public int read(byte[] b)
  throws IOException
  {
    return this.read(b, 0, b.length);
  }

  @Override
	public int read(byte[] b, int off, int len)
  throws IOException
  {
    if (readPos + len > maxLimit)
      len = maxLimit - readPos;

    if (len > 0) {
      int bytesRead = in.read(b, off, len);

      if (bytesRead > 0)
        readPos += bytesRead;

      if (bytesRead < 0 && readPos < minLimit)
          throw new IOException("inputStream is closed; only "+readPos+" Bytes read, but minLimit = "+minLimit+" Bytes!");

      if (bytesRead == 0)
        throw new IllegalStateException("according to the documentation, InputStream.read(...) should never return 0 if len != 0, but it happened!");

      return bytesRead;
    }
    else if (len < 0)
      throw new IndexOutOfBoundsException("len < 0!");
    else
      return -1;
  }

/*
  public void reset()
  throws IOException
  {
  }
*/

  @Override
	public long skip(long len)
  throws IOException
  {
    if (readPos + len > maxLimit)
      len = maxLimit - readPos;

    if (len > 0) {
      long bytesSkipped = in.skip(len);

      if (bytesSkipped > 0)
        readPos += bytesSkipped;

      return bytesSkipped;
    }
    else
      return -1;
  }

}
