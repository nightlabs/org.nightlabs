/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.rcp.property.I18nTextLabelProvider;

public class NamePropertyDescriptor 
extends PropertyDescriptor
{
	protected DrawComponent dc;
	public NamePropertyDescriptor(DrawComponent dc, Object id, String displayName) {
		super(id, displayName);
		this.dc = dc;
	}

  public ILabelProvider getLabelProvider() {
//  	return new I18nTextLabelProvider(dc.getI18nText(), dc.getLanguageId());
  	return new I18nTextLabelProvider(dc.getI18nText(),
  			NameLanguageManager.sharedInstance().getCurrentLanguageID());  	
	}	  
  
  public CellEditor createPropertyEditor(Composite parent) {
    CellEditor editor = new TextCellEditor(parent);
    if (getValidator() != null)
        editor.setValidator(getValidator());
    return editor;
  } 
}
