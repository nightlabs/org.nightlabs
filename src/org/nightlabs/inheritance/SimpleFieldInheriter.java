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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.Util;

/**
 * This <code>FieldInheriter</code> assigns the value of the field of the mother
 * to the child, if the meta-data says so. If the field is itself {@link Inheritable},
 * you might want to use an {@link InheritableFieldInheriter} instead.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SimpleFieldInheriter implements FieldInheriter
{
	private static final Logger logger = Logger.getLogger(SimpleFieldInheriter.class);

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
			if (Map.class.isAssignableFrom(motherField.getType())) {
				Map<?,?> motherMap = (Map<?,?>)motherField.get(mother);
				Map<Object, Object> childMap = CollectionUtil.castMap((Map<?, ?>)childField.get(child));
				if (childMap != null) {
					// to reduce the overhead for JDO we remove only those keys which are not present
					// in the mother map instead of completely clearing the map
					for (Iterator<Map.Entry<Object, Object>> it = childMap.entrySet().iterator(); it.hasNext(); ) {
						Map.Entry<Object, Object> me = it.next();
						if (!motherMap.containsKey(me.getKey()))
							it.remove();
					}

					// we then override the values in the child map with those of the mother map
					for (Map.Entry<?,?> me : motherMap.entrySet()) {
						if (childMap.containsKey(me.getKey())) {
							// if the child-map already contains the key
							// we have to compare the values in order to
							// perform a put-operation only if really necessary
							if (!Util.equals(me.getValue(), childMap.get(me.getKey()))) {
								childMap.put(me.getKey(), me.getValue());
							}
						} else {
							// if the key is not in the child-map, we have to insert
							childMap.put(me.getKey(), me.getValue());
						}
					}
				}
			}
			else if (List.class.isAssignableFrom(motherField.getType())) {
				// List requires special handling, because its order is important.
				// Therefore we cannot use the same code as for the Collection below.
				List<?> motherList = (List<?>)motherField.get(mother);
				if (motherList == null)
					logger.warn("mother-list-field is null! Skipping \"" + motherField.getName() + "\" of class \"" + motherClass.getName() + "\" of instance: " + mother);
				else {
					List<Object> childList = CollectionUtil.castList((List<?>)childField.get(child));
					if (childList == null)
						logger.warn("child-list-field is null! Skipping \"" + childField.getName() + "\" of class \"" + childClass.getName() + "\" of instance: " + child);
					else {
						int listIndex = -1;
						for (Object motherElement : motherList) {
							if (++listIndex >= childList.size())
								childList.add(motherElement);
							else {
								Object childElement = childList.get(listIndex);
								if (!Util.equals(motherElement, childElement)) {
									// In this case, we could (maybe later in the future) optimize, because the current
									// algorithm behaves very badly in case of element-swaps in the mother-list.
									childList.add(listIndex, motherElement);
								}
							}
						}
						while (childList.size() > motherList.size())
							childList.remove(childList.size() - 1);
					}
				}
			}
			else if (Collection.class.isAssignableFrom(motherField.getType())) {
				// This is usually a Set, since List is already handled above. Therefore, the
				// order of the elements doesn't matter here.
				Collection<?> motherCol = (Collection<?>)motherField.get(mother);
				if (motherCol == null)
					logger.warn("mother-collection-field is null! Skipping \"" + motherField.getName() + "\" of class \"" + motherClass.getName() + "\" of instance: " + mother);
				else {
					Collection<Object> childCol = CollectionUtil.castCollection((Collection<?>)childField.get(child));
					if (childCol == null)
						logger.warn("child-collection-field is null! Skipping \"" + childField.getName() + "\" of class \"" + childClass.getName() + "\" of instance: " + child);
					else {
						for (Iterator<Object> it = childCol.iterator(); it.hasNext(); ) {
							if (!motherCol.contains(it.next()))
								it.remove();
						}
						for (Object object : motherCol) {
							if (!childCol.contains(object))
								childCol.add(object);
						}
					}
				}
			}
//			else if (Collection.class.isAssignableFrom(field.getType())) {
//				Collection<?> motherCol = (Collection<?>)field.get(mother);
//				Collection<Object> childCol = (Collection<Object>)field.get(child);
//				if (childCol != null) {
//					childCol.clear();
//					childCol.addAll(motherCol);
//				}
//			}
			else
				motherField.set(child, motherField.get(mother));
//		} catch (IllegalArgumentException e) {
//			throw e;
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
		} catch (Exception e) {
			throw new RuntimeException(
					"copyFieldValue(...) failed: motherClass=" +
					motherClass.getName() + " childClass=" + childClass.getName() + " field=\""+motherField.toString()+"\"", e);
		}
	}

}
