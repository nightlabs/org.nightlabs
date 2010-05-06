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
package org.nightlabs.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A {@link Map} implementation which provides the possibility to create and parse an URL-query like the underlined part
 * in this URL: http://host.domain.tld/whatever?<u>key1=value1&key2=value2&</u>
 * </p>
 * <p>
 * All keys and values in this <code>Map</code> are encoded using the given {@link ParameterCoder}
 * or {@link ParameterCoderURL} as default (using UTF-8) and can therefore contain every character.
 * </p>
 * <p>
 * Instead of the default entry-separator '{@value #DEFAULT_ENTRY_SEPARATOR}' and the default
 * key/value-separator '{@value #DEFAULT_KEY_VALUE_SEPARATOR}', other control-characters can be used.
 * If used with the default {@link ParameterCoderURL} however, they must neither be "normal"
 * data-characters (A-Z, a-z, 0-9) nor the special-characters '-', '_', '.', '!', '~', '*', ''', '('
 * and ')', because these are data-characters that are not encoded according to RFC 2396
 * (see {@link URLEncoder}).
 * </p>
 *
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 * @author Marco Schulze -- Marco[at]NightLabs[dot]de
 */
public class ParameterMap
extends HashMap<String, String>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 *
	 * @see HashMap
	 */
	public ParameterMap() { }

	/**
	 * Constructor calling the corresponding super-constructor of the extended {@link HashMap}.
	 */
	public ParameterMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor calling the corresponding super-constructor of the extended {@link HashMap}.
	 */
	public ParameterMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor calling the corresponding super-constructor of the extended {@link HashMap}.
	 */
	public ParameterMap(Map<? extends String, ? extends String> m) {
		super(m);
	}

	/**
	 * Create a new instance of <code>ParameterMap</code> and populate it with the values encoded in the given
	 * query-string.
	 * <p>
	 * This constructor delegates to {@link #load(String)}.
	 * </p>
	 *
	 * @param query the encoded single-string-data.
	 */
	public ParameterMap(String query) {
		load(query);
	}

	/**
	 * Create a new instance of <code>ParameterMap</code> and populate it with the values encoded in the given
	 * query-string using alternative separators.
	 *
	 * @param query the encoded single-string-data.
	 * @param keyValueSeparator the separator to be used instead of the default '='.
	 * @param entrySeparator the separator to be used instead of the default '&'.
	 */
	public ParameterMap(String query, char keyValueSeparator, char entrySeparator) {
		load(query, keyValueSeparator, entrySeparator);
	}

	/**
	 * In every key-value-pair, this separator will be inserted between the URL-encoded key and
	 * the URL-encoded value.
	 */
	public static final char DEFAULT_KEY_VALUE_SEPARATOR = '=';
	
	/**
	 * This separator will be appended after every entry (entry = key-value-pair).
	 */
	public static final char DEFAULT_ENTRY_SEPARATOR = '&';
	
	/**
	 * Encode all entries of this <code>Map</code> into a single URL-query-string using
	 * the default entry-separator '&' and the default key/value-separator '='.
	 * <p>
	 * Use the method {@link #load(String)} to decode the single-string-data in another <code>ParameterMap</code>
	 * or create it via the constructor {@link ParameterMap#URLParameterMap(String)}.
	 * </p>
	 * <p>
	 * This is a convenience method calling {@link #dump(char, char)} with the parameters '=', '&'.
	 * </p>
	 *
	 * @return the encoded string containing all entries.
	 */
	public String dump()
	{
		return dump(DEFAULT_KEY_VALUE_SEPARATOR, DEFAULT_ENTRY_SEPARATOR);
	}

	/**
	 * Encode all entries of this <code>Map</code> into a single URL-query-string using
	 * the given separators.
	 * <p>
	 * Use the method {@link #load(String)} to decode the single-string-data in another <code>ParameterMap</code>
	 * or create it via the constructor {@link ParameterMap#URLParameterMap(String)}.
	 * </p>
	 * <p>
	 * Instead of the usual separators {@value #DEFAULT_KEY_VALUE_SEPARATOR} and
	 * {@value #DEFAULT_ENTRY_SEPARATOR} employed in URL-queries, this method will use the
	 * control-characters defined by <code>keyValueSeparator</code> and <code>entrySeparator</code>.
	 * </p>
	 *
	 * @param keyValueSeparator the separator to be used instead of the default '='. In every key-value-pair, this separator will be inserted between the URL-encoded key and the URL-encoded value.
	 * @param entrySeparator the separator to be used instead of the default '&'. This separator will be appended after every entry (entry = key-value-pair).
	 * @return the encoded string containing all entries.
	 */
	public String dump(char keyValueSeparator, char entrySeparator)
	{
		return dump(keyValueSeparator, entrySeparator, new ParameterCoderURL());
	}
	
	/**
	 * Encode all entries of this <code>Map</code> into a single URL-query-string using
	 * the given separators.
	 * <p>
	 * Use the method {@link #load(String)} to decode the single-string-data in another <code>ParameterMap</code>
	 * or create it via the constructor {@link ParameterMap#URLParameterMap(String)}.
	 * </p>
	 * <p>
	 * Instead of the usual separators {@value #DEFAULT_KEY_VALUE_SEPARATOR} and
	 * {@value #DEFAULT_ENTRY_SEPARATOR} employed in URL-queries, this method will use the
	 * control-characters defined by <code>keyValueSeparator</code> and <code>entrySeparator</code>.
	 * </p>
	 *
	 * @param keyValueSeparator the separator to be used instead of the default '='. In every key-value-pair, this separator will be inserted between the URL-encoded key and the URL-encoded value.
	 * @param entrySeparator the separator to be used instead of the default '&'. This separator will be appended after every entry (entry = key-value-pair).
	 * @param parameterCoder the {@link ParameterCoder} used for encoding the parameters.
	 * @return the encoded string containing all entries.
	 */
	public String dump(char keyValueSeparator, char entrySeparator, ParameterCoder parameterCoder)
	{
		StringBuffer url = new StringBuffer();
		for (Map.Entry<String, String> me : entrySet()) {
			String paramNameEncoded = parameterCoder.encode(me.getKey() == null ? "" : me.getKey());
			String paramValueEncoded = parameterCoder.encode(me.getValue()  == null ? "" : me.getValue());
			url.append(paramNameEncoded);
			url.append(keyValueSeparator);
			url.append(paramValueEncoded);
			url.append(entrySeparator);
		}
		return url.toString();
	}

	/**
	 * Populate this instance of <code>ParameterMap</code> with the entries encoded in the given URL-query.
	 * <p>
	 * If this <code>Map</code> already contains entries for keys occurring in the given <code>query</code>, they
	 * will be overwritten.
	 * </p>
	 * <p>
	 * This is a convenience method delegating to {@link #load(String, char, char)} with the separators '=' and '&'.
	 * </p>
	 *
	 * @param dump an URL-query-like string. The result of {@link #dump()} can be passed here.
	 */
	public void load(String dump)
	{
		load(dump, '=', '&');
	}

	/**
	 * Populate this instance of <code>ParameterMap</code> with the entries encoded in the given URL-query.
	 * <p>
	 * If this <code>Map</code> already contains entries for keys occurring in the given <code>query</code>, they
	 * will be overwritten.
	 * </p>
	 * <p>
	 * Instead of the usual separators '=' and '&' employed in URL-queries, this method will use the control-characters
	 * defined by <code>keyValueSeparator</code> and <code>entrySeparator</code>. In order to parse a string that has been produced
	 * by {@link #dump(char, char)}, it is essential that you use the same separator-characters!
	 * </p>
	 *
	 * @param dump an URL-query-like string. The result of {@link #dump(char, char)} can be passed here (using the same separator-characters!).
	 */
	public void load(String dump, char keyValueSeparator, char entrySeparator)
	{
		load(dump, keyValueSeparator, entrySeparator, new ParameterCoderURL());
	}

	/**
	 * Populate this instance of <code>ParameterMap</code> with the entries encoded in the given URL-query.
	 * <p>
	 * If this <code>Map</code> already contains entries for keys occurring in the given <code>query</code>, they
	 * will be overwritten.
	 * </p>
	 * <p>
	 * Instead of the usual separators '{@value #DEFAULT_KEY_VALUE_SEPARATOR}' and '{@link #DEFAULT_ENTRY_SEPARATOR}'
	 * employed in URL-queries, this method will use the control-characters defined by
	 * <code>keyValueSeparator</code> and <code>entrySeparator</code>.
	 * In order to parse a string that has been produced by {@link #dump(char, char)},
	 * it is essential that you use the same separator-characters!
	 * </p>
	 * 
	 * @param dump the encoded ParameterMap {@link #dump(char, char, ParameterCoder)}.
	 * @param keyValueSeparator the separator to be used instead of the default '='. In every key-value-pair, this separator will be inserted between the URL-encoded key and the URL-encoded value.
	 * @param entrySeparator the separator to be used instead of the default '&'. This separator will be appended after every entry (entry = key-value-pair).
	 * @param parameterCoder the {@link ParameterCoder} used for decoding the encoded parameters.
	 */
	public void load(String dump, char keyValueSeparator, char entrySeparator, ParameterCoder parameterCoder)
	{
		String[] pathParts = dump.split(Character.toString(entrySeparator));
		for (String pathPart : pathParts) {
			String[] paramParts = pathPart.split(String.valueOf(keyValueSeparator));
			if (paramParts.length != 2)
				continue;

			String paramName = parameterCoder.decode(paramParts[0]);
			String paramValue = parameterCoder.decode(paramParts[1]);
			put(paramName, paramValue);
		}
	}

}
