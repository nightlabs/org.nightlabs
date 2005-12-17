/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 05.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.action;

import org.eclipse.jface.action.IAction;

public interface INewFileAction
extends IAction
{
	public String getFileExtension();
	public void setFileExtension(String fileExtension);
}
