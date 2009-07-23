/* *****************************************************************************
 * KeyReader - Framework library for reading keys from arbitrary reader-devices*
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.keyreader;

import java.util.Date;
import java.util.EventObject;

/**
 * This event is fired, if the key reader notifies an error with the device
 * while the KeyReaderListenerThread is running. The source of the event
 * will be the instance of KeyReader responsible for the device.
 *
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderErrorEvent extends EventObject
{
	private static final long serialVersionUID = 2L;

	private Date timestamp;

	private Throwable error;

	public KeyReaderErrorEvent(KeyReader _keyReader, Throwable _error)
	{
		super(_keyReader);
		this.timestamp = new Date();
		this.error = _error;
	}

	public Date getTimestamp() { return timestamp; }

	/**
	 * @return Returns the error.
	 */
	public Throwable getError() {
		return error;
	}
}
