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

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Level;
import org.nightlabs.util.Binary;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @author marco at nightlabs dot de
 */
public class PCXHeader
{
	public static byte MANUFACTURER_PC_PAINTBRUSH	 = 10;
	public static byte VERSION_2_5						= 0;
	public static byte VERSION_2_8p					 = 2;
	public static byte VERSION_2_8						= 3;
	public static byte VERSION_3_0p					 = 5;
	public static byte ENCODING_RLE		= 1;

	/**
	 * unsigned byte (we use short only because of java's signed bytes)
	 */
	private short manufacturer; // always 10 for ZSoft-PCX
	/**
	 * unsigned byte (we use short only because of java's signed bytes)
	 */
	private short version;
	/**
	 * unsigned byte (we use short only because of java's signed bytes)
	 */
	private short encoding;
	/**
	 * <p>
	 * unsigned byte (we use short only because of java's signed bytes)
	 * </p>
	 * <p>
	 * The number of bits / pixel / plane
	 * </p>
	 */
	private short depth;

	/**
	 * calculated from xmin and xmax
	 */
	private int width;
	/**
	 * calculated from ymin and ymax
	 */
	private int height;

	/**
	 * unsigned short (2 bytes)
	 */
	private int xmin;
	/**
	 * unsigned short (2 bytes)
	 */
	private int ymin;
	/**
	 * unsigned short (2 bytes)
	 */
	private int xmax;
	/**
	 * unsigned short (2 bytes)
	 */
	private int ymax;

	/**
	 * dpi horizontal - unsigned short (2 bytes)
	 */
	private int hres;
	/**
	 * dpi vertical - unsigned short (2 bytes)
	 */
	private int vres;

	public static class RGBTriple
	{
		private short red = 0;
		private short green = 0;
		private short blue = 0;

		public RGBTriple() {}

		public RGBTriple(int red, int green, int blue)
		{
			this((short)red, (short)green, (short)blue);
		}

		public RGBTriple(short red, short green, short blue)
		{
			setRed(red);
			setGreen(green);
			setBlue(blue);
		}

		public short getBlue()
		{
			return blue;
		}
		public void setBlue(short blue)
		{
			if (blue < 0 || blue > 255)
				throw new IllegalArgumentException("value " + blue + " out of range 0...255!");

			this.blue = blue;
		}
		public short getGreen()
		{
			return green;
		}
		public void setGreen(short green)
		{
			if (green < 0 || green > 255)
				throw new IllegalArgumentException("value " + green + " out of range 0...255!");

			this.green = green;
		}
		public short getRed()
		{
			return red;
		}
		public void setRed(short red)
		{
			if (red < 0 || red > 255)
				throw new IllegalArgumentException("value " + red + " out of range 0...255!");

			this.red = red;
		}

	}

	private RGBTriple[] palette;

	/**
	 * <p>
	 * unsigned byte (we use short only because of java's signed bytes)
	 * </p>
	 * <p>
	 * only read in order to be able to write the same value later
	 * </p>
	 */
	private short reserved;

	/**
	 * unsigned byte (we use short only because of java's signed bytes)
	 */
	private short colorPlanes;

	/**
	 * Number of bytes per line (UNencoded)
	 */
	private int bytesPerLine;

	/**
	 * 1 = black/white or color; 2 = grayscale (probably without palette?!)
	 */
	private int paletteInfo;

//	private PCXColorMap pcxColorMap;

	public PCXHeader(BufferedImage image)
	throws IOException
	{
		if (image.getType() != BufferedImage.TYPE_BYTE_BINARY)
			throw new IllegalArgumentException("image.getType() != BufferedImage.TYPE_BYTE_BINARY: We currently support only 1-bit-PCX!");

		manufacturer = MANUFACTURER_PC_PAINTBRUSH;
		version = VERSION_3_0p;
		encoding = ENCODING_RLE;
		depth = 1;
		xmin = 0;
		ymin = 0;
		xmax = (short) (image.getWidth() - 1);
		ymax = (short) (image.getHeight() - 1);
		width = image.getWidth();
		height = image.getHeight();
//		hres = width;
//		vres = height;
		hres = 72; // TODO what?
		vres = 72; // TODO what?

		palette = new RGBTriple[16];
		for (int i = 0; i < palette.length; i++) {
			if (i == 1)
				palette[i] = new RGBTriple(255, 255, 255);
			else
				palette[i] = new RGBTriple(0, 0, 0);
		}

		reserved = 1;
		colorPlanes = 1;

		if (width % 8 == 0)
			bytesPerLine = width / 8;
		else
			bytesPerLine = width / 8 + 1;

		paletteInfo = 0; // TODO what?
	}

	public void logValues(Level level)
	{
		logString("Manufactor = "+getManufacturer(), level);
		logString("Version = "+getVersionString(), level);
		logString("Encoding = "+getEncoding(), level);
		logString("Bits per Pixel = "+getDepth(), level);
		logString("X Min = "+getXmin(), level);
		logString("X Max = "+getXmax(), level);
		logString("Y Min = "+getYmin(), level);
		logString("Y Max = "+getYmax(), level);
		logString("Horizontal Resolution DPI = "+getHres(), level);
		logString("Vertical Resolution DPI = "+getVres(), level);
		logString("Width = "+getWidth(), level);
		logString("Height = "+getHeight(), level);
	}

	protected void logString(String s, Level level) {
		System.out.println("PCXHeader: "+s);
	}

