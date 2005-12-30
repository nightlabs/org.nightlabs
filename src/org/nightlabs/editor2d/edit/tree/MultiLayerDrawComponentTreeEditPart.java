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

package org.nightlabs.editor2d.edit.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.editpolicy.DrawComponentContainerEditPolicy;
import org.nightlabs.editor2d.editpolicy.tree.MultiLayerDrawComponentTreeEditPolicy;
import org.nightlabs.editor2d.outline.filter.FilterManager;


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
   * @see org.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart#getIcon()
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
