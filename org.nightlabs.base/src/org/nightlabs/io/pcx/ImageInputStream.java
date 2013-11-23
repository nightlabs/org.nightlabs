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
package org.nightlabs.io.pcx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.imageio.stream.IIOByteBuffer;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class ImageInputStream
extends InputStream
implements javax.imageio.stream.ImageInputStream
{

	private javax.imageio.stream.ImageInputStream imageInpuStream = null;
	public ImageInputStream(javax.imageio.stream.ImageInputStream imageInputStream)
	{
		super();
		this.imageInpuStream = imageInputStream;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return imageInpuStream.read();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#setByteOrder(java.nio.ByteOrder)
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		imageInpuStream.setByteOrder(byteOrder);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getByteOrder()
	 */
	public ByteOrder getByteOrder() {
		return imageInpuStream.getByteOrder();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		imageInpuStream.close();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return imageInpuStream.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return imageInpuStream.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return imageInpuStream.read(b, off, len);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return imageInpuStream.read(b);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		imageInpuStream.reset();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return imageInpuStream.toString();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#flush()
	 */
	public void flush() throws IOException {
		imageInpuStream.flush();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#flushBefore(long)
	 */
	public void flushBefore(long pos) throws IOException {
		imageInpuStream.flushBefore(pos);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getBitOffset()
	 */
	public int getBitOffset() throws IOException {
		return imageInpuStream.getBitOffset();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getFlushedPosition()
	 */
	public long getFlushedPosition() {
		return imageInpuStream.getFlushedPosition();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getStreamPosition()
	 */
	public long getStreamPosition() throws IOException {
		return imageInpuStream.getStreamPosition();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCached()
	 */
	public boolean isCached() {
		return imageInpuStream.isCached();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCachedFile()
	 */
	public boolean isCachedFile() {
		return imageInpuStream.isCachedFile();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCachedMemory()
	 */
	public boolean isCachedMemory() {
		return imageInpuStream.isCachedMemory();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#length()
	 */
	public long length() throws IOException {
		return imageInpuStream.length();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#mark()
	 */
	public void mark() {
		imageInpuStream.mark();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBit()
	 */
	public int readBit() throws IOException {
		return imageInpuStream.readBit();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBits(int)
	 */
	public long readBits(int numBits) throws IOException {
		return imageInpuStream.readBits(numBits);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBoolean()
	 */
	public boolean readBoolean() throws IOException {
		return imageInpuStream.readBoolean();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readByte()
	 */
	public byte readByte() throws IOException {
		return imageInpuStream.readByte();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBytes(javax.imageio.stream.IIOByteBuffer, int)
	 */
	public void readBytes(IIOByteBuffer buf, int len) throws IOException {
		imageInpuStream.readBytes(buf, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readChar()
	 */
	public char readChar() throws IOException {
		return imageInpuStream.readChar();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readDouble()
	 */
	public double readDouble() throws IOException {
		return imageInpuStream.readDouble();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFloat()
	 */
	public float readFloat() throws IOException {
		return imageInpuStream.readFloat();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(byte[], int, int)
	 */
	public void readFully(byte[] b, int off, int len) throws IOException {
		imageInpuStream.readFully(b, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(byte[])
	 */
	public void readFully(byte[] b) throws IOException {
		imageInpuStream.readFully(b);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(char[], int, int)
	 */
	public void readFully(char[] c, int off, int len) throws IOException {
		imageInpuStream.readFully(c, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(double[], int, int)
	 */
	public void readFully(double[] d, int off, int len) throws IOException {
		imageInpuStream.readFully(d, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(float[], int, int)
	 */
	public void readFully(float[] f, int off, int len) throws IOException {
		imageInpuStream.readFully(f, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(int[], int, int)
	 */
	public void readFully(int[] i, int off, int len) throws IOException {
		imageInpuStream.readFully(i, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(long[], int, int)
	 */
	public void readFully(long[] l, int off, int len) throws IOException {
		imageInpuStream.readFully(l, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(short[], int, int)
	 */
	public void readFully(short[] s, int off, int len) throws IOException {
		imageInpuStream.readFully(s, off, len);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readInt()
	 */
	public int readInt() throws IOException {
		return imageInpuStream.readInt();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readLine()
	 */
	public String readLine() throws IOException {
		return imageInpuStream.readLine();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readLong()
	 */
	public long readLong() throws IOException {
		return imageInpuStream.readLong();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readShort()
	 */
	public short readShort() throws IOException {
		return imageInpuStream.readShort();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedByte()
	 */
	public int readUnsignedByte() throws IOException {
		return imageInpuStream.readUnsignedByte();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedInt()
	 */
	public long readUnsignedInt() throws IOException {
		return imageInpuStream.readUnsignedInt();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedShort()
	 */
	public int readUnsignedShort() throws IOException {
		return imageInpuStream.readUnsignedShort();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUTF()
	 */
	public String readUTF() throws IOException {
		return imageInpuStream.readUTF();
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#seek(long)
	 */
	public void seek(long pos) throws IOException {
		imageInpuStream.seek(pos);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#setBitOffset(int)
	 */
	public void setBitOffset(int bitOffset) throws IOException {
		imageInpuStream.setBitOffset(bitOffset);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#skipBytes(int)
	 */
	public int skipBytes(int n) throws IOException {
		return imageInpuStream.skipBytes(n);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#skipBytes(long)
	 */
	public long skipBytes(long n) throws IOException {
		return imageInpuStream.skipBytes(n);
	}
}
