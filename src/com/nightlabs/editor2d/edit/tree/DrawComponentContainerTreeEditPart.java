/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.editpolicy.DrawComponentContainerEditPolicy;
import com.nightlabs.editor2d.editpolicy.tree.DrawComponentTreeContainerEditPolicy;

public abstract class DrawComponentContainerTreeEditPart 
extends DrawComponentTreeEditPart 
{
  /**
   * Constructor initializes this with the given model.
   *
   * @param model  Model for this.
   */
  public DrawComponentContainerTreeEditPart(DrawComponentContainer model) {
  	super(model);
  }
    
  /**
   * Creates and installs pertinent EditPolicies.
   */
  protected void createEditPolicies() 
  {
  	super.createEditPolicies();
  	installEditPolicy(EditPolicy.CONTAINER_ROLE, new DrawComponentContainerEditPolicy());
  	installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE, new DrawComponentTreeContainerEditPolicy());
  	//If this editpart is the contents of the viewer, then it is not deletable!
  	if (getParent() instanceof RootEditPart)
  		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
  }

  /**
   * Returns the model of this as a LogicDiagram.
   *
   * @return  Model of this.
   */
  protected DrawComponentContainer getDrawComponentContainer() {
  	return (DrawComponentContainer)getModel();
  }

  /**
   * Returns the children of this from the model,
   * as this is capable enough of holding EditParts.
   *
   * @return  List of children.
   */
  protected List getModelChildren() 
  {
  	return getDrawComponentContainer().getDrawComponents();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) {
			LOGGER.debug(propertyName);
			refreshChildren();			
		}
		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) {
			LOGGER.debug(propertyName);
			refreshChildren();			
		}		
	}  
  
//  /**
//   * Returns the children of this from the model,
//   * as this is capable enough of holding EditParts.
//   *
//   * @return  List of children.
//   */
//  protected List getModelChildren() 
//  {
//  	if (!getFilterMan().isAllFilterSet()) {
//    	List filterChildren = new ArrayList();
//    	for (Iterator itFilter = getFilterMan().getFilters().iterator(); itFilter.hasNext(); ) {
//    		Class filter = (Class) itFilter.next();
//    		for (Iterator it = getDrawComponentContainer().getDrawComponents().iterator(); it.hasNext(); ) {
//    			DrawComponent dc = (DrawComponent) it.next();
//    			if (filter.isAssignableFrom(dc.getClass())) {
//    				filterChildren.add(dc);
//    			}
//    		}
//    	}
//    	return filterChildren;  		
//  	}
//  	else {
//  		return getDrawComponentContainer().getDrawComponents();
//  	}
//  }    
  
}
