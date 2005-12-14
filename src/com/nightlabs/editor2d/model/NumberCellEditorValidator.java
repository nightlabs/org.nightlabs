package com.nightlabs.editor2d.model;

import org.eclipse.jface.viewers.ICellEditorValidator;

import com.nightlabs.editor2d.EditorPlugin;

public class NumberCellEditorValidator
implements ICellEditorValidator
{
	private static NumberCellEditorValidator sharedInstance;

	public static NumberCellEditorValidator getSharedInstance() {
		if (sharedInstance == null) 
			sharedInstance = new NumberCellEditorValidator();
		return sharedInstance;
	}

	public String isValid(Object value) {
		try {
			new Integer((String)value);
			return null;
		} catch (NumberFormatException exc) {
			return EditorPlugin.getResourceString("property.error.notanumber");
		}
	}
}
