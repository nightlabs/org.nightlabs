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
package org.nightlabs.l10n;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DefaultDateFormatCfMod extends ConfigModule
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private String dateLong;
	private String dateShort;
	private String dateLongWeekday;
	private String dateShortWeekday;
	private String timeHM;
	private String timeHMS;
	private String timeHMSms;
	private String[] weekDaysLong;
	private String[] weekDaysShort;
	private String[] monthsLong;
	private String[] monthsShort;
	private String[] amPmStrings;
	private String[] eras;

	public DefaultDateFormatCfMod()
	{
	}
	
	/**
	 * @see org.nightlabs.config.ConfigModule#getIdentifier()
	 */
	@Override
	public String getIdentifier()
	{
		return super.getIdentifier();
	}
	/**
	 * @see org.nightlabs.config.ConfigModule#setIdentifier(java.lang.String)
	 */
	@Override
	public void setIdentifier(String _identifier)
	{
		super.setIdentifier(_identifier);
	}

	/**
	 * @see org.nightlabs.config.ConfigModule#init()
	 */
	@Override
	public void init() throws InitException
	{
		String identifier = getIdentifier();
		if (identifier == null)
			throw new IllegalStateException("identifier of this ConfigModule is null! It should be the language/country-code (e.g. en_US)!");

		String[] sa = identifier.split("_");
		if (sa.length < 1)
			throw new IllegalStateException("identifier of this ConfigModule is invalid (empty?!)! It should be the language/country-code (e.g. en_US)!");

		String language = sa[0];
		String country = null;
		if (sa.length > 1)
			country = sa[1];

		Locale locale = null;
		if (country != null)
			locale = new Locale(language, country);
		else
			locale = new Locale(language);
		
		if (dateShort == null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			if (df instanceof SimpleDateFormat) {
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				setDateShort(sdf.toPattern());
			}
			else
				setDateShort("yyyy-MM-dd");
		}

		if (dateShortWeekday == null)
			setDateShortWeekday("EE, " + dateShort);

		DateFormatSymbols dfs = new DateFormatSymbols(locale);

		if (dateLong == null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
			if (df instanceof SimpleDateFormat) {
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				setDateLong(sdf.toPattern());
			}
			else
				setDateLong("yyyy MMMM dd");
		}

		if (dateLongWeekday == null)
			setDateLongWeekday("EEEE, " + dateLong);

		if (timeHM == null) {
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
			if (df instanceof SimpleDateFormat) {
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				setTimeHM(sdf.toPattern());
			}
			else
				setTimeHM("HH:mm");
		}

		if (timeHMS == null) {
			DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
			if (df instanceof SimpleDateFormat) {
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				setTimeHMS(sdf.toPattern());
			}
			else
				setTimeHMS("HH:mm:ss");
		}

		if (timeHMSms == null)
			setTimeHMSms("HH:mm:ss.SSS");

		if (weekDaysLong == null)
			weekDaysLong = dfs.getWeekdays();

		if (weekDaysShort == null)
			weekDaysShort = dfs.getShortWeekdays();

		if (monthsLong == null)
			monthsLong = dfs.getMonths();

		if (monthsShort == null)
			monthsShort = dfs.getShortMonths();

		if (amPmStrings == null)
			amPmStrings = dfs.getAmPmStrings();

		if (eras == null)
			eras = dfs.getEras();
	}

	/**
	 * @return Returns the dateLong.
	 */
	public String getDateLong()
	{
		return dateLong;
	}
	/**
	 * @param dateLong The dateLong to set.
	 */
	public void setDateLong(String dateLong)
	{
		this.dateLong = dateLong;
		setChanged();
	}
	/**
	 * @return Returns the dateLongWeekday.
	 */
	public String getDateLongWeekday()
	{
		return dateLongWeekday;
	}
	/**
	 * @param dateLongWeekday The dateLongWeekday to set.
	 */
	public void setDateLongWeekday(String dateLongWeekday)
	{
		this.dateLongWeekday = dateLongWeekday;
		setChanged();
	}
	/**
	 * @return Returns the dateShort.
	 */
	public String getDateShort()
	{
		return dateShort;
	}
	/**
	 * @param dateShort The dateShort to set.
	 */
	public void setDateShort(String dateShort)
	{
		this.dateShort = dateShort;
		setChanged();
	}
	/**
	 * @return Returns the dateShortWeekday.
	 */
	public String getDateShortWeekday()
	{
		return dateShortWeekday;
	}
	/**
	 * @param dateShortWeekday The dateShortWeekday to set.
	 */
	public void setDateShortWeekday(String dateShortWeekday)
	{
		this.dateShortWeekday = dateShortWeekday;
		setChanged();
	}
	/**
	 * @return Returns the timeHM.
	 */
	public String getTimeHM()
	{
		return timeHM;
	}
	/**
	 * @param timeHM The timeHM to set.
	 */
	public void setTimeHM(String timeHM)
	{
		this.timeHM = timeHM;
		setChanged();
	}
	/**
	 * @return Returns the timeHMS.
	 */
	public String getTimeHMS()
	{
		return timeHMS;
	}
	/**
	 * @param timeHMS The timeHMS to set.
	 */
	public void setTimeHMS(String timeHMS)
	{
		this.timeHMS = timeHMS;
		setChanged();
	}
	/**
	 * @return Returns the timeHMSms.
	 */
	public String getTimeHMSms()
	{
		return timeHMSms;
	}
	/**
	 * @param timeHMSms The timeHMSms to set.
	 */
	public void setTimeHMSms(String timeHMSms)
	{
		this.timeHMSms = timeHMSms;
		setChanged();
	}
	/**
	 * @return Returns the amPmStrings.
	 */
	public String[] getAmPmStrings()
	{
		return amPmStrings;
	}
	/**
	 * @param amPmStrings The amPmStrings to set.
	 */
	public void setAmPmStrings(String[] amPmStrings)
	{
		this.amPmStrings = amPmStrings;
		setChanged();
	}
	/**
	 * @return Returns the eras.
	 */
	public String[] getEras()
	{
		return eras;
	}
	/**
	 * @param eras The eras to set.
	 */
	public void setEras(String[] eras)
	{
		this.eras = eras;
		setChanged();
	}
	/**
	 * @return Returns the monthsLong.
	 */
	public String[] getMonthsLong()
	{
		return monthsLong;
	}
	/**
	 * @param monthsLong The monthsLong to set.
	 */
	public void setMonthsLong(String[] monthsLong)
	{
		this.monthsLong = monthsLong;
		setChanged();
	}
	/**
	 * @return Returns the monthsShort.
	 */
	public String[] getMonthsShort()
	{
		return monthsShort;
	}
	/**
	 * @param monthsShort The monthsShort to set.
	 */
	public void setMonthsShort(String[] monthsShort)
	{
		this.monthsShort = monthsShort;
		setChanged();
	}
	/**
	 * @return Returns the weekDaysLong.
	 */
	public String[] getWeekDaysLong()
	{
		return weekDaysLong;
	}
	/**
	 * @param weekDaysLong The weekDaysLong to set.
	 */
	public void setWeekDaysLong(String[] weekDaysLong)
	{
		this.weekDaysLong = weekDaysLong;
		setChanged();
	}
	/**
	 * @return Returns the weekDaysShort.
	 */
	public String[] getWeekDaysShort()
	{
		return weekDaysShort;
	}
	/**
	 * @param weekDaysShort The weekDaysShort to set.
	 */
	public void setWeekDaysShort(String[] weekDaysShort)
	{
		this.weekDaysShort = weekDaysShort;
		setChanged();
	}
}
