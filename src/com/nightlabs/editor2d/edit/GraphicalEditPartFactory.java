/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.nightlabs.editor2d.EllipseDrawComponent;
import com.nightlabs.editor2d.ImageDrawComponent;
import com.nightlabs.editor2d.Layer;
import com.nightlabs.editor2d.LineDrawComponent;
import com.nightlabs.editor2d.MultiLayerDrawComponent;
import com.nightlabs.editor2d.RectangleDrawComponent;
import com.nightlabs.editor2d.TextDrawComponent;

public class GraphicalEditPartFactory 
implements EditPartFactory
{
	/**
	 * 
	 */
	public GraphicalEditPartFactory()
	{
		super();
	}

	/* 
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model)
	{
    if(model instanceof MultiLayerDrawComponent)
      return new MultiLayerDrawComponentEditPart((MultiLayerDrawComponent)model);
    
    else if (model instanceof Layer)
      return new LayerEditPart((Layer)model, (MultiLayerDrawComponentEditPart)context);
        
    else if (model instanceof EllipseDrawComponent)
    	return new EllipseEditPart((EllipseDrawComponent)model);

    else if (model instanceof RectangleDrawComponent)
    	return new RectangleEditPart((RectangleDrawComponent)model);

    else if (model instanceof LineDrawComponent)
      return new LineEditPart((LineDrawComponent)model);

    else if (model instanceof TextDrawComponent)
      return new TextEditPart((TextDrawComponent)model);

    else if (model instanceof ImageDrawComponent)
      return new ImageEditPart((ImageDrawComponent)model);
    
    return null;
	}

}