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
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

import org.nightlabs.util.Binary;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @author marco at nightlabs dot de
 */
public class PCXReader
{
	private PCXHeader pcxHeader = null;

	@SuppressWarnings("deprecation")
	public BufferedImage read(InputStream in)
	throws IOException
	{
		Binary binary = new Binary(in);
		pcxHeader = new PCXHeader(binary);
		BufferedImage image = new BufferedImage(
				pcxHeader.getWidth(),
				pcxHeader.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);

		int height = pcxHeader.getHeight();
		int bytesPerLine = pcxHeader.getBytesPerLine();

		WritableRaster raster = image.getRaster();

		for (int row = 0; row < height; ++row) {
			int col = 0; // col is not the x-coordinate, but the byte number! one byte encodes 8 x-coordinates in black-white pcx files
			while (col < bytesPerLine) {
				int b = binary.readUnsignedByte();
				if (b >= 0xC0) { // lauflaenge
					int l = b & 0x3F; // number of bytes that are written multiple times
					b = binary.readUnsignedByte();
					for (int i = 0; i < l; ++i) {
						if (col >= bytesPerLine)
							throw new PCXFormatException("Malformed PCX-flow: Too much data in one row!");

						set8BlackWhitePixels(col++, row, raster, b);
					}
				}
				else { // value
					set8BlackWhitePixels(col++, row, raster, b);
				}
			}
		}
		return image;
	}

	protected static void set8BlackWhitePixels(int col, int row, WritableRaster raster, int byteValue)
	{
		int xmax = raster.getBounds().width - 1;
		int[] iArray = new int[1];
		int base = col * 8;
		for (int i = 0; i < 8; ++i) {
			if (base + i > xmax)
				break;

			iArray[0] = (byteValue >> (7 - i)) & 1;
			raster.setPixel(base + i, row, iArray);
		}
	}

//	public PCXHeader getHeader()
//	{
//		if (pcxHeader == null)
//			throw new IllegalStateException("read(...) not yet called!");
//
//		return pcxHeader;
//	}
}
