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
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.imageio.stream.IIOByteBuffer;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class ImageOutputStream
extends OutputStream
implements javax.imageio.stream.ImageOutputStream
{
	
	private javax.imageio.stream.ImageOutputStream imageOutputStream = null;
	public ImageOutputStream(javax.imageio.stream.ImageOutputStream imageOutputStream)
	{
		super();
		this.imageOutputStream = imageOutputStream;
	}
	
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#close()
	 */
	@Override
	public void close() throws IOException {
		imageOutputStream.close();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		imageOutputStream.flush();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#flushBefore(long)
	 */
	public void flushBefore(long pos) throws IOException {
		imageOutputStream.flushBefore(pos);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getBitOffset()
	 */
	public int getBitOffset() throws IOException {
		return imageOutputStream.getBitOffset();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getByteOrder()
	 */
	public ByteOrder getByteOrder() {
		return imageOutputStream.getByteOrder();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getFlushedPosition()
	 */
	public long getFlushedPosition() {
		return imageOutputStream.getFlushedPosition();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#getStreamPosition()
	 */
	public long getStreamPosition() throws IOException {
		return imageOutputStream.getStreamPosition();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCached()
	 */
	public boolean isCached() {
		return imageOutputStream.isCached();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCachedFile()
	 */
	public boolean isCachedFile() {
		return imageOutputStream.isCachedFile();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#isCachedMemory()
	 */
	public boolean isCachedMemory() {
		return imageOutputStream.isCachedMemory();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#length()
	 */
	public long length() throws IOException {
		return imageOutputStream.length();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#mark()
	 */
	public void mark() {
		imageOutputStream.mark();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#read()
	 */
	public int read() throws IOException {
		return imageOutputStream.read();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return imageOutputStream.read(b, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return imageOutputStream.read(b);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBit()
	 */
	public int readBit() throws IOException {
		return imageOutputStream.readBit();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBits(int)
	 */
	public long readBits(int numBits) throws IOException {
		return imageOutputStream.readBits(numBits);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBoolean()
	 */
	public boolean readBoolean() throws IOException {
		return imageOutputStream.readBoolean();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readByte()
	 */
	public byte readByte() throws IOException {
		return imageOutputStream.readByte();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readBytes(javax.imageio.stream.IIOByteBuffer, int)
	 */
	public void readBytes(IIOByteBuffer buf, int len) throws IOException {
		imageOutputStream.readBytes(buf, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readChar()
	 */
	public char readChar() throws IOException {
		return imageOutputStream.readChar();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readDouble()
	 */
	public double readDouble() throws IOException {
		return imageOutputStream.readDouble();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFloat()
	 */
	public float readFloat() throws IOException {
		return imageOutputStream.readFloat();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(byte[], int, int)
	 */
	public void readFully(byte[] b, int off, int len) throws IOException {
		imageOutputStream.readFully(b, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(byte[])
	 */
	public void readFully(byte[] b) throws IOException {
		imageOutputStream.readFully(b);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(char[], int, int)
	 */
	public void readFully(char[] c, int off, int len) throws IOException {
		imageOutputStream.readFully(c, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(double[], int, int)
	 */
	public void readFully(double[] d, int off, int len) throws IOException {
		imageOutputStream.readFully(d, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(float[], int, int)
	 */
	public void readFully(float[] f, int off, int len) throws IOException {
		imageOutputStream.readFully(f, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(int[], int, int)
	 */
	public void readFully(int[] i, int off, int len) throws IOException {
		imageOutputStream.readFully(i, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(long[], int, int)
	 */
	public void readFully(long[] l, int off, int len) throws IOException {
		imageOutputStream.readFully(l, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readFully(short[], int, int)
	 */
	public void readFully(short[] s, int off, int len) throws IOException {
		imageOutputStream.readFully(s, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readInt()
	 */
	public int readInt() throws IOException {
		return imageOutputStream.readInt();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readLine()
	 */
	public String readLine() throws IOException {
		return imageOutputStream.readLine();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readLong()
	 */
	public long readLong() throws IOException {
		return imageOutputStream.readLong();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readShort()
	 */
	public short readShort() throws IOException {
		return imageOutputStream.readShort();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedByte()
	 */
	public int readUnsignedByte() throws IOException {
		return imageOutputStream.readUnsignedByte();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedInt()
	 */
	public long readUnsignedInt() throws IOException {
		return imageOutputStream.readUnsignedInt();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUnsignedShort()
	 */
	public int readUnsignedShort() throws IOException {
		return imageOutputStream.readUnsignedShort();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#readUTF()
	 */
	public String readUTF() throws IOException {
		return imageOutputStream.readUTF();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#reset()
	 */
	public void reset() throws IOException {
		imageOutputStream.reset();
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#seek(long)
	 */
	public void seek(long pos) throws IOException {
		imageOutputStream.seek(pos);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#setBitOffset(int)
	 */
	public void setBitOffset(int bitOffset) throws IOException {
		imageOutputStream.setBitOffset(bitOffset);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#setByteOrder(java.nio.ByteOrder)
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		imageOutputStream.setByteOrder(byteOrder);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#skipBytes(int)
	 */
	public int skipBytes(int n) throws IOException {
		return imageOutputStream.skipBytes(n);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageInputStream#skipBytes(long)
	 */
	public long skipBytes(long n) throws IOException {
		return imageOutputStream.skipBytes(n);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		imageOutputStream.write(b, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		imageOutputStream.write(b);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		imageOutputStream.write(b);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeBit(int)
	 */
	public void writeBit(int bit) throws IOException {
		imageOutputStream.writeBit(bit);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeBits(long, int)
	 */
	public void writeBits(long bits, int numBits) throws IOException {
		imageOutputStream.writeBits(bits, numBits);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeBoolean(boolean)
	 */
	public void writeBoolean(boolean v) throws IOException {
		imageOutputStream.writeBoolean(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeByte(int)
	 */
	public void writeByte(int v) throws IOException {
		imageOutputStream.writeByte(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeBytes(java.lang.String)
	 */
	public void writeBytes(String s) throws IOException {
		imageOutputStream.writeBytes(s);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeChar(int)
	 */
	public void writeChar(int v) throws IOException {
		imageOutputStream.writeChar(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeChars(char[], int, int)
	 */
	public void writeChars(char[] c, int off, int len) throws IOException {
		imageOutputStream.writeChars(c, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeChars(java.lang.String)
	 */
	public void writeChars(String s) throws IOException {
		imageOutputStream.writeChars(s);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeDouble(double)
	 */
	public void writeDouble(double v) throws IOException {
		imageOutputStream.writeDouble(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeDoubles(double[], int, int)
	 */
	public void writeDoubles(double[] d, int off, int len) throws IOException {
		imageOutputStream.writeDoubles(d, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeFloat(float)
	 */
	public void writeFloat(float v) throws IOException {
		imageOutputStream.writeFloat(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeFloats(float[], int, int)
	 */
	public void writeFloats(float[] f, int off, int len) throws IOException {
		imageOutputStream.writeFloats(f, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeInt(int)
	 */
	public void writeInt(int v) throws IOException {
		imageOutputStream.writeInt(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeInts(int[], int, int)
	 */
	public void writeInts(int[] i, int off, int len) throws IOException {
		imageOutputStream.writeInts(i, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeLong(long)
	 */
	public void writeLong(long v) throws IOException {
		imageOutputStream.writeLong(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeLongs(long[], int, int)
	 */
	public void writeLongs(long[] l, int off, int len) throws IOException {
		imageOutputStream.writeLongs(l, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeShort(int)
	 */
	public void writeShort(int v) throws IOException {
		imageOutputStream.writeShort(v);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeShorts(short[], int, int)
	 */
	public void writeShorts(short[] s, int off, int len) throws IOException {
		imageOutputStream.writeShorts(s, off, len);
	}
	/* (non-Javadoc)
	 * @see javax.imageio.stream.ImageOutputStream#writeUTF(java.lang.String)
	 */
	public void writeUTF(String s) throws IOException {
		imageOutputStream.writeUTF(s);
	}
}
