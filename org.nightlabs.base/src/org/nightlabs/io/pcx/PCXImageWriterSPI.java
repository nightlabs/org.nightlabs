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
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/**
 * PCX writer Service provider interface.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class PCXImageWriterSPI
extends ImageWriterSpi
{

	public PCXImageWriterSPI()
	{
		super();
		init();
	}

	@SuppressWarnings("unchecked")
	public PCXImageWriterSPI(String vendorName, String version, String[] names,
			String[] suffixes, String[] MIMETypes, String writerClassName,
			Class[] outputTypes, String[] readerSpiNames,
			boolean supportsStandardStreamMetadataFormat,
			String nativeStreamMetadataFormatName,
			String nativeStreamMetadataFormatClassName,
			String[] extraStreamMetadataFormatNames,
			String[] extraStreamMetadataFormatClassNames,
			boolean supportsStandardImageMetadataFormat,
			String nativeImageMetadataFormatName,
			String nativeImageMetadataFormatClassName,
			String[] extraImageMetadataFormatNames,
			String[] extraImageMetadataFormatClassNames)
	{
		super(vendorName, version, names, suffixes, MIMETypes, writerClassName,
				outputTypes, readerSpiNames, supportsStandardStreamMetadataFormat,
				nativeStreamMetadataFormatName, nativeStreamMetadataFormatClassName,
				extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames,
				supportsStandardImageMetadataFormat, nativeImageMetadataFormatName,
				nativeImageMetadataFormatClassName, extraImageMetadataFormatNames,
				extraImageMetadataFormatClassNames);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.ImageWriterSpi#canEncodeImage(javax.imageio.ImageTypeSpecifier)
	 */
	@Override
	public boolean canEncodeImage(ImageTypeSpecifier type)
	{
//		// TODO: should also encode colored pcx images
//		if (type.getBufferedImageType() == BufferedImage.TYPE_BYTE_BINARY)
//			return true;
//
//		return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.ImageWriterSpi#createWriterInstance(java.lang.Object)
	 */
	@Override
	public ImageWriter createWriterInstance(Object extension) throws IOException {
		return new PCXImageWriter(this);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.IIOServiceProvider#getDescription(java.util.Locale)
	 */
	@Override
	public String getDescription(Locale locale) {
		return "PCX Format Writer";
	}

	protected void init()
	{
		outputTypes = new Class[] {OutputStream.class, ImageOutputStream.class};
		suffixes = PCXReaderWriterConstants.suffixes;
		names = PCXReaderWriterConstants.names;
		MIMETypes = PCXReaderWriterConstants.mimeTypes;
	}
}
