/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.i18n;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * Abstract class to be used for multilangual String members. <br/>
 * Subclass this, implement the methods and make the Map you return in
 * {@link #getI18nMap()} persistence-modifier="persistent" and the member
 * you return in {@link #getText()} persistence-modifier="transactional".
 *
 * TODO: Make I18nText and Language management work with Locales not with languageID
 *
 * @version $Revision: 14842 $ - $Date: 2009-06-26 16:31:45 +0200 (Fr, 26 Jun 2009) $
 * @author Alexander Bieber - alex at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class I18nText
implements Serializable
{
	private static final long serialVersionUID = 20090311L;

	public static final String DEFAULT_LANGUAGEID = Locale.ENGLISH.getLanguage();

	private static class ThreadLocale extends ThreadLocal<Locale> {
		@Override
		protected Locale initialValue() {
			return NLLocale.getDefault();
		}
	};

	private transient static ThreadLocale threadLocale;

	/**
	 * Set the Locale that will be used in {@link #getText()} for the current
	 * thread.
	 *
	 * @param locale The locale to be used for the current Thread
	 */
	public synchronized static void setThreadLocale(Locale locale) {
		if (threadLocale == null)
			threadLocale = new ThreadLocale();
		threadLocale.set(locale);
	}

	/**
	 * Reset the Locale used for {@link #getText()} for the current thread.
	 */
	public synchronized static void removeThreadLocale() {
		if (threadLocale == null)
			return;
		threadLocale.remove();
	}

	/**
	 * Implementors are responsible to
	 * return a non null Map with
	 * String languageIDs as keys and String values as entries
	 */
	protected abstract Map<String, String> getI18nMap();

	/**
	 * This will check whether a {@link Locale} is set for current Thread ({@link #setThreadLocale(Locale)}).
	 * If so it will use this {@link Locale} to return the text in its language.
	 * If not it will call {@link #getText(String)} with <code>NLLocale.getDefault().getLanguage()</code>.
	 */
	public String getText() {
		Locale locale = threadLocale == null ? null : threadLocale.get();
		if (locale == null)
			locale = NLLocale.getDefault();

		return getText(locale);
	}

	/**
	 * Implementors can return a String here used as
	 * fallback for the member when not even one value
	 * is set.
	 * @param languageID
	 */
	protected abstract String getFallBackValue(String languageID);

	/**
	 * Checks if the value returned by {@link #getI18nMap()} is null
	 * and throws a {@link IllegalStateException} if so.
	 */
	private Map<String, String> getEnsuredMap() {
		Map<String, String> result = getI18nMap();
		if (result == null)
			throw new IllegalStateException("Subclasses of I18nText must not return null in implementations of getI18nMap()!");
		return result;
	}

//	/**
//	 * Localizes the member.<br/>
//	 * Checks if a value is set for the given languageID.<br/>
//	 * If not tries for the {@link #DEFAULT_LANGUAGEID}.<br/>
//	 * If that fails too checks if at least one value is set.<br/>
//	 * Finally calls {@link #getFallBackValue(String)} for fallback.
//	 * @param languageID
//	 */
//	public void localize(String languageID)
//	{
//		localize(languageID, this);
//	}
//
//	/**
//	 * This method localizes this instance of I18nText after it has been detached.
//	 * It fetches its localized value from the given livingInstance (which is still
//	 * connected to the datastore).
//	 *
//	 * @param languageID
//	 * @param livingInstance
//	 */
//	public void localize(String languageID, I18nText livingInstance)
//	{
//		Map map = livingInstance.getEnsuredMap();
//		String text = (String) map.get(languageID);
//		if (text == null)
//			text = (String) map.get(DEFAULT_LANGUAGEID);
//		if (text == null && !map.isEmpty())
//			text = (String) map.values().iterator().next();
//		if (text == null)
//			text = livingInstance.getFallBackValue(languageID);
//		setText(text);
//	}

	public String getText(Locale locale)
	{
		return getText(locale, true);
	}

	/**
	 * Returns the String for the given languageID.
	 * If no value is found for the given language the one for
	 * {@link I18nText#DEFAULT_LANGUAGEID} is returned. If this
	 * doesn't exist and the Map is not empty, then just one arbitrary
	 * value is returned. Thus, the result might be null.
	 *
	 * @param languageID Language to find the String for
	 * @return String in the given language
	 */
	public String getText(String languageID)
	{
		return getText(languageID, true);
	}
	public String getText(Locale locale, boolean intelligentLookupStrategy)
	{
		return getText(locale.getLanguage(), intelligentLookupStrategy);
	}
	/**
	 * @param languageID Language to find the String for
	 * @param intelligentLookupStrategy If <code>true</code> and no entry for the given <code>languageID</code> exists, it
	 *		will try to find an entry for the default language, any language or the fallback value in this order. If <code>false</code>, it
	 *		will return <code>null</code>, if there is no entry for the given <code>languageID</code>. Note, that an empty <code>String</code> is
	 *		converted to <code>null</code> in the {@link #setText(String, String)} method.
	 * @return The text in the given languageID or a fall-back value.
	 */
	public String getText(String languageID, boolean intelligentLookupStrategy)
	{
		Map<String, String> map = getEnsuredMap();
		String result = map.get(languageID);

		if (!intelligentLookupStrategy)
			return result;

		if (result == null)
			result = map.get(DEFAULT_LANGUAGEID);
		if (result == null && !map.isEmpty())
			result = map.values().iterator().next();
		if (result == null)
			result = getFallBackValue(languageID);
		return result;
	}

	public void setText(Locale locale, String text) {
		setText(locale.getLanguage(), text);
	}

	/**
	 * Sets the text for a given langugageID
	 * @param languageID The language the text should be set for.
	 * @param text The text to be set.
	 */
	public void setText(String languageID, String text) {
		Map<String, String> m = getEnsuredMap();

		if ("".equals(text) || text == null) {
			if (m.containsKey(languageID))
				m.remove(languageID);
		}
		else {
			if (!Util.equals(m.get(languageID), text))
				m.put(languageID, text);
		}
	}

	/**
	 * Returns true if no value is set for the text, false otherwise
	 */
	public boolean isEmpty() {
		return getEnsuredMap().isEmpty();
	}

	/**
	 * @return a {@link Set} of all localized Strings.
	 */
	public Set<Map.Entry<String, String>> getTexts()
	{
		return getEnsuredMap().entrySet();
	}

	/**
	 * @return a {@link Set} of all languageIDs where localized Strings exists for
	 */
	public Set<String> getLanguageIDs() {
		return getEnsuredMap().keySet();
	}

//	/**
//	 * This method stores all entries from this object into another
//	 * <tt>I18nText</tt> and overwrites all previously stored data.
//	 * <p>
//	 * Note, that the fallbackValue is ignored by this method!
//	 *
//	 * @param other The <tt>I18nText</tt> instance into which to store the data.
//	 * @deprecated Use {@link #copyTo(I18nText)} instead!
//	 */
//	public void store(I18nText other)
//	{
//		copyTo(other);
//	}

	/**
	 * This method copies all entries from this object into another
	 * <tt>I18nText</tt> and overwrites all previously stored data.
	 * <p>
	 * Note, that the fallbackValue is ignored by this method!
	 *
	 * @param other The <tt>I18nText</tt> instance into which to store the data.
	 */
	public boolean copyTo(I18nText other)
	{
		return other.copyFrom(this);

//		Map<String, String> otherI18nMap = other.getI18nMap();
//		otherI18nMap.clear();
//		for (Map.Entry<String, String> me : getEnsuredMap().entrySet()) {
//			String key = me.getKey();
//			String value = me.getValue();
//
//			if (value == null)
//				continue;
//
//			otherI18nMap.put(key, value);
//		}
	}

//	/**
//	 * This method loads all entries from another <tt>I18nText</tt> and
//	 * overwrites all data that has been stored previously in this object.
//	 * <p>
//	 * Note, that the fallbackValue is ignored by this method!
//	 *
//	 * @param other The <tt>I18nText</tt> instance from which to load the data.
//	 * @deprecated Use {@link #copyFrom(I18nText)} instead!
//	 */
//	public void load(I18nText other)
//	{
//		copyFrom(other);
//	}

	/**
	 * This method copies all entries from another <tt>I18nText</tt> and
	 * overwrites all data that has been stored previously in this object.
	 * <p>
	 * Note, that the fallbackValue is ignored by this method!
	 *
	 * @param other The <tt>I18nText</tt> instance from which to load the data.
	 */
	@SuppressWarnings("unchecked")
	public boolean copyFrom(I18nText other)
	{
		boolean modified = false;

		Map o = other.getEnsuredMap();
		Map m = getEnsuredMap();
		Set keysToRemove = new HashSet();
		for (Iterator it = m.entrySet().iterator(); it.hasNext();) {
			Map.Entry	me = (Map.Entry) it.next();
			String languageID = (String) me.getKey();
			String value = (String) me.getValue();

			if (value != null && value.equals(o.get(languageID)))
				continue;

			keysToRemove.add(languageID);
		}

		for (Iterator it = keysToRemove.iterator(); it.hasNext();) {
			m.remove(it.next());
			modified = true;
		}

		for (Iterator it = o.entrySet().iterator(); it.hasNext();) {
			Map.Entry	me = (Map.Entry) it.next();
			String languageID = (String) me.getKey();
			String value = (String) me.getValue();

			if (value != null && !value.equals(m.get(languageID))) {
				m.put(languageID, value);
				modified = true;
			}
		}

		return modified;

//		getEnsuredMap().clear();
//		for (Map.Entry<String, String> me : other.getI18nMap().entrySet()) {
//			String key = me.getKey();
//			String value = me.getValue();
//
//			if (value == null)
//				continue;
//
//			getEnsuredMap().put(key, value);
//		}
	}

	public boolean containsLanguageID(String languageID)
	{
		return getEnsuredMap().containsKey(languageID);
	}

	@Override
	public String toString() {
		return getEnsuredMap().toString();
	}

	/**
	 * Clears all entries in the {@link I18nText}.
	 */
	public void clear() {
		getEnsuredMap().clear();
	}

	/**
	 * Read the values of this {@link I18nText} from the given {@link MultiLanguagePropertiesBundle}.
	 * <p>
	 * This method is useful if you want to read the values for many {@link I18nText}s
	 * from the same {@link MultiLanguagePropertiesBundle} and don't want to re-create the bundle
	 * every time.
	 * </p>
	 * <p>
	 * If you want to read the values for one {@link I18nText} only you can also use {@link #readFromProperties(String, ClassLoader, String)}.
	 * </p>
	 *
	 * @param propertiesBundle The bundle to read the values from.
	 * @param key The key under which the values are stored in the bundle.
	 */
	public void readFromMultiLanguagePropertiesBundle(MultiLanguagePropertiesBundle propertiesBundle, String key) {
		for (Locale locale : propertiesBundle.getLocales()) {
			String value = propertiesBundle.getProperty(locale, key);
			if (value != null) {
				setText(locale.getLanguage(), value);
			}
		}
	}

	/**
	 * Reads the values of this {@link I18nText} from properties that can be found
	 * using the given baseName and {@link ClassLoader} (a {@link MultiLanguagePropertiesBundle} will be created for that).
	 * <p>
	 * Use this method only if you want to read the values for one {@link I18nText} only.
	 * If you are reading the values for multiple {@link I18nText}s from the same
	 * resources you should create the {@link MultiLanguagePropertiesBundle} and pass
	 * it to {@link #readFromMultiLanguagePropertiesBundle(MultiLanguagePropertiesBundle, String)}.
	 * </p>
	 *
	 * @param baseName This is the baseName (prefix) for the resources where the values can be found.
	 * @param loader This is the ClassLoader to use in order to load the resources.
	 * @param key The key under which the values are stored in the resources.
	 */
	public void readFromProperties(String baseName, ClassLoader loader, String key) {
		MultiLanguagePropertiesBundle propertiesBundle = new MultiLanguagePropertiesBundle(baseName, loader, true);
		readFromMultiLanguagePropertiesBundle(propertiesBundle, key);
	}

}
