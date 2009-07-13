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
package org.nightlabs.inheritance;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface FieldMetaData
{
	/**
	 * @return Returns the field name.
	 */
	String getFieldName();

	static final byte WRITABLEBYCHILDREN_NO = 0;
	static final byte WRITABLEBYCHILDREN_YES = 1;
	static final byte WRITABLEBYCHILDREN_INHERITED = 2;

	/**
	 * @return Returns whether or not the children are allowed to write into the field. It is a bitmask.
	 */
	byte getWritableByChildren();

	/**
	 * Sets whether or not children may change the value.
	 *
	 * @param writableByChildren This may be either {@link #WRITABLEBYCHILDREN_YES} or {@link #WRITABLEBYCHILDREN_NO}.
	 *		Internally, it may additionally pass {@link #WRITABLEBYCHILDREN_INHERITED} (ored (||) with one of the other two
	 *		constants), but you should never pass {@link #WRITABLEBYCHILDREN_INHERITED} directly, if you don't know
	 *		what you're doing.
	 */
	void setWritableByChildren(byte writableByChildren);

	/**
	 * @return Returns whether the value of the field may be written.
	 * This method must return false if the mother's writableByChildren
	 * is false.
	 */
	boolean isWritable();

	/**
	 * This method should check via isWritable whether the field may be written
	 * and throw an NotWritableException, if it's not.
	 * 
	 * @throws NotWritableException If the field is not writable
	 */
	void assertWritable()
		throws NotWritableException;

	/**
	 * Sets whether or not the value may be written. This method
	 * is called by the engine if the mother's writableByChildren
	 * is altered.
	 *
	 * @param writable
	 */
	void setWritable(boolean writable);

	/**
	 * @return Whether the value stored in the field is set on child
	 * level or inherited from the mother. If the mother's value changes,
	 * the child's value is updated automatically, if this method returns
	 * true.
	 */
	boolean isValueInherited();

	/**
	 * @param valueInherited
	 */
	void setValueInherited(boolean valueInherited);

}
