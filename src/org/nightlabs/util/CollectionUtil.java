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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility mathods for collections.
 */
public class CollectionUtil
{
	/*
	 * This method casts a {@link Collection} by instantiating an instance of one of
	 * the following classes:
	 * <ul>
	 * <li>{@link DelegatingSet}</li>
	 * <li>{@link DelegatingList}</li>
	 * <li>{@link DelegatingCollection}</li>
	 * </ul>
	 * Which one of the above classes is chosen, depends on the interface implemented by
	 * the <code>in</code> parameter.
	 */
	/**
	 * This method casts any collection into another.
	 * <b>Warning:</b> This should be used with care! Only use it, when you're absolutely sure that
	 * the cast is correct! This method is not type-safe!
	 * <p>
	 * There are many situations where
	 * you are absolutely sure that your cast is correct, but illegal due to the way generics are
	 * implemented in Java. In these cases you can use this method which is not expensive since it
	 * returns exactly the parameter passed to it.
	 * </p>
	 *
	 * @param in The collection that needs to be casted.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> castCollection(Collection<?> in)
	{
		return (Collection<T>) in;
//		if (in instanceof Set)
//			return new DelegatingSet<T>((Set) in);
//		else if (in instanceof List)
//			return new DelegatingList<T>((List)in);
//		else
//			return new DelegatingCollection<T>(in);
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> castCollection(Object obj) {
		return (Collection<T>) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> castList(List<?> in)
	{
//		return new DelegatingList<T>(in);
		return (List<T>) in;
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> castSet(Set<?> in)
	{
//		return new DelegatingSet<T>(in);
		return (Set<T>) in;
	}

	@SuppressWarnings("unchecked")
	public static <T, U> Map<T, U> castMap(Map<?, ?> in)
	{
		return (Map<T, U>) in;
	}


	/**
	 * Given an Object obj, usually from a resulting Object obtained after executing a query (in which this resulting Object
	 * is known to be a Collection of instances of the type T), return this as a HashSet of the type T.
	 */
	public static <T> HashSet<T> createHashSetFromCollection(Object obj) {
		Collection<? extends T> results = CollectionUtil.castCollection(obj);
		return new HashSet<T>( results );
	}

	/**
	 * This method calls {@link Util#array2ArrayList(Object[], boolean)} with
	 * <tt>canReturnNull = true</tt>.
	 *
	 * @param objects An array of objects - can be <tt>null</tt>.
	 * @return Returns an <tt>ArrayList</tt> (or <tt>null</tt> if <tt>objects == null</tt>).
	 */
	public static <T> ArrayList<T> array2ArrayList(T[] objects)
	{
		return array2ArrayList(objects, true);
	}

	/**
	 * @param canReturnNull If <tt>false</tt>, the result will never be <tt>null</tt>,
	 *		but an empty list if <tt>objects</tt> is null.
	 * @param objects An array of objects - can be <tt>null</tt>.
	 * @return Returns an <tt>ArrayList</tt> - never <tt>null</tt>.
	 * The <tt>ArrayList</tt> is empty if <tt>objects</tt> is <tt>null</tt> and
	 * <tt>canReturnNull</tt> is <tt>false</tt>. If <tt>canReturnNull == true</tt>
	 * and <tt>objects == null</tt>, the result will be <tt>null</tt>.
	 */
	public static <T> ArrayList<T> array2ArrayList(T[] objects, boolean canReturnNull)
	{
		if (canReturnNull && objects == null)
			return null;

		ArrayList<T> l = new ArrayList<T>(objects == null ? 0 : objects.length);
		if (objects != null) {
			for (T element : objects)
				l.add(element);
		}
		return l;
	}

	/**
	 * This method calls {@link Util#array2HashSet(Object[], boolean)} with
	 * <tt>canReturnNull = true</tt>.
	 *
	 * @param objects An array of objects - can be <tt>null</tt>.
	 * @return Returns an <tt>ArrayList</tt> (or <tt>null</tt>, if <tt>objects == null</tt>).
	 */
	public static <T> HashSet<T> array2HashSet(T[] objects)
	{
		return CollectionUtil.array2HashSet(objects, true);
	}

