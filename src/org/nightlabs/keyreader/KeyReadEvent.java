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
 * This event is fired, when a complete key has been read. The source of
 * this event is the KeyReader that sent it.
 *
 * <p>Title: NightLabsBarcodeReaders</p>
 * <p>Description: shared libraries for support of key readers</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NightLabs GmbH</p>
 * @author Marco Schulze
 * @version 1.0
 */

public class KeyReadEvent extends EventObject
{
	private static final long serialVersionUID = 2L;

	private Date timestamp;
	public Date getTimestamp() { return timestamp; }

	private String key;
	public String getKey() { return key; }

	public KeyReadEvent(KeyReader keyReader, String _key)
	{
		super(keyReader);
		this.timestamp = new Date();
		this.key = _key;
	}
}
