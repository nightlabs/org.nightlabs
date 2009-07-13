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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * @version $Revision$ - $Date$
 */
public class Column implements Serializable {

	private static final long serialVersionUID = 1L;

	public Column(String _columnName, String complexColumnType, boolean _nullable)
	throws SQLException
	{
		// first we extract the simple type name (without size definition)
		StringTokenizer st = new StringTokenizer(complexColumnType, "(");
		String simpleTypeName = st.nextToken().toUpperCase();
		int fieldTypeID = TypeConverter.getTypeIDByName(simpleTypeName);

		int displaySize = -1;
		int scale = -1;
		if (st.hasMoreTokens()) {
			// now, we have to find out what size
			st = new StringTokenizer(st.nextToken(), ")");
			String bracketContent = st.nextToken();
//			Log.logDefault(Log.DEBUG, "parsing complex column name. bracketContent: "+bracketContent);
			String displaySizeStr = bracketContent;
			String scaleStr = null;
			if (bracketContent.indexOf(',') >= 0) {
				st = new StringTokenizer(bracketContent, ",");
				displaySizeStr = st.nextToken();
				scaleStr = st.nextToken();
			}
//			Log.logDefault(Log.DEBUG, "parsing complex column name. displaySizeStr: "+displaySizeStr);
//			Log.logDefault(Log.DEBUG, "parsing complex column name. scaleStr: "+scaleStr);

			try {
				displaySize = Integer.parseInt(displaySizeStr);
			} catch (Throwable t) {
				throw new SQLException("columnName \""+columnName+"\" has invalid format. Size cannot be parsed as integer! Expected is \"simpletypename\", \"simpletypename(size)\" or \"simpletypename(size,scale)\".");
			}
			if (scaleStr != null) {
				try {
					scale = Integer.parseInt(scaleStr);
				} catch (Throwable t) {
					throw new SQLException("columnName \""+columnName+"\" has invalid format. Size cannot be parsed as integer! Expected is \"simpletypename\", \"simpletypename(size)\" or \"simpletypename(size,scale)\".");
				}
			}

		} // if (st.hasMoreTokens()) {

		this.columnName = _columnName;
		this.columnType = fieldTypeID;
		this.columnDisplaySize = displaySize;
		this.columnScale = scale;
		this.nullable = _nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
	}

	/**
	 * fieldSize is only necessary if the field is of type VARCHAR (or later added
	 * dynamic sizable field types). Otherwise (if the field size is static), the
	 * fieldSize is ignored and set to -1.
	 */
	public Column(String _columnName, int _columnType, int _columnDisplaySize, int _columnScale, boolean _nullable)
	{
		this(
				_columnName,
				_columnType,
				_columnDisplaySize,
				_columnScale,
				_nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls
		);
	}

	public Column(String columnName, int columnType, int columnDisplaySize, int columnScale, int nullable)
	{
		if (!TypeConverter.isFieldTypeValid(columnType))
			throw new IllegalArgumentException("FieldType \""+columnType+"\" is not known!");
		this.columnName = columnName;
		this.columnType = columnType;
		this.columnDisplaySize = columnDisplaySize;
		this.columnScale = columnScale;
		this.nullable = nullable;
	}

	/**
	 * This constructor sets nullable to false.
	 */
	public Column(String columnName, int columnType, int columnDisplaySize)
	throws SQLException
	{
		this(columnName, columnType, columnDisplaySize, -1, false);
	}

	/**
	 * This constructor sets nullable to false.
	 */
	public Column(String columnName, int columnType, int columnDisplaySize, boolean nullable)
	throws SQLException
	{
		this(columnName, columnType, columnDisplaySize, -1, nullable);
	}


	/**
	 * This constructor sets nullable to false.
	 */
	public Column(String columnName, int columnType, int columnDisplaySize, int columnScale)
	throws SQLException
	{
		this(columnName, columnType, columnDisplaySize, columnScale, false);
	}


	/**
	 * Warning! You can use this constructor only for field types with static
	 * sizes. Dynamic sizeable field types like VARCHAR cannot be created with
	 * this constructor!
	 */
	public Column(String columnName, int columnType, boolean nullable)
	throws SQLException
	{
		this(columnName, columnType, -1, -1, nullable);
	}

	/**
	 * Warning! You can use this constructor only for field types with static
	 * sizes. Dynamic sizeable field types like VARCHAR cannot be created with
	 * this constructor!
	 * <br/><br/>
	 * This constructor sets nullable to false.
	 */
	public Column(String columnName, int columnType)
	throws SQLException
	{
		this(columnName, columnType, -1, -1, false);
	}

	public Column(ResultSetMetaData metaData, int column)
	throws SQLException
	{
		this(
				metaData.getColumnName(column),
				metaData.getColumnType(column),
				metaData.getColumnDisplaySize(column),
				metaData.getScale(column),
				metaData.isNullable(column)
			);
	}


	protected String columnName;
	public String getColumnName() { return columnName; }
	public void setColumnName(String _columnName) {
		this.columnName = _columnName;
	}

	protected int columnType;
	public int getColumnType() { return columnType; }
	public void setColumnType(int _columnType) {
		this.columnType = _columnType;
	}

	protected int columnDisplaySize;
	public int getColumnDisplaySize() { return columnDisplaySize; }
	public void setColumnDisplaySize(int _columnDisplaySize)
	{ this.columnDisplaySize = _columnDisplaySize; }

	protected int columnScale;
	public int getColumnScale() { return columnScale; }
	public void setColumnScale(int _columnScale)
	{ this.columnScale = _columnScale; }


	protected int nullable;
	public int isNullable() { return nullable; }
	public void setNullable(int _nullable) { this.nullable = _nullable; }

//	boolean autoIncrement = false;
//	boolean caseSensitive = true;
//	boolean searchable;
//	boolean currency = false;
//	boolean signed;
//	String columnLabel;
//	String schemaName;
//	int precision;
//	int scale;
//	String tableName;
//	String catalogName;
//	String columnTypeName;
//	boolean readOnly;
//	boolean writable;
//	boolean definitelyWritable;
//	String columnClassName;

}
