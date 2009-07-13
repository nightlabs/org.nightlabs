/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.util;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.ResolutionUnit;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class UnitUtil
{
	/**
	 * returns the value of the model in the given unit
	 * 
	 * @param modelValue the value fo the model given in dots {@link DotUnit}
	 * @param dotUnit the dotUnit of the model {@link RootDrawComponent#getResolution()}
	 * @param unit the unit in which you want to get the modelValue {@link IUnit}
	 * @return the value of the model for the given unit
	 */
	public static double getUnitValue(int modelValue, DotUnit dotUnit, IUnit unit)
	{
		if (unit.equals(dotUnit))
			return modelValue;
		else {
			double unitFactor = unit.getFactor();
			double dotFactor = dotUnit.getFactor();
			double returnVal = (modelValue / dotFactor) * unitFactor;
			return returnVal;
		}
	}

	/**
	 * returns the value of the model in the given unit
	 * 
	 * @param modelValue the value fo the model given in dots {@link DotUnit}
	 * @param root the {@link RootDrawComponent} where the modelValue comes from
	 * @param unit the unit in which you want to get the modelValue {@link IUnit}
	 * @return the value of the model for the given unit
	 */
	public static double getUnitValue(int modelValue, RootDrawComponent root, IUnit unit)
	{
		DotUnit dotUnit = new DotUnit(root.getResolution());
		return getUnitValue(modelValue, dotUnit, unit);
	}
	
	/**
	 * returns the value drawComponent of the given property in the given unit
	 * 
	 * @param dc the {@link DrawComponent} to get the value from
	 * @param propertyID the ID of the property, either {@link DrawComponent#PROP_X},
	 * {@link DrawComponent#PROP_Y}, {@link DrawComponent#PROP_WIDTH} or {@link DrawComponent#PROP_HEIGHT}
	 * @param unit the unit in which you want to get the modelValue {@link IUnit}
	 * @return the value of the property in the given unit or -1 if the propertyID is unknown
	 */
	public static double getUnitValue(DrawComponent dc, String propertyID, IUnit unit)
	{
		DotUnit dotUnit = new DotUnit(dc.getRoot().getResolution());
		
		if (propertyID.equals(DrawComponent.PROP_X))
			return getUnitValue(dc.getX(), dotUnit, unit);
		else if (propertyID.equals(DrawComponent.PROP_Y))
			return getUnitValue(dc.getY(), dotUnit, unit);
		else if (propertyID.equals(DrawComponent.PROP_WIDTH))
			return getUnitValue(dc.getWidth(), dotUnit, unit);
		else if (propertyID.equals(DrawComponent.PROP_HEIGHT))
			return getUnitValue(dc.getHeight(), dotUnit, unit);
		
		return -1;
	}
	
	/**
	 * returns the value in the given dotUnit
	 * 
	 * @param value the value in the given unit
	 * @param dotUnit the dotUnit of the model {@link RootDrawComponent#getResolution()}
	 * @param unit the unit of the given value
	 * @return the value in the given dotUnit
	 */
	public static int getModelValue(double value, DotUnit dotUnit, IUnit unit)
	{
		if (unit.equals(dotUnit))
			return (int) Math.rint(value);
		else {
			double unitFactor = unit.getFactor();
			double dotFactor = dotUnit.getFactor();
			double returnVal = (value / unitFactor) * dotFactor;
			return (int) Math.rint(returnVal);
		}
	}
			
	public static Point2D getResolutionScale(double deviceDPI, DrawComponent dc)
	{
		ResolutionUnit modelUnit = dc.getRoot().getModelUnit();
		return getResolutionScale(deviceDPI, deviceDPI, modelUnit.getResolution());
	}
	
	public static Point2D getResolutionScale(double deviceDPI_X, double deviceDPI_Y, DrawComponent dc)
	{
		ResolutionUnit modelUnit = dc.getRoot().getModelUnit();
		return getResolutionScale(deviceDPI_X, deviceDPI_Y, modelUnit.getResolution());
	}
	
	public static Point2D getResolutionScale(double deviceDPIX, double deviceDPIY, Resolution res)
	{
		double modelResolutionX = res.getResolutionX(IResolutionUnit.dpiUnit);
		double modelResolutionY = res.getResolutionY(IResolutionUnit.dpiUnit);
		return new Point2D.Double(
				modelResolutionX / deviceDPIX,
				modelResolutionY / deviceDPIY);
	}
	
	public static double getResolutionScale(double deviceDPI, Resolution res)
	{
		double modelResolutionX = res.getResolutionX(IResolutionUnit.dpiUnit);
		double modelResolutionY = res.getResolutionY(IResolutionUnit.dpiUnit);
		double modelResolution = Math.max(modelResolutionX, modelResolutionY);
		return modelResolution / deviceDPI;
	}
	
	/**
	 * returns the converted bounds of a drawComponent in the given boundsUnit
	 * 
	 * @param dc the DrawComponent to get the bounds from
	 * @param boundsUnit the unit in which you want to get the bounds
	 * @return the converted bounds in the given boundsUnit
	 */
	public static Rectangle2D getUnitBounds(DrawComponent dc, IUnit boundsUnit)
	{
		Rectangle bounds = dc.getBounds();
		DotUnit modelUnit = dc.getRoot().getModelUnit();
		double x = getUnitValue(bounds.x, modelUnit, boundsUnit);
		double y = getUnitValue(bounds.y, modelUnit, boundsUnit);
		double width = getUnitValue(bounds.width, modelUnit, boundsUnit);
		double height = getUnitValue(bounds.height, modelUnit, boundsUnit);
		return new Rectangle2D.Double(x, y, width, height);
	}
	
}
