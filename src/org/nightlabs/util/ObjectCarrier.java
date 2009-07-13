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
package org.nightlabs.util;

/**
 * Simple carrier for one object.
 * This is intended to be used when one needs a (final) carrier for
 * an object and can not use / pass the object itself.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ObjectCarrier<ObjectType extends Object> {
	private ObjectType object;
	/**
	 * Create a new {@link ObjectCarrier}.
	 */
	public ObjectCarrier() {
	}
	/**
	 * Create a new {@link ObjectCarrier} with the given object.
	 * @param object The object for the new carrier.
	 */
	public ObjectCarrier(ObjectType object) {
		this.object = object;
	}
	/**
	 * Get the object this carrier holds.
	 * @return The object this carrier holds.
	 */
	public ObjectType getObject() {
		return object;
	}
	/**
	 * Set the object for this carrier.
	 * @param object The object to set for this carrier.
	 */
	public void setObject(ObjectType object) {
		this.object = object;
	}
}
