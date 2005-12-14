/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.i18n.I18nText;

public class LanguagePropertyDescriptor 
extends PropertyDescriptor
{
	protected I18nText text;
	public LanguagePropertyDescriptor(I18nText text, Object id, String displayName) {
		super(id, displayName);
		this.text = text;
	}
	
  public ILabelProvider getLabelProvider() 
  {
  	return new LanguageLabelProvider(text);
	}

	public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new LanguageCellEditor(text, parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  } 

}
