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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * An InputStream that writes to a {@link org.nightlabs.io.DataBuffer}.
 * @author Marco Schulze
 */
public class DataBufferInputStream extends InputStream
{
  DataBuffer dataBuffer;

  protected DataBufferInputStream(DataBuffer _dataBuffer)
  {
    this.dataBuffer = _dataBuffer;
  }

//  public DataBufferInputStream(IDataBuffer dataBuffer, boolean seekToBeginning)
//  throws IOException
//  {
//    this.dataBuffer = dataBuffer;
//
//    if (seekToBeginning)
//      this.dataBuffer.seekToBeginning();
//  }

  protected long markedReadPos = 0;
  protected long dataReadPos = 0;

  /**
   * Is initialized when the dataBuffer switches to fileMode after this dataBuffer
   * has already been created. This happens when the next read occurs.
   */
  protected BufferedInputStream in = null;


  @Override
	public int available()
  throws IOException
  {
    if (in != null)
      return in.available();

    long avl = dataBuffer.length - dataReadPos;

    if (avl <= 0)
      return 0;

    if (avl > Integer.MAX_VALUE)
      return Integer.MAX_VALUE;

    return (int)avl;
  }

  protected boolean closed = false;

  @Override
	public void close()
  throws IOException
  {
    if (in != null)
      in.close();

    closed = true;
  }

  protected int markReadLimit = 0;
  @Override
	public void mark(int readlimit)
  {
    if (in != null)
      in.mark(readlimit);

    markReadLimit = readlimit;
    markedReadPos = dataReadPos;
  }

  @Override
	public boolean markSupported()
  {
    return true;
  }

  protected void checkMode()
  throws IOException
  {
    if (in == null && dataBuffer.mode == DataBuffer.MODE_FILE) {
      in = new BufferedInputStream(dataBuffer.createFileInputStream());
      if (markedReadPos > 0) {
        in.skip(markedReadPos);
        in.mark(markReadLimit);
      }
      in.skip(dataReadPos - markedReadPos);
    }
  }

  @Override
	public int read()
  throws IOException
  {
    checkMode();

    if (closed)
      throw new IOException("InputStream has already been closed!");

    if (in != null) {
      int i = in.read();
      if (i >= 0)
        dataReadPos++;
      return i;
    }

    if (dataReadPos > Integer.MAX_VALUE - 10)
      throw new IllegalStateException("dataReadPos is too big! Should have switched to file mode earlier!");

    if (dataReadPos < dataBuffer.length)
      return (dataBuffer.data[(int)dataReadPos++]) & 0xff;
    else
      return -1;
  }

  @Override
	public int read(byte[] b, int off, int len)
  throws IOException
  {
    checkMode();

    if (closed)
      throw new IOException("InputStream has already been closed!");

    int dr;

    if (in != null)
      dr = in.read(b, off, len);
    else {
      if (dataReadPos + len > dataBuffer.length)
        len = (int)(dataBuffer.length - dataReadPos);

      if (dataReadPos > Integer.MAX_VALUE - 10)
        throw new IllegalStateException("dataReadPos is too big! Should have switched to file mode earlier!");

      if (len > 0) {
        System.arraycopy(dataBuffer.data, (int)dataReadPos, b, off, len);
        dr = len;
      }
      else
        dr = -1;
    }

    if (dr > 0)
      dataReadPos += dr;

    return dr;
  }

  @Override
	public int read(byte[] b)
  throws IOException
  {
    return read(b, 0, b.length);
  }







//  public int read()
//  throws IOException
//  {
//    return dataBuffer.read();
//  }
//
//  public int read(byte[] b)
//  throws IOException
//  {
//    return dataBuffer.read(b);
//  }
//
//  public int read(byte[] b, int off, int len)
//  throws IOException
//  {
//    return dataBuffer.read(b, off, len);
//  }




  @Override
	public void reset()
  throws IOException
  {
    checkMode();

    if (in != null)
      in.reset();

    dataReadPos = markedReadPos;
  }

  @Override
	public long skip(long n)
  throws IOException
  {
    checkMode();
    if (in != null)
      n = in.skip(n);
    else {
      long max = dataBuffer.length - dataReadPos;
      if (n > max) n = max;
      dataReadPos += n;
    }
    return n;
  }


}
