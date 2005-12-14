/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.command.DeleteDrawComponentCommand;

public class DrawComponentEditPolicy 
extends ComponentEditPolicy
{
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) 
	{
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
//		if (parent instanceof MultiLayerDrawComponent && child instanceof DrawComponent) {
//		return new DeleteDrawComponentCommand((MultiLayerDrawComponent) parent, (DrawComponent) child);		
//	}		
		if (parent instanceof DrawComponentContainer && child instanceof DrawComponent) {
			return new DeleteDrawComponentCommand((DrawComponentContainer) parent, (DrawComponent) child);		
		}
		return super.createDeleteCommand(deleteRequest);	  
	}
}