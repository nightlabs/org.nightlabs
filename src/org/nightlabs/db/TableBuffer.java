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

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * An in-memory table implementation.
 * @version $Revision$ - $Date$
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class TableBuffer
implements Table, Serializable
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(TableBuffer.class);

	public TableBuffer() {
		this.tableBufferMetaData = new TableBufferMetaData(this);
		records = new ArrayList<Record>();
	}

	public TableBuffer(String tableName, java.sql.ResultSet rs)
	throws SQLException
	{
		this.setTableName(tableName);
		loadResultSet(rs);
	}

	protected void loadResultSet(java.sql.ResultSet rs)
	throws SQLException
	{
		this.tableBufferMetaData = new TableBufferMetaData(this, rs.getMetaData());
		records = new ArrayList<Record>();

		records.clear();
		int colCount = tableBufferMetaData.getColumnCount();

		while (rs.next()) {
			List<Object> fields = new ArrayList<Object>(colCount);

			for (int col = 1; col <= colCount; col++)
				fields.add(rs.getObject(col));

			records.add(new Record(fields));

		} // while (rs.next()) {
	}


	public TableBuffer(String tableName, Column[] _columns, Record[] _records)
	throws SQLException
	{
		this.setTableName(tableName);
		tableBufferMetaData = new TableBufferMetaData(this, _columns);
		records = new ArrayList<Record>();

		if (_records != null)
			addRecords(_records);
	}

	public TableBuffer(String tableName, Column[] _columns, Record record)
	throws SQLException
	{
		this.setTableName(tableName);
		tableBufferMetaData = new TableBufferMetaData(this, _columns);
		records = new ArrayList<Record>();

		if (record != null)
			addRecord(record);
	}

	public TableBuffer(String tableName, List<Column> _columns, Record record)
	throws SQLException
	{
		this.setTableName(tableName);
		tableBufferMetaData = new TableBufferMetaData(this, _columns);
		records = new ArrayList<Record>();

		if (record != null)
			addRecord(record);
	}

	public TableBuffer(String tableName, List<Column> _columns)
	throws SQLException
	{
		this(tableName, _columns, null);
	}

	public TableBuffer(String tableName, Column[] _columns)
	throws SQLException
	{
		this(tableName, _columns, (Record)null);
	}

	private String tableName;
	public String getTableName() throws SQLException {
		return tableName;
	}
	public void setTableName(String tableName) throws SQLException {
		this.tableName = tableName;
	}

	protected TableBufferMetaData tableBufferMetaData;

	protected List<Record> records;

	public boolean isEmpty()
	throws SQLException
	{
		return records.isEmpty();
	}

	public int getRecordCount()
	throws SQLException
	{
		return records.size();
	}


	public void addColumn(Column column, Object defaultValue)
	throws SQLException
	{
		if (column == null)
			throw new NullPointerException("Param column must not be null!");

		if (defaultValue == null && column.isNullable() != ResultSetMetaData.columnNullable)
			throw new NullPointerException("Param defaultValue must not be null, if column is not nullable!");

		tableBufferMetaData.addColumn(column);

		for (Object element : records) {
			Record record = (Record) element;
			record.addObject(defaultValue);
		} // for (Iterator it = records.iterator(); it.hasNext(); ) {
	}



	public void validateRecord(Record record)
	throws SQLException
	{
		if (record.getFieldCount() != tableBufferMetaData.getColumnCount())
			throw new SQLException("Number of fields in record does not match number of fields in table definition!");

		/** @todo Check all fields. */
//		for (int i = 0; i < record.getFieldCount(); i++) {
//		}
	}

	public Record getRecord()
	throws SQLException
	{
		if (row < 1) throw new SQLException("Cursor before first row!");
		if (row > records.size()) throw new SQLException("Cursor after last row!");

		return records.get(row - 1);
	}

	/**
	 * This method updates the current record by replacing it by the new one.
	 * @param record The new record that has to be put at the current cursor position instead of the current record.
	 */
	public void updateRecord(Record record)
	throws SQLException
	{
		if (isBeforeFirst())
			throw new IndexOutOfBoundsException("Cursor is before first record! You have to use next() before being able to access a newly created table buffer!");

		if (isAfterLast())
			throw new IndexOutOfBoundsException("Cursor is after last record!");

		validateRecord(record);

		records.remove(row-1);
		records.add(row-1, record);
	}

	/**
	 * This method adds a record to the table.
	 * @param record The record you want to add.
	 */
	public void addRecord(Record record)
	throws SQLException
	{
		validateRecord(record);

		records.add(record);
	}

	/**
	 * This method adds an array of records to the table.
	 * @param records The records you want to add.
	 */
	public void addRecords(Record[] records)
	throws SQLException
	{
		for (Record element : records)
			addRecord(element);
	}



	/**
	 * Current row according to ResultSet (=> 1-based, 1 = first row,
	 * 2 = second row, 0 = no row).
	 *
	 * @see #getRow()
	 */
	protected int row = 0;

	public boolean next() throws SQLException {
		if (isEmpty())
			return false;

		if (!isAfterLast())
			row++;

		return row <= records.size();
	}

	public void close() throws SQLException {
		// no op
	}

	protected boolean wasNull = false;

	public boolean wasNull() throws SQLException {
		return wasNull;
	}

	public String getString(int columnIndex, boolean returnEmptyStringIfNull) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return returnEmptyStringIfNull ? "" : null;

		return obj.toString();
	}

	public String getString(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return null;

		return obj.toString();
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return false;

		return ((Boolean)obj).booleanValue();
	}

	public byte getByte(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Byte even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Byte.parseByte((String)obj);
		}

		return ((Number)obj).byteValue();
	}

	public short getShort(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Short even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Short.parseShort((String)obj);
		}

		return ((Number)obj).shortValue();
	}

	public int getInt(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Integer even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Integer.parseInt((String)obj);
		}

		return ((Number)obj).intValue();
	}

	public long getLong(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Long even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Long.parseLong((String)obj);
		}

		return ((Number)obj).longValue();
	}

	public float getFloat(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Float even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Float.parseFloat((String)obj);
		}

		return ((Number)obj).floatValue();
	}

	public double getDouble(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnIndex + "\" of table \"" + tableName + "\" as Double even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Double.parseDouble((String)obj);
		}

		return ((Number)obj).doubleValue();
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBytes() not yet implemented.");
	}

	public Date getDate(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return null;

		if ((obj instanceof Date))
			return (Date)obj;
		else if (obj instanceof java.util.Date)
			return new Date(((java.util.Date)obj).getTime());
		else
			throw new ClassCastException("Expected class Date, but found: " + obj.getClass().getName());
	}

	public Time getTime(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return null;

		if (!(obj instanceof Time))
			throw new ClassCastException("Expected class Time, but found: " + obj.getClass().getName());

		return (Time)obj;
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if (obj == null)
			return null;

		if (obj instanceof Timestamp)
			return (Timestamp) obj;
		else if (obj instanceof java.util.Date)
			return new Timestamp(((java.util.Date) obj).getTime());
		throw new ClassCastException("Expected class Timestamp or Date, but found: " + obj.getClass().getName());
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getAsciiStream() not yet implemented.");
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBinaryStream() not yet implemented.");
	}

	public String getString(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return null;

		return obj.toString();
	}

	public String getString(String columnName, boolean returnEmptyStringIfNull) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return returnEmptyStringIfNull ? "" : null;

		return obj.toString();
	}

	public boolean getBoolean(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return false;

		return ((Boolean)obj).booleanValue();
	}

	public byte getByte(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Byte even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Byte.parseByte((String)obj);
		}

		return ((Number)obj).byteValue();
	}

	public short getShort(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Short even though it is a String! Converting \"" + (String)obj + "\".", new Exception());
			return Short.parseShort((String)obj);
		}

		return ((Number)obj).shortValue();
	}

	public int getInt(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Integer even though it is a String! Converting \"" + (String)obj + "\".", new Exception("Warning: implicit conversion of String into int!"));
			return Integer.parseInt((String)obj);
		}

		return ((Number)obj).intValue();
	}

	public long getLong(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Long even though it is a String! Converting \"" + (String)obj + "\".", new Exception("Warning: implicit conversion of String into long!"));
			return Long.parseLong((String)obj);
		}

		return ((Number)obj).longValue();
	}

	public float getFloat(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Float even though it is a String! Converting \"" + (String)obj + "\".", new Exception("Warning: implicit conversion of String into float!"));
			return Float.parseFloat((String)obj);
		}

		return ((Number)obj).floatValue();
	}

	public double getDouble(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return 0;

		if (obj instanceof String) {
			logger.warn("You are trying to access the field \"" + columnName + "\" of table \"" + tableName + "\" as Double even though it is a String! Converting \"" + (String)obj + "\".", new Exception("Warning: implicit conversion of String into double!"));
			return Double.parseDouble((String)obj);
		}

		return ((Number)obj).doubleValue();
	}

	public byte[] getBytes(String columnName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBytes() not yet implemented.");
	}

	public Date getDate(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return null;

		if ((obj instanceof Date))
			return (Date)obj;
		else if (obj instanceof java.util.Date)
			return new Date(((java.util.Date)obj).getTime());
		else
			throw new ClassCastException("Expected class Date, but found: " + obj.getClass().getName());
	}

	public Time getTime(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return null;

		if (!(obj instanceof Time))
			throw new ClassCastException("Expected class Time, but found: " + obj.getClass().getName());

		return (Time)obj;
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if (obj == null)
			return null;

		if (!(obj instanceof Timestamp))
			throw new ClassCastException("Expected class Timestamp, but found: " + obj.getClass().getName());

		return (Timestamp)obj;
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getAsciiStream() not yet implemented.");
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBinaryStream() not yet implemented.");
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getWarnings() not yet implemented.");
	}

	public void clearWarnings() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method clearWarnings() not yet implemented.");
	}

	public String getCursorName() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getCursorName() not yet implemented.");
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return tableBufferMetaData;
	}


	protected Record getCurrentRecord()
	throws SQLException
	{
		if (isBeforeFirst())
			throw new IndexOutOfBoundsException("Cursor is before first record! You have to use next() before being able to access a newly created table buffer!");

		if (isAfterLast())
			throw new IndexOutOfBoundsException("Cursor is after last record!");

		return records.get(row-1);
	}

	public Object getObject(int columnIndex) throws SQLException {
		Object obj = getCurrentRecord().getObject(columnIndex);
		wasNull = obj == null;
		return obj;
	}

	public Object getObject(String columnName) throws SQLException {
		int col = tableBufferMetaData.getColumnIdxByName(columnName);
		if (col < 1)
			throw new SQLException("No field named \""+columnName+"\" existing!");

		Object obj = getObject(col);
		wasNull = obj == null;

		return obj;
	}

	/**
	 * @param columnName The name of the column
	 * @return The index of the column beginning at 1 (not 0 because of jdbc-compatibility). < 1 if there is no column with this name.
	 */
	public int findColumn(String columnName) throws SQLException {
		return tableBufferMetaData.getColumnIdxByName(columnName);
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getCharacterStream() not yet implemented.");
	}
	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getCharacterStream() not yet implemented.");
	}
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		Object obj = getObject(columnIndex);
		if (obj == null)
			return null;
		if (obj instanceof Double)
			return new BigDecimal(((Double)obj).doubleValue());
		else if (obj instanceof Long)
			return new BigDecimal(((Long)obj).longValue());
		throw new java.lang.UnsupportedOperationException("Method getBigDecimal() not yet implemented.");
	}
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		int col = tableBufferMetaData.getColumnIdxByName(columnName);
		return getBigDecimal(col);
	}

	public boolean isBeforeFirst() throws SQLException {
		if (isEmpty())
			return false;

		return row < 1;
	}

	public boolean isAfterLast() throws SQLException {
		return row > records.size();
	}

	public boolean isFirst() throws SQLException {
		return row == 1;
	}

	public boolean isLast() throws SQLException {
		return row == records.size();
	}

	public void beforeFirst() throws SQLException {
		row = 0;
	}

	public void afterLast() throws SQLException {
		if (!isEmpty())
			row = records.size() + 1;
	}

	public boolean first() throws SQLException {
		if (isEmpty())
			return false;

		row = 1;
		return true;
	}

	public boolean last() throws SQLException {
		if (isEmpty())
			return false;

		row = records.size();
		return true;
	}

	public int getRow() throws SQLException {
		if (isAfterLast())
			return 0;

		return row;
	}

	public boolean absolute(int _row) throws SQLException {
		int afterLastPos = records.size() + 1;
		if (_row > 0) {
			if (_row > afterLastPos) _row = afterLastPos;
			row = _row;
		}
		else if (_row < 0) {
			_row = afterLastPos + _row;
			if (_row < 0) _row = 0;
			row = _row;
		}
		else
			throw new SQLException("Cannot scroll resultset to row 0!");

		return this.row > 0 && this.row < afterLastPos;
	}

	public boolean relative(int _rows) throws SQLException {
		if (records.isEmpty())
			throw new SQLException("No current row! TableBuffer is empty!");

		if (row < 1)
			throw new SQLException("No current row! Is before first!");

		if (row > records.size())
			throw new SQLException("No current row! Is after last!");

		int afterLastPos = records.size() + 1;

		int _row = this.row + _rows;
		if (_row < 0) _row = 0;
		if (_row > afterLastPos) _row = afterLastPos;

		this.row = _row;

		return this.row > 0 && this.row < afterLastPos;
	}

	public boolean previous() throws SQLException {
		if (row > 0)
			row--;

		return this.row > 0 && this.row <= records.size();
	}

	public void setFetchDirection(int direction) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method setFetchDirection() not yet implemented.");
	}

	public int getFetchDirection() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getFetchDirection() not yet implemented.");
	}

	public void setFetchSize(int rows) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method setFetchSize() not yet implemented.");
	}

	public int getFetchSize() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getFetchSize() not yet implemented.");
	}

	public int getType() throws SQLException {
		return TYPE_SCROLL_INSENSITIVE;
	}

	public int getConcurrency() throws SQLException {
		return CONCUR_UPDATABLE;
	}

	public boolean rowUpdated() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method rowUpdated() not yet implemented.");
	}

	public boolean rowInserted() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method rowInserted() not yet implemented.");
	}

	public boolean rowDeleted() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method rowDeleted() not yet implemented.");
	}

	public void updateNull(int columnIndex) throws SQLException {
		updateObject(columnIndex, null);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		updateObject(columnIndex, new Boolean(x));
	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		updateObject(columnIndex, new Byte(x));
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		updateObject(columnIndex, new Short(x));
	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		updateObject(columnIndex, new Integer(x));
	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		updateObject(columnIndex, new Long(x));
	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		updateObject(columnIndex, new Float(x));
	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		updateObject(columnIndex, new Double(x));
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		updateObject(columnIndex, x);
	}

	public void updateString(int columnIndex, String x) throws SQLException {
		updateObject(columnIndex, x);
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		updateObject(columnIndex, x);
	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		updateObject(columnIndex, x);
	}
	public void updateTime(int columnIndex, Time x) throws SQLException {
		updateObject(columnIndex, x);
	}
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		updateObject(columnIndex, x);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateAsciiStream() not yet implemented.");
	}
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateBinaryStream() not yet implemented.");
	}
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateCharacterStream() not yet implemented.");
	}

	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		getCurrentRecord().setObject(columnIndex, x);
	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		getCurrentRecord().setObject(columnIndex, x);
	}

	public void updateNull(String columnName) throws SQLException {
		updateObject(columnName, null);
	}
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		updateObject(columnName, new Boolean(x));
	}
	public void updateByte(String columnName, byte x) throws SQLException {
		updateObject(columnName, new Byte(x));
	}
	public void updateShort(String columnName, short x) throws SQLException {
		updateObject(columnName, new Short(x));
	}
	public void updateInt(String columnName, int x) throws SQLException {
		updateObject(columnName, new Integer(x));
	}
	public void updateLong(String columnName, long x) throws SQLException {
		updateObject(columnName, new Long(x));
	}
	public void updateFloat(String columnName, float x) throws SQLException {
		updateObject(columnName, new Float(x));
	}
	public void updateDouble(String columnName, double x) throws SQLException {
		updateObject(columnName, new Double(x));
	}
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateString(String columnName, String x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateDate(String columnName, Date x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateTime(String columnName, Time x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		updateObject(columnName, x);
	}
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateAsciiStream() not yet implemented.");
	}
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateBinaryStream() not yet implemented.");
	}
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateCharacterStream() not yet implemented.");
	}

	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		int col = tableBufferMetaData.getColumnIdxByName(columnName);
		if (col < 0)
			throw new SQLException("No field named \""+columnName+"\" existing!");

		updateObject(col, x, scale);
	}

	public void updateObject(String columnName, Object x) throws SQLException {
		int col = tableBufferMetaData.getColumnIdxByName(columnName);
		if (col < 1)
			throw new SQLException("No field named \""+columnName+"\" existing!");

		updateObject(col, x);
	}

	public void insertRow() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method insertRow() not yet implemented.");
	}
	public void updateRow() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateRow() not yet implemented.");
	}
	public void deleteRow() throws SQLException {
		if (row < 1 || row > records.size())
			throw new SQLException("no current row!");

		records.remove(row - 1);
	}
	public void refreshRow() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method refreshRow() not yet implemented.");
	}
	public void cancelRowUpdates() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method cancelRowUpdates() not yet implemented.");
	}
	public void moveToInsertRow() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method moveToInsertRow() not yet implemented.");
	}
	public void moveToCurrentRow() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method moveToCurrentRow() not yet implemented.");
	}
	public Statement getStatement() throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getStatement() not yet implemented.");
	}
	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
	{
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getObject() not yet implemented.");
	}
	public Ref getRef(int i) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getRef() not yet implemented.");
	}
	public Blob getBlob(int i) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBlob() not yet implemented.");
	}
	public Clob getClob(int i) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getClob() not yet implemented.");
	}
	public Array getArray(int i) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getArray() not yet implemented.");
	}
	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException
	{
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getObject() not yet implemented.");
	}
	public Ref getRef(String colName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getRef() not yet implemented.");
	}
	public Blob getBlob(String colName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getBlob() not yet implemented.");
	}
	public Clob getClob(String colName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getClob() not yet implemented.");
	}
	public Array getArray(String colName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getArray() not yet implemented.");
	}
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getDate() not yet implemented.");
	}
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getDate() not yet implemented.");
	}
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getTime() not yet implemented.");
	}
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getTime() not yet implemented.");
	}
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getTimestamp() not yet implemented.");
	}
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getTimestamp() not yet implemented.");
	}
	public URL getURL(int columnIndex) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getURL() not yet implemented.");
	}
	public URL getURL(String columnName) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method getURL() not yet implemented.");
	}
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateRef() not yet implemented.");
	}
	public void updateRef(String columnName, Ref x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateRef() not yet implemented.");
	}
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateBlob() not yet implemented.");
	}
	public void updateBlob(String columnName, Blob x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateBlob() not yet implemented.");
	}
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateClob() not yet implemented.");
	}
	public void updateClob(String columnName, Clob x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateClob() not yet implemented.");
	}
	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateArray() not yet implemented.");
	}
	public void updateArray(String columnName, Array x) throws SQLException {
		// TODO Implement this java.sql.ResultSet method
		throw new java.lang.UnsupportedOperationException("Method updateArray() not yet implemented.");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		throw new java.lang.UnsupportedOperationException("Method getBigDecimal(String columnName, int scale) not implemented.");
	}
	/**
	 * @deprecated
	 */
	@Deprecated
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		throw new java.lang.UnsupportedOperationException("Method getUnicodeStream(String columnName) not implemented.");
	}
	/**
	 * @deprecated
	 */
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new java.lang.UnsupportedOperationException("Method getUnicodeStream(int columnIndex) not implemented.");
	}
	/**
	 * @deprecated
	 */
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw new java.lang.UnsupportedOperationException("Method getBigDecimal(int columnIndex, int scale) not implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getHoldability()
	*/
	public int getHoldability() throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNCharacterStream(int)
	*/
	public Reader getNCharacterStream(int columnIndex) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
	*/
	public Reader getNCharacterStream(String columnLabel) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNClob(int)
	*/
	public NClob getNClob(int columnIndex) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNClob(java.lang.String)
	*/
	public NClob getNClob(String columnLabel) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNString(int)
	*/
	public String getNString(int columnIndex) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getNString(java.lang.String)
	*/
	public String getNString(String columnLabel) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getRowId(int)
	*/
	public RowId getRowId(int columnIndex) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getRowId(java.lang.String)
	*/
	public RowId getRowId(String columnLabel) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getSQLXML(int)
	*/
	public SQLXML getSQLXML(int columnIndex) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#getSQLXML(java.lang.String)
	*/
	public SQLXML getSQLXML(String columnLabel) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#isClosed()
	*/
	public boolean isClosed() throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
	*/
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
	*/
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
	*/
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	*/
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
	*/
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
	*/
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
	*/
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	*/
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
	*/
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
	*/
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
	*/
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
	*/
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
	*/
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
	*/
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
	*/
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
	*/
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateClob(int, java.io.Reader)
	*/
	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
	*/
	public void updateClob(String columnLabel, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
	*/
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
	*/
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
	*/
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
	*/
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
	*/
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	*/
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
	*/
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
	*/
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
	*/
	public void updateNClob(int columnIndex, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
	*/
	public void updateNClob(String columnLabel, Reader reader) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
	*/
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
	*/
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNString(int, java.lang.String)
	*/
	public void updateNString(int columnIndex, String nString) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
	*/
	public void updateNString(String columnLabel, String nString) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
	*/
	public void updateRowId(int columnIndex, RowId x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
	*/
	public void updateRowId(String columnLabel, RowId x) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
	*/
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
	*/
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	*/
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}

	/* (non-Javadoc)
	* @see java.sql.Wrapper#unwrap(java.lang.Class)
	*/
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
	// TODO Implement this java.sql.ResultSet method
	throw new java.lang.UnsupportedOperationException("java.sql.ResultSet Method not yet implemented.");
	}
}
