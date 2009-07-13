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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipOutputStream;

import org.nightlabs.io.DataBuffer;

/**
 * This is a collection of utility functions. All methods
 * of this class are static.
 * @author Alex Bieber, Marc Klinger, Marco Schulze, Niklas Schiffler
 * @version 1.4 ;-)
 */
public abstract class Util
{
	/**
	 * Specifies usage of the MD5 algorithm in {@link #hash(byte[], String)} (or one of the other hash methods).
	 */
	public static final String HASH_ALGORITHM_MD5 = "MD5";
	/**
	 * Specifies usage of the SHA algorithm in {@link #hash(byte[], String)} (or one of the other hash methods).
	 */
	public static final String HASH_ALGORITHM_SHA = "SHA";

//	private static final Logger logger = Logger.getLogger(Util.class);

	/**
	 * Check two objects for equality.
	 * <p>
	 * This method is a convenience reducing code:
	 * <code>obj0 == obj1 || (obj0 != null && obj0.equals(obj1))</code>
	 * will be replaced by <code>Utils.equals(obj0, obj1)</code>
	 * and you do not need to worry about <code>null</code> anymore.
	 * <p>
	 * Additionally if you pass two arrays to this method
	 * (whose equals method only checks for equality [TODO doesn't this mean "identity" instead of "equality"?])
	 * this method will consult {@link Arrays#equals(Object[], Object[])}
	 * for equality of the parameters instead, of course after a <code>null</code> check.
	 *
	 * @param obj0 One object to check for equality
	 * @param obj1 The other object to check for equality
	 * @return <code>true</code> if both objects are <code>null</code> or
	 * 		if they are equal or if both objects are Object arrays
	 *    and equal according to {@link Arrays#equals(Object[], Object[])} -
	 *    <code>false</code> otherwise
	 */
	public static boolean equals(Object obj0, Object obj1)
	{
		if (obj0 instanceof Object[] && obj1 instanceof Object[])
			return obj0 == obj1 || Arrays.equals((Object[])obj0, (Object[])obj1);
		return obj0 == obj1 || (obj0 != null && obj0.equals(obj1));
	}

	/**
	 * Check two <code>long</code>s for equality.
	 * <p>
	 * In order to provide the same API for <code>Object</code> and <code>long</code>
	 * which both are often used as IDs, it is recommended to use this method
	 * instead of writing <code>id0 == id1</code>.
	 * </p>
	 * <p>
	 * To write <code>id0 == id1</code> is considered more error-prone if refactorings happen:
	 * Imagine, you create an object with a long unique id. Later on, you decide that a String id
	 * is better. You won't recognize that some old code <code>id0 == id1</code> is broken.
	 * When using this method instead, the compiler will automatically switch to {@link #equals(Object, Object)}
	 * and a correct result will be calculated.
	 * </p>
	 * <p>
	 * Even though Java 5 (and higher) implicitely converts simple datatypes to their corresponding object
	 * (e.g. <code>long</code> to <code>java.lang.Long</code>), it's unnecessary to perform this conversion
	 * and better to have this method instead.
	 * </p>
	 *
	 * @param l0 One long to check.
	 * @param l1 The other long to check.
	 * @return the result of: <code>l0 == l1</code>.
	 * @see #equals(Object, Object)
	 */
	public static boolean equals(long l0, long l1)
	{
		return l0 == l1;
	}
	/**
	 * Check two <code>int</code>s for equality.
	 * <p>
	 * This method does the same for <code>int</code>s as {@link #equals(long, long)}
	 * does for <code>long</code>s.
	 * </p>
	 *
	 * @param i0 One int to check.
	 * @param i1 The other int to check.
	 * @return the result of: <code>i0 == i1</code>.
	 * @see #equals(long, long)
	 */
	public static boolean equals(int i0, int i1)
	{
		return i0 == i1;
	}

	/**
	 * @param l The long number for which to calculate the hashcode.
	 * @return the same as new Long(l).hashCode() would do, but
	 * 		without the overhead of creating a Long instance.
	 */
	public static int hashCode(long l)
	{
		return (int)(l ^ (l >>> 32));
	}

	/**
	 * Get a hash code for an object. This method also handles
	 * <code>null</code>-Objects.
	 * @param obj An object or <code>null</code>
	 * @return <code>0</code> if <code>obj == null</code> -
	 * 		<code>obj.hashCode()</code> otherwise
	 */
	public static int hashCode(Object obj)
	{
		return obj == null ? 0 : obj.hashCode();
	}

	/**
	 * Returns a String with zeros prefixing
	 * the given base. The returned String will
	 * have at least a length of the given strLength.
	 * <p>
	 * This method calls  {@link #addLeadingChars(String, int, char)} with '0' as
	 * <code>fillChar</code>.
	 * </p>
	 *
	 * @param base The base String to prefix.
	 * @param strLength The length of the resulting String
	 * @return A string with zeros prefixing the given base.
	 */
	public static String addLeadingZeros(String base, int strLength)
	{
		return addLeadingChars(base, strLength, '0');
	}

	/**
	 * This method adds the character passed as <code>fillChar</code>
	 * to the front of the string <code>s</code> until the total length
	 * of the string reaches <code>length</code>. If the given string
	 * <code>s</code> is longer or exactly as long as defined by
	 * <code>length</code>, no characters will be added.
	 *
	 * @param s The string to which characters are appended (before).
	 * @param length The minimum length of the result.
	 * @param fillChar The character that will be added.
	 * @return the resulting string with as many <code>fillChar</code> characters added to it as necessary.
	 */
	public static String addLeadingChars(String s, int length, char fillChar)
	{
		if (s != null && s.length() >= length)
			return s;

		StringBuilder sb = new StringBuilder(length);
		int l = s == null ? length : length - s.length();
		while (sb.length() < l)
			sb.append(fillChar);

		if (s != null)
			sb.append(s);

		return sb.toString();
	}

	/**
	 * This method adds the character passed as <code>fillChar</code>
	 * to the end of the string <code>s</code> until the total length
	 * of the string reaches <code>length</code>. If the given string
	 * <code>s</code> is longer or exactly as long as defined by
	 * <code>length</code>, no characters will be added.
	 *
	 * @param s The string to which characters are appended (after).
	 * @param length The minimum length of the result.
	 * @param fillChar The character that will be added.
	 * @return the resulting string with as many <code>fillChar</code> characters added to it as necessary.
	 */
	public static String addTrailingChars(String s, int length, char fillChar)
	{
		if (s != null && s.length() >= length)
			return s;

		StringBuilder sb = new StringBuilder(length);
		if (s != null)
			sb.append(s);

		while (sb.length() < length)
			sb.append(fillChar);

		return sb.toString();
	}

	/**
	 * Returns a String with spaces prefixing
	 * the given base. The returned String will
	 * have at least a length of the given strLength.
	 * <p>
	 * This method calls {@link #addLeadingChars(String, int, char)} with spaces (' ') as
	 * <code>fillChar</code>.
	 * </p>
	 *
	 * @param base The base String to prefix.
	 * @param strLength The length of the resulting String
	 * @return A string with zeros prefixing the given base.
	 */
	public static String addLeadingSpaces(String base, int strLength)
	{
		return addLeadingChars(base, strLength, ' ');
	}

	/**
	 * This method encodes a byte array into a human readable hex string. For each byte,
	 * two hex digits are produced. They are concatted without any separators.
	 * <p>
	 * This is a convenience method for <code>encodeHexStr(buf, 0, buf.length)</code>
	 *
	 * @param buf The byte array to translate into human readable text.
	 * @return a human readable string like "fa3d70" for a byte array with 3 bytes and these values.
	 * @see #encodeHexStr(byte[], int, int)
	 * @see #decodeHexStr(String)
	 */
	public static String encodeHexStr(byte[] buf)
	{
		return encodeHexStr(buf, 0, buf.length);
	}

