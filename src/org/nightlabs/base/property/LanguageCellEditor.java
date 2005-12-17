/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.nightlabs.base.language.I18nTextEditor;
import org.nightlabs.base.language.LanguageChooserImageCombo;
import org.nightlabs.i18n.I18nText;

public class LanguageCellEditor 
extends CellEditor
{	
	protected I18nText text;
	public LanguageCellEditor(I18nText text, Composite parent) 
	{
		super(parent, SWT.DEFAULT);	
		this.text = text;		
	}

	protected LanguageChooserImageCombo comboBox;
	public LanguageChooserImageCombo getLanguageChooser() {
		return comboBox;
	}	
	
	protected I18nTextEditor textEditor;
		
  /**
   * Creates a ColorCombo and adds some listener to it
   */
  protected Control createControl(Composite parent) 
  {	
  	Composite panel = new Composite(parent, SWT.DEFAULT);  	
	  comboBox = new LanguageChooserImageCombo(panel, false);
	  textEditor = new I18nTextEditor(panel, getLanguageChooser());
	  
	  return panel;
	}
	
  /**
   * sets the focus to the ComboBox
   */
  protected void doSetFocus() {
    if (textEditor != null) {
    	textEditor.setFocus();    	
    }
  }

	protected Object doGetValue() {
		return textEditor.getEditText();
	}

	protected void doSetValue(Object value) {
		if (value instanceof I18nText)
			textEditor.setI18nText((I18nText)value);
	}
    
  
}