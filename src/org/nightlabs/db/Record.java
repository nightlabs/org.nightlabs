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
package org.nightlabs.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision$ - $Date$
 * @author Alexander Bieber, Marc Klinger, Marco Schulze, Niklas Schiffler
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Object> fields;

	public Record(List<Object> fields) {
		this.fields = fields;
	}

	public Record(Object ... _fields) {
		fields = new ArrayList<Object>();
		for (Object element : _fields)
			this.fields.add(element);
	}

	public Record(Object _field) {
		this(new Object[] {_field});
	}

	public Object getObject(int col) {
		return fields.get(col - 1);
	}

	public Object setObject(int col, Object object) {
		if (col < 1)
			throw new IndexOutOfBoundsException("No column with col < 1! ColumnIndexes are based on 1, not 0!");

		if (col > fields.size())
			throw new IndexOutOfBoundsException("No column with this index! You cannot add columns with this method!");

		return fields.set(col - 1, object);
	}

	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * This method adds a field to a record. Because a table must always have
	 * a rectangular form, this method is not public and should only be executed
	 * by the TableBuffer [in TableBuffer.addColumn(...)].
	 *
	 * @see TableBuffer#addColumn(FieldDef fieldDef, Object defaultValue)
	 */
	void addObject(Object newColumnObj) {
		fields.add(newColumnObj);
	}

}
