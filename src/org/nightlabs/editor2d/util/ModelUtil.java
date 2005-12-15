/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.09.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.util;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.outline.filter.FilterNameProvider;

public class ModelUtil
implements FilterNameProvider
{

	public ModelUtil() {
		super();
	}

	public String getTypeName(Class c) 
	{
//		if (DrawComponent.class.isAssignableFrom(c)) {
//			return EditorPlugin.getResourceString("model.drawComponent.name");
//		}
//		else if (ShapeDrawComponent.class.isAssignableFrom(c)) {
//			return EditorPlugin.getResourceString("model.shapeDrawComponent.name");
//		}
		if (ImageDrawComponent.class.isAssignableFrom(c)) {
			return EditorPlugin.getResourceString("model.imageDrawComponent.name");
		}
		else if (RectangleDrawComponent.class.isAssignableFrom(c)) {
			return EditorPlugin.getResourceString("model.rectangleDrawComponent.name");
		}
		else if (EllipseDrawComponent.class.isAssignableFrom(c)) {
			return EditorPlugin.getResourceString("model.ellipseDrawComponent.name");
		}		
		else if (LineDrawComponent.class.isAssignableFrom(c)) {
			return EditorPlugin.getResourceString("model.lineDrawComponent.name");
		}
		else if (TextDrawComponent.class.isAssignableFrom(c)) {
			return EditorPlugin.getResourceString("model.textDrawComponent.name");
		}
		
		return EditorPlugin.getResourceString("model.drawComponent.name");
	}
}
