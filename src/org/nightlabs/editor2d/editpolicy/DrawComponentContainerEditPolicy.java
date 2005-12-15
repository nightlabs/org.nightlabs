/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 28.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


public class DrawComponentContainerEditPolicy 
extends ContainerEditPolicy 
{

  /* (non-Javadoc)
   * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
   */
  protected Command getCreateCommand(CreateRequest request) {
    return null;
  }
  
//  public Command getOrphanChildrenCommand(GroupRequest request) 
//  {
//  	List parts = request.getEditParts();
//  	CompoundCommand result = 
//  		new CompoundCommand(EditorPlugin.getResourceString("command_orphan_children"));
//  	for (int i = 0; i < parts.size(); i++) {
//  		OrphanChildCommand orphan = new OrphanChildCommand();
//  		orphan.setChild((LogicSubpart)((EditPart)parts.get(i)).getModel());
//  		orphan.setParent((LogicDiagram)getHost().getModel());
//  		orphan.setLabel(LogicMessages.LogicElementEditPolicy_OrphanCommandLabelText);
//  		result.add(orphan);
//  	}
//  	return result.unwrap();
//  }  
}
