/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.09.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.util;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.EllipseDrawComponent;
import com.nightlabs.editor2d.ImageDrawComponent;
import com.nightlabs.editor2d.LineDrawComponent;
import com.nightlabs.editor2d.RectangleDrawComponent;
import com.nightlabs.editor2d.TextDrawComponent;
import com.nightlabs.editor2d.outline.filter.FilterNameProvider;

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
