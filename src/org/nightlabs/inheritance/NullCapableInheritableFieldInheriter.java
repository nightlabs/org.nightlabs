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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just like {@link InheritableFieldInheriter} this implementation of {@link FieldInheriter} does another
 * inheritance-copy between the mother-field-value and the child-field-value.
 * <p>
 * However, this implementation allows mother and child fields to be <code>null</code>. 
 * </p>
 *
 * @param IType The type of the inheritable whose field is inherited.
 * @param FType The type of the field that should be inherited.
 *  
 * @author abieber
 */
public class NullCapableInheritableFieldInheriter<IType extends Inheritable, FType extends Inheritable>
		implements FieldInheriter
{

	private static final Logger logger = LoggerFactory.getLogger(NullCapableInheritableFieldInheriter.class);

	/**
	 * An instance of this {@link ChildFieldCreator} is used by
	 * {@link NullCapableInheritableFieldInheriter} to create a new child.
	 */
	public static interface ChildFieldCreator<IType, FType> {
		/**
		 * Create a new instance of the child-field and assign it to the child-instance.
		 * 
		 * @param mother The mother-instance.
		 * @param motherFieldValueObj The mother-field value.
		 * @param child The child-instance.
		 * @return The newly created child-field instance.
		 */
		FType createAndAssignChildField(IType mother, FType motherFieldValueObj, IType child);
	}
	
	private ChildFieldCreator<IType, FType> childFieldCreator;
	
	
	
	public NullCapableInheritableFieldInheriter(ChildFieldCreator<IType, FType> childFieldCreator) {
		this.childFieldCreator = childFieldCreator;
	}

	@SuppressWarnings("unchecked")
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

			if (motherFieldValueObj == null) {
				// Mother field is null, the has to be null, too
				if (childFieldValueObj != null)
					childField.set(child, null);
				return;
			}
			
			FType childFieldValue = (FType) childFieldValueObj;
			
			if (childFieldValue == null) {
				// motherFieldValueObj is not null, but child is, we have to create a new child-object
				if (childFieldCreator != null) 
				{
					childFieldValue = childFieldCreator.createAndAssignChildField((IType)mother, (FType)motherFieldValueObj, (IType)child);
				} 
				else {
					logger.warn("copyFieldValue: Can't create new child-field for field {} of {}, because childFieldCreator is null.", childField.getName(), child);
				}
			}

			if (childFieldValue != null) {
				logger.warn("copyFieldValue: Although mother-field is not null, the child-field-value is null so the inheritance can't be applied. mother={}, child={}, field={}", new Object[] {mother, child, childField.getName()});
			}

			Inheritable motherFieldValue = (Inheritable) motherFieldValueObj;

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