	/**
	 * Encode a byte array into a human readable hex string. For each byte,
	 * two hex digits are produced. They are concatted without any separators.
	 *
	 * @param buf The byte array to translate into human readable text.
	 * @param pos The start position (0-based).
	 * @param len The number of bytes that shall be processed beginning at the position specified by <code>pos</code>.
	 * @return a human readable string like "fa3d70" for a byte array with 3 bytes and these values.
	 * @see #encodeHexStr(byte[])
	 * @see #decodeHexStr(String)
	 */
	public static String encodeHexStr(byte[] buf, int pos, int len)
	{
		 StringBuffer hex = new StringBuffer();
		 while (len-- > 0) {
				byte ch = buf[pos++];
				int d = (ch >> 4) & 0xf;
				hex.append((char)(d >= 10 ? 'a' - 10 + d : '0' + d));
				d = ch & 0xf;
				hex.append((char)(d >= 10 ? 'a' - 10 + d : '0' + d));
		 }
		 return hex.toString();
	}

	/**
	 * Decode a string containing two hex digits for each byte.
	 * @param hex The hex encoded string
	 * @return The byte array represented by the given hex string
	 * @see #encodeHexStr(byte[])
	 * @see #encodeHexStr(byte[], int, int)
	 */
	public static byte[] decodeHexStr(String hex)
	{
		if (hex.length() % 2 != 0)
			throw new IllegalArgumentException("The hex string must have an even number of characters!");

		byte[] res = new byte[hex.length() / 2];

		int m = 0;
		for (int i = 0; i < hex.length(); i += 2) {
			res[m++] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}

		return res;
	}

	/**
	 * Get a 32 character MD5 hash in hex notation for the given input string.
	 * @param clear The input string to build the hash on
	 * @return The MD5 encoded hex string.
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5HexString(String clear)
	throws NoSuchAlgorithmException
	{
		byte[] enc = MessageDigest.getInstance("MD5").digest(clear.getBytes());
//		System.out.println(new String(enc));
		return encodeHexStr(enc, 0, enc.length);
	}

	/**
	 * Hash a byte array with the given algorithm.
	 *
	 * @param data The data to hash
	 * @param algorithm The name of the hash alogorithm (e.g. MD5, SHA) as supported by {@link MessageDigest}. If using one of these
	 *		algorithms, you should use the appropriate constant: {@link #HASH_ALGORITHM_MD5} or {@link #HASH_ALGORITHM_SHA}.
	 * @return the array of bytes for the resulting hash value.
	 * @throws NoSuchAlgorithmException if the algorithm is not available in the caller's environment.
	 *
	 * @see #hash(File, String)
	 * @see #hash(InputStream, long, String)
	 * @see #encodeHexStr(byte[])
	 */
	public static byte[] hash(byte[] data, String algorithm) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(data);
		return md.digest();
	}

	/**
	 * Hash an {@link InputStream} with the given algorithm.
	 *
	 * @param in The {@link InputStream} which will be read. This stream is not closed, but read until its end or until the number of bytes specified
	 *		in <code>bytesToRead</code> has been read.
	 * @param bytesToRead If -1, the {@link InputStream} <code>in</code> will be read till its end is reached. Otherwise, the amount of bytes specified by
	 *		this parameter is read. If the {@link InputStream} ends before having read the specified amount of bytes, an {@link IOException} will be thrown.
	 * @param algorithm The name of the hash alogorithm (e.g. MD5, SHA) as supported by {@link MessageDigest}. If using one of these
	 *		algorithms, you should use the appropriate constant: {@link #HASH_ALGORITHM_MD5} or {@link #HASH_ALGORITHM_SHA}.
	 * @return the array of bytes for the resulting hash value.
	 * @throws NoSuchAlgorithmException if the algorithm is not available in the caller's environment.
	 * @throws IOException if reading from the {@link InputStream} fails.
	 *
	 * @see #hash(byte[], String)
	 * @see #encodeHexStr(byte[])
	 */
	public static byte[] hash(InputStream in, long bytesToRead, String algorithm)
	throws NoSuchAlgorithmException, IOException
	{
		if (bytesToRead < 0 && bytesToRead != -1)
			throw new IllegalArgumentException("bytesToRead < 0 && bytesToRead != -1");

		long bytesReadTotal = 0;
		MessageDigest md = MessageDigest.getInstance(algorithm);
		byte[] data = new byte[10240];
		while (true) {
			int len;

			if(bytesToRead < 0)
				len = data.length;
			else {
				len = (int)Math.min(data.length, bytesToRead - bytesReadTotal);
				if (len < 1)
					break;
			}

			int bytesRead = in.read(data, 0, len);
			if (bytesRead < 0) {
				if (bytesToRead >= 0)
					throw new IOException("Unexpected EndOfStream! bytesToRead==" + bytesToRead + " but only " + bytesReadTotal + " bytes could be read from InputStream!");

				break;
			}

			bytesReadTotal += bytesRead;

			if (bytesRead > 0)
				md.update(data, 0, bytesRead);
		}
		return md.digest();
	}

	/**
	 * This methods reads a given file and calls {@link #hash(InputStream, long, String)}.
	 *
	 * @param file The file to read an hash.
	 * @param algorithm The algorithm to use.
	 * @return The result of {@link #hash(InputStream, long, String)}.
	 * @throws NoSuchAlgorithmException if the algorithm is not available in the caller's environment.
	 * @throws IOException if reading the <code>file</code> fails.
	 *
	 * @see #hash(byte[], String)
	 * @see #encodeHexStr(byte[])
	 */
	public static byte[] hash(File file, String algorithm)
	throws NoSuchAlgorithmException, IOException
	{
		FileInputStream in = new FileInputStream(file);
		try {
			return hash(in, -1, algorithm);
		} finally {
			in.close();
		}
	}

