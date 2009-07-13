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
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * PCX reader Service provider interface.
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class PCXImageReaderSPI
extends ImageReaderSpi
{
	public PCXImageReaderSPI()
	{
		super();
		init();
	}

	@SuppressWarnings("unchecked")
	public PCXImageReaderSPI(String vendorName, String version, String[] names,
			String[] suffixes, String[] MIMETypes, String readerClassName,
			Class[] inputTypes, String[] writerSpiNames,
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
		super(vendorName, version, names, suffixes, MIMETypes, readerClassName,
				inputTypes, writerSpiNames, supportsStandardStreamMetadataFormat,
				nativeStreamMetadataFormatName, nativeStreamMetadataFormatClassName,
				extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames,
				supportsStandardImageMetadataFormat, nativeImageMetadataFormatName,
				nativeImageMetadataFormatClassName, extraImageMetadataFormatNames,
				extraImageMetadataFormatClassNames);
	}

	protected void init()
	{
		inputTypes = new Class[] {InputStream.class, ImageInputStream.class};
		suffixes = PCXReaderWriterConstants.suffixes;
		names = PCXReaderWriterConstants.names;
		MIMETypes = PCXReaderWriterConstants.mimeTypes;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.ImageReaderSpi#canDecodeInput(java.lang.Object)
	 */
	@Override
	public boolean canDecodeInput(Object source) throws IOException
	{
		if (source != null)
		{
			if (source instanceof InputStream) {
				return checkHeader((InputStream)source);
			}
			else if (source instanceof ImageInputStream) {
				ImageInputStream imageStream = (ImageInputStream) source;
				org.nightlabs.io.pcx.ImageInputStream iis = new org.nightlabs.io.pcx.ImageInputStream(imageStream);
				return checkHeader(iis);
			}
		}
		return false;
	}

	protected boolean checkHeader(InputStream in)
	{
		// TODO: check if header contains meanigful values
		return PCXImageReader.getHeader(in) != null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.ImageReaderSpi#createReaderInstance(java.lang.Object)
	 */
	@Override
	public ImageReader createReaderInstance(Object extension) throws IOException
	{
		return new PCXImageReader(this);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.spi.IIOServiceProvider#getDescription(java.util.Locale)
	 */
	@Override
	public String getDescription(Locale locale)
	{
		return "PCX Format Reader";
	}
}
