/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.editpolicy;

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

