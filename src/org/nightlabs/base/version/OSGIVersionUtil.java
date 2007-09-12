package org.nightlabs.base.version;

import java.util.StringTokenizer;

import org.nightlabs.version.MalformedVersionException;
import org.nightlabs.version.Version;
import org.nightlabs.version.VersionPattern;
import org.nightlabs.version.VersionRangeEndPoint;
import org.nightlabs.version.VersionRangeEndPoint.EndPointLocation;

/**
 * This utility class converts all OSGI version related string representations to the corresponding 
 * NightLabs classes.
 *  
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 */
public final class OSGIVersionUtil
{
	
	public static final String OSGI_SEPARATOR_VERSIONELEMENTS = ".";
	/**
	 * @param osgiVersion the string representing an OSGI version.
	 * @return a Version with <code>major = osgi.major</code>, <code>minor = osgi.minor</code>, 
	 * 	<code>release = osgi.micro</code>, <code>patchLevel = 0</code> and 
	 * 	<code>suffix = osgi.suffix</code>.
	 * @throws MalformedVersionException if the given <code>String</code> does not conform to the 
	 * 	pattern of an OSGI version.
	 */
	public static Version parseOSGIVersionString(String osgiVersion) 
		throws MalformedVersionException 
	{
		StringTokenizer tok = new StringTokenizer(osgiVersion, OSGI_SEPARATOR_VERSIONELEMENTS);
		int _major;
		int _minor;
		int _release;
		String _suffix = "";
		
		try {
			String major = tok.nextToken();
			_major = Integer.parseInt(major);
		} catch (Exception x) {
			throw new MalformedVersionException("Major is missing or not a valid int!");
		}
		
		if (! tok.hasMoreTokens()) {
			return new Version(_major, 0, 0, 0);
		}
		
		try {
			String minor = tok.nextToken();
			_minor = Integer.parseInt(minor);
		} catch (Exception x) {
			throw new MalformedVersionException("Minor is missing or not a valid int!");
		}
		
		if (! tok.hasMoreTokens()) {
			return new Version(_major, _minor, 0, 0);
		}
		
		try {
			String release = tok.nextToken();
			_release = Integer.parseInt(release);
		} catch (Exception x) {
			throw new MalformedVersionException("Release is missing or not a valid int!");
		}

		if (! tok.hasMoreTokens()) {
			return new Version(_major, _minor, _release, 0);
		}
		
		if (tok.hasMoreTokens()) {
			_suffix = tok.nextToken();
			if (_suffix.length() > Version.MAX_STRING.length())
				throw new IllegalArgumentException("The given suffix is too long! It is allowed to have " +
						"a maximum of "+Version.MAX_STRING.length()+" characters!");
		}
		
		if (tok.hasMoreTokens())
			throw new IllegalArgumentException("The given String has got to many separators! " +
					"Only 3 are allowed!");

		return new Version(_major, _minor, _release, 0, _suffix);
	}
	
	/**
	 * The brackets used by OSGI for the inclusion and exclusion of an version range end point. 
	 */
	public static final char[] INCLUSIVE_BRACKETS = {'[', ']'};
	public static final char[] EXCLUSIVE_BRACKETS = {'(', ')'};
	
	/**
	 * Returns a {@link VersionRangeEndPoint} for the given String representation of an OSGI version 
	 * pattern end point.
	 * <p>Example of an MANIFEST.MF entry of an Eclipse RCP Plugin<br>
	 * <code> org.eclipse.ui.forms;bundle-version="[1.0.0,2.0.0)"</code> <br>
	 * The string representation of the two version range end points are:<code>[1.0.0</code> and 
	 * <code>2.0.0)</code>. </p>
	 * 
	 * @param rangePointString the string representation of an OSGI version end point.
	 * @return the corresponding NightLabs {@link VersionRangeEndPoint}
	 * @throws MalformedVersionException if the given string representation does not conform to the 
	 * 	OSGI definition of a version.
	 */
	public static VersionRangeEndPoint parseOSGIVersionEndPoint(String rangePointString) 
		throws MalformedVersionException 
	{
		boolean inclusive;
		if (rangePointString.startsWith(String.valueOf(INCLUSIVE_BRACKETS[0])) || 
				rangePointString.endsWith(String.valueOf(INCLUSIVE_BRACKETS[1])) )
			inclusive = true;
		else if (rangePointString.startsWith(String.valueOf(EXCLUSIVE_BRACKETS[0])) || 
				rangePointString.endsWith(String.valueOf(EXCLUSIVE_BRACKETS[1])) )
			inclusive = false;
		else 
			throw new IllegalArgumentException("The given string of an encoded VersionRangeEndPoint " +
					"does not start/end with a valid inclusion/exclusion symbol! Valid Symbols:"+
					String.valueOf(INCLUSIVE_BRACKETS)+" or "+String.valueOf(EXCLUSIVE_BRACKETS)
					+". Given String: "+	rangePointString);
		
		EndPointLocation location;
		if (rangePointString.startsWith(String.valueOf(INCLUSIVE_BRACKETS[0])) || 
				rangePointString.startsWith(String.valueOf(EXCLUSIVE_BRACKETS[0])) )
			location = EndPointLocation.LOWER;
		else
			location = EndPointLocation.UPPER;
		
		String versionString = rangePointString.replaceAll(
				"[\\"+INCLUSIVE_BRACKETS[0]+"\\"+INCLUSIVE_BRACKETS[1]
		    +"\\"+EXCLUSIVE_BRACKETS[0]+"\\"+EXCLUSIVE_BRACKETS[1]+"]", "");

		Version endpoint = parseOSGIVersionString(versionString); 
		return new VersionRangeEndPoint(endpoint, inclusive, location);
	}
	
	/**
	 * The OSGI separator used to separate two versions in a pattern/filter.
	 */
	public static final String OSGI_SEPARATOR_VERSION = ",";
	
	/**
	 * Parses a string representing an OSGI version pattern, e.g. as given in MANIFEST.MF of an RCP 
	 * Plugin, and returns the corresponding NightLabs {@link VersionPattern}. 
	 * @param patternString the string representation of an OSGI version pattern.
	 * @return the corresponding {@link VersionPattern}.
	 * @throws MalformedVersionException 
	 */
	public static VersionPattern parseOSGIVersionPattern(String patternString) 
		throws MalformedVersionException 
	{
		if (patternString == null || patternString.trim().length() == 0)
			throw new IllegalArgumentException("The given VersionPattern string must not " +
					"be null or empty!");
		
		patternString = patternString.trim();
		
		StringTokenizer tokenizer = new StringTokenizer(patternString, OSGI_SEPARATOR_VERSION);
		if (tokenizer.countTokens() != 2)
			throw new IllegalArgumentException("The given patternString is unparseable, since the " +
					"separator char ',' is encountered more than once in the given " +
					"String");
		
		VersionRangeEndPoint min, max;
		min = parseOSGIVersionEndPoint(tokenizer.nextToken());
		max = parseOSGIVersionEndPoint(tokenizer.nextToken());

		return new VersionPattern(min, max);
	}
}
