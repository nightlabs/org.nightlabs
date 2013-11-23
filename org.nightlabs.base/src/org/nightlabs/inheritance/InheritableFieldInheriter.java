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
package org.nightlabs.inheritance;

import java.lang.reflect.Field;

/**
 * In contrast to the {@link SimpleFieldInheriter}, this implementation of {@link FieldInheriter} does
 * not simply assign the field of the child by value of the mother's field. Instead, it does another
 * inheritance-copy between the mother-field-value and the child-field-value.
 * <p>
 * Therefore, both fields' values must implement {@link Inheritable} and must not be <code>null</code>.
 * The current behaviour is an {@link IllegalArgumentException}, if this premise is not met, but the
 * behaviour might well change (and become silent).
 * </p>
 * <p>
 * Please contact me (Marco), if you run into this
 * situation and think we should change this {@link FieldInheriter}'s behaviour.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class InheritableFieldInheriter
		implements FieldInheriter
{

	@Override
	public void copyFieldValue(
			Inheritable mother, Inheritable child,
			Class<?> motherClass, Class<?> childClass,
			Field motherField, Field childField,
			FieldMetaData motherFieldMetaData, FieldMetaData childFieldMetaData
	)
	{
		try {
			motherField.setAccessible(true);
			childField.setAccessible(true);
			Object motherFieldValueObj = motherField.get(mother);
			Object childFieldValueObj = childField.get(child);

			if (motherFieldValueObj == null)
				throw new IllegalArgumentException("Field " + motherField.getName() + " of mother object \"" + mother + "\" (" + mother.getClass() + ") must not be null!");

			if (childFieldValueObj == null)
				throw new IllegalArgumentException("Field " + childField.getName() + " of child object \"" + child + "\" (" + child.getClass() + ") must not be null!");

			if (!(motherFieldValueObj instanceof Inheritable))
				throw new IllegalArgumentException("Field " + motherField.getName() + " of mother object \"" + mother + "\" (" + mother.getClass() + ") contains an object which does not implement Inheritable!");

			if (!(childFieldValueObj instanceof Inheritable))
				throw new IllegalArgumentException("Field " + childField.getName() + " of child object \"" + child + "\" (" + child.getClass() + ") contains an object which does not implement Inheritable!");

			Inheritable motherFieldValue = (Inheritable) motherFieldValueObj;
			Inheritable childFieldValue = (Inheritable) childFieldValueObj;

			createInheritanceManager().inheritAllFields(motherFieldValue, childFieldValue);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected InheritanceManager createInheritanceManager()
	{
		return new InheritanceManager();
	}
}
