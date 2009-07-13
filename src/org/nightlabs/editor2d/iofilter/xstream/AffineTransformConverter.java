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

package org.nightlabs.editor2d.iofilter.xstream;

import java.awt.geom.AffineTransform;

import org.nightlabs.editor2d.impl.Editor2DFactoryImpl;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class AffineTransformConverter
//extends AbstractBasicConverter
extends AbstractSingleValueConverter
{
	protected Editor2DFactoryImpl factory;
	public AffineTransformConverter() {
		super();
		factory = new Editor2DFactoryImpl();
	}

	@Override
	public Object fromString(String str) {
		return factory.createAffineTransformFromString(str);
	}

  @Override
	public String toString(Object obj) {
    return factory.convertAffineTransformToString(obj);
  }
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type)
	{
		if (type.equals(AffineTransform.class))
			return true;
		
		return false;
	}

}
