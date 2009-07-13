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
 * This interface makes the values of the class' members inheritable.
 * <p>
 * It is highly recommended to have a <code>public static final</code> inner class called
 * <code>FieldName</code> where all field names are defined as <code>public static final String</code>
 * constants with the same name as the field itself.
 * In the future the JFire project will probably autogenerate this class,
 * but until then you should implement it manually.
 * This avoids the use of hardcoded strings for retrieving {@link FieldMetaData}
 * or {@link FieldInheriter} of an implementation of Inheritable.
 * </p>
 * <p>
 * See the wiki page <a href="https://www.jfire.org/modules/phpwiki/index.php/Framework%20DataInheritance">Framework DataInheritance</a>
 * for further information.
 * </p>
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface Inheritable
{
	/**
	 * This method should return an instance of {@link FieldMetaData} or <code>null</code> for
	 * a given <code>fieldName</code>. If it returns <code>null</code>, the field is ignored and not
	 * inherited.
	 * <p>
	 * In your implementation of this method, you should use constants of a subclass called <code>FieldName</code>
	 * (see class-based documentation above), rather than hardcoding {@link String}s.
	 * </p>
	 *
	 * @param fieldName the simple name of the field.
	 * @return an instance of <code>FieldMetaData</code> or <code>null</code>.
	 */
	FieldMetaData getFieldMetaData(String fieldName);

	/**
	 * To perform the copying of a field value, a {@link FieldInheriter} is used.
	 * The child is asked for a {@link FieldInheriter} to do the job.
	 *
	 * @param fieldName the simple name of the field.
	 * @return an instance if FieldInheriter
	 */
	FieldInheriter getFieldInheriter(String fieldName);
}
