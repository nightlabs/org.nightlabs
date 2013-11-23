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
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @version $Revision$ - $Date$
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class TableSQLRef implements Table
{
  protected String tableName;
  protected ResultSet rs;

  public TableSQLRef(String _tableName, ResultSet _rs) {
    this.tableName = _tableName;
    this.rs = _rs;
  }

  public String getTableName() throws SQLException {
    return tableName;
  }

  public void setTableName(String _tableName) throws SQLException {
    this.tableName = _tableName;
  }

  public boolean isEmpty() throws SQLException {
    return (rs.getRow() == 0) && (!rs.isBeforeFirst()) && (!rs.isAfterLast());
  }

  public String getString(int columnIndex, boolean returnEmptyStringIfNull) throws SQLException {
    String res = rs.getString(columnIndex);
    if (res == null && returnEmptyStringIfNull) res = "";
    return res;
  }

  public String getString(String columnName, boolean returnEmptyStringIfNull) throws SQLException {
    String res = rs.getString(columnName);
    if (res == null && returnEmptyStringIfNull) res = "";
    return res;
  }

  public int findColumn(String columnName) throws SQLException
  {
    return rs.findColumn(columnName);
  }
  public Reader getCharacterStream(int columnIndex) throws SQLException
  {
    return rs.getCharacterStream(columnIndex);
  }
  public Reader getCharacterStream(String columnName) throws SQLException
  {
    return rs.getCharacterStream(columnName);
  }
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    return rs.getBigDecimal(columnIndex);
  }
  public BigDecimal getBigDecimal(String columnName) throws SQLException
  {
    return rs.getBigDecimal(columnName);
  }
  public boolean isBeforeFirst() throws SQLException
  {
    return rs.isBeforeFirst();
  }
  public boolean isAfterLast() throws SQLException
  {
    return rs.isAfterLast();
  }
  public boolean isFirst() throws SQLException
  {
    return rs.isFirst();
  }
  public boolean isLast() throws SQLException
  {
    return rs.isLast();
  }
  public void beforeFirst() throws SQLException
  {
    rs.beforeFirst();
  }
  public void afterLast() throws SQLException
  {
    rs.afterLast();
  }
  public boolean first() throws SQLException
  {
    return rs.first();
  }
  public boolean next() throws SQLException
  {
    return rs.next();
  }
  public boolean last() throws SQLException
  {
    return rs.last();
  }
  public int getRow() throws SQLException
  {
    return rs.getRow();
  }
  public boolean absolute(int row) throws SQLException
  {
    return rs.absolute(row);
  }
  public boolean relative(int rows) throws SQLException
  {
    return rs.relative(rows);
  }
  public boolean previous() throws SQLException
  {
    return rs.previous();
  }
  public void setFetchDirection(int direction) throws SQLException
  {
    rs.setFetchDirection(direction);
  }
  public int getFetchDirection() throws SQLException
  {
    return rs.getFetchDirection();
  }
  public void setFetchSize(int rows) throws SQLException
  {
    rs.setFetchSize(rows);
  }
  public int getFetchSize() throws SQLException
  {
    return rs.getFetchSize();
  }
  public int getType() throws SQLException
  {
    return rs.getType();
  }
  public int getConcurrency() throws SQLException
  {
    return rs.getConcurrency();
  }
  public boolean rowUpdated() throws SQLException
  {
    return rs.rowUpdated();
  }
  public boolean rowInserted() throws SQLException
  {
    return rs.rowInserted();
  }
  public boolean rowDeleted() throws SQLException
  {
    return rs.rowDeleted();
  }
  public void updateNull(int columnIndex) throws SQLException
  {
    rs.updateNull(columnIndex);
  }
  public void updateBoolean(int columnIndex, boolean x) throws SQLException
  {
    rs.updateBoolean(columnIndex, x);
  }
  public void updateByte(int columnIndex, byte x) throws SQLException
  {
    rs.updateByte(columnIndex, x);
  }
  public void updateShort(int columnIndex, short x) throws SQLException
  {
    rs.updateShort(columnIndex, x);
  }
  public void updateInt(int columnIndex, int x) throws SQLException
  {
    rs.updateInt(columnIndex, x);
  }
  public void updateLong(int columnIndex, long x) throws SQLException
  {
    rs.updateLong(columnIndex, x);
  }
  public void updateFloat(int columnIndex, float x) throws SQLException
  {
    rs.updateFloat(columnIndex, x);
  }
  public void updateDouble(int columnIndex, double x) throws SQLException
  {
    rs.updateDouble(columnIndex, x);
  }
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
  {
    rs.updateBigDecimal(columnIndex, x);
  }
  public void updateString(int columnIndex, String x) throws SQLException
  {
    rs.updateString(columnIndex, x);
  }
  public void updateBytes(int columnIndex, byte[] x) throws SQLException
  {
    rs.updateBytes(columnIndex, x);
  }
  public void updateDate(int columnIndex, Date x) throws SQLException
  {
    rs.updateDate(columnIndex, x);
  }
  public void updateTime(int columnIndex, Time x) throws SQLException
  {
    rs.updateTime(columnIndex, x);
  }
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
  {
    rs.updateTimestamp(columnIndex, x);
  }
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
  {
    rs.updateAsciiStream(columnIndex, x, length);
  }
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
  {
    rs.updateBinaryStream(columnIndex, x, length);
  }
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
  {
    rs.updateCharacterStream(columnIndex, x, length);
  }
  public void updateObject(int columnIndex, Object x, int scale) throws SQLException
  {
    rs.updateObject(columnIndex, x, scale);
  }
  public void updateObject(int columnIndex, Object x) throws SQLException
  {
    rs.updateObject(columnIndex, x);
  }
  public void updateNull(String columnName) throws SQLException
  {
    rs.updateNull(columnName);
  }
  public void updateBoolean(String columnName, boolean x) throws SQLException
  {
    rs.updateBoolean(columnName, x);
  }
  public void updateByte(String columnName, byte x) throws SQLException
  {
    rs.updateByte(columnName, x);
  }
  public void updateShort(String columnName, short x) throws SQLException
  {
    rs.updateShort(columnName, x);
  }
  public void updateInt(String columnName, int x) throws SQLException
  {
    rs.updateInt(columnName, x);
  }
  public void updateLong(String columnName, long x) throws SQLException
  {
    rs.updateLong(columnName, x);
  }
  public void updateFloat(String columnName, float x) throws SQLException
  {
    rs.updateFloat(columnName, x);
  }
  public void updateDouble(String columnName, double x) throws SQLException
  {
    rs.updateDouble(columnName, x);
  }
  public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
  {
    rs.updateBigDecimal(columnName, x);
  }
  public void updateString(String columnName, String x) throws SQLException
  {
    rs.updateString(columnName, x);
  }
  public void updateBytes(String columnName, byte[] x) throws SQLException
  {
    rs.updateBytes(columnName, x);
  }
  public void updateDate(String columnName, Date x) throws SQLException
  {
    rs.updateDate(columnName, x);
  }
  public void updateTime(String columnName, Time x) throws SQLException
  {
    rs.updateTime(columnName, x);
  }
  public void updateTimestamp(String columnName, Timestamp x) throws SQLException
  {
    rs.updateTimestamp(columnName, x);
  }
  public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
  {
    rs.updateAsciiStream(columnName, x, length);
  }
  public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
  {
    rs.updateBinaryStream(columnName, x, length);
  }
  public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
  {
    rs.updateCharacterStream(columnName, reader, length);
  }
  public void updateObject(String columnName, Object x, int scale) throws SQLException
  {
    rs.updateObject(columnName, x, scale);
  }
  public void updateObject(String columnName, Object x) throws SQLException
  {
    rs.updateObject(columnName, x);
  }
  public void insertRow() throws SQLException
  {
    rs.insertRow();
  }
  public void updateRow() throws SQLException
  {
    rs.updateRow();
  }
  public void deleteRow() throws SQLException
  {
    rs.deleteRow();
  }
  public void refreshRow() throws SQLException
  {
    rs.refreshRow();
  }
  public void cancelRowUpdates() throws SQLException
  {
    rs.cancelRowUpdates();
  }
  public void moveToInsertRow() throws SQLException
  {
    rs.moveToInsertRow();
  }
  public void moveToCurrentRow() throws SQLException
  {
    rs.moveToCurrentRow();
  }
  public Statement getStatement() throws SQLException
  {
    return rs.getStatement();
  }
  public Object getObject(int i, Map<String, Class<?>> map) throws SQLException
  {
    return rs.getObject(i, map);
  }
  public Ref getRef(int i) throws SQLException
  {
    return rs.getRef(i);
  }
  public Blob getBlob(int i) throws SQLException
  {
    return rs.getBlob(i);
  }
  public Clob getClob(int i) throws SQLException
  {
    return rs.getClob(i);
  }
  public Array getArray(int i) throws SQLException
  {
    return rs.getArray(i);
  }
  public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException
  {
    return rs.getObject(colName, map);
  }
  public Ref getRef(String colName) throws SQLException
  {
    return rs.getRef(colName);
  }
  public Blob getBlob(String colName) throws SQLException
  {
    return rs.getBlob(colName);
  }
  public Clob getClob(String colName) throws SQLException
  {
    return rs.getClob(colName);
  }
  public Array getArray(String colName) throws SQLException
  {
    return rs.getArray(colName);
  }
  public Date getDate(int columnIndex, Calendar cal) throws SQLException
  {
    return rs.getDate(columnIndex, cal);
  }
  public Date getDate(String columnName, Calendar cal) throws SQLException
  {
    return rs.getDate(columnName, cal);
  }
  public Time getTime(int columnIndex, Calendar cal) throws SQLException
  {
    return rs.getTime(columnIndex, cal);
  }
  public Time getTime(String columnName, Calendar cal) throws SQLException
  {
    return rs.getTime(columnName, cal);
  }
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
  {
    return rs.getTimestamp(columnIndex, cal);
  }
  public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
  {
    return rs.getTimestamp(columnName, cal);
  }
  public URL getURL(int columnIndex) throws SQLException
  {
    return rs.getURL(columnIndex);
  }
  public URL getURL(String columnName) throws SQLException
  {
    return rs.getURL(columnName);
  }
  public void updateRef(int columnIndex, Ref x) throws SQLException
  {
    rs.updateRef(columnIndex, x);
  }
  public void updateRef(String columnName, Ref x) throws SQLException
  {
    rs.updateRef(columnName, x);
  }
  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
    rs.updateBlob(columnIndex, x);
  }
  public void updateBlob(String columnName, Blob x) throws SQLException
  {
    rs.updateBlob(columnName, x);
  }
  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
    rs.updateClob(columnIndex, x);
  }
  public void updateClob(String columnName, Clob x) throws SQLException
  {
    rs.updateClob(columnName, x);
  }
  public void updateArray(int columnIndex, Array x) throws SQLException
  {
    rs.updateArray(columnIndex, x);
  }
  public void updateArray(String columnName, Array x) throws SQLException
  {
    rs.updateArray(columnName, x);
  }
  public void close() throws SQLException
  {
    rs.close();
  }
  public boolean wasNull() throws SQLException
  {
    return rs.wasNull();
  }
  public String getString(int columnIndex) throws SQLException {
    return rs.getString(columnIndex);
  }
  public boolean getBoolean(int columnIndex) throws SQLException {
    return rs.getBoolean(columnIndex);
  }
  public byte getByte(int columnIndex) throws SQLException {
    return rs.getByte(columnIndex);
  }
  public short getShort(int columnIndex) throws SQLException {
    return rs.getShort(columnIndex);
  }
  public int getInt(int columnIndex) throws SQLException {
    return rs.getInt(columnIndex);
  }
  public long getLong(int columnIndex) throws SQLException {
    return rs.getLong(columnIndex);
  }
  public float getFloat(int columnIndex) throws SQLException {
    return rs.getFloat(columnIndex);
  }
  public double getDouble(int columnIndex) throws SQLException {
    return rs.getDouble(columnIndex);
  }
  /**
   * @deprecated Is deprecated in <tt>java.sql.ResultSet</tt>.
   * @see java.sql.ResultSet#getBigDecimal(int, int)
   */
  @Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return rs.getBigDecimal(columnIndex, scale);
  }
  public byte[] getBytes(int columnIndex) throws SQLException {
    return rs.getBytes(columnIndex);
  }
  public Date getDate(int columnIndex) throws SQLException {
    return rs.getDate(columnIndex);
  }
  public Time getTime(int columnIndex) throws SQLException {
    return rs.getTime(columnIndex);
  }
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return rs.getTimestamp(columnIndex);
  }
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return rs.getAsciiStream(columnIndex);
  }
  /**
   * @deprecated Is deprecated in <tt>java.sql.ResultSet</tt>.
   * @see java.sql.ResultSet#getUnicodeStream(int)
   */
  @Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return rs.getUnicodeStream(columnIndex);
  }
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return rs.getBinaryStream(columnIndex);
  }
  public String getString(String columnName) throws SQLException {
    return rs.getString(columnName);
  }
  public boolean getBoolean(String columnName) throws SQLException {
    return rs.getBoolean(columnName);
  }
  public byte getByte(String columnName) throws SQLException {
    return rs.getByte(columnName);
  }
  public short getShort(String columnName) throws SQLException {
    return rs.getShort(columnName);
  }
  public int getInt(String columnName) throws SQLException {
    return rs.getInt(columnName);
  }
  public long getLong(String columnName) throws SQLException {
    return rs.getLong(columnName);
  }
  public float getFloat(String columnName) throws SQLException {
    return rs.getFloat(columnName);
  }
  public double getDouble(String columnName) throws SQLException {
    return rs.getDouble(columnName);
  }
  public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
    return rs.getBigDecimal(columnName);
  }
  public byte[] getBytes(String columnName) throws SQLException {
    return rs.getBytes(columnName);
  }
  public Date getDate(String columnName) throws SQLException {
    return rs.getDate(columnName);
  }
  public Time getTime(String columnName) throws SQLException {
    return rs.getTime(columnName);
  }
  public Timestamp getTimestamp(String columnName) throws SQLException {
    return rs.getTimestamp(columnName);
  }
  public InputStream getAsciiStream(String columnName) throws SQLException {
    return rs.getAsciiStream(columnName);
  }
  /**
   * @deprecated Is deprecated in <tt>java.sql.ResultSet</tt>.
   * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
   */
  @Deprecated
	public InputStream getUnicodeStream(String columnName) throws SQLException {
    return rs.getUnicodeStream(columnName);
  }
  public InputStream getBinaryStream(String columnName) throws SQLException {
    return rs.getBinaryStream(columnName);
  }
  public SQLWarning getWarnings() throws SQLException {
    return rs.getWarnings();
  }
  public void clearWarnings() throws SQLException {
    rs.clearWarnings();
  }
  public String getCursorName() throws SQLException {
    return rs.getCursorName();
  }
  public ResultSetMetaData getMetaData() throws SQLException {
    return rs.getMetaData();
  }
  public Object getObject(int columnIndex) throws SQLException {
    return rs.getObject(columnIndex);
  }
  public Object getObject(String columnName) throws SQLException {
    return rs.getObject(columnName);
  }

  public int getRecordCount()
  throws SQLException
  {
    throw new UnsupportedOperationException("TableSQLRef does not support getRecordCount()!");
  }

  public void addColumn(Column col, Object defaultValue)
  throws SQLException
  {
    throw new UnsupportedOperationException("TableSQLRef does not support addColumn(...)!");
  }

  /**
   * This method returns the current record, if supported by the concrete Table implementation.
   *
   * @return the current record
   */
  public Record getRecord()
  throws SQLException
  {
    throw new UnsupportedOperationException("TableSQLRef does not support getRecord()!");
  }


  /**
   * This method updates the current record by replacing it by the new one.
   * @param record The new record that has to be put at the current cursor position instead of the current record.
   */
  public void updateRecord(Record record)
  throws SQLException
  {
    throw new UnsupportedOperationException("TableSQLRef does not support updateRecord(...)!");
  }

  /**
   * This method adds a record to the table.
   * @param record The record you want to add.
   */
  public void addRecord(Record record)
  throws SQLException
  {
    throw new UnsupportedOperationException("TableSQLRef does not support addRecord(...)!");
  }

	public int getHoldability() throws SQLException
	{
		return rs.getHoldability();
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException
	{
		return rs.getNCharacterStream(columnIndex);
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException
	{
		return rs.getNCharacterStream(columnLabel);
	}

	public NClob getNClob(int columnIndex) throws SQLException
	{
		return rs.getNClob(columnIndex);
	}

	public NClob getNClob(String columnLabel) throws SQLException
	{
		return rs.getNClob(columnLabel);
	}

	public String getNString(int columnIndex) throws SQLException
	{
		return rs.getNString(columnIndex);
	}

	public String getNString(String columnLabel) throws SQLException
	{
		return rs.getNString(columnLabel);
	}

	public RowId getRowId(int columnIndex) throws SQLException
	{
		return rs.getRowId(columnIndex);
	}

	public RowId getRowId(String columnLabel) throws SQLException
	{
		return rs.getRowId(columnLabel);
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException
	{
		return rs.getSQLXML(columnIndex);
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException
	{
		return rs.getSQLXML(columnLabel);
	}

	public boolean isClosed() throws SQLException
	{
		return rs.isClosed();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return rs.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return rs.unwrap(iface);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		rs.updateAsciiStream(columnIndex, x, length);
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
	{
		rs.updateAsciiStream(columnIndex, x);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		rs.updateAsciiStream(columnLabel, x, length);
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
	{
		rs.updateAsciiStream(columnLabel, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		rs.updateBinaryStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
	{
		rs.updateBinaryStream(columnIndex, x);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		rs.updateBinaryStream(columnLabel, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
	{
		rs.updateBinaryStream(columnLabel, x);
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
	{
		rs.updateBlob(columnIndex, inputStream, length);
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
		rs.updateBlob(columnIndex, inputStream);
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
	{
		rs.updateBlob(columnLabel, inputStream, length);
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
	{
		rs.updateBlob(columnLabel, inputStream);
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		rs.updateCharacterStream(columnIndex, x, length);
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		rs.updateCharacterStream(columnIndex, x);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		rs.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		rs.updateCharacterStream(columnLabel, reader);
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		rs.updateClob(columnIndex, reader, length);
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
		rs.updateClob(columnIndex, reader);
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		rs.updateClob(columnLabel, reader, length);
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException
	{
		rs.updateClob(columnLabel, reader);
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		rs.updateNCharacterStream(columnIndex, x, length);
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		rs.updateNCharacterStream(columnIndex, x);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		rs.updateNCharacterStream(columnLabel, reader, length);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		rs.updateNCharacterStream(columnLabel, reader);
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException
	{
		rs.updateNClob(columnIndex, nClob);
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		rs.updateNClob(columnIndex, reader, length);
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException
	{
		rs.updateNClob(columnIndex, reader);
	}

	public void updateNClob(String columnLabel, NClob nClob) throws SQLException
	{
		rs.updateNClob(columnLabel, nClob);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		rs.updateNClob(columnLabel, reader, length);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException
	{
		rs.updateNClob(columnLabel, reader);
	}

	public void updateNString(int columnIndex, String nString) throws SQLException
	{
		rs.updateNString(columnIndex, nString);
	}

	public void updateNString(String columnLabel, String nString) throws SQLException
	{
		rs.updateNString(columnLabel, nString);
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException
	{
		rs.updateRowId(columnIndex, x);
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException
	{
		rs.updateRowId(columnLabel, x);
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
	{
		rs.updateSQLXML(columnIndex, xmlObject);
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
	{
		rs.updateSQLXML(columnLabel, xmlObject);
	}
}
