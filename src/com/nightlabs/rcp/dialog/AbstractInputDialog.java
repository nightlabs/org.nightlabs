/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.dialog;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import com.nightlabs.base.NLBasePlugin;

public abstract class AbstractInputDialog 
extends InputDialog
{
	public AbstractInputDialog(Shell shell, String title, String message, String initalValue, IInputValidator validator) {
		super(shell, title, message, initalValue, validator);
	}

	public AbstractInputDialog(Shell shell, String title, String message, String initValue) {
		this(shell, title, message, initValue, inputValidator);
	}	

	public AbstractInputDialog(Shell shell, String title, String message) {
		this(shell, title, message, "", inputValidator);
	}	
		
	protected static IInputValidator inputValidator = new IInputValidator() 
	{
    public String isValid(String input) 
    {
      if(input.trim().equals(""))
        return NLBasePlugin.getResourceString("dialog.input.message.emptyString");
      
      return null;
    }		
	};
	
	public IInputValidator getInputValidator() {
		return inputValidator;
	}

	protected abstract void okPressed();
}
