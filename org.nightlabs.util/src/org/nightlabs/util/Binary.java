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
package org.nightlabs.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A helper class to read or write binary data from/to
 * streams. This class provides methods to handle binary
 * data in- and outputs more easily - especially
 * reverse order number formats like they are produced using
 * C/C++ or Pascal/Delphi raw data writing and reading
 * functions.
 * 
 * @author Marc Klinger <marc[at]nightlabs[dot]de>
 * @author Daniel Mazurek
 * @author Marco Schulze
 * @deprecated This does not work correctly for unsigned values. Consider using
 * 		the Jakarta Commons IO package with classes org.apache.commons.io.EndianUtils
 * 		and org.apache.commons.io.input.SwappedDataInputStream
 */
@Deprecated
public class Binary
{

  public InputStream in = null;
  public OutputStream out = null;

  private long bytesRead = 0;
  public long getBytesRead() {
  	return bytesRead;
  }
  
  /**
   * Initialize this Binary with an InputStream.
   * Only the read... methods will work.
   * @param in The InputStream to read from
   */
	public Binary(InputStream in)
	{
    this.in = in;
	}

  /**
   * Initialize this Binary with an OutputStream.
   * Only the write... methods will work.
   * @param out The OutputStream to read from
   */
  public Binary(OutputStream out)
	{
    this.out = out;
	}

  /**
   * Read a byte from the InputStream.
   * @return The byte read.
   * @throws IOException if a read error occurs
   * @see InputStream#read()
   */
	public int read()
	throws IOException
	{
    if (in == null)
      throw new IllegalStateException("This binary is in write mode! Can't execute read()!");

//    bytesRead++; // @nozkiller: this line is wrong, if the end of the stream has been reached!
//    return in.read();
    int res = in.read();
    if (res >= 0)
    	++bytesRead;

    return res;
	}

	public long skip(long n)
	throws IOException
	{
		if (in == null)
      throw new IllegalStateException("This binary is in write mode! Can't execute skip()!");

//		bytesRead += n; // @nozkiller: This line is wrong, if n bytes cannot be skipped. Marco.
//		return in.skip(n);

		// @nozkiller: this should be correct - at least I hope so ;-) Marco.
		long res = in.skip(n);
		if (res > 0)
			bytesRead += res;

		return res;
	}

	/**
	 * Close the stream attached to this Binary.
	 * @throws IOException if an error occurs.
	 * @see InputStream#close()
	 * @see OutputStream#close()
	 */
  public void close()
  throws IOException
  {
		if (in != null)
    	in.close();
		
		if (out != null)
    	out.close();
  }

  /**
   * Read the given number of bytes from the InputStream
   * and reverse their order to match the Java order.
   * @param byteCount How many bytes to use. Must be between
   * 		1 and 8.
   * @return The number in Java byte-order
   * @throws IOException
   */
  private long readReversedBytes( int byteCount )
	throws IOException
	{
		if(byteCount > 8)
			throw new IllegalArgumentException("nByteCount > 8!!!");
		
		if(byteCount < 1)
			throw new IllegalArgumentException("nByteCount < 1!!!");
		
		// if( nByteCount > 8 ) nByteCount = 8;
		// if( nByteCount < 1 ) nByteCount = 1;
		long bBytes[] = new long[byteCount];
		for( int i=0; i<byteCount; i++ )
    {
			bBytes[i] = read();
    }
		long nResult = 0;
		for( int i=byteCount-1; i>0; i-- )
    {
			nResult = nResult | ((0xff & bBytes[i]) << (8*i));
		}
		nResult = nResult | bBytes[0];
		return( nResult );
	}

  public byte readByte()
  throws IOException
  {
  	int res = read();
  	if (res < 0)
  		throw new EOFException("Cannot read one more byte!");

    return (byte) res;
  }

  public int readUnsignedByte()
  throws IOException
  {
		// TO DO: is this correct?? Does the implicit cast produce an unsigned number??
  	// Why shouldn't this work? read() calls the InputStream.read() method which ALWAYS returns a value from 0 to 255 as documented
  	// in the JavaDoc of java.io.InputStream. Marco.
 	int res = read();
  	if (res < 0)
  		throw new EOFException("Cannot read one more byte!");

  	return res;
  }

	/**
	 * Read a 2-bytes signed number as reversed binary
	 * from the input stream
	 * @return the read value as <code>short</code>
	 * @throws IOException if a read error occurs
	 */
	public short readReversedShort()
	throws IOException
	{
		return( (short) readReversedBytes( 2 ) );
	}

	/**
	 * Read a 2-bytes unsigned number as reversed binary
	 * from the input stream
	 * @return the read value as <code>int</code> >= 0
	 *    because	a Java <code>short</code> is to small.
	 * @throws IOException if a read error occurs
	 */
	public int readReversedUnsignedShort()
	throws IOException
	{
		return( (int) readReversedBytes( 2 ) );
	}

