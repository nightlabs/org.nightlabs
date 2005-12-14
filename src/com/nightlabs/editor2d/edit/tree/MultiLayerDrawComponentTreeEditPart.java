/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.Layer;
import com.nightlabs.editor2d.MultiLayerDrawComponent;
import com.nightlabs.editor2d.editpolicy.DrawComponentContainerEditPolicy;
import com.nightlabs.editor2d.editpolicy.tree.MultiLayerDrawComponentTreeEditPolicy;
import com.nightlabs.editor2d.outline.filter.FilterManager;


public class MultiLayerDrawComponentTreeEditPart 
extends DrawComponentContainerTreeEditPart 
{
  public static Image SUN_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/sun16.gif").createImage();  
  
  /**
   * @param model
   */
  public MultiLayerDrawComponentTreeEditPart(MultiLayerDrawComponent model, FilterManager filterMan) {
    super(model);
    this.filterMan = filterMan;
  }
//  public MultiLayerDrawComponentTreeEditPart(MultiLayerDrawComponent model) {
//    super(model);
//  }
  
  protected FilterManager filterMan;
  public FilterManager getFilterMan() {
  	return filterMan;
  }
  
  public MultiLayerDrawComponent getMultiLayerDrawComponent() {
  	return (MultiLayerDrawComponent) getModel();
  }
  
  /* (non-Javadoc)
   * @see com.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart#getIcon()
   */
  public Image getImage() {
    return SUN_ICON;
  }

  /**
   * Creates and installs pertinent EditPolicies.
   */
  protected void createEditPolicies() 
  {
  	super.createEditPolicies();
  	installEditPolicy(EditPolicy.CONTAINER_ROLE, new DrawComponentContainerEditPolicy());
  	installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE, new MultiLayerDrawComponentTreeEditPolicy());
  	//If this editpart is the contents of the viewer, then it is not deletable!
  	installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
  }  
  
  protected List getModelChildren()
  { 
  	if (getFilterMan().isAllFilterSet()) {
  		return getMultiLayerDrawComponent().getDrawComponents(); 
  	}
  	else {
      List filterChildren = new ArrayList(); 
    	for (Iterator itFilter = getFilterMan().getFilters().iterator(); itFilter.hasNext(); ) {
    		Class filter = (Class) itFilter.next();
        for (Iterator itLayers = getDrawComponentContainer().getDrawComponents().iterator(); itLayers.hasNext(); ) {
          Layer l = (Layer) itLayers.next();        
          for (Iterator itDrawOrder = l.getDrawComponents().iterator(); itDrawOrder.hasNext(); ) {
            DrawComponent dc = (DrawComponent) itDrawOrder.next();
      			if (filter.isAssignableFrom(dc.getClass())) {
      				filterChildren.add(dc);
      			}        
          }
        }
    	}      
      return filterChildren;  		
  	}
  }	  
}
