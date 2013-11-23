/**
 * 
 */
package org.nightlabs.i18n;

import org.nightlabs.util.NLLocale;

/**
 * An {@link I18nTextBuffer} that allows the setting of a non-internationalized String.
 * It will be set as fallback-value and as value for the current locale in {@link NLLocale}.
 * 
 * @author Alexander Bieber
 * @version $Revision: 13191 $, $Date: 2009-01-18 22:18:46 +0100 (So, 18 Jan 2009) $
 */
public class StaticI18nText extends I18nTextBuffer {

	private static final long serialVersionUID = 20090116L;

	/**
	 * 
	 */
	public StaticI18nText(String staticText) {
		setFallBackValue(staticText);
	}
	
	/**
	 * Sets the given text as fallback value and for the language
	 * of the current locale in {@link NLLocale}.
	 * 
	 * @param staticText The text to set.
	 */
	public void setStaticText(String staticText) {
		if (staticText == null)
			staticText = "";
		setText(NLLocale.getDefault().getLanguage(), staticText);
		setFallBackValue(staticText);
	}
}