	/**
	 * Read a 4-bytes signed number as reversed binary
	 * from the input stream
	 * @return the read value as <code>int</code>
	 * @throws IOException if a read error occurs
	 */
	public int readReversedInt()
	throws IOException
	{
		return( (int) readReversedBytes( 4 ) );
	}

	/**
	 * Read a 4-bytes unsigned number as reversed binary
	 * from the input stream
	 * @return the read value as <code>long</code> >= 0
	 *    because	a Java <code>int</code> is to small.
	 * @throws IOException if a read error occurs
	 */
	public long readReversedUnsignedInt()
	throws IOException
	{
		long nTest = readReversedBytes( 4 );
		return( nTest );
	}

	/**
	 * This method reads a zero-terminated string from the InputStream
	 * associated to this binary. The termination 0 is read from the
	 * Stream, but not part of the resulting String.
	 */
	public String readString()
	throws IOException
	{
		int c;
		StringBuffer sRead = new StringBuffer();
		c = read();
		while( c != 0 ) {
			sRead.append( (char) c );
			c = read();
		}
		return( sRead.toString() );
	}

	/**
	 * This method reads the given number of bytes from the
	 * InputStream associated to this Binary. It does not stop
	 * on 0-bytes.
	 */
	public String readString( int _length )
	throws IOException
	{
		int c;
		StringBuffer sRead = new StringBuffer();
		for( int i=0; i<_length; i++ ) {
			c = read();
			sRead.append( (char) c );
		}
		return( sRead.toString() );
	}

	/**
	 * Read a decimal number as string from the
	 * input stream.
	 * @return The number as <code>int</code>.
	 * @throws NumberFormatException if no number can be extracted
	 * @throws IOException if a read error occurs
	 */
	public int readNumber()
	throws NumberFormatException, IOException
	{
		StringBuffer sRead = new StringBuffer();
    if (in.markSupported())
      in.mark(256);

    boolean doReset = true;
    try {
      int c = read();
      while( c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
             c == '5' || c == '6' || c == '7' || c == '8' || c == '9' ) {
        sRead.append( (char) c );
        c = read();
      }

      doReset = false;
    } finally {
      if (doReset && in.markSupported())
        in.reset();
    }
    return( Integer.parseInt( sRead.toString() ) );
  }

	/**
	 * Read an 8-bytes signed number as reversed binary
	 * from the input stream
	 * @return the read value as <code>long</code>
	 * @throws IOException if a read error occurs
	 */
	public long readReversedLong()
	throws IOException
	{
		long nTest = readReversedBytes( 8 );
		return( nTest );
	}

	private void writeReversedBytes(byte[] bytes)
	throws IOException
	{
		byte[] rBytes = new byte[bytes.length];
		int m = 0;
		for (int i = bytes.length - 1; i >= 0; --i)
			rBytes[m++] = bytes[i];

		out.write(rBytes);
	}
	
	public void write(byte[] bytes)
	throws IOException
	{
		out.write(bytes);
	}

	public void write(int b)
	throws IOException
	{
		out.write(b);
	}
	
	public byte[] getByteArray(long v, int length)
	{
		byte b[] = new byte[length];
		int i, shift;
		for(i = 0, shift = (length - 1) * 8; i < length; i++, shift -= 8)
			b[i] = (byte)(v >> shift);
//			b[i] = (byte)(0xFF & (v >> shift));

		return b;
	}
	
	public void writeReversedUnsignedByte(short val)
	throws IOException
	{
		writeReversedBytes(getByteArray(val, 1));
	}
	
	public void writeReversedUnsignedShort(int val)
	throws IOException
	{
		writeReversedBytes(getByteArray(val, 2));
	}
	
	public void writeReversedShort(short val)
	throws IOException
	{
		writeReversedBytes(getByteArray(val, 2));
	}
	
	public void writeReversedUnsignedInt(long val)
	throws IOException
	{
		writeReversedBytes(getByteArray(val, 4));
	}
	
	public void writeReversedInt(int val)
	throws IOException
	{
		writeReversedBytes(getByteArray(val, 4));
	}
	
	/**
	 * This method writes a zero terminated String into the OutputStream.
	 * @param s The string to write
	 * @throws IOException
	 */
	public void writeString(String s)
	throws IOException
	{
		write(s.getBytes(CHARSET));
		write((byte)0); // ASCII-Z
	}
	
	public static final String CHARSET = "ISO-8859-1";
	
	/**
	 * This method writes the given number of bytes to the OutputStream.
	 * If the String does not exactly match the defined length, an IllegalArgumentException is thrown.
	 * @param s
	 * @param length
	 * @throws IOException
	 */
	public void writeString(String s, int length)
	throws IOException
	{
		if (s.length() != length)
			throw new IllegalArgumentException("Param length does not match String.length()! s=\""+s+"\" length=\""+length+"\"");
		
		write(s.getBytes(CHARSET));
	}
}
