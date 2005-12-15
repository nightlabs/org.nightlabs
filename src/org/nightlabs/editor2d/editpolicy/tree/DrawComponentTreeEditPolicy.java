/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.editpolicy.tree;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;


public class DrawComponentTreeEditPolicy 
extends AbstractEditPolicy 
{
  public Command getCommand(Request req){
  	if (REQ_MOVE.equals(req.getType()))
  		return getMoveCommand((ChangeBoundsRequest)req);
  	return null;	
  }

  protected Command getMoveCommand(ChangeBoundsRequest req){
  	EditPart parent = getHost().getParent();
  	if(parent != null){
  		ChangeBoundsRequest request = new ChangeBoundsRequest(REQ_MOVE_CHILDREN);
  		request.setEditParts(getHost());
  		request.setLocation(req.getLocation());
  		return parent.getCommand(request);
  	}
  	return UnexecutableCommand.INSTANCE;
  }

}
