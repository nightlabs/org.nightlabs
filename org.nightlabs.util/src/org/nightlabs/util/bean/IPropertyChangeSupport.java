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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Interface to indicate that implementations support the registration and
 * triggering of property changes. Properties are hereby referenced as Strings.
 *
 * Use {@link PropertyChangeSupport} as super class for implementations where possible.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IPropertyChangeSupport {
	/**
	 * Add a PropertyChangeListener to the listener list
	 * @param listener The listener to add.
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	/**
	 * Add a PropertyChangeListener for a specific property.
	 * The listener will be invoked only when a the property
	 * with the given specific name had been changed.
	 *
	 * @param propertyName The property name to listen to changes.
	 * @param listener The listener to add.
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The listener to remove.
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The property name the listener should not be triggerd on any more.
	 * @param listener The listener to remove.
	 */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}
