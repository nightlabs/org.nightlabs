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
package org.nightlabs.util.collection;

import java.util.Map;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class MapChangedEvent<K, V>
{
	public enum MapChange
	{
		CLEAR,
		PUT,
		PUT_ALL,
		REMOVE
	}
	
	public MapChangedEvent(MapChange mapChange, Map<K, V> map) {
		this(mapChange, map, null, null);
	}

	public MapChangedEvent(MapChange mapChange, Map<K, V> map, K key) {
		this(mapChange, map, key, null);
	}

	public MapChangedEvent(MapChange mapChange, Map<K, V> map, K key, V value) {
		this.mapChange = mapChange;
		this.map = map;
		this.key = key;
		this.value = value;
	}
	
	private Map<K, V> map;
	public Map<K, V> getMap() {
		return map;
	}

	private MapChange mapChange;
	public MapChange getMapChange() {
		return mapChange;
	}
	
	private K key;
	public K getKey() {
		return key;
	}
	
	private V value;
	public V getValue() {
		return value;
	}
}
