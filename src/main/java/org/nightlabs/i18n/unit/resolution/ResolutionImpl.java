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
package org.nightlabs.i18n.unit.resolution;

/**
 * This class is the implementation of the {@link Resolution}-interface
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class ResolutionImpl
implements Resolution
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	private IResolutionUnit resolutionUnit = new DPIResolutionUnit();
	private double resolutionX = DEFAULT_RESOLUTION_DPI;
	private double resolutionY = DEFAULT_RESOLUTION_DPI;
	
	public ResolutionImpl(IResolutionUnit unit, double resolutionX, double resolutionY)
	{
		this.resolutionUnit = unit;
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
	}

	public ResolutionImpl(IResolutionUnit unit, double resolution)
	{
		this.resolutionUnit = unit;
		this.resolutionX = resolution;
		this.resolutionY = resolution;
	}
	
	public ResolutionImpl()
	{
		this.resolutionUnit = new DPIResolutionUnit();
		this.resolutionX = DEFAULT_RESOLUTION_DPI;
		this.resolutionY = DEFAULT_RESOLUTION_DPI;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#getResolutionUnit()
	 */
	public IResolutionUnit getResolutionUnit() {
		return resolutionUnit;
	}
		
	/**
	 * {@inheritDoc}
	 * <p>
	 * For the default value see {@link #DEFAULT_RESOLUTION_DPI}
	 */
	public double getResolutionX() {
		return resolutionX;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#setResolutionX(double)
	 */
	public void setResolutionX(double value) {
		this.resolutionX = value;
	}
		
	/**
	 * {@inheritDoc}
	 * <p>
	 * For the default value see {@link #DEFAULT_RESOLUTION_DPI}
	 */
	public double getResolutionY() {
		return resolutionY;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#setResolutionY(double)
	 */
	public void setResolutionY(double value) {
		this.resolutionY = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("How the hell can clone fail?!", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#getResolutionX(org.nightlabs.i18n.unit.resolution.IResolutionUnit)
	 */
	public double getResolutionX(IResolutionUnit unit)
	{
		if (unit.equals(resolutionUnit))
			return getResolutionX();
		else {
			double oldfactor = resolutionUnit.getUnit().getFactor();
			return (resolutionX * unit.getUnit().getFactor()) / oldfactor;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#getResolutionY(org.nightlabs.i18n.unit.resolution.IResolutionUnit)
	 */
	public double getResolutionY(IResolutionUnit unit)
	{
		if (unit.equals(resolutionUnit))
			return getResolutionY();
		else {
			double oldfactor = resolutionUnit.getUnit().getFactor();
			return (resolutionY * unit.getUnit().getFactor()) / oldfactor;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.i18n.unit.resolution.Resolution#setResolutionUnit(org.nightlabs.i18n.unit.resolution.IResolutionUnit)
	 */
	public void setResolutionUnit(IResolutionUnit unit)
	{
		if (unit.getUnit().getFactor() != this.resolutionUnit.getUnit().getFactor()) {
			double oldfactor = resolutionUnit.getUnit().getFactor();
			resolutionX = (resolutionX * unit.getUnit().getFactor()) / oldfactor;
			resolutionY = (resolutionY * unit.getUnit().getFactor()) / oldfactor;
		}
		this.resolutionUnit = unit;
	}

	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("resolutionX = "+resolutionX);
		sb.append(", ");
		sb.append("resolutionY = "+resolutionY);
		sb.append(" ");
		if (resolutionUnit != null) {
			sb.append(resolutionUnit.getName().getText());
		}
		return sb.toString();
	}
	
}
