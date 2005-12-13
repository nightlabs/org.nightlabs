/* ************************************************************************** *
 * Copyright (C) 2004 NightLabs GmbH, Marco Schulze                           *
 * All rights reserved.                                                       *
 * http://www.NightLabs.de                                                    *
 *                                                                            *
 * This program and the accompanying materials are free software; you can re- *
 * distribute it and/or modify it under the terms of the GNU Lesser General   *
 * Public License as published by the Free Software Foundation; either ver 2  *
 * of the License, or any later version.                                      *
 *                                                                            *
 * This module is distributed in the hope that it will be useful, but WITHOUT *
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FIT- *
 * NESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more *
 * details.                                                                   *
 *                                                                            *
 * You should have received a copy of the GNU General Public License along    *
 * with this module; if not, write to the Free Software Foundation, Inc.:     *
 *    59 Temple Place, Suite 330                                              *
 *    Boston MA 02111-1307                                                    *
 *    USA                                                                     *
 *                                                                            *
 * Or get it online:                                                          *
 *    http://www.opensource.org/licenses/lgpl-license.php                     *
 *                                                                            *
 * In case, you want to use this module or parts of it in a commercial way    * 
 * that is not allowed by the LGPL, pleas contact us and we will provide a    *
 * commercial licence.                                                        *
 * ************************************************************************** */

/*
 * Created on 02.10.2004
 */
package com.nightlabs.classloader;

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
	 * @param name The fully qualified class name (e.g. "com.nightlabs.classloader.ClassLoaderDelegate")
	 * @return Returns either an instance of ClassData defining the desired class or null. ClassData can
	 * 	either contain a byte array or an InputStream. It must contain exactly one of them.
	 */
	public ClassData getClassData(String name);
}
