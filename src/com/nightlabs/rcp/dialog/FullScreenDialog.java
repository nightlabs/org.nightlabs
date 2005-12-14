/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 27.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

public class FullScreenDialog extends Dialog
{

	public FullScreenDialog(Shell arg0) {
		super(arg0);
	}

	public FullScreenDialog(IShellProvider arg0) {
		super(arg0);
	}

	public void create() 
	{
		super.create();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		getShell().setSize(screenSize.width, screenSize.height);
		getShell().setLocation(0,0);				
	}
	
}
