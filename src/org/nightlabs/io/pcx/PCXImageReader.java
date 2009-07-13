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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

import org.nightlabs.util.Binary;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class PCXImageReader
extends ImageReader
{
	public PCXImageReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getNumImages(boolean)
	 */
	@Override
	public int getNumImages(boolean allowSearch) throws IOException
	{
		// TODO: only reading of one image is supported
		return 1;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getWidth(int)
	 */
	@Override
	public int getWidth(int imageIndex) throws IOException
	{
		if (imageIndex == 0) {
			if (header != null)
				return header.getWidth();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getHeight(int)
	 */
	@Override
	public int getHeight(int imageIndex) throws IOException
	{
		if (imageIndex == 0) {
			if (header != null)
				return header.getHeight();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getImageTypes(int)
	 */
	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex)
	throws IOException
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getStreamMetadata()
	 */
	@Override
	public IIOMetadata getStreamMetadata() throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#getImageMetadata(int)
	 */
	@Override
	public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
		return null;
	}

	private PCXReader pcxReader = null;
	protected PCXReader getPCXReader() {
		if (pcxReader == null)
			pcxReader = new PCXReader();
		return pcxReader;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#read(int, javax.imageio.ImageReadParam)
	 */
	@Override
	public BufferedImage read(int imageIndex, ImageReadParam param)
	throws IOException
	{
		// TODO: support ImageReadParam and further imageIndexes
		if (imageIndex == 0) {
			if (getInputStream() != null) {
				return getPCXReader().read(getInputStream());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageReader#setInput(java.lang.Object, boolean, boolean)
	 */
	@Override
	public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata)
	{
		super.setInput(input, seekForwardOnly, ignoreMetadata);
		inputStream = null;
//		header = getHeader(getInputStream());
	}

	private PCXHeader header = null;

	@SuppressWarnings("deprecation")
	public static PCXHeader getHeader(InputStream in)
	{
		try {
			BufferedInputStream bin = new BufferedInputStream(in);
			bin.mark(128);
			Binary binary = new Binary(in);
			PCXHeader header = new PCXHeader(binary);
			bin.reset();
			return header;
		} catch (IOException ioe) {
			return null;
		}
	}

	private InputStream inputStream = null;
	protected InputStream getInputStream()
	{
		if (inputStream == null) {
			if (input != null) {
				if (input instanceof javax.imageio.stream.ImageInputStream) {
					javax.imageio.stream.ImageInputStream imageInpuStream = (javax.imageio.stream.ImageInputStream) input;
					inputStream = new ImageInputStream(imageInpuStream);
				}
				else if (input instanceof InputStream) {
					inputStream = (InputStream) input;
				}
			}
		}
		return inputStream;
	}

}
