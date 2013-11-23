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

import org.nightlabs.editor2d.RootDrawComponent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class XStreamTemplateFilter
extends XStreamFilter
{
	public static final String FILE_EXTENSION = "e2t"; //$NON-NLS-1$
	public static final String TEMPLATE_CONTENT_TYPE = "application/x-nightlabs-editor2dtemplate"; //$NON-NLS-1$

	@Override
	protected String[] initFileExtensions() {
		return new String[] {FILE_EXTENSION};
	}

	@Override
	protected String initDescription() {
		return "Editor2D Template File Format";
	}

	@Override
	protected String initName() {
		return "Editor2D Template";
	}

	@Override
	public void write(Object o, OutputStream out)
	throws IOException
	{
		RootDrawComponent root = (RootDrawComponent) o;
		root.setTemplate(true);
		super.write(o, out);
	}

	@Override
	public RootDrawComponent read(InputStream in)
	throws IOException
	{
		RootDrawComponent root = super.read(in);
		root.setTemplate(false);
		return root;
	}

	@Override
	protected ManifestWriter createManifestWriter() {
		ManifestWriter manifestWriter = super.createManifestWriter();
		manifestWriter.setContentType(TEMPLATE_CONTENT_TYPE);
		return manifestWriter;
	}

}
