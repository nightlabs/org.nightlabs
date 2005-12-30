/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.language;

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

import org.nightlabs.base.custom.ColorCombo;
import org.nightlabs.language.LanguageCf;

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
