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

package org.nightlabs.editor2d;

public interface EllipseDrawComponent
extends ShapeDrawComponent, IConnectable
{
	public static final String PROP_START_ANGLE = "startAngle";
	public static final String PROP_END_ANGLE = "endAngle";
	
  public static final int START_ANGLE_DEFAULT = 0;
  public static final int END_ANGLE_DEFAULT = 360;
  public static final boolean CONNECT_DEFAULT = false;
  
	/**
	 * returns the startAngle of the EllipseDrawComponent in degrees
	 * @return the startAngle of the EllipseDrawComponent in degrees
	 */
  int getStartAngle();
  /**
   * sets the startAngle of the EllipseDrawComponent in degrees
   * @param value the new startAngle to set
   */
  void setStartAngle(int value);

  /**
   * returns the endAngle of the EllipseDrawComponent in degrees
   * @return the endAngle of the EllipseDrawComponent in degrees
   */
  int getEndAngle();
  
  /**
   * sets the endAngle of the EllipseDrawComponent in degrees
   * @param value the new endAngle to set
   */
  void setEndAngle(int value);
} // EllipseDrawComponent
