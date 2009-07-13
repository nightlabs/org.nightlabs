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
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class PCXImageWriter
extends ImageWriter
{
	public PCXImageWriter(ImageWriterSpi originatingProvider) {
		super(originatingProvider);
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageWriter#getDefaultStreamMetadata(javax.imageio.ImageWriteParam)
	 */
	@Override
	public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageWriter#getDefaultImageMetadata(javax.imageio.ImageTypeSpecifier, javax.imageio.ImageWriteParam)
	 */
	@Override
	public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
			ImageWriteParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageWriter#convertStreamMetadata(javax.imageio.metadata.IIOMetadata, javax.imageio.ImageWriteParam)
	 */
	@Override
	public IIOMetadata convertStreamMetadata(IIOMetadata inData,
			ImageWriteParam param)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.imageio.ImageWriter#convertImageMetadata(javax.imageio.metadata.IIOMetadata, javax.imageio.ImageTypeSpecifier, javax.imageio.ImageWriteParam)
	 */
	@Override
	public IIOMetadata convertImageMetadata(IIOMetadata inData,
			ImageTypeSpecifier imageType, ImageWriteParam param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.imageio.ImageWriter#write(javax.imageio.metadata.IIOMetadata, javax.imageio.IIOImage, javax.imageio.ImageWriteParam)
	 */
	@Override
	public void write(IIOMetadata streamMetadata, IIOImage image,
			ImageWriteParam param) throws IOException
	{
		// TODO Why not throw exceptions (e.g. if getOutputStream() returns null)? If the write method must for any reason silently
		// ignore errors, this should be documented here in a short comment. Marco.

		// TODO: take streamMetaData and param into account
		if (image.getRenderedImage() instanceof BufferedImage &&
				getOutputStream() != null)
		{
			BufferedImage bi = (BufferedImage) image.getRenderedImage();
			if (bi.getType() != BufferedImage.TYPE_BYTE_BINARY) { // convert silently to B/W since we only support 1-bit-output
				BufferedImage tmpImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
				tmpImg.createGraphics().drawImage(bi, null, 0, 0);
				bi = tmpImg;
			}
			getPCXWriter().write(getOutputStream(), bi);
		}
	}

  /* (non-Javadoc)
   * @see javax.imageio.ImageWriter#setOutput(java.lang.Object)
   */
  @Override
	public void setOutput(Object output)
  {
  	super.setOutput(output);
  	outputStream = null;
  }
	
	private OutputStream outputStream = null;
	protected OutputStream getOutputStream()
	{
		if (outputStream == null) {
			if (output != null) {
				if (output instanceof javax.imageio.stream.ImageOutputStream) {
					javax.imageio.stream.ImageOutputStream imageOutputStream = (javax.imageio.stream.ImageOutputStream) output;
					outputStream = new ImageOutputStream(imageOutputStream);
				}
				else if (output instanceof OutputStream) {
					outputStream = (OutputStream) output;
				}
			}
		}
		return outputStream;
	}
	
	private PCXWriter pcxWriter = null;
	protected PCXWriter getPCXWriter()
	{
		if (pcxWriter == null) {
			pcxWriter = new PCXWriter();
		}
		return pcxWriter;
	}
}
