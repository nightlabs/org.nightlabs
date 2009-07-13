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

//public class PositionConstants
public interface PositionConstants
{
	public static final int ALIGNMENT_LEFT = 1;
	public static final int ALIGNMENT_CENTER = 2;
	public static final int ALIGNMENT_RIGHT = 3;
	
	/** None */
	int NONE  =  0;

	/** Left */
	int LEFT   =  1;
	/** Center (Horizontal) */
	int CENTER =  2;
	/** Right */
	int RIGHT  =  4;

	/** Top */
	int TOP    =  8;
	/** Middle (Vertical) */
	int MIDDLE = 16;
	/** Bottom */
	int BOTTOM = 32;
	
	/** North */
	int NORTH =  1;
	/** South */
	int SOUTH =  4;
	/** West */
	int WEST  =  8;
	/** East */
	int EAST  = 16;

	/** A constant indicating horizontal direction */
	int HORIZONTAL = 64;
	/** A constant indicating vertical direction */
	int VERTICAL = 128;

	/** North-East: a bit-wise OR of {@link #NORTH} and {@link #EAST} */
	int NORTH_EAST = NORTH | EAST;
	/** North-West: a bit-wise OR of {@link #NORTH} and {@link #WEST} */
	int NORTH_WEST = NORTH | WEST;
	/** South-East: a bit-wise OR of {@link #SOUTH} and {@link #EAST} */
	int SOUTH_EAST = SOUTH | EAST;
	/** South-West: a bit-wise OR of {@link #SOUTH} and {@link #WEST} */
	int SOUTH_WEST = SOUTH | WEST;
	/** North-South: a bit-wise OR of {@link #NORTH} and {@link #SOUTH} */
	int NORTH_SOUTH = NORTH | SOUTH;
	/** East-West: a bit-wise OR of {@link #EAST} and {@link #WEST} */
	int EAST_WEST = EAST | WEST;
}
