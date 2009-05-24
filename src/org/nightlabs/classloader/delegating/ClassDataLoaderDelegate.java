/* *****************************************************************************
 * DelegatingClassLoader - NightLabs extendable classloader                    *
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

package org.nightlabs.classloader.delegating;

import java.io.InputStream;
import java.security.ProtectionDomain;

/**
 * @author marco
 */
public interface ClassDataLoaderDelegate extends ResourceFinder
{
	/**
	 * ClassData is a descriptor for defining binary class information ready to be converted
	 * into a java class. The binary data can be defined either as a byte array or as an
	 * InputStream.
	 */
	public static class ClassData {
		public ClassData(byte[] _classDataAsByteArray) {
			this(_classDataAsByteArray, 0, -1);
		}
		/**
		 * @param _classDataAsByteArray
		 * @param _offset must be >= 0
		 * @param _length may be < 0 to specify that the loader should read till the end of the byte array.
		 */
		public ClassData(byte[] _classDataAsByteArray, int _offset, int _length) {
			if (_classDataAsByteArray == null)
				throw new NullPointerException("constructor-param classDataAsByteArray must not be null.");
			if (_offset < 0)
				throw new IllegalArgumentException("constructor-param offset < 0!");
			if (_length < 0)
				_length = _classDataAsByteArray.length;
			this.classDataAsByteArray = _classDataAsByteArray;
			this.offset = _offset;
			this.length = _length;
		}
		public ClassData(InputStream _classDataAsInputStream) {
			if (_classDataAsInputStream == null)
				throw new NullPointerException("constructor-param classDataAsInputStream must not be null.");
			this.classDataAsInputStream = _classDataAsInputStream;
		}

		private byte[] classDataAsByteArray = null;
		private int offset = 0;
		private int length = 0;

		private InputStream classDataAsInputStream = null;
		
		private ProtectionDomain protectionDomain = null;

		public byte[] getClassDataAsByteArray()
		{
			return classDataAsByteArray;
		}
		/**
		 * The offset is only used in combination with the classDataAsByteArray. If using an InputStream,
		 * it's meaningless.
		 *
		 * @return Returns the offset.
		 */
		public int getOffset()
		{
			return offset;
		}
		/**
		 * The length is only used in combination with the classDataAsByteArray. If using an InputStream,
		 * it's meaningless.
		 *
		 * @return Returns the length.
		 */
		public int getLength()
		{
			return length;
		}
		public InputStream getClassDataAsInputStream()
		{
			return classDataAsInputStream;
		}
		/**
		 * @return Returns the protectionDomain.
		 */
		public ProtectionDomain getProtectionDomain()
		{
			return protectionDomain;
		}
		/**
		 * @param protectionDomain The protectionDomain to set.
		 */
		public void setProtectionDomain(ProtectionDomain protectionDomain)
		{
			this.protectionDomain = protectionDomain;
		}
	}
	
	/**
	 * This method is called by <tt>DelegatingClassLoader.findClass(...)</tt> to
	 * get the class in binary form. If an instance of ClassData is returned, the DelegatingClassLoader
	 * calls <tt>defineClass(...)</tt>; if null is returned, the next registered ClassLoaderDelegate
	 * will be asked.
	 *
	 * @param name The fully qualified class name (e.g. "org.nightlabs.classloader.ClassLoaderDelegate")
	 * @return Returns either an instance of ClassData defining the desired class or null. ClassData can
	 * 	either contain a byte array or an InputStream. It must contain exactly one of them.
	 */
	public ClassData getClassData(String name);
}
