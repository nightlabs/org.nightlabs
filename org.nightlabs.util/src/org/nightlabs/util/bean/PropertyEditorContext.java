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
import java.util.Iterator;
import java.util.Map;

public class PropertyEditorContext
{
	protected Map<Class<? extends Object>, Class<? extends PropertyEditor>> targetClass2PropertyEditorClass;
	protected Map<String, Class<? extends PropertyEditor>> propertyName2PropertyEditor;

	public PropertyEditorContext()
	{
		targetClass2PropertyEditorClass = new HashMap<Class<? extends Object>, Class<? extends PropertyEditor>>();
		propertyName2PropertyEditor = new HashMap<String, Class<? extends PropertyEditor>>();
	}

	public void registerEditor(Class<? extends Object> targetType, Class<? extends PropertyEditor> editorClass)
	{
		if (targetType == null)
			throw new NullPointerException("Param targetType must not be null!");

		if (editorClass == null)
			throw new NullPointerException("Param editorClass must not be null!");

		targetClass2PropertyEditorClass.put(targetType, editorClass);
	}

	public void registerEditor(Object target, Class<? extends PropertyEditor> editorClass)
	{
		if (target == null)
			throw new IllegalArgumentException("Param target must not be null!");

		Class<? extends Object> targetClass = target.getClass();
		registerEditor(targetClass, editorClass);
	}

	public void registerEditor(String propertyName, Class<? extends PropertyEditor> editorClass)
	{
		if(propertyName == null)
			throw new NullPointerException("Param propertyName must not be null!");

		if (editorClass == null)
			throw new NullPointerException("Param editorClass must not be null!");

		propertyName2PropertyEditor.put(propertyName, editorClass);
	}

	public PropertyEditor getEditor(Object o)
	throws IllegalAccessException,
				 InstantiationException
	{
		if (o == null)
			throw new IllegalArgumentException("Param o must not be null!");

		Class<? extends Object> targetClass = o.getClass();
		PropertyEditor pe = getEditor(targetClass);
		if (pe != null) {
			return pe;
		} else {
			for (Iterator<Class<? extends Object>> it = targetClass2PropertyEditorClass.keySet().iterator(); it.hasNext(); ) {
				Class<? extends Object> targetType = it.next();
				if (targetType.isAssignableFrom(targetClass)) {
					return (targetClass2PropertyEditorClass.get(targetType).newInstance());
				}
			}
		}
		return null;
	}

	public PropertyEditor getEditor(Class<? extends Object> targetType)
	throws InstantiationException,
				 IllegalAccessException
	{
		if (targetClass2PropertyEditorClass.containsKey(targetType))
		{
			Class<? extends PropertyEditor> pecClass = targetClass2PropertyEditorClass.get(targetType);
			PropertyEditor pe = pecClass.newInstance();
			return pe;
		}
		return null;
	}

	public PropertyEditor getEditor(String propertyName)
	{
		if (propertyName2PropertyEditor.containsKey(propertyName))
		{
			PropertyEditor pe;
			try {
				pe = propertyName2PropertyEditor.get(propertyName).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return pe;
		}
		return null;
	}

}
