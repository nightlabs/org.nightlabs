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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * This class should be used as ancestor for all jdo object id classes. It implements
 * Serializable and it makes sure, hashCode(), equals(...), toString() are working in
 * a jdo compliant way. The only thing, you have to do yourself for making your class
 * jdo compliant, is just creating two constructors like in this example:
 * 
 * <code><pre>
 * public class MyObjectIdClass extends BaseObjectID {
 * 
 *   // This constructor is needed by JDO and NOT meant to
 *   // be executed manually.
 *   public MyObjectIdClass() { }
 *
 *   // This constructor is needed by JDO and creates an instance with all
 *   // members set to the values encoded in keyStr. It is not meant to
 *   // be executed manually.
 *   public MyObjectIdClass(String keyStr)
 *     throws ObjectIDException
 *   {
 *     // initFields(...) parses the keyStr and sets all member variables of this instance
 *     initFields(keyStr);
 *   }
 * 
 *   // Because the constructors are needed by jdo, it's a good idea to always
 *   // use static create methods for manual creation of objectId instances.
 *   public static MyObjectIdClass create(String _thePK)
 *   {
 *     MyObjectIdClass instance = new MyObjectIdClass();
 *     instance.thePK = _thePK;
 *     return instance;
 *   }
 * 
 *   protected String thePK;
 * 
 *   public String getThePK()
 *   {
 *     return thePK;
 *   }
 * }
 * </pre></code>
 * 
 * 
 * @author marco
 *
 * @deprecated XDoclet can now create JDO objectid-classes. Therefore you should not
 *		create them manually anymore! The static methods are now available in
 *		{@link org.nightlabs.jdo.ObjectIDUtil}!
 */
