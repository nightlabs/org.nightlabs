/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 24.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.actions;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


public abstract class ZoomAction 
extends Action
implements ZoomListener, Disposable
{
	/**
	* The ZoomManager used to zoom in or out 
	*/
	protected ZoomManager zoomManager;
	
	/**
	* Constructor
	* @param text the action's text, or <code>null</code> if there is no text
	* @param image the action's image, or <code>null</code> if there is no image
	* @param zoomManager the ZoomManager used to zoom in or out
	*/
	public ZoomAction(String text, ImageDescriptor image, ZoomManager zoomManager) 
	{
		super(text, image);
		this.zoomManager = zoomManager;
		zoomManager.addZoomListener(this);
	}
	
	/**
	* @see org.eclipse.gef.Disposable#dispose()
	*/
	public void dispose() {
	  zoomManager.removeZoomListener(this);
	} 

}
