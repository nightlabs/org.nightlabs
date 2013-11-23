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
import java.io.OutputStream;

/**
 * An OutputStream that writes to a {@link org.nightlabs.io.DataBuffer}.
 * @author Marco Schulze
 */
public class DataBufferOutputStream extends OutputStream
{
  DataBuffer dataBuffer;

  protected DataBufferOutputStream(DataBuffer _dataBuffer)
  {
    this.dataBuffer = _dataBuffer;
  }

  protected boolean closed = false;

  @Override
	public void close()
  throws IOException
  {
    if (closed)
      return;

    dataBuffer.closeOutputStream();
    closed = true;
  }

  @Override
	public void flush()
  throws IOException
  {
    if (closed)
      throw new IOException("OutputStream is already closed!");

    dataBuffer.flushOutputStream();
  }

  @Override
	public void write(byte[] b)
  throws java.io.IOException
  {
    if (closed)
      throw new IOException("OutputStream is already closed!");

    dataBuffer.write(b, 0, b.length);
  }

  @Override
	public void write(byte[] b, int off, int len)
  throws IOException
  {
    if (closed)
      throw new IOException("OutputStream is already closed!");

    dataBuffer.write(b, off, len);
  }

  byte[] oneByteByteArray = null;

  @Override
	public void write(int b)
  throws java.io.IOException
  {
    if (closed)
      throw new IOException("OutputStream is already closed!");

    if (oneByteByteArray == null)
      oneByteByteArray = new byte[1];

    oneByteByteArray[0] = (byte)b;
    dataBuffer.write(oneByteByteArray, 0, 1);
  }

}
