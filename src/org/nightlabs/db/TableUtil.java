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

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.nightlabs.util.Util;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class TableUtil
{
	/**
	 * Uses {@link System#out} for output.
	 */
	public static void dumpHumanReadable(Table table)
	throws IOException, SQLException
	{
		dumpHumanReadable(table, System.out);
	}
	public static void dumpHumanReadable(Table table, PrintStream printStream)
	throws IOException, SQLException
	{
		Writer w = new PrintWriter(printStream);
		dumpHumanReadable(table, w);
		w.flush();
	}
	public static void dumpHumanReadable(Table table, Writer writer)
	throws SQLException, IOException
	{
		if (!table.isBeforeFirst())
			table.beforeFirst();

		ResultSetMetaData rsmd = table.getMetaData();
		int[] colWidths = new int[rsmd.getColumnCount()];
		for (int i = 0; i < colWidths.length; i++) {
			colWidths[i] = rsmd.getColumnDisplaySize(i + 1);
		}

		Align[] colAligns = new Align[colWidths.length];

		// print column header
		for (int i = 0; i < colWidths.length; i++) {
			int colType = rsmd.getColumnType(i + 1);

			Align align;
			switch (colType) {
				case Types.VARCHAR:
				case Types.CHAR:
					align = Align.LEFT;
					break;

				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					align = Align.RIGHT;
					colWidths[i] = Math.max(colWidths[i], 19);
					break;

				case Types.INTEGER:
					align = Align.RIGHT;
					colWidths[i] = Math.max(colWidths[i], String.valueOf(Integer.MAX_VALUE).length());
					break;

				case Types.BIGINT:
					align = Align.RIGHT;
					colWidths[i] = Math.max(colWidths[i], String.valueOf(Long.MAX_VALUE).length());
					break;

				default:
					align = Align.RIGHT;
					colWidths[i] = Math.max(colWidths[i], 5);
					break;
			}

			colAligns[i] = align;

			String colName =layoutString(
					rsmd.getColumnName(i + 1) + " " +
					TypeConverter.getFullTypeName(
							rsmd.getColumnType(i + 1),
							rsmd.getColumnDisplaySize(i + 1),
							rsmd.getScale(i + 1)),
					colWidths[i], align);

			colWidths[i] = Math.max(colWidths[i], colName.length());

			writer.append(colName);

			if (i + 1 < colWidths.length)
				writer.append(" | ");
		}

		writer.append('\n');

		// print horizontal line under columns
		for (int i = 0; i < colWidths.length; i++) {
			writer.append(layoutString("", colWidths[i], Align.LEFT, '-'));

			if (i + 1 < colWidths.length)
				writer.append("-|-");
		}

		writer.append('\n');

		// dump data
		while (table.next()) {
			for (int i = 0; i < colWidths.length; i++) {
				int colType = rsmd.getColumnType(i + 1);

				String colData;
				switch (colType) {
					case Types.DATE:
						Date date = table.getDate(i + 1);
						colData = date == null ? "" : TypeConverter.getDefaultDateFormatLocal().format(date);
						break;
					case Types.TIME:
						Time time = table.getTime(i + 1);
						colData = time == null ? "" : TypeConverter.getDefaultDateFormatLocal().format(time);
						break;
					case Types.TIMESTAMP:
						Timestamp timestamp = table.getTimestamp(i + 1);
						colData = timestamp == null ? "" : TypeConverter.getDefaultDateFormatLocal().format(timestamp);
						break;
					default:
						colData = table.getString(i + 1);
						break;
				}

				writer.append(layoutString(colData, colWidths[i], colAligns[i]));

				if (i + 1 < colWidths.length)
					writer.append(" | ");
			}

			writer.append('\n');
		}

		try {
			table.beforeFirst();
		} catch (Exception x) {
			// ignore
		}
	}

	private static enum Align {
		LEFT, RIGHT
	}

	private static String layoutString(String s, int length, Align align)
	{
		return layoutString(s, length, align, ' ');
	}
	private static String layoutString(String s, int length, Align align, char fillChar)
	{
		switch (align) {
			case LEFT:
				return Util.addTrailingChars(s, length, fillChar);
			case RIGHT:
				return Util.addLeadingChars(s, length, fillChar);
			default:
				throw new IllegalArgumentException("Unknown align: " + align);
		}
//		if (s != null && s.length() >= length)
//			return s;
//
//		StringBuffer sb = new StringBuffer(length);
//		switch (align) {
//			case LEFT:
//				if (s != null)
//					sb.append(s);
//
//				while (sb.length() < length)
//					sb.append(fillChar);
//
//				break;
//			case RIGHT:
//				int l = s == null ? length : length - s.length();
//				while (sb.length() < l)
//					sb.append(fillChar);
//
//				if (s != null)
//					sb.append(s);
//
//				break;
//			default:
//				throw new IllegalArgumentException("Unknown align: " + align);
//		}
//
//		return sb.toString();
	}
}
