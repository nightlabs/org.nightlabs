package org.nightlabs.jdo.search;

import org.nightlabs.jdo.resource.Messages;

/**
 * This enum defines constants for the various match types of search filter items.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public enum MatchType {
	/** The left-hand-side argument <b>begins with</b> with the right-hand-side argument. */
	BEGINSWITH("MATCHTYPE_BEGINSWITH"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>is contained in</b> the right-hand-side argument. */
	CONTAINED("MATCHTYPE_CONTAINED"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>contains</b> the right-hand-side argument. */
	CONTAINS("MATCHTYPE_CONTAINS"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>ends with</b> the right-hand-side argument. */
	ENDSWITH("MATCHTYPE_ENDSWITH"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>equals</b> the right-hand-side argument. */
	EQUALS("MATCHTYPE_EQUALS"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>is greater than</b> the right-hand-side argument. */
	GREATER_THAN("MATCHTYPE_GREATER_THAN"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>is less than</b> the right-hand-side argument. */
	LESS_THAN("MATCHTYPE_LESS_THAN"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>matches</b> the right-hand-side argument. */
	MATCHES("MATCHTYPE_MATCHES"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>does not contain</b> the right-hand-side argument. */
	NOTCONTAINS("MATCHTYPE_NOTCONTAINS"), //$NON-NLS-1$
	
	/** The left-hand-side argument <b>does not equal</b> the right-hand-side argument. */
	NOTEQUALS("MATCHTYPE_NOTEQUALS"); //$NON-NLS-1$
	
//	public static MatchType DEFAULT = CONTAINS;
	
	private String localisationKey;
	
	private MatchType(String localisationKey) {
		this.localisationKey = localisationKey;
	}
	
	public String getLocalisedName() {
		return Messages.getString(MatchType.class.getName() + '.' + this.localisationKey);
	}
	
	@Override
	public String toString() {
		return getLocalisedName();
	}
}