//	public static void main(String[] args)
//	{
//		File f = new File("/home/marco/workspaces/jfire/JFireTrade/dist/JFireTrade.jar");
//		try {
//			System.out.println(byteArrayToHexString(hash(f, HASH_ALGORITHM_MD5)));
//
//			byte[] data = new byte[(int)f.length()];
//			RandomAccessFile raf = new RandomAccessFile(f, "r");
//			raf.read(data);
//			System.out.println(byteArrayToHexString(hash(data, HASH_ALGORITHM_MD5)));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Truncates a double value at the given decimal position.
	 * E.g. <code>d</code> = 9.45935436363463 and <code>numDigits</code> = 2
	 * will return 9.45.
	 *
	 * @param d the double to shorten
	 * @param numDigits the number of decimal places after the decimal separator.
	 * @return the shorted double
	 */
	public static double truncateDouble(double d, int numDigits)
	{
		double multiplier = Math.pow(10, numDigits);
		return ((int) (d * multiplier)) / multiplier;
	}

	/**
	 * Makes a Double out of Integer. The parameter <code>numDigits</code>
	 * determines where the decimal separator should be, seen from the end.
	 * E.g. <code>value</code> = 135 and <code>numDigits</code> = 2 will
	 * return 1.35.
	 *
	 * @param value the integer to transform into a double
	 * @param numDigits determines where the decimal separator should be,
	 * 		seen from the end
	 * @return the transformed double
	 */
	public static double getDouble(int value, int numDigits)
	{
		double multiplier = Math.pow(10, numDigits);
		return (value) / multiplier;
	}

	public static <T> Collection<T> cloneSerializableAll(Collection<T> originalCollection)
	{
		return cloneSerializableAll(originalCollection, null);
	}

	public static <T> Collection<T> cloneSerializableAll(Collection<T> originalCollection, ClassLoader classLoader)
	{
		if (originalCollection == null)
			throw new IllegalArgumentException("originalCollection must not be null!");

		Collection<Object> result;
		if (originalCollection instanceof List)
			result = new ArrayList<Object>(originalCollection.size());
		else if (originalCollection instanceof SortedSet)
			result = new TreeSet<Object>();
		else if (originalCollection instanceof Set)
			result = new HashSet<Object>(originalCollection.size());
		else
			result = new ArrayList<Object>(originalCollection.size());

		for (T original : originalCollection) {
			result.add(cloneSerializable(original, classLoader));
		}

		Collection<T> res = CollectionUtil.castCollection(result);
		return res;
	}

	/**
	 * Helper method to clone an object that implements the {@link Cloneable}
	 * interface.
	 *
	 * @param <T> a class that implements {@link Cloneable}.
	 * @param original the instance that shall be cloned.
	 * @return the clone.
	 */
	public static <T extends Cloneable> T cloneCloneable(T original)
	{
		Method cloneMethod = null;
		try {
			cloneMethod = Object.class.getDeclaredMethod("clone");
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Why the hell does the clone() method not exist?!");
		}

		try {
			return (T) cloneMethod.invoke(original);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method clones a given object by serializing it (into a <code>DataBuffer</code>)
	 * and deserializing it again. It uses java native serialization, thus the object needs
	 * to implement {@link Serializable}.
	 *
	 * @param <T> Any class. It is not defined as "&lt;T extends Serializable&gt;" in order to allow
	 *		interfaces (e.g. <code>java.util.Map</code>) that don't extend {@link Serializable} but whose
	 *		implementations usually do.
	 * @param original The original object. It will be serialized and therefore needs to implement <code>Serializable</code>.
	 * @return The copy (deserialized clone) of the given <code>original</code>.
	 */
	public static <T> T cloneSerializable(T original)
	{
		if (original == null)
			return null;

		return cloneSerializable(original, original.getClass().getClassLoader());
//		DataBuffer db;
//		try {
//			db = new DataBuffer(10240);
//			ObjectOutputStream out = new ObjectOutputStream(db.createOutputStream());
//			try {
//				out.writeObject(original);
//			} finally {
//				out.close();
//			}
//		} catch (IOException x) { // there should never be a problem (under normal circumstances) as the databuffer should nearly always work in RAM only.
//			throw new RuntimeException(x);
//		}
//
//		try {
//			ObjectInputStream in = new ObjectInputStream(db.createInputStream());
//			try {
//				return (T) in.readObject();
//			} finally {
//				in.close();
//			}
//		} catch (ClassNotFoundException x) { // we deserialize an object of the same type as our parameter => the class should always be known
//			throw new RuntimeException(x);
//		} catch (IOException x) { // there should never be a problem (under normal circumstances) as the databuffer should nearly always work in RAM only.
//			throw new RuntimeException(x);
//		}
	}

	/**
	 * An {@link ObjectInputStream} instance that uses the given
	 * {@link ClassLoader} to resolve classes that are to be deserialized.
	 * @author Marc Klinger - marc[at]nightlabs[dot]de
	 */
	private static class ClassLoaderObjectInputStream extends ObjectInputStream
	{
		private ClassLoader classLoader;
		public ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
			super(in);
			this.classLoader = classLoader;
		}
		/* (non-Javadoc)
		 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
		 */
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc)
				throws IOException, ClassNotFoundException
		{
			if(classLoader == null)
				return super.resolveClass(desc);
			String name = desc.getName();
			try {
			    return Class.forName(name, false, classLoader);
			} catch (ClassNotFoundException ex) {
				return super.resolveClass(desc);
			}
		}
	}

	/**
	 * This method clones a given object by serializing it (into a <code>DataBuffer</code>)
	 * and deserializing it again. It uses java native serialization, thus the object needs
	 * to implement {@link Serializable}.
	 *
	 * @param <T> Any class. It is not defined as "&lt;T extends Serializable&gt;" in order to allow
	 *		interfaces (e.g. <code>java.util.Map</code>) that don't extend {@link Serializable} but whose
	 *		implementations usually do.
	 * @param original The original object. It will be serialized and therefore needs to implement <code>Serializable</code>.
	 * @param classLoader The class loader to use to resolve loaded classes or <code>null</code> to use the
	 * 		default lookup mechanism.
	 * @return The copy (deserialized clone) of the given <code>original</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneSerializable(T original, final ClassLoader classLoader)
	{
		if (original == null)
			return null;

		DataBuffer db;
		try {
			db = new DataBuffer(10240);
			ObjectOutputStream out = new ObjectOutputStream(db.createOutputStream());
			try {
				out.writeObject(original);
			} finally {
				out.close();
			}
		} catch (IOException x) { // there should never be a problem (under normal circumstances) as the databuffer should nearly always work in RAM only.
			throw new RuntimeException(x);
		}

		try {
			ObjectInputStream in =
					classLoader == null
					? new ObjectInputStream(db.createInputStream())
					: new ClassLoaderObjectInputStream(db.createInputStream(), classLoader);
			try {
				return (T) in.readObject();
			} finally {
				in.close();
			}
		} catch (ClassNotFoundException x) { // we deserialize an object of the same type as our parameter => the class should always be known
			throw new RuntimeException(x);
		} catch (IOException x) { // there should never be a problem (under normal circumstances) as the databuffer should nearly always work in RAM only.
			throw new RuntimeException(x);
		}
	}

	/**
	 * Get the stack trace representation of a <code>Throwable</code>
	 * as string.
	 * @param t The <code>Throwable</code>
	 * @return The stack trace as string.
	 */
	public static String getStackTraceAsString(Throwable t)
	{
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Append a field string representation for an Oject to
	 * a {@link StringBuffer}.
	 * @param o The Object to represent
	 * @param s A {@link StringBuffer} to append the string.
	 */
	public static void toFieldString(Object o, StringBuffer s)
	{
    final Field fields[] = o.getClass().getDeclaredFields();
    for (Field element : fields) {
    	s.append(",");
    	s.append(element.getName());
    	s.append("=");
  		try {
  			element.setAccessible(true);
  			s.append(element.get(o));
  		} catch (IllegalAccessException ex) {
  			s.append("*private*");
  		}
    }
	}

	/**
	 * Get a field string representation for an Oject.
	 * @param o The Object to represent
	 * @return The field string representation.
	 */
	public static String toFieldString(Object o)
	{
		StringBuffer s = new StringBuffer();
		toFieldString(o, s);
		return s.toString();
	}

	/**
	 * A generic reflection-based toString method.
	 * @param o The Object to represent
	 * @return The string representation of the object.
	 */
	public static String toString(Object o)
	{
		StringBuffer s = new StringBuffer();
		s.append(o.getClass().getName());
		s.append("@");
		s.append(Integer.toHexString(o.hashCode()));
		s.append("[");
		toFieldString(o, s);
		s.append("]");
		return s.toString();
	}

	/**
	 * Convert an URL to an URI.
	 * @param url The URL to cenvert
	 * @return The URI
	 * @throws MalformedURLException if the given URL is malformed
	 */
	public static URI urlToUri(URL url) throws MalformedURLException
	{
		if (url == null)
			return null;
		try {
			return new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url
					.getQuery(), url.getRef());
		} catch (URISyntaxException e) {
			MalformedURLException newEx = new MalformedURLException("URL " + url
					+ " was malformed");
			newEx.initCause(e);
			throw newEx;
		}
	}

	/**
	 * Get a string representation of the elapsed time.
	 * @param start The elapsed time in millis
	 * @return The elapsed time as string representation
	 */
	public static String getTimeString(long millis)
	{
		if(millis < 0)
			return "n/a";

		long seconds = millis/1000;
		long minutes = seconds/60;
		long hours = minutes/60;

		StringBuffer sb = new StringBuffer();

		if(hours > 0) {
			minutes -= hours*60;
			seconds -= hours*60*60;
			millis -= hours*60*60*1000;
			sb.append(hours);
			sb.append("h");
		}
		if(minutes > 0) {
			seconds -= minutes*60;
			millis -= minutes*60*1000;
			if(sb.length() > 0)
				sb.append(" ");
			sb.append(minutes);
			sb.append("m");
		}
		if(seconds > 0) {
			millis -= seconds*1000;
			if(sb.length() > 0)
				sb.append(" ");
			sb.append(seconds);
			sb.append("s");
		}
		if(sb.length() == 0 || millis > 0) {
			if(sb.length() > 0)
				sb.append(" ");
			sb.append(millis);
			sb.append("ms");
		}

		return sb.toString();
	}

	/**
	 * Get a string representation of the elapsed time between start and now.
	 * @param start The start time in millis
	 * @return The elapsed time as string representation
	 */
	public static String getTimeDiffString(long start)
	{
		return getTimeDiffString(start, System.currentTimeMillis());
	}

	/**
	 * Get a string representation of the elapsed time between start and end.
	 * @param start The start time in millis
	 * @param end The end time in millis
	 * @return The elapsed time as string representation
	 */
	public static String getTimeDiffString(long start, long end)
	{
		return getTimeString(end-start);
	}

	/**
	 * Create a random string out of the given alphabet.
	 * @param alphabet The alphabet to chose characters from
	 * @param minLen The minimum length (included).
	 * @param maxLen The maximum length (included).
	 * @return The newly created random string
	 */
	public static String createRandomString(CharSequence alphabet, int minLen, int maxLen)
	{
		if(alphabet == null || alphabet.length() < 1)
			throw new IllegalArgumentException("The password alphabet is empty");
		if(minLen < 0)
			throw new IllegalArgumentException("Invalid minLen (<0): "+minLen);
		if(maxLen < 0)
			throw new IllegalArgumentException("Invalid maxLen (<0): "+maxLen);
		if(maxLen < minLen)
			throw new IllegalArgumentException("Invalid minLen/maxLen combination (maxLen<minLen): "+maxLen+"<"+minLen);

		Random random = new Random();
		int len;
		if(minLen == maxLen)
			len = minLen;
		else
			len = minLen + random.nextInt(maxLen - minLen + 1);
		StringBuffer pw = new StringBuffer();
		for (int i = 0; i < len; ++i) {
			int v = alphabet.charAt(random.nextInt(alphabet.length()));
			pw.append((char)v);
		}
		return pw.toString();
	}





	// ################################################
	// ################################################
	// ##### DEPRECATED IO                        #####
	// ################################################
	// ################################################

	/**
	 * UTF-8 caracter set name.
	 * @deprecated Use {@link IOUtil#CHARSET_NAME_UTF_8} instead
	 */
	@Deprecated
	public static final String CHARSET_NAME_UTF_8 = IOUtil.CHARSET_NAME_UTF_8;

	/**
	 * UTF-8 caracter set.
	 * @deprecated Use {@link IOUtil#CHARSET_UTF_8} instead
	 */
	@Deprecated
	public static final Charset CHARSET_UTF_8 = IOUtil.CHARSET_UTF_8;

	/**
	 * 1 GB in bytes.
	 * This holds the result of the calculation 1 * 1024 * 1024 * 1024
	 * @deprecated Use {@link IOUtil#GIGABYTE} instead
	 */
	@Deprecated
	public static final long GIGABYTE = IOUtil.GIGABYTE;

	/**
	 * This method finds - if possible - a relative path for addressing
	 * the given <code>file</code> from the given <code>baseDir</code>.
	 * <p>
	 * If it is not possible to denote <code>file</code> relative to <code>baseDir</code>,
	 * the absolute path of <code>file</code> will be returned.
	 * </p>
	 * See {@link IOUtil#getRelativePath(File, String)} for examples.
	 *
	 * @param baseDir This directory is used as start for the relative path. It can be seen as the working directory
	 *		from which to point to <code>file</code>.
	 * @param file The file to point to.
	 * @return the path to <code>file</code> relative to <code>baseDir</code> or the absolute path,
	 *		if a relative path cannot be formulated (i.e. they have no directory in common).
	 * @throws IOException In case of an error
	 * @deprecated Use {@link IOUtil#getRelativePath(String,String)} instead
	 */
	@Deprecated
	public static String getRelativePath(String baseDir, String file)
	throws IOException
	{
		return IOUtil.getRelativePath(baseDir, file);
	}

	/**
	 * This method finds - if possible - a relative path for addressing
	 * the given <code>file</code> from the given <code>baseDir</code>.
	 * <p>
	 * If it is not possible to denote <code>file</code> relative to <code>baseDir</code>,
	 * the absolute path of <code>file</code> will be returned.
	 * </p>
	 * See {@link IOUtil#getRelativePath(File, String)} for examples.
	 *
	 * @param baseDir This directory is used as start for the relative path. It can be seen as the working directory
	 *		from which to point to <code>file</code>.
	 * @param file The file to point to.
	 * @return the path to <code>file</code> relative to <code>baseDir</code> or the absolute path,
	 *		if a relative path cannot be formulated (i.e. they have no directory in common).
	 * @throws IOException In case of an error
	 * @deprecated Use {@link IOUtil#getRelativePath(File,File)} instead
	 */
	@Deprecated
	public static String getRelativePath(File baseDir, File file)
	throws IOException
	{
		return IOUtil.getRelativePath(baseDir, file);
	}

	/**
	 * This method finds - if possible - a relative path for addressing
	 * the given <code>file</code> from the given <code>baseDir</code>.
	 * <p>
	 * If it is not possible to denote <code>file</code> relative to <code>baseDir</code>,
	 * the absolute path of <code>file</code> will be returned.
	 * </p>
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>
	 *   <code>baseDir="/home/marco"</code><br/>
	 *   <code>file="temp/jfire/jboss/bin/run.sh"</code><br/>
	 *     or<br/>
	 *   <code>file="/home/marco/temp/jfire/jboss/bin/run.sh"</code><br/>
	 *   <code>result="temp/jfire/jboss/bin/run.sh"</code>
	 * </li>
	 * <li>
	 *   <code>baseDir="/home/marco/workspace.jfire/JFireBase"</code><br/>
	 *   <code>file="/home/marco/temp/jfire/jboss/bin/run.sh"</code><br/>
	 *     or<br/>
	 *   <code>file="../../temp/jfire/jboss/bin/run.sh"</code><br/>
	 *   <code>result="../../temp/jfire/jboss/bin/run.sh"</code>
	 * </li>
	 * <li>
	 *   <code>baseDir="/tmp/workspace.jfire/JFireBase"</code><br/>
	 *   <code>file="/home/marco/temp/jfire/jboss/bin/run.sh"</code><br/>
	 *   <code>result="/home/marco/temp/jfire/jboss/bin/run.sh"</code> (absolute, because relative is not possible)
	 * </li>
	 * </ul>
	 * </p>
	 *
	 * @param baseDir This directory is used as start for the relative path. It can be seen as the working directory
	 *		from which to point to <code>file</code>.
	 * @param file The file to point to.
	 * @return the path to <code>file</code> relative to <code>baseDir</code> or the absolute path,
	 *		if a relative path cannot be formulated (i.e. they have no directory in common).
	 * @throws IOException In case of an error
	 * @deprecated Use {@link IOUtil#getRelativePath(File,String)} instead
	 */
	@Deprecated
	public static String getRelativePath(File baseDir, String file)
	throws IOException
	{
		return IOUtil.getRelativePath(baseDir, file);
	}

	/**
	 * This method removes double slashes, single-dot-directories and double-dot-directories
	 * from a path. This means, it does nearly the same as <code>File.getCanonicalPath</code>, but
	 * it does not resolve symlinks. This is essential for the method <code>getRelativePath</code>,
	 * because this method first tries it with a simplified path before using the canonical path
	 * (prevents problems with iteration through directories, where there are symlinks).
	 * <p>
	 * Please note that this method makes the given path absolute!
	 *
	 * @param path A path to simplify, e.g. "/../opt/java/jboss/../jboss/./bin/././../server/default/lib/."
	 * @return the simplified string (absolute path), e.g. "/opt/java/jboss/server/default/lib"
	 * @deprecated Use {@link IOUtil#simplifyPath(File)} instead
	 */
	@Deprecated
	public static String simplifyPath(File path)
	{
		return IOUtil.simplifyPath(path);
	}

	/**
	 * Transfer all available data from an {@link InputStream} to an {@link OutputStream}.
	 * <p>
	 * This is a convenience method for <code>transferStreamData(in, out, 0, -1)</code>
	 * @param in The stream to read from
	 * @param out The stream to write to
	 * @return The number of bytes transferred
	 * @throws IOException In case of an error
	 * @deprecated Use {@link IOUtil#transferStreamData(InputStream, OutputStream)} instead
	 */
	@Deprecated
	public static long transferStreamData(java.io.InputStream in, java.io.OutputStream out)
	throws java.io.IOException
	{
		return IOUtil.transferStreamData(in, out, 0, -1);
	}


	/**
	 * This method deletes the given directory recursively. If the given parameter
	 * specifies a file and no directory, it will be deleted anyway. If one or more
	 * files or subdirectories cannot be deleted, the method still continues and tries
	 * to delete as many files/subdirectories as possible.
	 *
	 * @param dir The directory or file to delete
	 * @return True, if the file or directory does not exist anymore. This means it either
	 * was not existing already before or it has been successfully deleted. False, if the
	 * directory could not be deleted.
	 * @deprecated Use {@link IOUtil#deleteDirectoryRecursively(String)} instead
	 */
	@Deprecated
	public static boolean deleteDirectoryRecursively(String dir)
	{
		return IOUtil.deleteDirectoryRecursively(dir);
	}

	/**
	 * This method deletes the given directory recursively. If the given parameter
	 * specifies a file and no directory, it will be deleted anyway. If one or more
	 * files or subdirectories cannot be deleted, the method still continues and tries
	 * to delete as many files/subdirectories as possible.
	 *
	 * @param dir The directory or file to delete
	 * @return <code>true</code> if the file or directory does not exist anymore.
	 * 		This means it either was not existing already before or it has been
	 * 		successfully deleted. <code>false</code> if the directory could not be
	 * 		deleted.
	 * @deprecated Use {@link IOUtil#deleteDirectoryRecursively(File)} instead
	 */
	@Deprecated
	public static boolean deleteDirectoryRecursively(File dir)
	{
		return IOUtil.deleteDirectoryRecursively(dir);
	}

	/**
	 * Tries to find a unique, not existing folder name under the given root folder with a random
	 * number (in hex format) added to the given prefix. When found, the directory will be created.
	 * <p>
	 * This is a convenience method for {@link IOUtil#createUniqueRandomFolder(File, String, long, long)}
	 * and calls it with 10000 as maxIterations and 10000 as number range.
	 * <p>
	 * Note that this method might throw a {@link IOException}
	 * if it will not find a unique name within 10000 iterations.
	 * <p>
	 * Note that the creation of the directory is not completely safe. This method is
	 * synchronized, but other processes could "steal" the unique filename.
	 *
	 * @param rootFolder The rootFolder to find a unique subfolder for
	 * @param prefix A prefix for the folder that has to be found.
	 * @return A File pointing to an unique (non-existing) Folder under the given rootFolder and with the given prefix
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#createUniqueRandomFolder(File,String)} instead
	 */
	@Deprecated
	public static File createUniqueRandomFolder(File rootFolder, final String prefix) throws IOException
	{
		return IOUtil.createUniqueRandomFolder(rootFolder, prefix);
	}

	/**
	 * Tries to find a unique, not existing folder name under the given root folder with a random
	 * number (in hex format) added to the given prefix. When found, the directory will be created.
	 * <p>
	 * The method will try to find a name for <code>maxIterations</code> number
	 * of itarations and use random numbers from 0 to <code>uniqueOutOf</code>.
	 * <p>
	 * Note that the creation of the directory is not completely safe. This method is
	 * synchronized, but other processes could "steal" the unique filename.
	 *
	 * @param rootFolder The rootFolder to find a unique subfolder for
	 * @param prefix A prefix for the folder that has to be found.
	 * @param maxIterations The maximum number of itarations this method shoud do.
	 * 		If after them still no unique folder could be found, a {@link IOException}
	 * 		is thrown.
	 * @param uniqueOutOf The range of random numbers to apply (0 - given value)
	 *
	 * @return A File pointing to an unique folder under the given rootFolder and with the given prefix
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#createUniqueRandomFolder(File,String,long,long)} instead
	 */
	@Deprecated
	public static synchronized File createUniqueRandomFolder(File rootFolder, final String prefix, long maxIterations, long uniqueOutOf) throws IOException
	{
		return IOUtil.createUniqueRandomFolder(rootFolder, prefix, maxIterations, uniqueOutOf);
	}

	/**
	 * Tries to find a unique, not existing folder name under the given root folder
	 * suffixed with a number. When found, the directory will be created.
	 * It will start with 0 and make Integer.MAX_VALUE number
	 * of iterations maximum. The first not existing foldername will be returned.
	 * If no foldername could be found after the maximum iterations a {@link IOException}
	 * will be thrown.
	 * <p>
	 * Note that the creation of the directory is not completely safe. This method is
	 * synchronized, but other processes could "steal" the unique filename.
	 *
	 * @param rootFolder The rootFolder to find a unique subfolder for
	 * @param prefix A prefix for the folder that has to be found.
	 * @return A File pointing to an unique (not existing) Folder under the given rootFolder and with the given prefix
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#createUniqueIncrementalFolder(File,String)} instead
	 */
	@Deprecated
	public static synchronized File createUniqueIncrementalFolder(File rootFolder, final String prefix) throws IOException
	{
		return IOUtil.createUniqueIncrementalFolder(rootFolder, prefix);
	}

	/**
	 * Transfer data between streams
	 * @param in The input stream
	 * @param out The output stream
	 * @param inputOffset How many bytes to skip before transferring
	 * @param inputLen How many bytes to transfer. -1 = all
	 * @return The number of bytes transferred
	 * @throws IOException if an error occurs.
	 * @deprecated Use {@link IOUtil#transferStreamData(java.io.InputStream,java.io.OutputStream,long,long)} instead
	 */
	@Deprecated
	public static long transferStreamData(java.io.InputStream in, java.io.OutputStream out, long inputOffset, long inputLen)
	throws java.io.IOException
	{
		return IOUtil.transferStreamData(in, out, inputOffset, inputLen);
	}

	/**
	 * Copy a resource loaded by the class loader of a given class to a file.
	 * <p>
	 * This is a convenience method for <code>copyResource(sourceResClass, sourceResName, new File(destinationFilename))</code>.
	 * @param sourceResClass The class whose class loader to use. If the class
	 * 		was loaded using the bootstrap class loaderClassloader.getSystemResourceAsStream
	 * 		will be used. See {@link Class#getResourceAsStream(String)} for details.
	 * @param sourceResName The name of the resource
	 * @param destinationFilename Where to copy the contents of the resource
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#copyResource(Class,String,String)} instead
	 */
	@Deprecated
	public static void copyResource(Class<?> sourceResClass, String sourceResName, String destinationFilename)
	throws IOException
	{
		IOUtil.copyResource(sourceResClass, sourceResName, destinationFilename);
	}

	/**
	 * Copy a resource loaded by the class loader of a given class to a file.
	 * @param sourceResClass The class whose class loader to use. If the class
	 * 		was loaded using the bootstrap class loaderClassloader.getSystemResourceAsStream
	 * 		will be used. See {@link Class#getResourceAsStream(String)} for details.
	 * @param sourceResName The name of the resource
	 * @param destinationFile Where to copy the contents of the resource
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#copyResource(Class, String, File)} instead
	 */
	@Deprecated
	public static void copyResource(Class<?> sourceResClass, String sourceResName, File destinationFile)
	throws IOException
	{
		IOUtil.copyResource(sourceResClass, sourceResName, destinationFile);
	}

	/**
	 * Copy a file.
	 * <p>
	 * This is a convenience method for <code>copyFile(new File(sourceFilename), new File(destinationFilename))</code>
	 * @param sourceFilename The source file to copy
	 * @param destinationFilename To which file to copy the source
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#copyFile(String,String)} instead
	 */
	@Deprecated
	public static void copyFile(String sourceFilename, String destinationFilename)
	throws IOException
	{
		IOUtil.copyFile(sourceFilename, destinationFilename);
	}

	/**
	 * Copy a file.
	 * @param sourceFile The source file to copy
	 * @param destinationFile To which file to copy the source
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#copyFile(File, File)} instead
	 */
	@Deprecated
	public static void copyFile(File sourceFile, File destinationFile)
	throws IOException
	{
		IOUtil.copyFile(sourceFile, destinationFile);
	}

	/**
	 * Copy a directory recursively.
	 * @param sourceDirectory The source directory
	 * @param destinationDirectory The destination directory
	 * @throws IOException in case of an error
	 * @deprecated Use {@link IOUtil#copyDirectory(File,File)} instead
	 */
	@Deprecated
	public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException
	{
		IOUtil.copyDirectory(sourceDirectory, destinationDirectory);
	}

	/**
	 * Get a file object from a base directory and a list of subdirectories or files.
	 * @param file The base directory
	 * @param subDirs The subdirectories or files
	 * @return The new file instance
	 * @deprecated Use {@link IOUtil#getFile(File,String...)} instead
	 */
	@Deprecated
	public static File getFile(File file, String ... subDirs)
	{
		return IOUtil.getFile(file, subDirs);
	}


	/**
	 * Write text to a file using UTF-8 encoding.
	 * @param file The file to write the text to
	 * @param text The text to write
	 * @throws IOException in case of an io error
	 * @throws FileNotFoundException if the file exists but is a directory
	 *                   rather than a regular file, does not exist but cannot
	 *                   be created, or cannot be opened for any other reason
	 * @deprecated Use {@link IOUtil#writeTextFile(File,String)} instead
	 */
	@Deprecated
	public static void writeTextFile(File file, String text)
	throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		IOUtil.writeTextFile(file, text);
	}

	/**
	 * Write text to a file.
	 * @param file The file to write the text to
	 * @param text The text to write
	 * @param encoding The caracter set to use as file encoding (e.g. "UTF-8")
	 * @throws IOException in case of an io error
	 * @throws FileNotFoundException if the file exists but is a directory
	 *                   rather than a regular file, does not exist but cannot
	 *                   be created, or cannot be opened for any other reason
	 * @throws UnsupportedEncodingException If the named encoding is not supported
	 * @deprecated Use {@link IOUtil#writeTextFile(File,String,String)} instead
	 */
	@Deprecated
	public static void writeTextFile(File file, String text, String encoding)
	throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		IOUtil.writeTextFile(file, text, encoding);
	}

	/**
	 * Read a UTF-8 encoded text file and return the contents as string.
	 * <p>For other encodings, use {@link IOUtil#readTextFile(File, String)}.
	 * @param f The file to read, maximum size 1 GB
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException in case of an io error
	 * @return The contents of the text file
	 * @deprecated Use {@link IOUtil#readTextFile(File)} instead
	 */
	@Deprecated
	public static String readTextFile(File f)
	throws FileNotFoundException, IOException
	{
		return IOUtil.readTextFile(f);
	}

	/**
	 * Read a text file and return the contents as string.
	 * @param f The file to read, maximum size 1 GB
	 * @param encoding The file encoding, e.g. "UTF-8"
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException in case of an io error
	 * @throws UnsupportedEncodingException If the named encoding is not supported
	 * @return The contents of the text file
	 * @deprecated Use {@link IOUtil#readTextFile(File,String)} instead
	 */
	@Deprecated
	public static String readTextFile(File f, String encoding)
	throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		return IOUtil.readTextFile(f, encoding);
	}

	/**
	 * Read a text file from an {@link InputStream} using
	 * UTF-8 encoding.
	 * <p>
	 * This method does NOT close the input stream!
	 * @param in The stream to read from. It will not be closed by this operation.
	 * @return The contents of the input stream file
	 * @deprecated Use {@link IOUtil#readTextFile(InputStream)} instead
	 */
	@Deprecated
	public static String readTextFile(InputStream in)
	throws FileNotFoundException, IOException
	{
		return IOUtil.readTextFile(in);
	}

	/**
	 * Read a text file from an {@link InputStream} using
	 * the given encoding.
	 * <p>
	 * This method does NOT close the input stream!
	 * @param in The stream to read from. It will not be closed by this operation.
	 * @param encoding The charset used for decoding, e.g. "UTF-8"
	 * @return The contents of the input stream file
	 * @deprecated Use {@link IOUtil#readTextFile(InputStream,String)} instead
	 */
	@Deprecated
	public static String readTextFile(InputStream in, String encoding)
	throws FileNotFoundException, IOException
	{
		return IOUtil.readTextFile(in, encoding);
	}

	/**
	 * Get the extension of a filename.
	 * @param fileName A file name (might contain the full path) or <code>null</code>.
	 * @return <code>null</code>, if the given <code>fileName</code> doesn't contain
	 *		a dot (".") or if the given <code>fileName</code> is <code>null</code>. Otherwise,
	 *		returns all AFTER the last dot.
	 * @deprecated Use {@link IOUtil#getFileExtension(String)} instead
	 */
	@Deprecated
	public static String getFileExtension(String fileName)
	{
		return IOUtil.getFileExtension(fileName);
	}

	/**
	 * Get a filename without extension.
	 * @param fileName A file name (might contain the full path) or <code>null</code>.
	 * @return all before the last dot (".") or the full <code>fileName</code> if no dot exists.
	 * 		Returns <code>null</code>, if the given <code>fileName</code> is <code>null</code>.
	 * @deprecated Use {@link IOUtil#getFileNameWithoutExtension(String)} instead
	 */
	@Deprecated
	public static String getFileNameWithoutExtension(String fileName)
	{
		return IOUtil.getFileNameWithoutExtension(fileName);
	}

	/**
	 * Get the temporary directory.
	 *
	 * FIXME: it seems not to be a good practise to create
	 * 		a temp file just to get the directory. accessing
	 * 		the system temp dir property would work without
	 * 		hd access and without throwing an IOException.
	 * 		See File.getTempDir() (private) (Marc)
	 *
	 * @return The temporary directory.
	 * @throws IOException In case of an error
	 * @deprecated Use {@link IOUtil#getTempDir()} instead
	 */
	@Deprecated
	public static File getTempDir()
	throws IOException
	{
		return IOUtil.getTempDir();
	}


	/**
	 * Compares two InputStreams.
	 *
	 * @param in1 the first InputStream
	 * @param in2 the second InputStream
	 * @param length the length to read from each InputStream
	 * @return true if both InputStreams contain the identical data or false if not
	 * @throws IOException if an I/O error occurs while reading <code>length</code> bytes
	 * 		from one of the input streams.
	 * @deprecated Use {@link IOUtil#compareInputStreams(InputStream,InputStream,int)} instead
	 */
	@Deprecated
	public static boolean compareInputStreams(InputStream in1, InputStream in2, int length)
	throws IOException
	{
		return IOUtil.compareInputStreams(in1, in2, length);
	}

	/**
	 * Recursively zips all entries of the given zipInputFolder to
	 * a zipFile defined by zipOutputFile.
	 *
	 * @param zipOutputFile The file to write to (will be deleted if existent).
	 * @param zipInputFolder The inputFolder to zip.
	 * @deprecated Use {@link IOUtil#zipFolder(File,File)} instead
	 */
	@Deprecated
	public static void zipFolder(File zipOutputFile, File zipInputFolder)
	throws IOException
	{
		IOUtil.zipFolder(zipOutputFile, zipInputFolder);
	}

	/**
	 * Recursively writes all found files as entries into the given ZipOutputStream.
	 *
	 * @param out The ZipOutputStream to write to.
	 * @param zipOutputFile the output zipFile. optional. if it is null, this method cannot check whether
	 *		your current output file is located within the zipped directory tree. You must not locate
	 *		your zip-output file within the source directory, if you leave this <code>null</code>.
	 * @param files The files to zip (optional, defaults to all files recursively). It must not be <code>null</code>,
	 *		if <code>entryRoot</code> is <code>null</code>.
	 * @param entryRoot The root folder of all entries. Entries in subfolders will be
	 *		added relative to this. If <code>entryRoot==null</code>, all given files will be
	 *		added without any path (directly into the zip's root). <code>entryRoot</code> and <code>files</code> must not
	 *		both be <code>null</code> at the same time.
	 * @throws IOException in case of an I/O error.
	 * @deprecated Use {@link IOUtil#zipFilesRecursively(ZipOutputStream,File,File[],File)} instead
	 */
	@Deprecated
	public static void zipFilesRecursively(ZipOutputStream out, File zipOutputFile, File[] files, File entryRoot)
	throws IOException
	{
		IOUtil.zipFilesRecursively(out, zipOutputFile, files, entryRoot);
	}

	/**
	 * Unzip the given archive into the given folder.
	 *
	 * @param zipArchive The zip file to unzip.
	 * @param unzipRootFolder The folder to unzip to.
	 * @throws IOException in case of an I/O error.
	 * @deprecated Use {@link IOUtil#unzipArchive(File,File)} instead
	 */
	@Deprecated
	public static void unzipArchive(File zipArchive, File unzipRootFolder)
	throws IOException
	{
		IOUtil.unzipArchive(zipArchive, unzipRootFolder);
	}

	/**
	 * Add a trailing file separator character to the
	 * given directory name if it does not already
	 * end with one.
	 * @see File#separator
	 * @param directory A directory name
	 * @return the directory name anding with a file seperator
	 * @deprecated Use {@link IOUtil#addFinalSlash(String)} instead
	 */
	@Deprecated
	public static String addFinalSlash(String directory)
	{
		return IOUtil.addFinalSlash(directory);
	}

	/**
	 * Generate a file from a template. The template file can contain variables which are formatted <code>"${variable}"</code>.
	 * All those variables will be replaced, for which a value has been passed in the map <code>variables</code>.
	 * <p>
	 * Example:<br/>
	 * <pre>
	 * ***
	 * Dear ${receipient.fullName},
	 * this is a spam mail trying to sell you ${product.name} for a very cheap price.
	 * Best regards, ${sender.fullName}
	 * ***
	 * </pre>
	 * <br/>
	 * In order to generate a file from the above template, the map <code>variables</code> needs to contain values for these keys:
	 * <ul>
	 * <li>receipient.fullName</li>
	 * <li>product.name</li>
	 * <li>sender.fullName</li>
	 * </ul>
	 * </p>
	 * <p>
	 * If a key is missing in the map, the variable will not be replaced but instead written as-is into the destination file (a warning will be
	 * logged).
	 * </p>
	 *
	 * @param destinationFile The file (absolute!) that shall be created out of the template.
	 * @param templateFile The template file to use. Must not be <code>null</code>.
	 * @param variables This map defines what variable has to be replaced by what value. The
	 *		  key is the variable name (without '$' and brackets '{', '}'!) and the value is the
	 *		  value for the variable to replace. This must not be <code>null</code>.
	 * @deprecated Use {@link IOUtil#replaceTemplateVariables(File, File, Map, String)} instead. This mehtod
	 * 		will delegate to that using {@link Charset#defaultCharset()}.
	 */
	@Deprecated
	public static void replaceTemplateVariables(File destinationFile, File templateFile, Map<String, String> variables)
	throws IOException
	{
		IOUtil.replaceTemplateVariables(destinationFile, templateFile, Charset.defaultCharset().name(), variables);
	}




	// ################################################
	// ################################################
	// ##### DEPRECATED COLLECTION                #####
	// ################################################
	// ################################################

	/**
	 * This method calls {@link #array2ArrayList(Object[], boolean)} with
	 * <code>canReturnNull = true</code>.
	 *
	 * @param objects An array of objects - can be <code>null</code>.
	 * @return an <code>ArrayList</code> (or <code>null</code> if <code>objects == null</code>).
	 * @deprecated Use {@link CollectionUtil#array2ArrayList(T[])} instead
	 */
	@Deprecated
	public static <T> ArrayList<T> array2ArrayList(T[] objects)
	{
		return CollectionUtil.array2ArrayList(objects);
	}

	/**
	 * @param canReturnNull If <code>false</code>, the result will never be <code>null</code>,
	 *		but an empty list if <code>objects</code> is null.
	 * @param objects An array of objects - can be <code>null</code>.
	 * @return an <code>ArrayList</code> - never <code>null</code>.
	 * The <code>ArrayList</code> is empty if <code>objects</code> is <code>null</code> and
	 * <code>canReturnNull</code> is <code>false</code>. If <code>canReturnNull == true</code>
	 * and <code>objects == null</code>, the result will be <code>null</code>.
	 * @deprecated Use {@link CollectionUtil#array2ArrayList(T[],boolean)} instead
	 */
	@Deprecated
	public static <T> ArrayList<T> array2ArrayList(T[] objects, boolean canReturnNull)
	{
		return CollectionUtil.array2ArrayList(objects, canReturnNull);
	}

	/**
	 * This method calls {@link #array2HashSet(Object[], boolean)} with
	 * <code>canReturnNull = true</code>.
	 *
	 * @param objects An array of objects - can be <code>null</code>.
	 * @return an <code>ArrayList</code> (or <code>null</code>, if <code>objects == null</code>).
	 * @deprecated Use {@link CollectionUtil#array2HashSet(T[])} instead
	 */
	@Deprecated
	public static <T> HashSet<T> array2HashSet(T[] objects)
	{
		return CollectionUtil.array2HashSet(objects);
	}

	/**
	 * @param canReturnNull If <code>false</code>, the result will never be <code>null</code>,
	 *		but an empty list if <code>objects</code> is null.
	 * @param objects An array of objects - can be <code>null</code>.
	 * @return an <code>ArrayList</code> - never <code>null</code>.
	 * The <code>ArrayList</code> is empty if <code>objects</code> is <code>null</code> and
	 * <code>canReturnNull</code> is <code>false</code>. If <code>canReturnNull == true</code>
	 * and <code>objects == null</code>, the result will be <code>null</code>.
	 * @deprecated Use {@link CollectionUtil#array2HashSet(T[],boolean)} instead
	 */
	@Deprecated
	public static <T> HashSet<T> array2HashSet(T[] objects, boolean canReturnNull)
	{
		return CollectionUtil.array2HashSet(objects, canReturnNull);
	}

	/**
	 * This method calls {@link #collection2TypedArray(Collection, Class, boolean)} with
	 * <code>canReturnNull = true</code>.
	 *
	 * @param c Either <code>null</code> or a <code>Collection</code> with instances of the type specified by <code>clazz</code> (or descendants of it).
	 * @param clazz The type of the elements of the returned object-array (e.g. <code>String.class</code> for the returned type <code>String[]</code>).
	 * @return a typed object array (or <code>null</code>, if <code>c == null</code>).
	 * @deprecated Use {@link CollectionUtil#collection2TypedArray(Collection<T>,Class<T>)} instead
	 */
	@Deprecated
	public static <T> T[] collection2TypedArray(Collection<T> c, Class<T> clazz)
	{
		return CollectionUtil.collection2TypedArray(c, clazz);
	}

	/**
	 * @param c Either <code>null</code> or a <code>Collection</code> with instances of the type specified by <code>clazz</code> (or descendants of it).
	 * @param clazz The type of the elements of the returned object-array (e.g. <code>String.class</code> for the returned type <code>String[]</code>).
	 * @param canReturnNull If <code>false</code>, the result will never be <code>null</code>,
	 *		but an empty array if <code>c == null</code>.
	 *
	 * @return a typed object array (or <code>null</code>, if <code>c == null && canReturnNull</code>).
	 *		If <code>canReturnNull</code> is false and <code>c == null</code>, an empty array will be returned.
	 * @deprecated Use {@link CollectionUtil#collection2TypedArray(Collection<T>,Class<T>,boolean)} instead
	 */
	@Deprecated
	public static <T> T[] collection2TypedArray(Collection<T> c, Class<T> clazz, boolean canReturnNull)
	{
		return CollectionUtil.collection2TypedArray(c, clazz, canReturnNull);
	}

	/**
	 * Moves the given element up in the given list.
	 *
	 * @param list The list
	 * @param element The element
	 * @deprecated Use {@link CollectionUtil#moveListElementUp(List,Object)} instead
	 */
	@Deprecated
	public static <T> void moveListElementUp(List<T> list, T element) {
		CollectionUtil.moveListElementUp(list, element);
	}

	/**
	 * Moves the given element down in the given list.
	 * @param list The list
	 * @param element The element
	 * @deprecated Use {@link CollectionUtil#moveListElementDown(List,Object)} instead
	 */
	@Deprecated
	public static <T> void moveListElementDown(List<T> list, T element) {
		CollectionUtil.moveListElementDown(list, element);
	}




	// ################################################
	// ################################################
	// ##### DEPRECATED OTHER                     #####
	// ################################################
	// ################################################

	/**
	 * @deprecated Use String.format("%0{minDigits}d", i) instead
	 */
	@Deprecated
	public static String int2StringMinDigits(int i, int minDigits)
	{
		String s = Integer.toString(i);
		if (s.length() < minDigits) {
			StringBuffer sBuf = new StringBuffer(s);
			while (sBuf.length() < minDigits)
				sBuf.insert(0, '0');

			s = sBuf.toString();
		}

		return s;
	}

	/**
	 * @deprecated FIXME: This method has the wrong name. It does in fact encode - not decode. Use
	 *		{@link #encodeHexStr(byte[], int, int)} or {@link #encodeHexStr(byte[])}.
	 */
	@Deprecated
	public static String decodeHexStr(byte[] buf, int pos, int len)
	{
		return encodeHexStr(buf, pos, len);
	}

	/**
	 * @deprecated FIXME: this method has a misleading name.
	 * 		It is a mixture of something like htmlEntities
	 * 		(but not complete), nl2br (but nox XHTML conform)
	 * 		and tab2spaces. (Marc)
	 */
	@Deprecated
	public static String htmlEncode(String s)
	{
		if (s == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '\n')
				sb.append("<br>");
			else if (ch == '<')
				sb.append("&lt;");
			else if (ch == '>')
				sb.append("&gt;");
			else if (ch == '\t')
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
				sb.append(ch);
		} // for (int i = 0; i < bytesRead; i++) {
		return sb.toString();
	}

	/**
	 * @deprecated This method has a wrong name. It does not create a
	 * 		unique key, but hashes the input value. Use {@link #hash(byte[], String)}
	 * 		instead.
	 *
	 * Hash a byte array with the given algorithm.
	 *
	 * @param imageData the data to generate a unique key for
	 * @param algorithm the name of the alogorithm (e.g. MD5, SHA)
	 * @return a unique key for the given byte[]
	 */
	@Deprecated
	public static byte[] generateUniqueKey(byte[] data, String algorithm)
	{
		try {
			return hash(data, algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @deprecated Use {@link Arrays#equals(byte[], byte[])}.
	 *
	 * compares two byte[]s
	 *
	 * @param b1 the first byte[]
	 * @param b2 the second byte[]
	 * @return true if both byte[]s contain the identical data or false if not
	 */
	@Deprecated
	public static boolean compareByteArrays(byte[] b1, byte[] b2)
	{
		return Arrays.equals(b1, b2);
	}

	/**
	 * @deprecated Use {@link CollectionUtil#enum2List(Enum<T>)} instead
	 */
	@Deprecated
	public static <T extends Enum<T>> List<T> enum2List(Enum<T> e)
	{
		return CollectionUtil.enum2List(e);
	}

	/**
	 * @deprecated Misleading name: Use {@link #truncateDouble(double, int)}.
	 */
	@Deprecated
	public static double shortenDouble(double d, int numDigits)
	{
		return truncateDouble(d, numDigits);
	}

	/**
	 * @deprecated Use {@link #getStackTraceAsString(Throwable)} instead
	 */
	@Deprecated
	public static String getStacktraceAsString(Throwable t)
	{
		return getStackTraceAsString(t);
	}

	/**
	 * Get a byte array as hex string.
	 * <p>
	 * This method used upper-case "A"..."F" till version 1.2 of this class. From version 1.3 of this class on,
	 * it uses "a"..."f" because it now delegates to {@link #encodeHexStr(byte[])}.
	 * </p>
	 * @param in The source byte array
	 * @return the hex string.
	 * @deprecated Use {@link #encodeHexStr(byte[])} instead.
	 */
	@Deprecated
	public static String byteArrayToHexString(byte in[])
	{
		return encodeHexStr(in);
	}

	/**
	 * Rotates value n bits left through the sign.
	 * (source: http://mindprod.com/jgloss/rotate.html)
	 *
	 * @param value Value to rotated.
	 * @param n how many bits to rotate left.
	 * must be in range 0..31
	 * @return Value rotated.
	 */
	public static int rotateLeft ( int value, int n )
	{
		return( value << n ) | ( value >>> (32 - n) );
	}

	/**
	 * Rotates value n bits right through the sign.
	 * (source: http://mindprod.com/jgloss/rotate.html)
	 *
	 * @param value Value to rotated.
	 * @param n how many bits to rotate night.
	 * must be in range 0..31
	 * @return Value rotated.
	 */
	public static int rotateRight ( int value, int n )
	{
		return( value >>> n ) | ( value << (32- n) );
	}

	/**
	 * Rotates value n bits left through the sign.
	 * (source: http://mindprod.com/jgloss/rotate.html)
	 *
	 * @param value Value to rotated.
	 * @param n how many bits to rotate left.
	 * must be in range 0..63
	 * @return Value rotated.
	 */
	public static long rotateLeft ( long value, int n )
	{
		return( value << n ) | ( value >>> (64 - n) );
	}

	/**
	 * Rotates value n bits right through the sign.
	 * (source: http://mindprod.com/jgloss/rotate.html)
	 *
	 * @param value Value to rotated.
	 * @param n how many bits to rotate night.
	 * must be in range 0..63
	 * @return Value rotated.
	 */
	public static long rotateRight ( long value, int n )
	{
		return( value >>> n ) | ( value << (64- n) );
	}

	/**
	 * Get the user name of the user who is currently authenticated at the operating system.
	 * This method simply calls <code>System.getProperty("user.name");</code>.
	 *
	 * @return the user name of the current operating system user.
	 */
	public static String getUserName()
	{
		return System.getProperty("user.name"); //$NON-NLS-1$
	}
}