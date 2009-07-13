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
import java.io.InputStream;
import java.io.OutputStream;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.util.ImageGenerator;
import org.nightlabs.io.AbstractSingleFileExtensionIOFilter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractImageFilter
extends AbstractSingleFileExtensionIOFilter
{
	@Override
	protected boolean supportsRead() {
		return false;
	}

	@Override
	protected boolean supportsWrite() {
		return true;
	}

	public Object read(InputStream in) throws IOException {
//		return null;
		throw new UnsupportedOperationException("ImageFilter can only write; NOT read!");
	}

	public void write(Object o, OutputStream out) throws IOException
	{
		DrawComponent dc = (DrawComponent) o;
		dc.clearBounds();
		int width = dc.getWidth();
		int height = dc.getHeight();
		ImageGenerator.writeModelAsImage(dc, width, height, initFileExtension(), out,
				dc.getRenderMode(), dc.getRenderModeManager());
	}

}
