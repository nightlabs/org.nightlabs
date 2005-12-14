/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 01.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.language;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nightlabs.language.LanguageCf;
import com.nightlabs.rcp.custom.ColorCombo;

public class LanguageContributionItem 
extends ContributionItem
{
	public static final String ID = LanguageContributionItem.class.getName();
	
	protected LanguageManager langMan;
	protected boolean onlyImage = true;
	
	public LanguageContributionItem(LanguageManager langMan) {
		this(langMan, false);
	}
	
	public LanguageContributionItem(LanguageManager langMan, boolean onlyImage) {
		super(ID);
		this.langMan = langMan;
		this.onlyImage = onlyImage;
	}

	protected Map index2Language;
	protected Control createControl(Composite parent) 
	{
		ColorCombo combo = new ColorCombo(parent, SWT.DEFAULT);
		combo.setSize(200, 30);
		combo.addSelectionListener(comboListener);
		index2Language = new HashMap();
		int index = 0;
		
		for (Iterator it = langMan.getLanguages().iterator(); it.hasNext(); ) {
			LanguageCf language = (LanguageCf) it.next();
			Image image = LanguageManager.getImage(language.getLanguageID());
			if (onlyImage)
				combo.add(image, "", index);
			else
				combo.add(image, LanguageManager.getNativeLanguageName(language.getLanguageID()), index);
			index2Language.put(new Integer(index), language);
			index++;			
		}
		return combo;
	}

	public void fill(Composite parent) {
		createControl(parent);
	}

	public void fill(ToolBar parent, int index) {
		ToolItem toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
  	Control control = createControl(parent);
  	toolitem.setControl(control);	
	}
		
	protected SelectionListener comboListener = new SelectionListener() 
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			ColorCombo combo = (ColorCombo) e.getSource();
			int index = combo.getSelectionIndex();
			LanguageCf language = (LanguageCf) index2Language.get(new Integer(index));
			// TODO: Must set cuurentLanguage
			langMan.setCurrentLanguage(language);
		}	
	};
}
