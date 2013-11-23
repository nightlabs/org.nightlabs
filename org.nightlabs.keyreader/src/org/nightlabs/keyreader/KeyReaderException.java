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

/**
 * <p>Title: NightLabsBarcodeReaders</p>
 * <p>Description: shared libraries for support of barcode readers</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NightLabs GmbH</p>
 * @author Marco Schulze
 * @version 1.0
 */

public class KeyReaderException extends Exception
{
	private static final long serialVersionUID = 1L;

	public KeyReaderException()
	{
	}

	public KeyReaderException(String message)
	{
		super(message);
	}

	public KeyReaderException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public KeyReaderException(Throwable cause)
	{
		super(cause);
	}
}