@Deprecated
public class BaseObjectID
implements ObjectID
{
	private static final long serialVersionUID = 1L;
	private static Pattern pattern_makeValidIDString = null;
	private static Pattern pattern_isValidIDString = null;

	/**
	 * @deprecated Use {@link ObjectIDUtil#makeValidIDString(String)}.
	 */
	@Deprecated
	public static String makeValidIDString(String id)
	{
		return makeValidIDString(id, false);
	}

	/**
	 * @deprecated Use {@link ObjectIDUtil#makeValidIDString(String, boolean)}.
	 */
	@Deprecated
	public static String makeValidIDString(String id, boolean appendUniqueID)
	{
		if (id == null || "".equals(id))
			appendUniqueID = true;
	
		if (appendUniqueID) {
			StringBuffer sb = id == null ? new StringBuffer() : new StringBuffer(id);
//			SimpleDateFormat dateFormat = new SimpleDateFormat();
//			dateFormat.applyPattern("yyyyMMddHHmmssSSS");
//			sb.append(dateFormat.format(new Date()));
			sb.append(Long.toHexString(System.currentTimeMillis()));
			sb.append(Integer.toHexString((int)(Integer.MAX_VALUE * Math.random())));
			id = sb.toString();
		}
		
		if (pattern_makeValidIDString == null)
			pattern_makeValidIDString = Pattern.compile("[^a-zA-Z0-9\\.\\$!_-]");
		
		return id = pattern_makeValidIDString.matcher(id).replaceAll("_").toLowerCase();
	}

	/**
	 * This method checks an id string for validity. The given id is valid,
	 * if it contains only the following characters: "a"-"z", "A"-"Z", "0"-"9", ".", "$", "!", "_", "-"
	 *
	 * @param id The id to be validated.
	 * @return True if valid, false otherwise.
	 *
	 * @deprecated Use {@link ObjectIDUtil#isValidIDString(String)}
	 */
	@Deprecated
	public static boolean isValidIDString(String id)
	{
		if (id == null || "".equals(id))
			return false;

		if (pattern_isValidIDString == null)
			pattern_isValidIDString = Pattern.compile("[a-zA-Z0-9\\.\\$!_-]*");
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
	 *
	 * @deprecated Use {@link ObjectIDUtil#assertValidIDString(String)}
	 */
	@Deprecated
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
	 *
	 * @deprecated Use {@link ObjectIDUtil#assertValidIDString(String, String)}
	 */
	@Deprecated
	public static void assertValidIDString(String id, String idName)
		throws IllegalArgumentException
	{
		if (!isValidIDString(id))
			throw new IllegalArgumentException(idName + " \""+id+"\" is not a valid id string!");
	}

	public BaseObjectID() { }

	/**
	 * This constructor creates a new instance of BaseObjectID by parsing a keyStr
	 * that has been created by BaseObjectID.toString() and setting all fields
	 * to the values from the string.
	 *
	 * @param keyStr A String formatted as "jdo/{className}?{field0}={value0}&{field1}={value1}...&{fieldN}={valueN}"
	 * 	where all values are url encoded.
	 *
	 * @throws ObjectIDException
	 */
	public BaseObjectID(String keyStr)
		throws ObjectIDException
	{
		try {
//			keyStringCache = null;
			Class<?> clazz = this.getClass();

			StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
			String jdoPrefix = st.nextToken();
			if (!JDO_PREFIX.equals(jdoPrefix))
				throw new ObjectIDException("keyStr \""+keyStr+"\" does not start with jdo prefix \""+JDO_PREFIX+"\"!");
			if (!st.hasMoreTokens() || st.nextToken().charAt(0) != JDO_PREFIX_SEPARATOR)
				throw new ObjectIDException("keyStr \""+keyStr+"\" is missing separator \""+JDO_PREFIX_SEPARATOR+"\" after jdo prefix!");

			String className = st.nextToken();
			if (!className.equals(clazz.getName()))
				throw new ObjectIDException("keyStr defines class \""+className+"\", but this is an instance of \""+clazz.getName()+"\"!");

			if (!st.hasMoreTokens() || st.nextToken().charAt(0) != CLASS_SEPARATOR)
				throw new ObjectIDException("keyStr \""+keyStr+"\" is missing separator \""+CLASS_SEPARATOR+"\" after class!");

			while (st.hasMoreTokens()) {
				String key = st.nextToken();
				String valStr = EMPTYSTRING;
				if (st.hasMoreTokens()) {
					String sep = st.nextToken();
					if (!SEPARATOR_KEY_VALUE.equals(sep))
						throw new KeyFormatException("Expected \""+SEPARATOR_KEY_VALUE+"\", but found \""+sep+"\"!");

					if (st.hasMoreTokens()) {
						valStr = st.nextToken();
						if (SEPARATOR_ENTRY.equals(valStr)) {
							sep = valStr;
							valStr = EMPTYSTRING;
						}
						else
							try {
								valStr = URLDecoder.decode(valStr, ENCODING);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
								throw new RuntimeException(e);
							}
					}
					
					if (!SEPARATOR_ENTRY.equals(sep)) {
						if (st.hasMoreTokens()) {
							sep = st.nextToken();
							if (!SEPARATOR_ENTRY.equals(sep))
								throw new KeyFormatException("Expected \""+SEPARATOR_ENTRY+"\", but found \""+sep+"\"!");
						}
					} // if (!SEPARATOR_ENTRY.equals(val)) {
				} // if (st.hasMoreTokens()) {
				Field field = clazz.getField(key);
				
//			} // while (st.hasMoreTokens()) {
//
//			Field[] fields = this.getClass().getDeclaredFields();
//			for (int i = 0; i < fields.length; ++i) {
//				Field field = fields[i];
//				if (ignoreField(field))
//					continue;
//
				Class<?> fieldType = field.getType();
//
//				String key = field.getName();
//				String valStr = (String)keyMap.get(key);
				
				field.setAccessible(true);
				
				if (valStr == null) {
					if (!fieldType.isPrimitive())
						field.set(this, null);
					else {
						if (boolean.class.isAssignableFrom(fieldType))
							field.set(this, Boolean.FALSE);
						
						if (char.class.isAssignableFrom(fieldType))
							field.set(this, NULLCHAR);
							
						if (byte.class.isAssignableFrom(fieldType) ||
								int.class.isAssignableFrom(fieldType) ||
								float.class.isAssignableFrom(fieldType) ||
								long.class.isAssignableFrom(fieldType))
							field.set(this, NULLBYTE);
					}
				}
				else {
					Object val = null;
					if (String.class.isAssignableFrom(fieldType)) {
						val = valStr;
					}
					else if (Number.class.isAssignableFrom(fieldType)) {
						Constructor<?> c = fieldType.getConstructor(new Class[] {String.class});
						val = c.newInstance(new Object[] {valStr});
					}
					else if (boolean.class.isAssignableFrom(fieldType)) {
						val = new Boolean(valStr);
					}
					else if (char.class.isAssignableFrom(fieldType)) {
						val = new Character(valStr.charAt(0));
					}
					else if (byte.class.isAssignableFrom(fieldType)) {
						val = new Byte(valStr);
					}
					else if (int.class.isAssignableFrom(fieldType)) {
						val = new Integer(valStr);
					}
					else if (long.class.isAssignableFrom(fieldType)) {
						val = new Long(valStr);
					}
					else
						throw new IllegalArgumentException("Type "+fieldType.getName()+" of member "+key+" is not unsupported!");
		
					field.set(this, val);
				}
			}
		} catch (Exception x) {
			if (x instanceof ObjectIDException)
				throw (ObjectIDException)x;
			else
				throw new ObjectIDException(x);
		}
	}

	/**
	 * The values of all members are url encoded in UTF-8.
	 */
	public static final String ENCODING="UTF-8";

	protected static final String JDO_PREFIX = "jdo";
	protected static final char JDO_PREFIX_SEPARATOR = '/';
	protected static final char CLASS_SEPARATOR = '?';
	protected static final String EMPTYSTRING = "";
	protected static final String SEPARATORS_FOR_TOKENIZER = "/?=&";
	protected static final String SEPARATOR_KEY_VALUE = "=";
	protected static final String SEPARATOR_ENTRY = "&";
	protected static final Byte NULLBYTE = new Byte((byte)0);
	protected static final Character NULLCHAR = new Character((char)0);

//	/**
//	 * This method parses the keyStr, decodes it and returns a name-value-map with the key
//	 * being the memberName within the object id class and the value being a String
//	 * representation of the value of this member.
//	 * <br/><br/>
//	 * This method is a helper method of <code>initFields(String keyStr)</code>. You probably
//	 * never need to use it directly.
//	 *
//	 * @param keyStr
//	 * @return Map: String key, String value
//	 * @throws KeyFormatException If the key is not formatted like a URL-param
//	 *
//	 * @see #initFields(String keyStr)
//	 * @see #encodeKeyStr(Map keyMap)
//	 */
//	public static final Map decodeKeyStr(String keyStr)
//	throws KeyFormatException
//	{
//		Map keyMap = new HashMap();
//
//		StringTokenizer st = new StringTokenizer(keyStr, SEPARATORS_FOR_TOKENIZER, true);
//		while (st.hasMoreTokens()) {
//			String key = st.nextToken();
//			String val = EMPTYSTRING;
//			if (st.hasMoreTokens()) {
//				String sep = st.nextToken();
//				if (!SEPARATOR_KEY_VALUE.equals(sep))
//					throw new KeyFormatException("Expected \""+SEPARATOR_KEY_VALUE+"\", but found \""+sep+"\"!");
//
//				if (st.hasMoreTokens()) {
//					val = st.nextToken();
//					if (SEPARATOR_ENTRY.equals(val)) {
//						sep = val;
//						val = EMPTYSTRING;
//					}
//					else
//						try {
//							val = URLDecoder.decode(val, ENCODING);
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//							throw new RuntimeException(e);
//						}
//				}
//
//				if (!SEPARATOR_ENTRY.equals(sep)) {
//					if (st.hasMoreTokens()) {
//						sep = st.nextToken();
//						if (!SEPARATOR_ENTRY.equals(sep))
//							throw new KeyFormatException("Expected \""+SEPARATOR_ENTRY+"\", but found \""+sep+"\"!");
//					}
//				} // if (!SEPARATOR_ENTRY.equals(val)) {
//			} // if (st.hasMoreTokens()) {
//			keyMap.put(key, val);
//		} // while (st.hasMoreTokens()) {
//
//		return keyMap;
//	}
	
//	/**
//	 * This method takes a map and encodes it into a single string. It uses
//	 * an url-like syntax ("name1=value1&name2=value2&name3=value3"). Hereby
//	 * it encodes all the values with an URLEncoder and encoding UTF-8. The
//	 * keys are <u>not</u> encoded.
//	 * <br/><br/>
//	 * This method is a helper method of <code>toString()</code>. You probably
//	 * never need to use it directly.
//	 *
//	 * @param keyMap
//	 * @return A string containing all name-value pairs of the map.
//	 *
//	 * @see #toString()
//	 * @see #decodeKeyStr(String keyStr)
//	 */
//	public static final String encodeKeyStr(Map keyMap)
//	{
//		StringBuffer sb = new StringBuffer();
//
//		for (Iterator it = keyMap.entrySet().iterator(); it.hasNext(); ) {
//			Map.Entry me = (Map.Entry)it.next();
//
//			// no need to encode the key
//			sb.append((String)me.getKey());
//			sb.append('=');
//
//			try {
//				// but we need to encode the value
//				sb.append(URLEncoder.encode((String)me.getValue(), ENCODING));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//				throw new RuntimeException(e);
//			}
//
//			if (it.hasNext())
//				sb.append('&');
//		}
//
//		return sb.toString();
//	}
	
	/**
	 * We ignore members that are static, final or transient. This method
	 * is used to check that.
	 * 
	 * @param field Ignore this field?
	 * @return true if the field should not be included in the keyStr.
	 */
	protected static boolean ignoreField(Field field)
	{
		int modifiers = field.getModifiers();
		
		if ((modifiers & Modifier.STATIC) != 0)
			return true;
		
		if ((modifiers & Modifier.FINAL) != 0)
			return true;
		
		if ((modifiers & Modifier.TRANSIENT) != 0)
			return true;
		
		return false;
	}
	
//	/**
//	 * This member is used to speed up the <code>toString()</code> method. Because
//	 * <code>toString()</code> is used by <code>hashCode()</code> and
//	 * <code>equals(...)</code>, this caching is really necessary.
//	 * <br/><br/>
//	 * Normally, the values of the members in an objectId never change, but if they
//	 * do, you <b>must</b> set this member to <code>null</code>!!!
//	 */
//	protected transient String keyStringCache = null;

	/**
	 * JDO expects the result of this method being compatible with a String constructor.
	 * This method takes all the fields within this class and encodes them with their name
	 * and their value. Only final, static and transient members are not included. If
	 * a member is <code>null</code>, it will not be included, either.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		if (keyStringCache == null) {
//			System.out.println(this.getClass().getName()+".toString(): have no cache - creating string.");
			
			StringBuffer sb = new StringBuffer(JDO_PREFIX);
			sb.append(JDO_PREFIX_SEPARATOR);
			sb.append(this.getClass().getName());
			sb.append(CLASS_SEPARATOR);

			boolean firstField = true;
			Field[] fields = this.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				Field field = fields[i];
				if (ignoreField(field))
					continue;
	
				String key = field.getName();
				String val = null;
	
				field.setAccessible(true);
				
				try {
					Object o = field.get(this);
					if (o != null)
						val = o.toString();
				} catch (Throwable e) {
					e.printStackTrace();
					if (e instanceof RuntimeException)
						throw (RuntimeException)e;
					else
						throw new RuntimeException(e);
				}

				if (val != null) {
					if (firstField)
						firstField = false;
					else
						sb.append('&');

					// no need to encode the key
					sb.append(key);
					sb.append('=');

					try {
						// but we need to encode the value
						sb.append(URLEncoder.encode(val, ENCODING));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				} // if (val != null) {
				
				
			} // for (int i = 0; i < fields.length; ++i) {

			return sb.toString();
	
//			String keyStringCache = encodeKeyStr(keyMap);
////		} // if (keyStringCache == null) {
//
//		System.out.println(this.getClass().getName()+".toString(): return \""+keyStringCache+"\"");
//		return keyStringCache;
	}

	/**
	 * This method returns true, if the parameter object is either the same instance
	 * or containing exactly the same values in all non-transient, non-static, non-final
	 * object fields. Only if the type is not compatible or if the values are different,
	 * this method will return false.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!this.getClass().isInstance(obj))
			return false;

		return this.toString().equals(obj.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
