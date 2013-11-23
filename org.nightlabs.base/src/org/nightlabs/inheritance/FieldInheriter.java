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
 * Implementations of this interface are responsible for copying a field when data is inherited.
 * <p>
 * See the wiki page
 * <a href="https://www.jfire.org/modules/phpwiki/index.php/Framework%20DataInheritance">Framework DataInheritance</a>
 * for further information.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface FieldInheriter
{
	/**
	 * Copy a field's value from one instance (called "mother") to another instance (called "child"). Since the way
	 * how the copying shall be done might vary drastically from simply assigning the same reference to the same
	 * object to actually cloning the field's value, the copy-process is delegated to instances of this interface.
	 *
	 * @param mother the mother instance - i.e. the object <b>from</b> which to copy data.
	 * @param child the child instance - i.e. the object <b>to</b> which to copy data.
	 * @param motherClass the class of the mother - the same as returned by the method {@link Object#getClass()} of the <code>mother</code> instance.
	 * @param childClass the class of the child - the same as returned by the method {@link Object#getClass()} of the <code>child</code> instance.
	 * @param motherField the {@link Field} as found when iterating all declared fields of the <code>motherClass</code> (and its super-classes).
	 * @param childField TODO
	 * @param motherFieldMetaData the {@link FieldMetaData} of the <code>mother</code> for the current field.
	 * @param childFieldMetaData the {@link FieldMetaData} of the <code>child</code> for the current field.
	 */
	void copyFieldValue (
			Inheritable mother, Inheritable child,
			Class<?> motherClass, Class<?> childClass,
			Field motherField,
			Field childField,
			FieldMetaData motherFieldMetaData, FieldMetaData childFieldMetaData);
}
