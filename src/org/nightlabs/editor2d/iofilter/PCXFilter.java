/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.editor2d.iofilter;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import org.nightlabs.io.pcx.PCXImageReaderSPI;
import org.nightlabs.io.pcx.PCXImageWriterSPI;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PCXFilter
extends AbstractImageFilter
{
	public PCXFilter()
	{
		String fileExtension = initFileExtension();
		if (!ImageIO.getImageReadersByFormatName(fileExtension).hasNext()) {
			IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageReaderSPI());
		}
		if (!ImageIO.getImageWritersByFormatName(fileExtension).hasNext()) {
			IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageWriterSPI());
		}
	}

	@Override
	public void write(Object o, OutputStream out)
			throws IOException
	{
		// TODO set the render-mode and ensure that a 1-bit-BufferedImage is created (because the correct render-mode will
		// cause the best transformation method to be used - the one the user defined in the image-import). Our PCX-filter
		// supports only 1-bit (and we don't need colours since we could use PNG if we wanted colours).
		// PCX is a legacy format and only supported because ticket-printers commonly use 1-bit-PCX logos.
		super.write(o, out);
	}

	@Override
	protected String initDescription() {
		return "PCX Filter";
	}

	@Override
	protected String initFileExtension() {
		return "pcx";
	}

	@Override
	protected String initName() {
		return "PCX Filter";
	}

}
