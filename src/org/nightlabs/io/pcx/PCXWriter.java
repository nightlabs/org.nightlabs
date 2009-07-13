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
import java.io.OutputStream;

import org.nightlabs.util.Binary;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @author marco at nightlabs dot de
 */
public class PCXWriter
{
	@SuppressWarnings("deprecation")
	public void write(OutputStream out, BufferedImage image)
	throws IOException
	{
		Binary binary = new Binary(out);
		PCXHeader pcxHeader = new PCXHeader(image);
		pcxHeader.write(binary);
		int bytesPerLine = pcxHeader.getBytesPerLine();
		int height = pcxHeader.getHeight();
		WritableRaster raster = image.getRaster();

		for (int row = 0; row < height; ++row) {
			int col = 0;  // col is not the x-coordinate, but the byte number! one byte encodes 8 x-coordinates in black-white pcx files
			while (col < bytesPerLine) {
				int byteValue = get8BlackWhitePixels(col, row, raster);
				int byteCount = 1;
				++col;
				while (col < bytesPerLine) {
					int nextByteValue = get8BlackWhitePixels(col, row, raster);
					if (byteValue != nextByteValue)
						break;

					++byteCount;
					++col;
					if (byteCount == 0x3F)
						break;
				}

				if (byteCount > 1 || byteValue > 0x3f) {
					int b = byteCount | 0xC0;
					binary.write(b);
					binary.write(byteValue);
				}
				else
					binary.write(byteValue);
			}
		}
	}

	protected static int get8BlackWhitePixels(int col, int row, WritableRaster raster)
	{
		int xmax = raster.getBounds().width - 1;
		int res = 0;
		int[] iArray = new int[1];
		int base = col * 8;
		for (int i = 0; i < 8; ++i) {
			if (base + i > xmax)
				iArray[0] = 1;
			else
				raster.getPixel(base + i, row, iArray);

			res = res | (iArray[0] << (7 - i));
		}

		return res;
	}

}
