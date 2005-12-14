/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class CheckboxPropertyDescriptor 
extends PropertyDescriptor
{
	/**
	 * @param id
	 * @param displayName
	 */
	public CheckboxPropertyDescriptor(Object id, String displayName) 
	{
		super(id, displayName);
		setValidator(new BooleanValidator());
	}

	/*
	 * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
	 */
	public CellEditor createPropertyEditor(Composite parent) 
	{
		CellEditor editor = new CheckboxCellEditor(parent);
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}

}

