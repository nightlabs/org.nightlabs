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

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class TypeConverter
{
	/**
	 * Get the default date format which parses and formats according
	 * to the pattern "yyyy-MM-dd HH:mm:ss". Note: Optional time zone will
	 * be added to the pattern soon.
	 * <p>
	 * If no time zone is contained in a string to be parsed, this <code>DateFormat</code>
	 * assumes that it is local time zone (in contrast to {@link #getDefaultDateFormatUTC()}).
	 * </p>
	 * <p>
	 * Because the creation of a {@link SimpleDateFormat} (used by this implementation) is
	 * quite expensive (creating 10000 instances requires about 350 msec), we keep the DateFormat
	 * instance. However, since <code>DateFormat</code> is not thread-safe, we manage
	 * the <code>DateFormat</code> instances per thread. <b>Therefore, you must not modify
	 * the returned object! It is reused later (on the same thread).</b>
	 * </p>
	 * <p>
	 * TODO Add optional time-zone to pattern!
	 * </p>
	 *
	 * @return the date format for the current thread. You must not modify it (i.e. not apply a different pattern).
	 */
	public static DateFormat getDefaultDateFormatLocal()
	{
		return dateFormatLocal.get();
	}

	/**
	 * Get the default date format which parses and formats according
	 * to the pattern "yyyy-MM-dd HH:mm:ss". Note: Optional time zone will
	 * be added to the pattern soon.
	 * <p>
	 * If no time zone is contained in a string to be parsed, this <code>DateFormat</code>
	 * assumes that it is UTC time zone (in contrast to {@link #getDefaultDateFormatLocal()}).
	 * </p>
	 * <p>
	 * Because the creation of a {@link SimpleDateFormat} (used by this implementation) is
	 * quite expensive (creating 10000 instances requires about 350 msec), we keep the DateFormat
	 * instance. However, since <code>DateFormat</code> is not thread-safe, we manage
	 * the <code>DateFormat</code> instances per thread. <b>Therefore, you must not modify
	 * the returned object! It is reused later (on the same thread).</b>
	 * </p>
	 * <p>
	 * TODO Add optional time-zone to pattern!
	 * </p>
	 *
	 * @return the date format for the current thread. You must not modify it (i.e. not apply a different pattern).
	 */
	public static DateFormat getDefaultDateFormatUTC()
	{
		return dateFormatUTC.get();
	}

	private static ThreadLocal<DateFormat> dateFormatLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simpleDateFormat;
		}
	};
	private static ThreadLocal<DateFormat> dateFormatUTC = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			simpleDateFormat.setTimeZone(UTC);
			return simpleDateFormat;
		}
	};

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

// DateFormat is not thread-safe and requires synchronization!!! Therefore we replaced it by thread-locals.
//	public static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//	public static void main(String[] args)
//	throws Exception
//	{
//		String dateStr = "2008-08-13 12:34:56";
//		Date dateLocal = getDefaultDateFormatLocal().parse(dateStr);
//		Date dateUtc = getDefaultDateFormatUTC().parse(dateStr);
//		System.out.println(dateLocal.getTime());
//		System.out.println(dateUtc.getTime());
//
//		System.out.println(dateLocal);
//		System.out.println(dateUtc);
//	}

  protected TypeConverter() {
  }

  public static Object createJavaObjectFromString(int typeID, String value)
  	throws SQLException, ParseException
  {
    if (value == null || value.equals(""))
      return null;

    switch (typeID) {
      case Types.TINYINT:
        return Byte.valueOf(value);
      case Types.SMALLINT:
        return Short.valueOf(value);
      case Types.INTEGER:
        return Integer.valueOf(value);
      case Types.BIGINT:
        return new BigInteger(value);
      case Types.FLOAT:
        return Float.valueOf(value);
      case Types.DOUBLE:
        return Double.valueOf(value); /** TODO testen, ob das ueberhaupt unser gewuenschtes Format ("m.n", z.B. "23.45245897" verwendet */
      case Types.BOOLEAN:
        return Boolean.valueOf(value);
      case Types.DATE:
        	if(value.length() == 10)
        		value += " 00:00:00";
          return getDefaultDateFormatLocal().parse(value); // TODO we should pass as argument whether we want local or UTC!
      case Types.TIME:
//      	if(value.length() == 10) // is this necessary?
//      		value += " 00:00:00";

        return new Time(getDefaultDateFormatLocal().parse(value).getTime()); // TODO we should pass as argument whether we want local or UTC!
      case Types.TIMESTAMP:
      	if(value.length() == 10)
      		value += " 00:00:00"; // ipanema1 may send only "1999-01-01"
        return new Timestamp(getDefaultDateFormatLocal().parse(value).getTime()); // TODO we should pass as argument whether we want local or UTC!
      case Types.DECIMAL:
        return new Float(value);
      case Types.CHAR:
        return value;
      case Types.VARCHAR:
        return value;
      case Types.LONGVARCHAR:
        return value;
      case Types.CLOB:
    	  return value;
      default:
        throw new SQLException("Unknown field type ID: "+typeID);
    }
  }


