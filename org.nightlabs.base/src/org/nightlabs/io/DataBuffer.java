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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nightlabs.util.IOUtil;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
public class DataBuffer // implements IDataBuffer // , Cloneable
{
  protected static final int defaultMaxSizeForRAM = 4 * 1024 * 1024; // 4 MB
//  protected static int defaultMaxSizeForRAM = 250;

  protected static final byte MODE_RAM = 0;
  protected static final byte MODE_FILE = 1;
  protected byte mode = MODE_RAM;

  public long expectedSize;
  public int maxSizeForRAM;

  protected byte[] data;

  protected File file = null;

  protected long length = 0;

//  private byte[] oneByteByteArray = new byte[1];

  public DataBuffer()
  	throws FileNotFoundException, IOException
  {
  	this(1024);
  }

  /**
   * This is a convenience constructor which does the same as the default
   * constructor in conjunction with {@link Util#transferStreamData(InputStream, OutputStream)}.
   */
  public DataBuffer(long expectedSize, InputStream in)
	throws FileNotFoundException, IOException
	{
  	this(expectedSize, defaultMaxSizeForRAM, in);
	}
  public DataBuffer(long expectedSize, int maxSizeForRAM, InputStream in)
	throws FileNotFoundException, IOException
	{
  	this(expectedSize, maxSizeForRAM);

  	OutputStream out = createOutputStream();
  	try {
  		IOUtil.transferStreamData(in, out);
  	} finally {
  		out.close();
  	}
	}

  /**
   * This is a convenience constructor which does the same as the default
   * constructor in conjunction with {@link Util#transferStreamData(InputStream, OutputStream)}.
   */
  public DataBuffer(InputStream in)
	throws FileNotFoundException, IOException
	{
  	this(1024, in);
	}

  public DataBuffer(long expectedSize, String _fileName)
  throws FileNotFoundException,
         IOException
  {
    this(expectedSize, defaultMaxSizeForRAM, new File(_fileName));
  }

  public DataBuffer(long expectedSize, File _file)
  throws FileNotFoundException,
         IOException
  {
    this(expectedSize, defaultMaxSizeForRAM, _file);
  }

  public DataBuffer(long expectedSize, int maxSizeForRAM, String _fileName)
  throws FileNotFoundException,
         IOException
  {
    this(expectedSize, maxSizeForRAM, new File(_fileName));
  }

  public DataBuffer(long expectedSize, int maxSizeForRAM)
  throws FileNotFoundException,
         IOException
  {
  	this(expectedSize, maxSizeForRAM, (File)null);
  }

  public DataBuffer(long expectedSize, int maxSizeForRAM, File _file)
  throws FileNotFoundException,
         IOException
  {
    this.expectedSize = expectedSize;
    this.maxSizeForRAM = maxSizeForRAM;
    this.file = _file;

    if (expectedSize > maxSizeForRAM || (file != null && file.exists()))
      this.mode = MODE_FILE;

    if (this.mode == MODE_FILE) {
      if (file == null)
        file = File.createTempFile("DataBuffer", ".tmp");

      length = file.length();
    }
    else {
      data = new byte[(int)expectedSize];
    }
  }

//  public DataBuffer(int expectedSize, int maxSizeForRAM)
//  throws FileNotFoundException,
//         IOException
//  {
//    this(expectedSize, maxSizeForRAM, null);
//  }

//  public DataBuffer(int expectedSize, String fileName)
//  throws FileNotFoundException,
//         IOException
//  {
//    this(expectedSize, defaultMaxSizeForRAM, fileName);
//  }
//
//  public DataBuffer(int expectedSize, String fileName, boolean append, boolean readOnly)
//  throws FileNotFoundException,
//         IOException
//  {
//    this(expectedSize, defaultMaxSizeForRAM, fileName, append, readOnly);
//  }
//
  public DataBuffer(long expectedSize)
  throws FileNotFoundException,
         IOException
  {
    this(expectedSize, defaultMaxSizeForRAM, (File)null);
  }


  protected FileInputStream createFileInputStream()
  throws FileNotFoundException
  {
    if (mode != MODE_FILE)
      throw new IllegalStateException("Cannot return a FileInputStream when not in MODE_FILE!");

    return new FileInputStream(file);
  }

  /**
   * This method creates a new InputStream to read from this DataBuffer. You
   * can read at the same time on multiple threads from one DataBuffer.
   * <p>
   * Note, that the returned input stream is already buffered! You should not
   * wrap a BufferedInputStream around!
   *
   * @return The new InputStream.
   */
  public InputStream createInputStream()
  throws IOException
  {
    if (mode == MODE_FILE)
      return new BufferedInputStream(new FileInputStream(file));
    else
      return new DataBufferInputStream(this);
  }

