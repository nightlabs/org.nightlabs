/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 23.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.util;

import java.awt.geom.Arc2D;

import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.GeneralShapeFactory;

public class EditorGeneralShapeFactory 
extends GeneralShapeFactory
{

	public EditorGeneralShapeFactory() {
		super();
	}

	public static GeneralShape createEllipse(org.eclipse.draw2d.geometry.Rectangle rect) 
	{
	  Arc2D arc = new Arc2D.Double(rect.x, rect.y, rect.width, rect.height, 0, 360, Arc2D.OPEN);      
	  return new GeneralShape(arc); 
	}
	
	public static GeneralShape createRectangle(org.eclipse.draw2d.geometry.Rectangle rect) 
	{      
	  return createRectangle(rect.x, rect.y, rect.width, rect.height); 
	}
	
}
