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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PCXUtil
{
	/**
	 * <p>
	 * This method rotates a given PCX encoded image by 90 degree (in mathematically positive direction)
	 * and writes the new encoded PCX data to the output stream.
	 * </p>
	 * <p>
	 * Currently, this method supports only 1-bit PCX files!
	 * </p>
	 *
	 * @param in read data from this input
	 * @param out write data to this output
	 * @throws IOException If read or write failed. Note, that a more specific {@link PCXFormatException}
	 *		might be thrown, if the input is not correctly encoded.
	 */
	public static void rotate90Degrees(InputStream in, OutputStream out)
	throws IOException
	{
		PCXReader reader = new PCXReader();
		BufferedImage image = reader.read(in);
		if (image.getType() != BufferedImage.TYPE_BYTE_BINARY)
			throw new UnsupportedOperationException("image.getType() != BufferedImage.TYPE_BYTE_BINARY");

		WritableRaster srcRaster = image.getRaster();
//		ImageIO.write(image, "bmp", new File("/tmp/out.bmp"));

		BufferedImage rimg = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster destRaster = rimg.getRaster();
		int maxY = image.getMinY() + image.getHeight() - 1;
		int maxX = image.getMinX() + image.getWidth() - 1;
		int[] iArray = new int[1];
		for (int y = image.getMinY(); y <= maxY; ++y) {
			for (int x = image.getMinX(); x <= maxX; ++x) {
				srcRaster.getPixel(x, y, iArray);
				destRaster.setPixel(y, maxX - x, iArray);
			}
		}

		PCXWriter writer = new PCXWriter();
		writer.write(out, rimg);
	}

	public static void main(String[] args)
	{
		try {
			FileInputStream fin = new FileInputStream("/tmp/ipanema-bw.pcx");
//			FileInputStream fin = new FileInputStream("/tmp/nightlabs-bw.pcx");
			FileOutputStream fout = new FileOutputStream("/tmp/out.pcx");
			rotate90Degrees(fin, fout);
			fin.close();
			fout.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
