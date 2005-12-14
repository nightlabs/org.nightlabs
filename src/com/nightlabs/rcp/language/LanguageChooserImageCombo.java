/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.nightlabs.language.LanguageCf;
import com.nightlabs.rcp.custom.ColorCombo;
import com.nightlabs.rcp.exceptionhandler.ExceptionHandlerRegistry;

public class LanguageChooserImageCombo 
extends AbstractLanguageChooser
{
	public static final Logger LOGGER = Logger.getLogger(LanguageChooserImageCombo.class);
	
	protected ColorCombo combo;
	protected List languages = new ArrayList();
	
	public LanguageChooserImageCombo(Composite parent) {
		this(parent, true);
	}
	
	public LanguageChooserImageCombo(Composite parent, boolean grabExcessHorizontalSpace)
	{
		super(parent, SWT.NONE, true);
		((GridData)getLayoutData()).grabExcessVerticalSpace = false;
		((GridData)getLayoutData()).grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		combo = new ColorCombo(this, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent selectionEvent)
					{
						LOGGER.debug("new language: "+getLanguage().getLanguageID());
						fireLanguageChangeEvent();
					}
				});

		try {
			// TODO We should load the languages only once into the client!
			// They usually don't change...

		  String userLanguageID = Locale.getDefault().getLanguage();
		  int languageIdx = -1;
		  for (Iterator it = LanguageManager.sharedInstance().getLanguages().iterator(); it.hasNext(); ) {
		  	LanguageCf language = (LanguageCf) it.next();
		  	if (userLanguageID.equals(language.getLanguageID()))
		  		languageIdx = languages.size();
		  	languages.add(language);
		  	Image image = LanguageManager.getImage(language.getLanguageID());
		  	combo.add(image, language.getNativeName());
		  }

		  if (languageIdx < 0)
				throw new IllegalStateException("The user's language \""+userLanguageID+"\" is not registered in the server!");

			combo.select(languageIdx);
		} catch (RuntimeException e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw e;
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw new RuntimeException(e);
		}
	}

	public LanguageCf getLanguage() 
	{
		return (LanguageCf)languages.get(combo.getSelectionIndex());
	}

}