	/**
	 * @param canReturnNull If <tt>false</tt>, the result will never be <tt>null</tt>,
	 *		but an empty list if <tt>objects</tt> is null.
	 * @param objects An array of objects - can be <tt>null</tt>.
	 * @return Returns an <tt>ArrayList</tt> - never <tt>null</tt>.
	 * The <tt>ArrayList</tt> is empty if <tt>objects</tt> is <tt>null</tt> and
	 * <tt>canReturnNull</tt> is <tt>false</tt>. If <tt>canReturnNull == true</tt>
	 * and <tt>objects == null</tt>, the result will be <tt>null</tt>.
	 */
	public static <T> HashSet<T> array2HashSet(T[] objects, boolean canReturnNull)
	{
		if (canReturnNull && objects == null)
			return null;

		HashSet<T> s = new HashSet<T>(objects == null ? 0 : objects.length);
		if (objects != null) {
			for (T element : objects)
				s.add(element);
		}
		return s;
	}

	/**
	 * This method calls {@link Util#collection2TypedArray(Collection, Class, boolean)} with
	 * <tt>canReturnNull = true</tt>.
	 *
	 * @param c Either <tt>null</tt> or a <tt>Collection</tt> with instances of the type specified by <tt>clazz</tt> (or descendants of it).
	 * @param clazz The type of the elements of the returned object-array (e.g. <tt>String.class</tt> for the returned type <tt>String[]</tt>).
	 * @return Returns a typed object array (or <tt>null</tt>, if <tt>c == null</tt>).
	 */
	public static <T> T[] collection2TypedArray(Collection<T> c, Class<T> clazz)
	{
		return CollectionUtil.collection2TypedArray(c, clazz, true);
	}

	/**
	 * @param c Either <tt>null</tt> or a <tt>Collection</tt> with instances of the type specified by <tt>clazz</tt> (or descendants of it).
	 * @param clazz The type of the elements of the returned object-array (e.g. <tt>String.class</tt> for the returned type <tt>String[]</tt>).
	 * @param canReturnNull If <tt>false</tt>, the result will never be <tt>null</tt>,
	 *		but an empty array if <tt>c == null</tt>.
	 *
	 * @return Returns a typed object array (or <tt>null</tt>, if <tt>c == null && canReturnNull</tt>).
	 *		If <tt>canReturnNull</tt> is false and <tt>c == null</tt>, an empty array will be returned.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] collection2TypedArray(Collection<T> c, Class<T> clazz, boolean canReturnNull)
	{
		if (canReturnNull && c == null)
			return null;

		Object array = Array.newInstance(clazz, c == null ? 0 : c.size());
		if (c != null) {
			int i = 0;
			for (Object element : c)
				Array.set(array, i++, element);
		}
		return (T[])array;
	}

	/**
	 * Merges all given Arrays to a new one and returns it.
	 * <p>
	 * If passed only one array, that one will be returned, not a new one.
	 * If no array is passed, <code>null</code> will be returned.
	 * </p>
	 *
	 * @param <T> The type of Array entries.
	 * @param ts The arrays to merge.
	 * @return A new array containing the entries of the given ones.
	 */
	public static <T> T[] mergeArrays(T[] ... ts)
	{
		if (ts.length == 0)
			return null;
		if (ts.length == 1)
			return ts[0];
		List<T> tList = array2ArrayList(ts[0]);
		for (int i = 1; i < ts.length; i++) {
			addAllToCollection(ts[i], tList);
		}
		return tList.toArray(ts[ts.length -1]);
	}

	/**
	 * Moves the given element up in the given list.
	 *
	 * @param list The list
	 * @param element The element
	 */
	public static <T> void moveListElementUp(List<T> list, T element)
	{
		int index = list.indexOf(element);
		if (index <= 0 || index >= list.size())
			return;
		list.remove(index);
		list.add(index-1, element);
	}

