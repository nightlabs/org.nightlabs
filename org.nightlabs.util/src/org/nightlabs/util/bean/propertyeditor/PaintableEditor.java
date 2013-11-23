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
package org.nightlabs.util.bean.propertyeditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyEditor;

/**
 * @author Daniel Mazurek
 */
public class PaintableEditor
extends Component
{
	private static final long serialVersionUID = 1L;

	/** Paintable PropertyEditor */
	PropertyEditor editor;

	/**
	 * Rectangle where value is painted
	 */
	Rectangle rect = new Rectangle(0, 0, 0, 0);

	/**
	 * Default Constructor
	 */
	public PaintableEditor(PropertyEditor editor){
		this.editor = editor;
	}

	@Override
	public void paint(Graphics g)
	{
		Dimension size = getSize();
		rect.width = size.width;
		rect.height = rect.height;
		editor.paintValue(g, rect);
	}
}