//  /**
//   * Use this method to find out, whether a type is dynamically sizeable. Most
//   * of the datatypes are not. E.g. an INT has always the same size (at least
//   * with the same database server). But there are types - so far, only the
//   * type <code>VARCHAR</code>, that can have whatever size you define (or at
//   * least within a specified range).
//   */
//  public static boolean isDynamicSizeable(int fieldType)
//  {
//    TypeDef typeDef = getTypeDefByID(fieldType);
//    return typeDef.dynamicWidth;
//  }
  public static String getTypeNameByID(int fieldType)
  throws SQLException
  {
    TypeDef typeDef = getTypeDefByID(fieldType);
    return typeDef.typeName;
  }

  /**
   * This method converts the numeric fieldType to its string representation.
   * If the type is not dynamically sizeable, the fieldSize is ignored. So
   * far, only the type <code>VARCHAR</code> is dynamically sizeable.
   */
  public static String getFullTypeName(int fieldType, int displaySize, int scale)
  throws SQLException
  {
    TypeDef typeDef = getTypeDefByID(fieldType);
    if (typeDef.dynamicSizeMode == SIZEMODE_DISPLAYSIZE)
      return typeDef.typeName + '(' + displaySize + ')';

    if (typeDef.dynamicSizeMode == SIZEMODE_DISPLAYSIZE_AND_SCALE)
      return typeDef.typeName + '(' + displaySize + ',' + scale + ')';

    return typeDef.typeName;
  }

  protected static TypeDef getTypeDefByName(String typeName)
  throws SQLException
  {
    TypeDef typeDef = typeName2DefMap.get(typeName);
    if (typeDef == null)
      throw new SQLException("No typeDef found for typeName=\""+typeName+"\"!");
    return typeDef;
  }

  /**
   * @param typeName the simple type name (no brackets, no sizes) of the searched type.
   * @return the searched type
   * @throws SQLException if there is no type
   */
  public static int getTypeIDByName(String typeName)
  throws SQLException
  {
    TypeDef typeDef = getTypeDefByName(typeName);
    return typeDef.typeID;
  }

  protected static TypeDef getTypeDefByID(int typeID)
  throws SQLException
  {
    TypeDef typeDef = typeID2DefMap.get(Integer.valueOf(typeID));
    if (typeDef == null)
      throw new SQLException("No typeDef found for typeID=\""+typeID+"\"!");
    return typeDef;
  }


  /**
   * key: Integer typeID<br/>
   * value: TypeDef typeDef
   */
  protected static final HashMap<Integer, TypeDef> typeID2DefMap = new HashMap<Integer, TypeDef>();

  /**
   * key: String typeName<br/>
   * value: TypeDef typeDef
   */
  protected static final HashMap<String, TypeDef> typeName2DefMap = new HashMap<String, TypeDef>();

  public static final int SIZEMODE_FIX = 0;
  public static final int SIZEMODE_DISPLAYSIZE = 1;
  public static final int SIZEMODE_DISPLAYSIZE_AND_SCALE = 2; // incl. scale


  protected static class TypeDef {
    public int typeID;
    public String typeName;
    public int dynamicSizeMode;

    public TypeDef(int _typeID, String _typeName, int _dynamicSizeMode)
    {
      this.typeID = _typeID;
      this.typeName = _typeName;
      this.dynamicSizeMode = _dynamicSizeMode;
    }
  }

  protected static void registerType(int typeID, String typeName, int dynamicSizeMode)
  {
    if (typeName == null)
      throw new NullPointerException("typeName must not be null!");

    TypeDef typeDef = new TypeDef(typeID, typeName, dynamicSizeMode);
    typeID2DefMap.put(Integer.valueOf(typeID), typeDef);
    typeName2DefMap.put(typeName, typeDef);
  }

  static {
//    registerType(Types.ARRAY, "ARRAY");
    registerType(Types.BIGINT, "BIGINT", SIZEMODE_FIX);
//    registerType(Types.BINARY, "BINARY");
//    registerType(Types.BIT, "BIT");
//    registerType(Types.BLOB, "BLOB");
    registerType(Types.BOOLEAN, "BOOL", SIZEMODE_FIX); // wird von MSSQL nicht unterstuetzt
    registerType(Types.CHAR, "CHAR", SIZEMODE_DISPLAYSIZE);
    registerType(Types.CLOB, "TEXT", SIZEMODE_FIX);
//    registerType(Types.DATALINK, "", SIZEMODE_FIX);
    registerType(Types.DATE, "DATE", SIZEMODE_FIX);
    registerType(Types.DECIMAL, "DECIMAL", SIZEMODE_DISPLAYSIZE_AND_SCALE);
    registerType(Types.DOUBLE, "DOUBLE", SIZEMODE_FIX);
//    registerType(Types.DISTINCT, "", SIZEMODE_FIX);
    registerType(Types.FLOAT, "FLOAT", SIZEMODE_FIX);
    registerType(Types.INTEGER, "INT", SIZEMODE_FIX);
//    registerType(Types.JAVA_OBJECT, "", SIZEMODE_FIX);
//    registerType(Types.LONGVARBINARY, "", SIZEMODE_FIX);
    registerType(Types.LONGVARCHAR, "TEXT", SIZEMODE_FIX);
//    registerType(Types.NULL, "", SIZEMODE_FIX);
    registerType(Types.NUMERIC, "NUMERIC", SIZEMODE_FIX);
//    registerType(Types.OTHER, "", SIZEMODE_FIX);
    registerType(Types.REAL, "REAL", SIZEMODE_FIX);
//    registerType(Types.REF, "", SIZEMODE_FIX);
    registerType(Types.SMALLINT, "SMALLINT", SIZEMODE_FIX);
//    registerType(Types.STRUCT, "STRUCT", SIZEMODE_FIX);
    registerType(Types.TIME, "TIME", SIZEMODE_FIX);

    // TIMESTAMP habe ich mit MSSQL und MySQL getestet:
    //   MySQL: DATETIME = 93 = Types.TIMESTAMP
    //   MySQL: TIMESTAMP = 93 = Types.TIMESTAMP
    //   MSSQL: DATETIME = 93 = Types.TIMESTAMP
    //   MSSQL: TIMESTAMP = -2 = Types.BINARY => nicht benutzbar
    // => Wir muessen den String "DATETIME" benutzen, der zum Glueck auf beiden Servern geht.
    //    Damit ist "DATETIME" auch der Name des Datentyps in unserem Protokoll.
    registerType(Types.TIMESTAMP, "DATETIME", SIZEMODE_FIX);


    registerType(Types.TINYINT, "TINYINT", SIZEMODE_FIX);
//    registerType(Types.VARBINARY, "", SIZEMODE_FIX);
    registerType(Types.VARCHAR, "VARCHAR", SIZEMODE_DISPLAYSIZE);
    // Used for Java objects
    registerType(Types.JAVA_OBJECT, "JAVA_OBJECT", SIZEMODE_FIX);
  }

  public static boolean isFieldTypeValid(int _columnType)
  {
    return typeID2DefMap.containsKey(Integer.valueOf(_columnType));
  }

  public static int getSizeMode(int _columnType)
  {
    TypeDef typeDef = typeID2DefMap.get(Integer.valueOf(_columnType));
    return typeDef.dynamicSizeMode;
  }


//  public static boolean isDynamicSizeable(int _columnType)
//  throws SQLException
//  {
//    TypeDef typeDef = getTypeDefByID(_columnType);
//    return typeDef.dynamicWidth;
//  }

}
