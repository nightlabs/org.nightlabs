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
package org.nightlabs.util.bean;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class PropertyEditorMan
{
	private static Map<Class<? extends PropertyEditorContext>, PropertyEditorContext> class2Pec = new HashMap<Class<? extends PropertyEditorContext>, PropertyEditorContext>();

	public PropertyEditorMan()
	{
	}

	public static void registerEditorContext(Class<? extends PropertyEditorContext> propertyEditorContextClass)
	throws InstantiationException,
				 IllegalAccessException
	{
		PropertyEditorContext pec = propertyEditorContextClass.newInstance();
		class2Pec.put(propertyEditorContextClass, pec);
	}

	public static void registerPropertyEditor(Class<? extends Object> targetClass, Class<? extends PropertyEditor> propertyEditorClass, Class<? extends PropertyEditorContext> propertyEditorContextClass)
	{
		if (class2Pec.containsKey(propertyEditorContextClass))
		{
			PropertyEditorContext pec = class2Pec.get(propertyEditorContextClass);
			pec.registerEditor(targetClass, propertyEditorClass);
		}
	}

	public static void registerPropertyEditor(Class<? extends Object> targetClass, Class<? extends PropertyEditor> propertyEditorClass)
	{
		registerPropertyEditor(targetClass, propertyEditorClass, DefaultPropertyEditorContext.class);
	}

	public static PropertyEditor getPropertyEditor(Class<? extends PropertyEditorContext> propertyEditorContextClass, Class<? extends Object> targetType)
//	throws IllegalAccessException,
//				 InstantiationException
	{
		if (class2Pec.containsKey(propertyEditorContextClass))
		{
			PropertyEditorContext pec = class2Pec.get(propertyEditorContextClass);
//			return pec.getEditor(targetType);
			try {
				return pec.getEditor(targetType);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static PropertyEditor getPropertyEditor(Class<? extends Object> targetType)
//	throws IllegalAccessException,
//	 			 InstantiationException
	{
		return getPropertyEditor(DefaultPropertyEditorContext.class, targetType);
	}

	public static PropertyEditor getPropertyEditor(Class<? extends PropertyEditorContext> propertyEditorContextClass, Object target)
//	throws IllegalAccessException,
//				 InstantiationException
	{
		if (target == null)
			throw new IllegalArgumentException("Param target must not be null!");

		Class<? extends Object> targetType = target.getClass();
		PropertyEditor pe = getPropertyEditor(propertyEditorContextClass, targetType);
		if (pe != null) {
			return pe;
		} else {
			if (class2Pec.containsKey(propertyEditorContextClass)) {
				PropertyEditorContext pec = class2Pec.get(propertyEditorContextClass);
				try {
					return pec.getEditor(target);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static PropertyEditor getPropertyEditor(Class<? extends PropertyEditorContext> propertyEditorContextClass, String propertyName)
	{
		if (class2Pec.containsKey(propertyEditorContextClass))
		{
			PropertyEditorContext pec = class2Pec.get(propertyEditorContextClass);
			PropertyEditor pe = pec.getEditor(propertyName);
			return pe;
		}
		return null;
	}

}
