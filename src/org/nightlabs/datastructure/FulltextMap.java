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
package org.nightlabs.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nightlabs.util.NLLocale;

/**
 * This {@link java.util.Map} implementation extends {@link java.util.HashMap} and
 * adds the possibility to search the keys by contains, begins-with and ends-with.
 * This <tt>Map</tt> does NOT support <tt>null</tt> keys. A key that is not a
 * <tt>String</tt> will be handled with {@link java.lang.String#valueOf(java.lang.Object)}.
 * <p>
 * You can only access the additional searching-features, if the internal indices
 * are existing. Therefore you need to pass one or more of the following feature constants:
 * <ul>
 *		<li>{@link #FEATURE_BEGINS_WITH}: With this feature activated, you can search using the
 *			mode {@link #FIND_MODE_BEGINS_WITH}.</li>
 *		<li>{@link #FEATURE_ENDS_WITH}: With this feature activated, you can search using the
 *			mode {@link #FIND_MODE_ENDS_WITH}.</li>
 *		<li>{@link #FEATURE_CONTAINS}: With this feature activated, you can search using the
 *			mode {@link #FIND_MODE_CONTAINS}.</li>
 * </ul>
 * If you want to have the indices ready for multiple find modes, you must pass all the
 * corresponding features combined via binary OR.
 *
 * @version $Revision$ - $Date$
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FulltextMap<K,V> extends HashMap<K,V>
{
  /**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Use this constant alone, if you do not want to activate any of the extended features.
	 * This instance can only be used like a usual HashMap then.
	 */
	public static final int FEATURE_NONE = 0;

	/**
	 * Activate this feature, if you want to be able to search for matches at the
	 * beginning of the keys.
	 */
	public static final int FEATURE_BEGINS_WITH = 1;
	/**
	 * Activate this feature, if you want to be able to search for matches at the
	 * end of the keys.
	 */
	public static final int FEATURE_ENDS_WITH = 1 << 1;
	/**
	 * Activate this feature, if you want to search for matches where the search-
	 * string is somewhere in the key (position irrelevant).
	 */
	public static final int FEATURE_CONTAINS = 1 << 2;

	public static final int FIND_MODE_BEGINS_WITH = FEATURE_BEGINS_WITH;
	public static final int FIND_MODE_ENDS_WITH = FEATURE_ENDS_WITH;
	public static final int FIND_MODE_CONTAINS = FEATURE_CONTAINS;

	protected Locale locale;

	/**
	 * If this feature is not activated (the default), it will change all keys
	 * in the index to be lower case. This is a bit expensive, but it is usually
	 * desired. If you want (faster) case-sensitive behaviour, you need to
	 * OR this member into the features.
	 */
	public static final int FEATURE_CASE_SENSITIVE = 1 << 3;

	public boolean isFeatureBeginsWithActive()
	{
		return (features & FEATURE_BEGINS_WITH) != 0;
	}

	public boolean isFeatureEndsWithActive()
	{
		return (features & FEATURE_ENDS_WITH) != 0;
	}

	public boolean isFeatureContainsActive()
	{
		return (features & FEATURE_CONTAINS) != 0;
	}

	/**
	 * @return <tt>true</tt> if case-sensitive, <tt>false</tt> if NOT case-sensitive.
	 */
	public boolean isFeatureCaseSensitiveActive()
	{
		return (features & FEATURE_CASE_SENSITIVE) != 0;
	}

	/**
	 * Bitmask with all active features.
	 */
	private int features;

	/**
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 *
	 * @see HashMap#HashMap()
	 */
	public FulltextMap(int features)
	{
		init(features, null);
	}

	/**
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 * @param locale This <tt>Locale</tt> is used for
	 *		{@link String#toLowerCase(java.util.Locale)} when this <tt>FulltextMap</tt>
	 *		operates in case insensitive mode. If you pass <tt>null</tt> here,
	 *		{@link Locale#getDefault()} will be used.
	 *
	 * @see HashMap#HashMap()
	 */
	public FulltextMap(int features, Locale locale)
	{
		init(features, locale);
	}

	/**
	 * @param initialCapacity The initial capacity.
	 * @param loadFactor The initial loadFactor.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 *
	 * @see HashMap#HashMap(int, float)
	 */
	public FulltextMap(int initialCapacity, float loadFactor, int features)
	{
		super(initialCapacity, loadFactor);
		init(features, null);
	}

	/**
	 * @param initialCapacity The initial capacity.
	 * @param loadFactor The initial loadFactor.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 * @param locale This <tt>Locale</tt> is used for
	 *		{@link String#toLowerCase(java.util.Locale)} when this <tt>FulltextMap</tt>
	 *		operates in case insensitive mode. If you pass <tt>null</tt> here,
	 *		{@link Locale#getDefault()} will be used.
	 *
	 * @see HashMap#HashMap(int, float)
	 */
	public FulltextMap(int initialCapacity, float loadFactor, int features, Locale locale)
	{
		super(initialCapacity, loadFactor);
		init(features, locale);
	}

	/**
	 * @param initialCapacity The initial capacity.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 *
	 * @see HashMap#HashMap(int)
	 */
	public FulltextMap(int initialCapacity, int features)
	{
		super(initialCapacity);
		init(features, null);
	}

	/**
	 * @param initialCapacity The initial capacity.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 * @param locale This <tt>Locale</tt> is used for
	 *		{@link String#toLowerCase(java.util.Locale)} when this <tt>FulltextMap</tt>
	 *		operates in case insensitive mode. If you pass <tt>null</tt> here,
	 *		{@link Locale#getDefault()} will be used.
	 *
	 * @see HashMap#HashMap(int)
	 */
	public FulltextMap(int initialCapacity, int features, Locale locale)
	{
		super(initialCapacity);
		init(features, locale);
	}

	/**
	 * @param map The source from which to copy all entries.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 *
	 * @see HashMap#HashMap(java.util.Map)
	 */
	public FulltextMap(Map<K,V> map, int features)
	{
		super(map);
		init(features, null);
	}

	/**
	 * @param map The source from which to copy all entries.
	 * @param features A bitmask combined out of {@link #FEATURE_BEGINS_WITH},
	 *		{@link #FEATURE_CONTAINS}, {@link #FEATURE_ENDS_WITH}. If you don't want
	 *		to activate any extra feature, use {@link #FEATURE_NONE} <tt>(== 0)</tt>.
	 * @param locale This <tt>Locale</tt> is used for
	 *		{@link String#toLowerCase(java.util.Locale)} when this <tt>FulltextMap</tt>
	 *		operates in case insensitive mode. If you pass <tt>null</tt> here,
	 *		{@link Locale#getDefault()} will be used.
	 *
	 * @see HashMap#HashMap(java.util.Map)
	 */
	public FulltextMap(Map<K,V> map, int features, Locale locale)
	{
		super(map);
		init(features, locale);
	}

	protected void init(int features, Locale locale)
	{
		this.features = features;
		this.locale = locale;
		if (this.locale == null)
			this.locale = NLLocale.getDefault();
		rebuildIndex();
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
		rebuildIndex();
	}

	public int getFeatures()
	{
		return features;
	}

	public void setFeatures(int features, Locale locale)
	{
		this.features = features;
		this.locale = locale;
		rebuildIndex();
	}
	public void setFeatures(int features)
	{
		this.features = features;
		rebuildIndex();
	}

	@Override
	public V put(K key, V value)
	{
		V oldObject = super.put(key, value);
		addIndex(key);
		return oldObject;
	}

	@Override
	public V remove(Object key)
	{
		// TODO implement this method
		throw new UnsupportedOperationException("NYI");
	}

	protected void rebuildIndex()
	{
		clearIndex();
		ensureTreesExist();
		for (K key : keySet())
			addIndex(key);
	}

	protected void clearIndex()
	{
		if (treeBeginsWith != null)
			treeBeginsWith.clear();

		if (treeContains != null)
			treeContains.clear();

		if (treeEndsWith != null)
			treeEndsWith.clear();
	}

	@Override
	public void clear()
	{
		clearIndex();
		super.clear();
	}

	@Override
	public Object clone()
	{
		return new FulltextMap<K, V>(this, features);
	}

	private Collection<V> unmodifiableValues = null;

	@Override
	public Collection<V> values()
	{
		if (unmodifiableValues == null)
			unmodifiableValues = Collections.unmodifiableCollection(super.values()); // otherwise we would need to implement Iterator.remove and similar

		return unmodifiableValues;
	}

	private Set<K> unmodifiableKeySet = null;

	@Override
	public Set<K> keySet()
	{
		if (unmodifiableKeySet == null)
			unmodifiableKeySet = Collections.unmodifiableSet(super.keySet());

		return unmodifiableKeySet;
	}

	private Set<Map.Entry<K,V>> unmodifiableEntrySet = null;

	@Override
	public Set<Map.Entry<K,V>> entrySet()
	{
		if (unmodifiableEntrySet == null)
			unmodifiableEntrySet = Collections.unmodifiableSet(super.entrySet());

		return unmodifiableEntrySet;
	}

	// TODO: use generics...
	private Map<?, ?> treeBeginsWith = null;
	private Map<?, ?> treeContains = null;
	private Map<?, ?> treeEndsWith = null;

	private void ensureTreesExist()
	{
		if (treeBeginsWith == null && (isFeatureBeginsWithActive() || isFeatureContainsActive()))
			treeBeginsWith = new HashMap();

		if (treeContains == null && isFeatureContainsActive())
			treeContains = new HashMap();

		if (treeEndsWith == null && isFeatureEndsWithActive() && !isFeatureContainsActive())
			treeEndsWith = new HashMap();
	}

	protected void addIndex(K _key)
	{
		if (!isFeatureBeginsWithActive() && !isFeatureContainsActive() && !isFeatureEndsWithActive())
			return;

		String key = String.valueOf(_key);

		String keyInIndex = isFeatureCaseSensitiveActive() ?
				key : key.toLowerCase(locale);

		if (isFeatureBeginsWithActive() || isFeatureContainsActive()) {
			addIndexForKeyPart(treeBeginsWith, keyInIndex, key, false);
		}

		if (isFeatureContainsActive()) {
			Map<?, ?> tree = treeContains;

			String keyPart = keyInIndex.substring(1); // the first char is already in treeBeginsWith - don't need it twice
			while (keyPart.length() > 0) {
				addIndexForKeyPart(tree, keyPart, key, false);
				keyPart = keyPart.substring(1);
			}
		}

		if (isFeatureEndsWithActive() && !isFeatureContainsActive()) {
			addIndexForKeyPart(treeEndsWith, keyInIndex, key, true);
		}
	}

	@SuppressWarnings("unchecked")
	protected static void addIndexForKeyPart(Map tree, String keyPart, String key, boolean inverse)
	{
		Map treeNode = tree;
		int charIdx = inverse ? keyPart.length() - 1 : 0;
		int increment = inverse ? -1 : 1;
		while (inverse ? (charIdx >= 0) : (charIdx < keyPart.length())) {
			Character c = new Character(keyPart.charAt(charIdx));
			Map childTreeNode = (Map) treeNode.get(c);
			if (childTreeNode == null) {
				childTreeNode = new HashMap(5);
				treeNode.put(c, childTreeNode);
			}
			Set keys = (Set) childTreeNode.get(null);
			if (keys == null) {
				keys = new HashSet();
				childTreeNode.put(null, keys);
			}
			keys.add(key);

			treeNode = childTreeNode;
			charIdx += increment;
		}
	}

	/**
	 * This method searches for all keys that contain (or begin/end with) the given
	 * <tt>keyPart</tt>. For the keys found, the mapped values will be returned.
	 *
	 * @param keyPart A part of the searched key <tt>String</tt>.
	 * @param findMode One of the constants {@link #FIND_MODE_BEGINS_WITH},
	 *		{@link #FIND_MODE_CONTAINS} or {@link #FIND_MODE_ENDS_WITH}.
	 *		Note, that the corresponding features need to be activated.
	 * @return Returns the values that are mapped by the found keys.
	 */
	public Collection<V> find(String keyPart, int findMode)
	{
		Set<K> keySet = findKeySet(keyPart, findMode);
		ArrayList<V> res = new ArrayList<V>(keySet.size());
		for (K key : keySet)
			res.add(super.get(key));
		return res;
	}

	/**
	 * This method searches for all keys that contain (or begin/end with) the
	 * given <tt>keyPart</tt>.
	 *
	 * @param keyPart A part of the searched key <tt>String</tt>.
	 * @param findMode One of the constants {@link #FIND_MODE_BEGINS_WITH},
	 *		{@link #FIND_MODE_CONTAINS} or {@link #FIND_MODE_ENDS_WITH}.
	 *		Note, that the corresponding features need to be activated.
	 * @return Returns the found keys.
	 */
	public Set<K> findKeySet(String keyPart, int findMode)
	{
		if (!isFeatureCaseSensitiveActive())
			keyPart = keyPart.toLowerCase(locale);

		switch (findMode) {
		case FIND_MODE_BEGINS_WITH:
			if (!isFeatureBeginsWithActive())
				throw new IllegalStateException("FIND_MODE_BEGINS_WITH requires FEATURE_BEGINS_WITH which is not active!");
			break;
		case FIND_MODE_CONTAINS:
			if (!isFeatureContainsActive())
				throw new IllegalStateException("FIND_MODE_CONTAINS requires FEATURE_CONTAINS which is not active!");
			break;
		case FIND_MODE_ENDS_WITH:
			if (!isFeatureContainsActive())
				throw new IllegalStateException("FIND_MODE_ENDS_WITH requires FEATURE_ENDS_WITH which is not active!");
			break;
		default:
			throw new IllegalArgumentException("findMode="+findMode+" is invalid! Must be one of: FIND_MODE_BEGINS_WITH, FIND_MODE_CONTAINS, FIND_MODE_ENDS_WITH");
		}


		Set<K> res = null;
		if (FIND_MODE_BEGINS_WITH == findMode || FIND_MODE_CONTAINS == findMode) {
			res = findKeySetForKeyPart(treeBeginsWith, keyPart, false);
		}

		if (FIND_MODE_CONTAINS == findMode) {
			Set<K> s = findKeySetForKeyPart(treeContains, keyPart, false);
			if (res != null && s != null)
				res.addAll(s);
			else if (s != null)
				res = s;
		}

		if (FIND_MODE_ENDS_WITH == findMode) {
			Set<K> s = findKeySetForKeyPart(treeEndsWith, keyPart, true);
			if (res != null && s != null)
				res.addAll(s);
			else if (s != null)
				res = s;
		}

		if (res == null)
			return Collections.unmodifiableSet(new HashSet<K>(0));

		return res;
	}

	// doesn't work as generic... :-(
	//protected static final Set EMPTY_SET = Collections.unmodifiableSet(new HashSet(0));

	@SuppressWarnings("unchecked")
	protected static <K> Set<K> findKeySetForKeyPart(Map tree, String keyPart, boolean inverse)
	{
		Map treeNode = tree;
		int charIdx = inverse ? keyPart.length() - 1 : 0;
		int increment = inverse ? -1 : 1;
		while (inverse ? (charIdx >= 0) : (charIdx < keyPart.length())) {
			Character c = new Character(keyPart.charAt(charIdx));
			Map childTreeNode = (Map) treeNode.get(c);
			if (childTreeNode == null)
				return null;

			treeNode = childTreeNode;
			charIdx += increment;
		}

		Set<K> res = (Set<K>) treeNode.get(null);

		if (res != null && res.isEmpty())
			res = null;

		return res;
	}

	protected void removeIndex(String key)
	{
		throw new UnsupportedOperationException("NYI");
	}
}
