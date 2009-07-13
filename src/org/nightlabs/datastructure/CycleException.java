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
package org.nightlabs.datastructure;

import java.util.Set;

/**
 * This exception indicates a directed graph to have a cycle when this is not allowed.
 *
 * @version $Revision$ - $Date$
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class CycleException extends Exception {

	private static final long serialVersionUID = -6297799111846699200L;
	private String cycleInfo;
	private Set<Object> remainingElements;

	public CycleException(String cycleInfo, Set<Object> remainingElements) {
		this("Graph contains a cycle: " + cycleInfo);
		this.remainingElements = remainingElements;
	}

	public CycleException(String cycleInfo) {
		super("Graph contains a cycle: " + cycleInfo);
		this.cycleInfo = cycleInfo;
	}

	public String getCycleInfo() {
		return cycleInfo;
	}

	/**
	 * @return the remaining elements whose corresponding nodes caused the cycle(s).
	 */
	public Set<Object> getRemainingElements()
	{
		return remainingElements;
	}
}
