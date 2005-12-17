/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.base.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.language.LanguageCf;

/**
 * This is an implementation of LanguageChooser showing all registered languages
 * in a combo box and automatically selecting the one from Locale.getDefault().getLanguage()
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageChooserCombo
	extends AbstractLanguageChooser
{
	public static final Logger LOGGER = Logger.getLogger(LanguageChooserCombo.class);

	private Combo combo;
	private List languages = new ArrayList();

	public LanguageChooserCombo(Composite parent)
	{
		this(parent, true);
	}

	public LanguageChooserCombo(Composite parent, boolean grabExcessHorizontalSpace)
	{
		super(parent, SWT.NONE, true);
		((GridData)getLayoutData()).grabExcessVerticalSpace = false;
		((GridData)getLayoutData()).grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		combo = new Combo(this, SWT.BORDER | SWT.READ_ONLY);
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
		  	combo.add(language.getNativeName());
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

	/**
	 * @see org.nightlabs.base.language.LanguageChooser#getCurrentLanguage()
	 */
	public LanguageCf getLanguage()
	{
		return (LanguageCf)languages.get(combo.getSelectionIndex());
	}

}
