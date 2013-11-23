/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
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

package org.nightlabs.jdo.inheritance;

import java.lang.reflect.Field;

import javax.jdo.JDOHelper;

import org.nightlabs.inheritance.FieldMetaData;
import org.nightlabs.inheritance.Inheritable;
import org.nightlabs.inheritance.SimpleFieldInheriter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class JDOSimpleFieldInheriter extends SimpleFieldInheriter
{
	@Override
	public void copyFieldValue(
			Inheritable mother, Inheritable child,
			Class<?> motherClass, Class<?> childClass,
			Field motherField, Field childField,
			FieldMetaData motherFieldMetaData, FieldMetaData childFieldMetaData
	)
	{
		super.copyFieldValue(mother, child, motherClass, childClass, motherField,
				childField, motherFieldMetaData, childFieldMetaData);
		JDOHelper.makeDirty(child, motherField.getName());
	}
}
