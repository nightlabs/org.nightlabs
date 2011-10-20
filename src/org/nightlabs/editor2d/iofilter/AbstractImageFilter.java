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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.util.ImageCreator;
import org.nightlabs.io.AbstractSingleFileExtensionIOFilter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractImageFilter
extends AbstractSingleFileExtensionIOFilter
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean supportsRead() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean supportsWrite() {
		return true;
	}
	
	/**
	 * Default type would support alpha filter (A in ARGB), but some formats (BMP, JPEG) are not correctly rendered with such a type.
	 * So this method should be overriden for formats that do not support the default type.
	 * 
	 * @return type of {@link BufferedImage}
	 */
	protected int initImageType(){
		return BufferedImage.TYPE_INT_ARGB;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object read(InputStream in) throws IOException {
		throw new UnsupportedOperationException("ImageFilter can only write; NOT read!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(Object o, OutputStream out) throws IOException
	{
		DrawComponent dc = (DrawComponent) o;
		dc.clearBounds();
		int width = dc.getWidth();
		int height = dc.getHeight();
		
		ImageCreator imgCreator = new ImageCreator(dc, width, height);
		imgCreator.setImageType(initImageType());
		imgCreator.setRenderMode(dc.getRenderMode());
		imgCreator.setRenderModeManager(dc.getRenderModeManager());
		imgCreator.writeImage(initFileExtension(), out);
	}

}
