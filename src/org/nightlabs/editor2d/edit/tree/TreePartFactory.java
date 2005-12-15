/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.edit.tree;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.outline.filter.FilterManager;


public class TreePartFactory 
implements EditPartFactory 
{
	public TreePartFactory(FilterManager filterMan) 
	{
		if (filterMan == null)
			throw new IllegalArgumentException("Param filterMan must not be null!");
		
		this.filterMan = filterMan;
	}
	protected FilterManager filterMan;
	
  public EditPart createEditPart(EditPart context, Object model) 
  {
  	if (model instanceof MultiLayerDrawComponent)
  		return new MultiLayerDrawComponentTreeEditPart((MultiLayerDrawComponent)model, filterMan);

  	else if (model instanceof Layer)
  		return new LayerTreeEditPart((Layer)model);
  	  	
  	else if (model instanceof EllipseDrawComponent)
  		return new EllipseTreeEditPart((EllipseDrawComponent)model);

  	else if (model instanceof RectangleDrawComponent)
  		return new RectangleTreeEditPart((RectangleDrawComponent)model);

  	else if (model instanceof LineDrawComponent)
  		return new LineTreeEditPart((LineDrawComponent)model);

    else if (model instanceof TextDrawComponent)
      return new TextTreeEditPart((TextDrawComponent)model);
    
    else if (model instanceof ImageDrawComponent)
      return new ImageTreeEditPart((ImageDrawComponent)model);
    
    return null;
  }

}
