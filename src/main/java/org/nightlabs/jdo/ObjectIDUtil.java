/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
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

package org.nightlabs.jdo;

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * This class is a util class for JDO objectid handling.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class ObjectIDUtil
{
	private static Pattern pattern_makeValidIDString = null;
	private static Pattern pattern_isValidIDString = null;

	public static String makeValidIDString(String id)
	{
		return makeValidIDString(id, false);
	}

	/**
	 * @param id The string that shall be used as base for the id string. Can be <tt>null</tt>.
	 *		If it is <tt>null</tt> or an empty string, <tt>appendUniqueID</tt> will be forced
	 *		to <tt>true</tt> (and your value ignored).
	 * @param appendUniqueID Whether or not to append a base36-encoded combination of
	 *		current-time + random number
	 * @return Returns an id, where the first part is the param <tt>id</tt> with all invalid chars converted to "_".
	 */
	public static String makeValidIDString(String id, boolean appendUniqueID)
	{
		if (id == null || "".equals(id))
			appendUniqueID = true;

		if (appendUniqueID) {
			StringBuffer sb = id == null ? new StringBuffer() : new StringBuffer(id);
			if (sb.length() > 0)
				sb.append('-');

//			sb.append(longObjectIDFieldToString(System.currentTimeMillis()));
//			sb.append('-');
//			sb.append(intObjectIDFieldToString((int)(46656 * Math.random()))); // should be 1..3 digits

			sb.append(Long.toString(System.currentTimeMillis(), 36));
			sb.append('-');
			sb.append(Integer.toString((int)(46656 * Math.random()), 36)); // should be 1..3 digits

			id = sb.toString();
		}

		if (pattern_makeValidIDString == null)
			pattern_makeValidIDString = Pattern.compile("[^a-zA-Z0-9#\\.\\$!_\\-/:+]");

		return id = pattern_makeValidIDString.matcher(id).replaceAll("_").toLowerCase();
	}

	/**
	 * This method checks an id string for validity. The given id is valid,
	 * if it contains only the following characters: "a"-"z", "A"-"Z", "0"-"9", ".", "#", "$", "!", "_", "-", "/", ":"
	 *
	 * @param id The id to be validated.
	 * @return True if valid, false otherwise.
	 */
	public static boolean isValidIDString(String id)
	{
		if (id == null || "".equals(id))
			return false;

		if (pattern_isValidIDString == null)
			pattern_isValidIDString = Pattern.compile("[a-zA-Z0-9#\\.\\$!_\\-/:+]*");
		return pattern_isValidIDString.matcher(id).matches();
	}

	/**
	 * This method checks a given id String with isValidIDString(...) and throws
	 * an IllegalArgumentException if the id is not valid.
	 *
	 * @param id The ID to validate.
	 * @throws IllegalArgumentException If the id is not valid.
	 *
	 * @see #assertValidIDString(String, String)
	 * @see #isValidIDString(String)
	 */
	public static void assertValidIDString(String id)
		throws IllegalArgumentException
	{
		if (!isValidIDString(id))
			throw new IllegalArgumentException("String id \""+id+"\" is not valid!");
	}

	/**
	 * This method does the same as the one-argument-version, but it includes
	 * the name in the exception message. This is useful especially with composite
	 * primary keys, where it should be made clear, which part of the pk is invalid.
	 *
	 * @param id The ID to validate.
	 * @param idName The name of the variable to be included in the exception message.
	 * @throws IllegalArgumentException If the id is not valid.
	 *
	 * @see #assertValidIDString(String)
	 * @see #isValidIDString(String)
	 */
	public static void assertValidIDString(String id, String idName)
		throws IllegalArgumentException
	{
		if (!isValidIDString(id))
			throw new IllegalArgumentException(idName + " \""+id+"\" is not a valid id string!");
	}

	protected static final String SEPARATORS_FOR_TOKENIZER = "/?";
	public static final String JDO_PREFIX = "jdo";
	public static final char JDO_PREFIX_SEPARATOR = '/';
	public static final char CLASS_SEPARATOR = '?';
	public static final String FIELD_SEPARATOR = "&";

	public static String createObjectIDString(String objectIDClassName, String objectIDFieldPart)
	{
		return JDO_PREFIX + JDO_PREFIX_SEPARATOR + objectIDClassName + CLASS_SEPARATOR + objectIDFieldPart;
	}

	/**
	 * This method splits the object-id-string (formatted as jdo/clazz?fields) into two
	 * strings: The class name of the JDO object id class and the encoded fields.
	 *
	 * @param keyStr The result of the toString method of the jdo object id.
	 * @return Returns a String[2] with res[0] being the class name and res[1] being the
	 *		encoded fields.
	 *
	 * @throws IllegalArgumentException If the passed string is not formed correctly.
	 */
	public static String[] splitObjectIDString(String keyStr)
	{
		StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
		String jdoPrefix = st.nextToken();
		if (!JDO_PREFIX.equals(jdoPrefix))
			throw new IllegalArgumentException("keyStr \""+keyStr+"\" does not start with jdo prefix \""+JDO_PREFIX+"\"!");

		if (!st.hasMoreTokens() || st.nextToken().charAt(0) != JDO_PREFIX_SEPARATOR)
			throw new IllegalArgumentException("keyStr \""+keyStr+"\" is missing separator \""+JDO_PREFIX_SEPARATOR+"\" after jdo prefix!");

		String className = st.nextToken();

		if (!st.hasMoreTokens() || st.nextToken().charAt(0) != CLASS_SEPARATOR)
			throw new IllegalArgumentException("keyStr \""+keyStr+"\" is missing separator \""+CLASS_SEPARATOR+"\" after class!");

		String fields = st.nextToken();
		if (fields.length() < 2)
			throw new IllegalArgumentException("keyStr \""+keyStr+"\" is missing fields!");

		return new String[] { className, fields };
	}

	private static Class<?> loadClass(String className)
	throws ClassNotFoundException
	{
		try {
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException x) {
			// ignore
		}
		return Class.forName(className);
	}

	/**
	 * @param keyStr The object-id-string formatted as jdo/clazz?fields or <code>null</code>.
	 * @return Returns an objectid-instance created via the one-string-constructor
	 *		(as required by the JDO standard) and with all values set according to the passed string or <code>null</code>,
	 *		if <code>keyStr</code> is <code>null</code>.
	 *
	 * @throws IllegalArgumentException If the passed string is either not formed correctly or if
	 *		creation of the object fails. Therefore, one of the following exceptions can be the cause:
	 *		ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	 */
	public static ObjectID createObjectID(String keyStr)
	{
		if (keyStr == null)
			return null;

		String[] parts = ObjectIDUtil.splitObjectIDString(keyStr);
		try {
			Class<?> objectIDClass = loadClass(parts[0]);
			Constructor<?> constructor = objectIDClass.getConstructor(new Class[]{ String.class });
			return (ObjectID) constructor.newInstance(new Object[]{ keyStr });
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static ObjectID createObjectID(String className, String fieldPart)
	{
		String keyStr = createObjectIDString(className, fieldPart);
		try {
			Class<?> objectIDClass = loadClass(className);
			Constructor<?> constructor = objectIDClass.getConstructor(new Class[]{ String.class });
			return (ObjectID) constructor.newInstance(new Object[]{ keyStr });
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * <p>
	 * We use numeric primary key fields in some classes. These fields are sometimes encoded into a String
	 * (e.g. when having additionally a one-String-primary-key representation). Additionally, the IDs are
	 * sometimes shown in the UI.
	 * </p>
	 * <p>
	 * Because the numbers' String-representation gets very long with the decimal system, we should not encode
	 * them with the normal Number.toString() or String.valueOf(...) methods. Instead, we should (nearly) always
	 * use base-36-encoding (for extremely long numbers, you might want to use {@link org.nightlabs.math.Base62Coder} - i.e. the
	 * radix 62 with a case-sensitive complete alpha-numeric alphabet - instead).
	 * </p>
	 * <p>
	 * Even though this method (just like {@link #intObjectIDFieldToString(int)}) simply uses {@link Long#toString(long, int)}
	 * with the radix 36, you <b>must not</b> use this directly in your code. Please <b>always</b> use one of these methods
	 * instead, which makes searching code and changes much easier!
	 * </p>
	 *
	 * @param id A numeric primary key field value.
	 * @return The value of the numeric value encoded as String.
	 *
	 * @see #intObjectIDFieldToString(int)
	 * @see #parseLongObjectIDField(String)
	 * @see #parseIntObjectIDField(String)
	 */
	public static String longObjectIDFieldToString(long id)
	{
//		return Long.toString(id, 36);
		return Long.toString(id);
	}

	/**
	 * This method is equivalent to {@link #longObjectIDFieldToString(long)}
	 * but encodes an int value.
	 *
	 * @param id A numeric primary key field value.
	 * @return The value of the numeric value encoded as String.
	 *
	 * @see #longObjectIDFieldToString(long)
	 */
	public static String intObjectIDFieldToString(int id)
	{
//		return Integer.toString(id, 36);
		return Integer.toString(id);
	}

	/**
	 * This method corresponds to {@link #longObjectIDFieldToString(long)}.
	 *
	 * @param id A <code>String</code> encoded numeric primary key field value.
	 * @return The value of the numeric value parsed from the String.
	 *
	 * @see #longObjectIDFieldToString(long)
	 */
	public static long parseLongObjectIDField(String id)
	{
//		return Long.parseLong(id, 36);
		return Long.parseLong(id);
	}

	/**
	 * This method corresponds to {@link #intObjectIDFieldToString(int)}.
	 *
	 * @param id A <code>String</code> encoded numeric primary key field value.
	 * @return The value of the numeric value parsed from the String.
	 *
	 * @see #longObjectIDFieldToString(long)
	 */
	public static int parseIntObjectIDField(String id)
	{
//		return Integer.parseInt(id, 36);
		return Integer.parseInt(id);
	}

	protected ObjectIDUtil() { }

}
