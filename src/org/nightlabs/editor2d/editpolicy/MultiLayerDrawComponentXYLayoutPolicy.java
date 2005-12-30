/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.editpolicy;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.XYLayout;



public class MultiLayerDrawComponentXYLayoutPolicy  
extends DrawComponentContainerXYLayoutPolicy
{
  public static final Logger LOGGER = Logger.getLogger(MultiLayerDrawComponentXYLayoutPolicy.class);
  
	/** 
	 * Create a new instance of this edit policy.
	 * @param layout a non-null XYLayout instance. This should be the layout of the editpart's 
	 *              figure where this instance is installed.
	 * @throws IllegalArgumentException if layout is null 
	 * @see DiagramEditPart#createEditPolicies()
	 */
	public MultiLayerDrawComponentXYLayoutPolicy(XYLayout layout)
	{
	  super(layout);
	}
		
//	public DrawComponentContainer getDrawComponentContainer() 
//	{
//		MultiLayerDrawComponent mldc = (MultiLayerDrawComponent)getHost().getModel();
//		return mldc.getCurrentLayer();		
//	}
}

