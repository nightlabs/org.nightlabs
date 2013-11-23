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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public interface Table extends ResultSet
{
  public String getTableName()
  throws SQLException;

  public void setTableName(String tableName)
  throws SQLException;

  public boolean isEmpty()
  throws SQLException;

  public String getString(int columnIndex, boolean returnEmptyStringIfNull)
  throws SQLException;

  public String getString(String columnName, boolean returnEmptyStringIfNull)
  throws SQLException;

  public int getRecordCount()
  throws SQLException;

  public void addColumn(Column column, Object defaultValue)
  throws SQLException;

  /**
   * This method returns the current record, if supported by the concrete Table implementation.
   *
   * @return the current record
   */
  public Record getRecord()
  throws SQLException;

  /**
   * This method updates the current record by replacing it by the new one.
   * @param record The new record that has to be put at the current cursor position instead of the current record.
   */
  public void updateRecord(Record record)
  throws SQLException;

  /**
   * This method adds a record to the table.
   * @param record The record you want to add.
   */
  public void addRecord(Record record)
  throws SQLException;

}
