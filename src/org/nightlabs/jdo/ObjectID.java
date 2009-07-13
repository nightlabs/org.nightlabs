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
package org.nightlabs.jdo;

import java.io.Serializable;

/**
 * This is a tagging interface which should be implemented by all JDO object ID classes; but
 * only if they fulfill the following requirements:
 * <ul>
 *	<li>
 *		All requirements defined by the JDO specification.
 *	</li>
 *	<li>
 *		The format of the <code>toString()</code> result is compatible with
 *		{@link org.nightlabs.jdo.ObjectIDUtil#createObjectID(String)}. This means, it must start with
 *		"jdo/" (see {@link org.nightlabs.jdo.ObjectIDUtil#JDO_PREFIX} and {@link org.nightlabs.jdo.ObjectIDUtil#JDO_PREFIX_SEPARATOR}).
 *		Then, the fully qualified class name of the object-id-class and all the encoded fields must follow
 *		(separated by {@link org.nightlabs.jdo.ObjectIDUtil#CLASS_SEPARATOR} which is "?"). The fields are encoded
 *		in the form "{fieldname}={value}" where {value} is url-encoded (using UTF-8). Multiple fields are separated
 *		using "&" (see {@link org.nightlabs.jdo.ObjectIDUtil#FIELD_SEPARATOR}).
 *	</li>
 *	<li>
 *		It is ensured that <code>toString()</code> always returns the same result, if and only if all fields of the
 *		objectID are the same. This means that the order in which the fields are encoded does not change.
 *	</li>
 * </ul>
 * <p>
 * As a convenience, this interface extends <tt>Serializable</tt>, which must be implemented by JDO
 * object ID classes, too.
 * </p>
 * <p>
 * An example of a <code>toString()</code> result might be this: jdo/org.nightlabs.blabla.MyObjectID?organisationID=my.organisation.com&type=external&id=2736
 * </p>
 * <p>
 * Note: This interface would fit much better into the project NightLabsJDO, but there are rare
 * usecases, where you want to tag a class with it without having dependencies on JDO (we just
 * had this case - Marco).
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ObjectID
extends Serializable
{
}
