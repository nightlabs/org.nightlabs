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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.util.collection.MapChangedEvent.MapChange;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class NotifyingMap<K, V>
extends HashMap<K,V>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NotifyingMap() {
		init();
	}

	/**
	 * @param initialCapacity
	 */
	public NotifyingMap(int initialCapacity) {
		super(initialCapacity);
		init();
	}

//	/**
//	 * @param m
//	 */
//	public NotifyingMap(Map<? extends K, ? extends V> m) {
//		super(m);
//		init();
//	}

	protected void init() {
		listeners = new HashSet<MapChangedListener<K, V>>();
	}

	private Set<MapChangedListener<K, V>> listeners = null;

	public void addMapChangedListener(MapChangedListener<K, V> listener) {
		listeners.add(listener);
	}

	public void removeMapChangedListener(MapChangedListener<K, V> listener) {
		listeners.remove(listener);
	}

	protected void fireMapChangedEvent(MapChangedEvent<K, V> event)
	{
		for (MapChangedListener<K, V> listener : listeners) {
			listener.mapChanged(event);
		}
	}

	@Override
	public void clear() {
		super.clear();
		fireMapChangedEvent(new MapChangedEvent<K, V>(MapChange.CLEAR, this));
	}

	@Override
	public V put(K key, V value) {
		fireMapChangedEvent(new MapChangedEvent<K, V>(MapChange.PUT, this, key, value));
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		fireMapChangedEvent(new MapChangedEvent<K, V>(MapChange.PUT_ALL, this));
		super.putAll(m);
	}

	@Override
	public V remove(Object key) {
		fireMapChangedEvent(new MapChangedEvent<K, V>(MapChange.REMOVE, this, (K) key));
//		fireMapChangedEvent(new MapChangedEvent<K, V>(MapChange.REMOVE, this, key));
		return super.remove(key);
	}

}