	/**
	 * Moves the given element down in the given list.
	 * @param list The list
	 * @param element The element
	 */
	public static <T> void moveListElementDown(List<T> list, T element)
	{
		int index = list.indexOf(element);
		if (index < 0 || index >= list.size()-1)
			return;
		list.remove(index);
		list.add(index+1, element);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> List<T> enum2List(Enum<T> e)
	{
		return array2ArrayList((T[])e.getClass().getEnumConstants());
	}

	/**
	 * Create a string representation for the given collection.
	 * @param c The collection to create a string representation for
	 * @return The string representation for the given collection
	 */
	public static String toString(Collection<?> c)
	{
		return toString(c, ",", true, null, false, "null");
	}

	/**
	 * Create a string representation for the given collection.
	 * @param c The collection to create a string representation for
	 * @param fieldPrefix A string to put before an element
	 * @param fieldPrefixBeforeFirst Whether to put fieldPrefix before the first element
	 * @param fieldSuffix A string to put after an element
	 * @param fieldSuffixAfterLast Whether to put fieldSuffix after the last element
	 * @param nullString The string to use as value when value is <code>null</code>
	 * @return The string representation for the given collection
	 */
	public static String toString(Collection<?> c, String fieldPrefix, boolean fieldPrefixBeforeFirst, String fieldSuffix, boolean fieldSuffixAfterLast, String nullString)
	{
		StringBuffer s = new StringBuffer();
		boolean first = true;
		for (Object object : c) {
			if(fieldPrefix != null) {
				if(first) {
					first = false;
					if(fieldPrefixBeforeFirst)
						s.append(fieldPrefix);
				} else {
					s.append(fieldPrefix);
				}
			}
			String valueString = (object==null?(nullString==null?"":nullString):object.toString());
			s.append(valueString);
			if(fieldSuffix != null)
				s.append(fieldSuffix);
		}
		if(!first && fieldSuffix != null && !fieldSuffixAfterLast)
			s.delete(s.length()-fieldSuffix.length(), s.length());
		return s.toString();
	}

	/**
	 * Create a string representation for the given map.
	 * @param m The map to create a string representation for
	 * @return The string representation for the given map
	 */
	public static String toString(Map<?, ?> m)
	{
		return toString(m, ",", true, null, false, "[", "]", "=", "null");
	}

	/**
	 * Create a string representation for the given map.
	 * @param m The map to create a string representation for
	 * @param fieldPrefix A string to put before an element
	 * @param fieldPrefixBeforeFirst Whether to put fieldPrefix before the first element
	 * @param fieldSuffix A string to put after an element
	 * @param fieldSuffixAfterLast Whether to put fieldSuffix after the last element
	 * @param keyPrefix A string to put before the key
	 * @param keySuffix A string to put after the key
	 * @param assignmentString A string to put between the key and the value
	 * @param nullString The string to use as value when value is <code>null</code>
	 * @return The string representation for the given map
	 */
	public static String toString(Map<?, ?> m, String fieldPrefix, boolean fieldPrefixBeforeFirst, String fieldSuffix, boolean fieldSuffixAfterLast, String keyPrefix, String keySuffix, String assignmentString, String nullString)
	{
		StringBuffer s = new StringBuffer();
		boolean first = true;
		for (Object key : m.keySet()) {
			if(fieldPrefix != null) {
				if(first) {
					first = false;
					if(fieldPrefixBeforeFirst)
						s.append(fieldPrefix);
				} else {
					s.append(fieldPrefix);
				}
			}
			if(keyPrefix != null)
				s.append(keyPrefix);
			s.append(key);
			if(keySuffix != null)
				s.append(keySuffix);
			if(assignmentString != null)
				s.append(assignmentString);
			Object value = m.get(key);
			String valueString = (value==null?(nullString==null?"":nullString):value.toString());
			s.append(valueString);
			if(fieldSuffix != null)
				s.append(fieldSuffix);
		}
		if(!first && !fieldSuffixAfterLast && fieldSuffix != null)
			s.delete(s.length()-fieldSuffix.length(), s.length());
		return s.toString();
	}

	/**
	 * Create a string representation for the given array.
	 * @param array The array to create a string representation for
	 * @return The string representation for the given array
	 */
	public static <T> String toString(T[] array)
	{
		return toString(array, ",", true, null, false, "[", "]", "=", "null");
	}

	/**
	 * Create a string representation for the given array.
	 * @param array The array to create a string representation for
	 * @param fieldPrefix A string to put before an element
	 * @param fieldPrefixBeforeFirst Whether to put fieldPrefix before the first element
	 * @param fieldSuffix A string to put after an element
	 * @param fieldSuffixAfterLast Whether to put fieldSuffix after the last element
	 * @param keyPrefix A string to put before the index
	 * @param keySuffix A string to put after the index
	 * @param assignmentString A string to put between the index and the value
	 * @param nullString The string to use as value when value is <code>null</code>
	 * @return The string representation for the given array
	 */
	public static <T> String toString(T[] array, String fieldPrefix, boolean fieldPrefixBeforeFirst, String fieldSuffix, boolean fieldSuffixAfterLast, String keyPrefix, String keySuffix, String assignmentString, String nullString)
	{
		StringBuffer s = new StringBuffer();
		for(int i=0; i<array.length; i++) {
			if(fieldPrefix != null && (i > 0 || fieldPrefixBeforeFirst))
				s.append(fieldPrefix);
			if(keyPrefix != null)
				s.append(keyPrefix);
			s.append(i);
			if(keySuffix != null)
				s.append(keySuffix);
			if(assignmentString != null)
				s.append(assignmentString);
			Object value = array[i];
			String valueString = (value==null?(nullString==null?"":nullString):value.toString());
			s.append(valueString);
			if(fieldSuffix != null && (i<array.length-1 || fieldSuffixAfterLast))
				s.append(fieldSuffix);
		}
		return s.toString();
	}

	/**
	 * Replaces all occurences of replace with replacement in the given collection.
	 * <p>
	 * Note that nothing will be done if the given object to replace is not contained
	 * in the given collection.
	 *
	 * @param collection The collection to replace values in.
	 * @param replace The object to be replaced.
	 * @param replacement The object that should be inserted into the collection.
	 */
	@SuppressWarnings("unchecked")
	public static void replaceAllInCollection(Collection collection, Object replace, Object replacement) {
		if (collection instanceof List) {
			List list = (List) collection;
			int idx = list.indexOf(replace);
			while (idx >= 0) {
				list.set(idx, replacement);
				idx = list.indexOf(replace);
			}
		}
		else {
			if (collection.contains(replace)) {
				collection.remove(replace);
				collection.add(replacement);
			}
		}
	}

	/**
	 * Creates an ArrayList with an initial capacity of <code>item.length</code>
	 * and adds the given items to it.
	 *
	 * @param <T> The type of the items.
	 * @param item The items to be added.
	 * @return Returns a new typed ArrayList which contains the given items.
	 */
	public static <T> ArrayList<T> createArrayList(T ... item)
	{
		ArrayList<T> list = new ArrayList<T>(item.length);
		for (T t : item)
			list.add(t);

		return list;
	}

	/**
	 * Creates a HashSet with an initial capacity of <code>item.length</code>
	 * and adds the given items to it.
	 *
	 * @param <T> The type of the items.
	 * @param item The items to be added.
	 * @return Returns a new typed HashSet which contains the given items.
	 */
	public static <T> HashSet<T> createHashSet(T ... item)
	{
		HashSet<T> set = new HashSet<T>(item.length);
		for (T t : item)
			set.add(t);

		return set;
	}

	/**
	 * Adds all entries of the given array to the given list.
	 *
	 * @param <T> The type of the items.
	 * @param array The array whose items should be added.
	 * @param list The list where the items should be added.
	 */
	public static <T> void addAllToCollection(T[] array, List<T> list)
	{
		for (T t : array)
			list.add(t);
	}

}
