/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.base.language;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.composite.XComposite;
import org.nightlabs.language.LanguageCf;

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
	 * @see org.nightlabs.ipanema.language.LanguageChooser#addLanguageChangeListener(org.nightlabs.ipanema.language.LanguageChangeListener)
	 */
	public void addLanguageChangeListener(LanguageChangeListener l)
	{
		languageChangeListeners.add(l);
	}

	/**
	 * @see org.nightlabs.ipanema.language.LanguageChooser#removeLanguageChangeListener(org.nightlabs.ipanema.language.LanguageChangeListener)
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
