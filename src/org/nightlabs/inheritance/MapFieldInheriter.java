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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.nightlabs.util.CollectionUtil;

/**
 * This {@link FieldInheriter} handles each entry in the map isolated, which means you can
 * define for each entry (by the key) whether it should be inherited or not. If you don't need
 * this, but simply want to copy all the entries, then you should use a {@link SimpleFieldInheriter}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class MapFieldInheriter implements FieldInheriter
{
	@Override
	public void copyFieldValue(
			Inheritable mother, Inheritable child,
			Class<?> motherClass, Class<?> childClass,
			Field motherField, Field childField,
			FieldMetaData motherFieldMetaData, FieldMetaData childFieldMetaData
	)
	{
		if (!Map.class.isAssignableFrom(motherField.getType()))
			throw new ClassCastException("field \""+motherField.getName()+"\" of class \""+motherClass.getName()+"\" is not a Map!");

//		if (!(motherFieldMetaData instanceof MapFieldMetaData))
//			throw new ClassCastException("motherFieldMetaData for field \""+field.getName()+"\" of class \""+motherClass.getName()+"\" is not an instance of MapFieldMetaData!");
//
//		if (!(childFieldMetaData instanceof MapFieldMetaData))
//			throw new ClassCastException("childFieldMetaData for field \""+field.getName()+"\" of class \""+childClass.getName()+"\" is not an instance of MapFieldMetaData!");

		MapFieldMetaData motherMapFieldMetaData = null;
		if (motherFieldMetaData instanceof MapFieldMetaData)
			motherMapFieldMetaData = (MapFieldMetaData)motherFieldMetaData;

		MapFieldMetaData childMapFieldMetaData = null;
		if (childFieldMetaData instanceof MapFieldMetaData)
			childMapFieldMetaData = (MapFieldMetaData)childFieldMetaData;

		if (motherMapFieldMetaData != null && childMapFieldMetaData == null)
			throw new IllegalStateException("Mother defines a MapFieldMetaData while Child does not!");

		if (childMapFieldMetaData != null && motherMapFieldMetaData == null)
			throw new IllegalStateException("Child defines a MapFieldMetaData while Mother does not!");

		Map<?, ?> motherMap;
		Map<Object, Object> childMap;
		try {
			motherField.setAccessible(true);
			childField.setAccessible(true);
			motherMap = (Map<?, ?>)motherField.get(mother);
			childMap = CollectionUtil.castMap((Map<?, ?>)childField.get(child));
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

//	 delete all values that do not exist anymore in the mother...
		HashSet<Object> keysToRemove = new HashSet<Object>();
		for (Iterator<Object> it = childMap.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			if (!motherMap.containsKey(key)) {
				MapEntryMetaData c_memd = null;

				if (childMapFieldMetaData != null) {
					c_memd = childMapFieldMetaData.getMapEntryMetaData(key);
					if (c_memd == null)
						throw new NullPointerException("childFieldMetaData.getMapEntryMetaData(\""+key+"\") field \""+childField.getName()+"\" of class \""+childClass.getName()+"\"  returned null!");
				}

				if (c_memd == null || c_memd.isValueInherited()) {
					keysToRemove.add(key);

					if (childMapFieldMetaData != null)
						childMapFieldMetaData.removeMapEntryMetaData(key);
				}
			} // if (!motherMap.containsKey(key)) {
		} // for (Iterator it = childMap.entrySet().iterator(); it.hasNext(); ) {

		for (Iterator<Object> it = keysToRemove.iterator(); it.hasNext();) {
			Object key = it.next();
			childMap.remove(key);
		}

//	and add/set all values that need to be copied
		for (Map.Entry<?, ?> me : motherMap.entrySet()) {
			Object key = me.getKey();
			Object value = me.getValue();

			MapEntryMetaData m_memd = null;
			if (motherMapFieldMetaData != null) {
				m_memd = motherMapFieldMetaData.getMapEntryMetaData(key);
				if (m_memd == null)
					throw new NullPointerException("motherFieldMetaData.getMapEntryMetaData(\""+key+"\") field \""+motherField.getName()+"\" of class \""+motherClass.getName()+"\"  returned null!");
			}

			MapEntryMetaData c_memd = null;
			if (childMapFieldMetaData != null) {
				c_memd = childMapFieldMetaData.getMapEntryMetaData(key);
				if (c_memd == null)
					throw new NullPointerException("childFieldMetaData.getMapEntryMetaData(\""+key+"\") field \""+childField.getName()+"\" of class \""+childClass.getName()+"\"  returned null!");
			}

			if (c_memd == null || c_memd.isValueInherited())
				copyMapEntry(mother, child, motherClass, childClass, motherField, childField, motherFieldMetaData,
						childFieldMetaData, motherMap, childMap, key, value);
		} // for (Iterator it = motherMap.entrySet().iterator(); it.hasNext(); ) {
	}

	protected void copyMapEntry(
			Inheritable mother, Inheritable child, Class<?> motherClass, Class<?> childClass, Field motherField, Field childField, FieldMetaData motherFieldMetaData,
			FieldMetaData childFieldMetaData, Map<?, ?> motherMap, Map<Object, Object> childMap, Object motherKey, Object motherValue)
	{
		childMap.put(motherKey, motherValue);
	}
}
