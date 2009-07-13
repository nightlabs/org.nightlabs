/**
 * 
 */
package org.nightlabs.timepattern;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * {@link InputTimePattern}s are time patterns that can be used to obtain a date-time value
 * based on a input-date-time. It consists of 6 String parts (year, month, dayOfMonth, hour, minute, second)
 * that all are formulas/scripts to compute the date-time based on the input. 
 * <p>
 * The parts get the corresponding value of the base date-time deployed and can perform operations (addition, substraction) on them,
 * of course the parts can also simply define static value. The parts of the input date-time gets deployed in
 * the part-scripts under special variable names:
 * <ul>
 * <li>Year: Variable name: <code>Y</code></li>
 * <li>Month: Variable name: <code>M</code>, note that in contradiction to Java {@link Calendar}s the static values here are 1-based (not 0-based) so use 1 for January</li>
 * <li>Day of month: Variable name: <code>D</code></li>
 * <li>Hour: Variable name: <code>h</code> in 24h notation</li>
 * <li>Minute: Variable name: <code>m</code></li>
 * <li>Second: Variable name: <code>s</code></li>
 * <li><b>Actual maximum</b>: Variable name: <b><code>L</code></b>. This special value is different for each part and  </li>
 * </ul>
 * The result of each part should be the value of the according date-time part in the result where the value can exceed the maximum
 * or be less then zero as the difference will be carried to the other result parts. So for example when the input month (M) is December 
 * and you add 2 (M+2) you will get February but in the next year.
 * </p>
 * Here are some examples of {@link InputTimePattern}s (parts separated by '|')
 * <li>
 * The period of the last month: <code>from = " Y | M-1 | 1 | 0 | 0 | 0", to = " Y | M-1 | L | L | L | L"</code> 
 * </li>
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class InputTimePattern implements Serializable {

	private static final long serialVersionUID = 20081228L;

	public static final String YEAR_VAR = "Y";
	public static final String MONTH_VAR = "M";
	public static final String DAX_OF_MONTH_VAR = "D";
	public static final String HOUR_VAR = "h";
	public static final String MINUTE_VAR = "m";
	public static final String SECOND_VAR = "s";
	
	private static class PatternPart {
		String varName;
		int calField;
		String partValue;
		int offset = 0; // used for the month field, to be able to enter 1 for Januaruar 12 for Feb.
		
		public PatternPart(String varName, int calField, String partValue) {
			super();
			this.varName = varName;
			this.calField = calField;
			this.partValue = partValue;
		}
	}
	
	private String year;
	private String month;
	private String dayOfMonth;
	private String hour;
	private String minute;
	private String second;
	
	private volatile transient List<PatternPart> patternParts = null;
	private transient String thisString = null;
	
	/**	
	 * Constructs a new {@link InputTimePattern} with the given pattern parts.
	 * 
	 * @param year The year part of the pattern.
	 * @param month The month part of the pattern.
	 * @param dayOfMonth The day-of-month part of the pattern.
	 * @param hour The hour part of the pattern.
	 * @param minute The minute part of the pattern.
	 * @param second The second part of the pattern.
	 */
	public InputTimePattern(String year, String month, String dayOfMonth,
			String hour, String minute, String second) {
		super();
		this.year = year;
		this.month = month;
		this.dayOfMonth = dayOfMonth;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	
	/**
	 * Constructs a new {@link InputTimePattern} by trying to parse
	 * the six parts of a pattern from the given patternString. The
	 * parts in the pattern should be separated by '|'.
	 * <p>
	 * An example for a pattern: <code>"Y|M-1|D|L|L|L"</code>.
	 * </p>
	 * 
	 * @param patternString The String to parse.
	 */
	public InputTimePattern(String patternString) {
		String[] parts = patternString.split("|");
		if (parts.length != 6)
			throw new IllegalArgumentException("The patternString '" + patternString + "' could not be parsed into an InputTimePattern, the number of parts does not match: " + parts.length);
		this.year = parts[0];
		this.month = parts[1];
		this.dayOfMonth = parts[2];
		this.hour = parts[3];
		this.minute = parts[4];
		this.second = parts[5];
	}

	/**
	 * @return The correctly ordered list of {@link PatternPart}s to configure a new date with.
	 */
	private List<PatternPart> getPatternParts() {
		if (patternParts == null) {
			synchronized (this) {
				if (patternParts == null) {
					patternParts = new ArrayList<PatternPart>(6);
					patternParts.add(new PatternPart(YEAR_VAR, Calendar.YEAR, year));
					PatternPart monthPart = new PatternPart(MONTH_VAR, Calendar.MONTH, month);
					monthPart.offset = 1;
					patternParts.add(monthPart);
					patternParts.add(new PatternPart(DAX_OF_MONTH_VAR, Calendar.DAY_OF_MONTH, dayOfMonth));
					patternParts.add(new PatternPart(HOUR_VAR, Calendar.HOUR_OF_DAY, hour));
					patternParts.add(new PatternPart(MINUTE_VAR, Calendar.MINUTE, minute));
					patternParts.add(new PatternPart(SECOND_VAR, Calendar.SECOND, second));
				}
			}
		}
		return patternParts;
	}

	/**
	 * This method configures a new {@link Date} instance based on the formulas
	 * in this {@link InputTimePattern} and the input {@link Date} value. 
	 * 
	 * @param input The {@link Date} value the formulas in this pattern should operate on.
	 * @return 
	 * 		A new {@link Date} instance configured on the basis of the formulas
	 * 		in this {@link InputTimePattern} and the input {@link Date} value.
	 */
	public Date getTime(Date input) {
		if (input == null)
			throw new IllegalArgumentException("Parameter input must not be null.");
		Calendar cal = Calendar.getInstance();
	    cal.setTime(input);
	    
	    ScriptEngineManager mgr = new ScriptEngineManager();
	    // jsEngine is a newly create engine, the bindings 
	    // made in this scope will only be valid for this engine.
	    ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

	    for (PatternPart patternPart : getPatternParts()) {
			int actualValue = cal.get(patternPart.calField) + patternPart.offset;
			int actualMaximum = cal.getActualMaximum(patternPart.calField) + patternPart.offset;
			Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put(patternPart.varName, actualValue);
			bindings.put("L", actualMaximum);
			Object scriptResult = null;
			try {
				scriptResult = jsEngine.eval(patternPart.partValue);
			} catch (ScriptException e) {
				throw new RuntimeException("Could not evaluate " + patternPart.varName + " part of this " + this, e);
			}
			if (scriptResult == null || !(scriptResult instanceof Number))
				throw new IllegalStateException(patternPart.varName + " part of " + this + " did not evaluate to an integer value.");
			cal.add(patternPart.calField, ((Number) scriptResult).intValue() - actualValue);
		}
	    
	    return cal.getTime();
	}
	
	/**
	 * @return The year part of the pattern.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year The year part of the pattern to set.
	 */
	public void setYear(String year) {
		patternParts = null;
		this.year = year;
	}

	/**
	 * @return The month part of the pattern.
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month The month part of the pattern to set.
	 */
	public void setMonth(String month) {
		patternParts = null;
		this.month = month;
	}

	/**
	 * @return The dayOfMonth part of the pattern.
	 */
	public String getDayOfMonth() {
		return dayOfMonth;
	}

	/**
	 * @param dayOfMonth The dayOfMonth part of the pattern to set.
	 */
	public void setDayOfMonth(String dayOfMonth) {
		patternParts = null;
		this.dayOfMonth = dayOfMonth;
	}

	/**
	 * @return The hour part of the pattern.
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * @param hour The hour part of the pattern to set.
	 */
	public void setHour(String hour) {
		patternParts = null;
		this.hour = hour;
	}

	/**
	 * @return The minute part of the pattern.
	 */
	public String getMinute() {
		return minute;
	}

	/**
	 * @param minute The minute part of the pattern to set.
	 */
	public void setMinute(String minute) {
		patternParts = null;
		this.minute = minute;
	}

	/**
	 * @return The second part of the pattern.
	 */
	public String getSecond() {
		return second;
	}

	/**
	 * @param second The second part of the pattern to set.
	 */
	public void setSecond(String second) {
		patternParts = null;
		this.second = second;
	}

	@Override
	public String toString() {
		if (thisString == null) {
			StringBuffer sb = new StringBuffer(this.getClass().getName());
			sb.append("[\"");
			sb.append(getYear());
			sb.append("\",\"");
			sb.append(getMonth());
			sb.append("\",\"");
			sb.append(getDayOfMonth());
			sb.append("\",\"");
			sb.append(getHour());
			sb.append("\",\"");
			sb.append(getMinute());
			sb.append("\",\"");
			sb.append(getSecond());
			sb.append("\"]");
			thisString = sb.toString();
		}
		return thisString;
	}
	
	public static void main(String[] args) throws Exception {
		InputTimePattern pat = new InputTimePattern("Y", "M-12", "1", "L", "L", "L");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date input = dateFormat.parse("2008-12-31 22:53:00");
	    System.out.println(dateFormat.format(pat.getTime(input)));
	}
	
	
}