  /**
    * This method creates a new OutputStream to write into this DataBuffer.
    * You should be very careful when writing into one DataBuffer from multiple
    * threads at the same time. Better don't do that! The result is
    * unpredictable. At the moment, there is no multi-thread access supported.
    * Thus, you should make sure, noone is reading while one thread writes!
    * <p>
    * Note, that the returned OutputStream is already buffered! You should not
    * wrap a BufferedOutputStream around!
    *
    * @return The new OutputStream
    */
  public OutputStream createOutputStream()
  throws IOException
  {
//    if (mode == MODE_FILE)
//      return new FileOutputStream(file);
//    else
    return new DataBufferOutputStream(this);
  }

  protected void switchModeToFile()
  throws IOException
  {
    if (mode == MODE_FILE)
      return;

    if (file == null)
      file = File.createTempFile("DataBuffer", ".tmp");

    os = new BufferedOutputStream(new FileOutputStream(file, false));

    if (length > Integer.MAX_VALUE)
      throw new IllegalStateException("length is too big! Switching mode to file should have happened earlier!");

    os.write(data, 0, (int)length);
    mode = MODE_FILE;
    data = null;
  }

  OutputStream os = null;
  protected void closeOutputStream()
  throws IOException
  {
    if (os != null) {
      os.close();
      os = null;
    }
  }

  protected void flushOutputStream()
  throws IOException
  {
    if (os != null)
      os.flush();
  }

  protected void write(byte[] b, int off, int len)
  throws IOException
  {
    if (mode == MODE_FILE) {
      if (os == null)
        os = new FileOutputStream(file, true);

      os.write(b, off, len);
    } // if (mode == MODE_FILE) {
    else {

      // Falls die maximale Groesse fuer den RAM ueberschritten wird,
      // alles in die Datei dumpen.

      if (length + len > maxSizeForRAM) {
        switchModeToFile();
        os.write(b, off, len);
      }
      else {

        if (data.length < length + len) {
          byte[] tmpData = new byte[((int)length + len) * 8 / 6]; // vergroessern
          System.arraycopy(data, 0, tmpData, 0, (int)length);
          data = tmpData;
        }

        System.arraycopy(b, off, data, (int)length, len);
      }
    } // if (mode != MODE_FILE) {

    length += len;
  }

//    public void write(byte[] b)
//    throws IOException
//    {
//      write(b, 0, b.length);
//    }
//
//    public void write(int b)
//    throws java.io.IOException
//    {
//      oneByteByteArray[0] = (byte)b;
//      this.write(oneByteByteArray, 0, 1);
//    }
//
//    public int read()
//    throws IOException
//    {
//      if (isClosed)
//        throw new IOException("InputStream has already been closed!");
//
//      if (fin != null)
//        return fin.read();
//      else {
//        if (dataReadPos < length)
//          return ((int)data[dataReadPos++]) & 0xff;
//        else
//          return -1;
//      }
//    }
//
//    public int read(byte[] b, int off, int len)
//    throws IOException
//    {
//      if (isClosed)
//        throw new IOException("InputStream has already been closed!");
//
//      int dr;
//
//      if (fin != null)
//        dr = fin.read(b, off, len);
//      else {
//        if (dataReadPos + len > length)
//          len = length - dataReadPos;
//
//        if (len > 0) {
//          System.arraycopy(data, dataReadPos, b, off, len);
//          dr = len;
//        }
//        else
//          dr = -1;
//      }
//
//      if (dr > 0)
//        dataReadPos += dr;
//
//      return dr;
//    }
//
//    public int read(byte[] b)
//    throws IOException
//    {
//      return read(b, 0, b.length);
//    }
//
  public long size()
  {
    return length;
  }
//
//    public int available()
//    {
//      int avl = length - dataReadPos;
//
//      if (avl <= 0)
//        avl = -1;
//
//      return avl;
//    }
//
//    public void reset()
//    throws IOException
//    {
//      if (fin != null)
//        fin.reset();
//
//      dataReadPos = dataMarkPos;
//    }
//
//
//    public long skip(long n)
//    throws IOException
//    {
//      long bytesSkipped;
//
//      if (fin != null)
//        bytesSkipped = fin.skip(n);
//      else {
//        if (dataReadPos + n > length)
//          bytesSkipped = length - dataReadPos;
//        else
//          bytesSkipped = n;
//      }
//
//      if (bytesSkipped > 0)
//        dataReadPos += bytesSkipped;
//
//      return bytesSkipped;
//    }
//
//    public void closeInputStream()
//    throws IOException
//    {
//      if (fin != null) {
//        fin.close();
//      }
//
//      isClosed = true;
//    }
//
//    public void flush()
//    throws IOException
//    {
//      if (fout != null)
//        fout.flush();
//    }
//
//    public boolean isOutputStreamClosed()
//    {
//      return osClosed;
//    }
//
//    public boolean isInputStreamClosed()
//    {
//      return isClosed;
//    }
//
//    public void closeOutputStream()
//    throws IOException
//    {
//      if (fout != null) {
//        fout.close();
//      }
//
//      osClosed = true;
//    }
//
//    public void seekToBeginning()
//    throws IOException
//    {
//      if (dataReadPos==0)
//        return;
//
//      if (fin != null) {
//        fin.close();
//        fin = new FileInputStream(fileName);
//      }
//      dataReadPos = 0;
//    }
//
//
    /**
     * This method copies the data within this databuffer into the destination
     * file. If this databuffer represents a file, the file is copied, but
     * the timestamp is not copied. This means, the new file's timestamp will
     * always be the current system time.
     *
     * @param dest The file name (full path) of the destination file.
     */
    public void vomitFile(String dest)
    throws IOException
    {
      vomitFile(new File(dest));
    }

