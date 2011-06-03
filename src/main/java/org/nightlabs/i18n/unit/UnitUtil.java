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
package org.nightlabs.i18n.unit;

/**
 * @version $Revision: 13220 $ - $Date: 2009-01-21 17:51:30 +0100 (Mi, 21 Jan 2009) $
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class UnitUtil
{
	/**
	 * returns the value in the given second unit
	 *
	 * @param value the value given in the valueUnit
	 * @param valueUnit the unit of the value
	 * @param unit the unit in which the value should be converted
	 * @return the value in the given second unit
	 */
	public static double getUnitValue(double value, IUnit valueUnit, IUnit unit)
	{
		if (unit == null)
			throw new IllegalArgumentException("Param unit must not be null");

		if (valueUnit == null)
			throw new IllegalArgumentException("Param valueUnit must not be null");

		if (unit.equals(valueUnit))
			return value;
		else {
			double unitFactor = unit.getFactor();
			double valueUnitFactor = valueUnit.getFactor();
			double returnVal = (value / valueUnitFactor) * unitFactor;
			return returnVal;
		}
	}
}
