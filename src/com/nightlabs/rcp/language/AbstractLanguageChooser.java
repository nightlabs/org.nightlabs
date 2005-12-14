/*
 * Created on Jan 6, 2005
 */
package com.nightlabs.rcp.language;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.nightlabs.language.LanguageCf;
import com.nightlabs.rcp.composite.XComposite;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractLanguageChooser
	extends XComposite
	implements LanguageChooser
{
	private List languageChangeListeners = new LinkedList();

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 */
	public AbstractLanguageChooser(Composite parent, int style,
			boolean setLayoutData)
	{
		super(parent, style, XComposite.LAYOUT_MODE_TIGHT_WRAPPER,
				setLayoutData ? XComposite.LAYOUT_DATA_MODE_GRID_DATA : LAYOUT_DATA_MODE_NONE);
	}
	
	/**
	 * @see com.nightlabs.ipanema.language.LanguageChooser#addLanguageChangeListener(com.nightlabs.ipanema.language.LanguageChangeListener)
	 */
	public void addLanguageChangeListener(LanguageChangeListener l)
	{
		languageChangeListeners.add(l);
	}

	/**
	 * @see com.nightlabs.ipanema.language.LanguageChooser#removeLanguageChangeListener(com.nightlabs.ipanema.language.LanguageChangeListener)
	 */
	public void removeLanguageChangeListener(LanguageChangeListener l)
	{
		languageChangeListeners.remove(l);
	}
	
	private LanguageCf oldLanguage = null;

	public void fireLanguageChangeEvent()
	{
		if (languageChangeListeners.size() < 1)
			return;

		LanguageChangeEvent languageChangeEvent = new LanguageChangeEvent(this, oldLanguage, getLanguage());
		for (Iterator it = languageChangeListeners.iterator(); it.hasNext(); ) {
			((LanguageChangeListener)it.next()).languageChanged(languageChangeEvent);
		}

		oldLanguage = getLanguage();
	}
}
