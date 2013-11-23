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

public class StaticFieldMetaData
implements FieldMetaData
{
	private String fieldName;
	private byte writableByChildren = WRITABLEBYCHILDREN_YES;
	private boolean writable = true;
	private boolean valueInherited = true;

	public StaticFieldMetaData(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public byte getWritableByChildren()
	{
		return writableByChildren;
	}

	public void setWritableByChildren(byte writableByChildren)
	{
		this.writableByChildren = writableByChildren;
	}

	public boolean isWritable()
	{
		return writable;
	}

	public void assertWritable() throws NotWritableException
	{
		if (!isWritable())
			throw new NotWritableException("Field " + fieldName + " is not writable!");
	}

	public void setWritable(boolean writable)
	{
		this.writable = writable;
	}

	public boolean isValueInherited()
	{
		return valueInherited;
	}
	
	public void setValueInherited(boolean valueInherited)
	{
		this.valueInherited = valueInherited;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + (valueInherited ? 1231 : 1237);
		result = prime * result + (writable ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StaticFieldMetaData other = (StaticFieldMetaData) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (valueInherited != other.valueInherited)
			return false;
		if (writable != other.writable)
			return false;
		if (writableByChildren != other.writableByChildren)
			return false;
		return true;
	}
	
}