    public void vomitFile(File dest)
    throws IOException
    {
      this.closeOutputStream();

      FileOutputStream o = new FileOutputStream(dest);

      if (data != null) {
      	if (length > Integer.MAX_VALUE)
      		throw new IllegalStateException("What the fuck?! length > Integer.MAX_VALUE !!! It should have switched to file mode already before!");

        o.write(data, 0, (int) length);
      }
      else {
        FileInputStream i = new FileInputStream(this.file);
        byte[] b = new byte[8192];
        int bytesRead;
        while (true) {
          bytesRead = i.read(b);
          if (bytesRead <= 0)
            break;

          o.write(b, 0, bytesRead);
        }

      }

      o.close();
    }

    /**
     * This generates a new byte array that has exactly the necessary length
     * and contains all the data from this DataBuffer. Note, that this method
     * may fail if this DataBuffer contains huge data (i.e. more than a java
     * array can hold).
     *
     * @return a byte array copy of this DataBuffer.
     * @throws IOException
     * @throws {@link ArrayIndexOutOfBoundsException} If the DataBuffer contains more data than fits into a byte array.
     */
    public byte[] createByteArray()
    	throws IOException
    {
    	if (length > Integer.MAX_VALUE)
    		throw new ArrayIndexOutOfBoundsException("This DataBuffer contains too much data for a byte array!");

    	byte[] res = new byte[(int)length];

    	if (mode == MODE_RAM) {
    		System.arraycopy(data, 0, res, 0, res.length);
    	}
    	else if (mode == MODE_FILE) {
    		InputStream in = createInputStream();
    		try {
	    		int pos = 0;
	    		int bytesRead;
	    		do {
	    			bytesRead = in.read(res, pos, res.length - pos);
	    			if (bytesRead > 0)
	    				pos += bytesRead;
	    		} while (bytesRead >= 0 && pos < res.length);
	    		if (pos < res.length)
	    			throw new IOException("Could not read all bytes (stream broken): pos < res.length!");
    		} finally {
    			in.close();
    		}
    	}
    	else
    		throw new IllegalStateException ("unknown mode: " + mode);

    	return res;
    }

//  public Object clone()
//  {
//    return clone(true);
//  }
//
//  public Object clone(boolean cloneBinary)
//  {
//    DataBuffer n = new DataBuffer(this.expectedSize);
//    if (cloneBinary) {
//      if (this.data != null) {
//        n.data = (byte[])this.data.clone();
//      }
//      else {
//        FileInputStream i = new FileInputStream(this.fileName);
//        DataBufferOutputStream dbos = new DataBufferOutputStream(n);
//        Utils.transferStreamData(i, dbos);
//        dbos.close();
//      } // else: if (this.data != null) {
//    } // if (cloneBinary) {
//    else {
//      n.data = this.data;
//      n.fileName = this.fileName;
//      n.length = this.length;
//      n.dataMarkPos = this.dataMarkPos;
//      n.dataReadPos = this.dataReadPos;
//      n.maxSizeForRAM = this.maxSizeForRAM;
//    } // else: if (cloneBinary) {
//
//  }

    public int getMaxSizeForRAM() {
			return maxSizeForRAM;
		}
}
