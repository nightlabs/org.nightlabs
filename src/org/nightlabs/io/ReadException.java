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
package org.nightlabs.io;

import java.io.FileNotFoundException;
import java.io.IOException;


public class ReadException
extends IOException
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
  boolean fileNotFound = false;
  boolean ioException = false;

  public ReadException(String message)
  {
    super(message);
  }

  public ReadException(String message, Throwable cause)
  {
    super(message);
    initCause(cause);

    if (cause instanceof FileNotFoundException) {
      fileNotFound = true;
    }
    else if (cause instanceof IOException) {
      ioException = true;
    }
  }

}
