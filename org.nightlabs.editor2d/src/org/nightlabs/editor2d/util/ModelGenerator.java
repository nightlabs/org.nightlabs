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
package org.nightlabs.editor2d.util;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.impl.Editor2DFactoryImpl;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class ModelGenerator
{
	public ModelGenerator() {
		this(new Editor2DFactoryImpl());
	}
	
	public ModelGenerator(Editor2DFactory factory) {
		this.factory = factory;
	}
	
	private Editor2DFactory factory = null;
	public Editor2DFactory getFactory() {
		return factory;
	}
	public void setFactory(Editor2DFactory factory) {
		this.factory = factory;
	}
	
//	public RootDrawComponent createDrawComponents(Class dcClass, int amount, int x, int y, int distX, int distY,
//			int width, int height, DrawComponentContainer parent, Map<String, Object> properties)
//	{
//		RootDrawComponent root = getFactory().createRootDrawComponent();
//		for (int i=1; i<=amount; i++)
//		{
//			DrawComponent dc = factory.createDrawComponent(dcClass);
//			int dcx = x * i + distX;
//			int dcy = x * i + distY;
//			dc.setX(dcx);
//			dc.setY(dcy);
//			dc.setWidth(width);
//			dc.setHeight(height);
//			// TODO: do something with the properties
//			parent.addDrawComponent(dc);
//		}
//		return root;
//	}
	
}
