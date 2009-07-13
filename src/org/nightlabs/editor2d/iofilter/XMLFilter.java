/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nightlabs.io.AbstractSingleFileExtensionIOFilter;

public class XMLFilter
extends AbstractSingleFileExtensionIOFilter
{
	public static final String XML_FILE_EXTENSION = "xml";
	 	
	public XMLFilter() {
		super();
	}
	
	@Override
	protected String initFileExtension() {
		return  XML_FILE_EXTENSION;
	}
	
	@Override
	protected String initDescription() {
		return "Editor2D XML Format";
	}
	
	@Override
	protected String initName() {
		return "Editor2D XML";
	}
  	
	@Override
	protected boolean supportsRead() {
		return true;
	}

	@Override
	protected boolean supportsWrite() {
		return true;
	}

	public Object read(InputStream in)
	throws IOException
	{
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
		Object o = decoder.readObject();
		decoder.close();
		return o;
	}

	public void write(Object o, OutputStream out)
	throws IOException
	{
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
		encoder.writeObject(o);
		encoder.close();
	}

}