	@SuppressWarnings("deprecation")
	public void write(Binary binary)
	throws IOException
	{
		binary.write(manufacturer);
		binary.write(version);
		binary.write(encoding);
		binary.write(depth);

		binary.writeReversedUnsignedShort(xmin);
		binary.writeReversedUnsignedShort(ymin);
		binary.writeReversedUnsignedShort(xmax);
		binary.writeReversedUnsignedShort(ymax);

		binary.writeReversedUnsignedShort(hres);
		binary.writeReversedUnsignedShort(vres);

		for (RGBTriple rgb : palette) {
			binary.write(rgb.getRed());
			binary.write(rgb.getGreen());
			binary.write(rgb.getBlue());
		}

		binary.write(reserved);

		binary.write(colorPlanes);
		binary.writeReversedUnsignedShort(bytesPerLine);
		binary.writeReversedUnsignedShort(paletteInfo);

		for(int i = 0; i < 58; ++i)
			binary.write(0);
	}

	@SuppressWarnings("deprecation")
	public PCXHeader(Binary binary)
	throws IOException
	{
		manufacturer = (short)binary.readUnsignedByte();
//		if (manufacturer != MANUFACTURER_PC_PAINTBRUSH)
//			throw new PCXFormatException("manufacturer " + manufacturer + " not supported!");

		version = (short)binary.readUnsignedByte();
		if (version != VERSION_2_5 && version != VERSION_2_8 && version != VERSION_2_8p && version != VERSION_3_0p)
			throw new PCXFormatException("version " + version + " not supported!");

		encoding = (short)binary.readUnsignedByte();
		if (encoding != ENCODING_RLE)
			throw new PCXFormatException("encoding " + encoding + " not supported!");

		depth = (short)binary.readUnsignedByte();

		xmin = binary.readReversedUnsignedShort();
		ymin = binary.readReversedUnsignedShort();
		xmax = binary.readReversedUnsignedShort();
		ymax = binary.readReversedUnsignedShort();

		width = (xmax - xmin + 1);
		height = (ymax - ymin + 1);

		hres = binary.readReversedUnsignedShort();
		vres = binary.readReversedUnsignedShort();

//		pcxColorMap = new PCXColorMap(dis, fileLength, version);
		palette = new RGBTriple[16];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = new RGBTriple(
					binary.readUnsignedByte(),
					binary.readUnsignedByte(),
					binary.readUnsignedByte());
		}

		reserved = (short) binary.readUnsignedByte();

		colorPlanes = (short) binary.readUnsignedByte();
		bytesPerLine = binary.readReversedUnsignedShort();
		paletteInfo = binary.readReversedUnsignedShort();

		if (depth * colorPlanes != 1)
			throw new PCXFormatException("File is not a 1-bit PCX file! Currently, other (colored/grayscale) PCX files are not yet supported! depth="+depth+" colorPlanes="+colorPlanes);

		// skip Filler (blank to fill out 128 byte header)
		binary.skip(58);
	}

//	public ColorModel getColorModel()
//	{
//		return pcxColorMap.getColorModel();
//	}

	public String getVersionString()
	{
		if (version == VERSION_2_5)
			return "2.5";
		if (version == VERSION_2_8)
			return "2.8";
		if (version == VERSION_2_8p)
			return "2.8p";
		if (version == VERSION_3_0p)
			return "3.0p";
		else
			return "Unknown Version";
	}

	public int getBytesPerLine()
	{
		return bytesPerLine;
	}

	protected void setBytesPerLine(int bytesPerLine)
	{
		this.bytesPerLine = bytesPerLine;
	}

	public short getColorPlanes()
	{
		return colorPlanes;
	}

	protected void setColorPlanes(short colorPlanes)
	{
		this.colorPlanes = colorPlanes;
	}

	public short getDepth()
	{
		return depth;
	}

	protected void setDepth(short depth)
	{
		this.depth = depth;
	}

	public short getEncoding()
	{
		return encoding;
	}

	protected void setEncoding(short encoding)
	{
		this.encoding = encoding;
	}

	public int getHeight()
	{
		return height;
	}

	protected void setHeight(int height)
	{
		this.height = height;
	}

	public int getHres()
	{
		return hres;
	}

	protected void setHres(int hres)
	{
		this.hres = hres;
	}

	public short getManufacturer()
	{
		return manufacturer;
	}

	protected void setManufacturer(short manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public int getPaletteInfo()
	{
		return paletteInfo;
	}

	protected void setPaletteInfo(int paletteInfo)
	{
		this.paletteInfo = paletteInfo;
	}

	public short getReserved()
	{
		return reserved;
	}

	protected void setReserved(short reserved)
	{
		this.reserved = reserved;
	}

	public short getVersion()
	{
		return version;
	}

	protected void setVersion(short version)
	{
		this.version = version;
	}

	public int getVres()
	{
		return vres;
	}

	protected void setVres(int vres)
	{
		this.vres = vres;
	}

	public int getWidth()
	{
		return width;
	}

	protected void setWidth(int width)
	{
		this.width = width;
	}

	public int getXmax()
	{
		return xmax;
	}

	protected void setXmax(int xmax)
	{
		this.xmax = xmax;
	}

	public int getXmin()
	{
		return xmin;
	}

	protected void setXmin(int xmin)
	{
		this.xmin = xmin;
	}

	public int getYmax()
	{
		return ymax;
	}

	protected void setYmax(int ymax)
	{
		this.ymax = ymax;
	}

	public int getYmin()
	{
		return ymin;
	}

	protected void setYmin(int ymin)
	{
		this.ymin = ymin;
	}


}
