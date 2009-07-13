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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version $Revision$ - $Date$
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class TableBufferMetaData implements ResultSetMetaData, Serializable
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The table buffer this meta data belongs to.
	 */
	protected TableBuffer tableBuffer;

	protected List<Column> columns;

	/**
	 * key: String columnName<br/>
	 * value: Integer columnIdx (1-based)
	 */
	protected HashMap<String, Integer> columnName2IDMap = null;

	public TableBufferMetaData(TableBuffer _tableBuffer)
	{
		this.tableBuffer = _tableBuffer;
		this.columns = new ArrayList<Column>();
	}

	public TableBufferMetaData(TableBuffer _tableBuffer, List<Column> _columns)
	{
		this.tableBuffer = _tableBuffer;
		this.columns = _columns;
	}

	public TableBufferMetaData(TableBuffer _tableBuffer, Column[] _columns)
	{
		this.tableBuffer = _tableBuffer;
		this.columns = new ArrayList<Column>(_columns.length);
		for (Column element : _columns)
			this.columns.add(element);
	}

	public TableBufferMetaData(TableBuffer _tableBuffer, ResultSetMetaData rsmd)
	throws SQLException
	{
		this.tableBuffer = _tableBuffer;
		this.columns = new ArrayList<Column>(rsmd.getColumnCount());
		for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
			Column col = new Column(
					rsmd.getColumnName(i),
					rsmd.getColumnType(i),
					rsmd.getColumnDisplaySize(i),
					rsmd.getScale(i),
					rsmd.isNullable(i)
			);
			this.columns.add(col);
		}
	}

	void addColumn(Column column)
	{
		columnName2IDMap = null;
		columns.add(column);
	}

	protected Column getColumn(int column)
	{
		return columns.get(column-1);
	}

	/**
	 * @param columnName The name of the column
	 * @return The index of the column beginning at 1 (not 0 because of jdbc-compatibility). < 1 if there is no column with this name.
	 */
	public int getColumnIdxByName(String columnName) throws SQLException
	{
		if (columnName2IDMap == null) {
			columnName2IDMap = new HashMap<String, Integer>();
			for (int col = 1; col <= columns.size(); col++)
				columnName2IDMap.put(
						getColumn(col).columnName,
						new Integer(col)
				);
		} // if (columnName2IDMap == null) {

		Integer colInt = columnName2IDMap.get(columnName);
		if (colInt == null)
			return 0;

		return colInt.intValue();
	}

	public int getColumnCount() throws SQLException {
		return columns.size();
	}

	public boolean isAutoIncrement(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isAutoIncrement() not yet implemented.");
	}

	public boolean isCaseSensitive(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isCaseSensitive() not yet implemented.");
	}

	public boolean isSearchable(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isSearchable() not yet implemented.");
	}

	public boolean isCurrency(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isCurrency() not yet implemented.");
	}

	public int isNullable(int column) throws SQLException {
		Column col = columns.get(column-1);
		return col.isNullable();
	}

	public boolean isSigned(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isSigned() not yet implemented.");
	}

	public int getColumnDisplaySize(int column) throws SQLException {
		Column col = columns.get(column-1);
		return col.getColumnDisplaySize();
	}
	public String getColumnLabel(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getColumnLabel() not yet implemented.");
	}
	public String getColumnName(int column) throws SQLException {
		Column col = columns.get(column-1);
		return col.getColumnName();
	}
	public String getSchemaName(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getSchemaName() not yet implemented.");
	}
	public int getPrecision(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getPrecision() not yet implemented.");
	}
	public int getScale(int column) throws SQLException {
		Column col = columns.get(column-1);
		return col.getColumnScale();
	}
	public String getTableName(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getTableName() not yet implemented.");
	}
	public String getCatalogName(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getCatalogName() not yet implemented.");
	}
	public int getColumnType(int column) throws SQLException {
		Column col = columns.get(column-1);
		return col.getColumnType();
	}
	public String getColumnTypeName(int column) throws SQLException {
		Column col = columns.get(column-1);
		return TypeConverter.getTypeNameByID(col.getColumnType());
	}
	public boolean isReadOnly(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isReadOnly() not yet implemented.");
	}
	public boolean isWritable(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isWritable() not yet implemented.");
	}
	public boolean isDefinitelyWritable(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isDefinitelyWritable() not yet implemented.");
	}
	public String getColumnClassName(int column) throws SQLException {
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method getColumnClassName() not yet implemented.");
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method isWrapperFor() not yet implemented.");
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		// TODO Implement this java.sql.ResultSetMetaData method
		throw new java.lang.UnsupportedOperationException("Method unwrap() not yet implemented.");
	}
}
