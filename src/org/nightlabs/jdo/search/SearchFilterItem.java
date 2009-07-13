/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jdo.search;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jdo.resource.Messages;

/**
 * Abstract class to represent searches performed by a user.
 * Each FilterItem defines a field this item is connected to,
 * a match type for this item and the search sequence (needle).
 * Is used as a row within a search query by containing
 * {@link org.nightlabs.ipanema.person.util.SearchFilter} holds instances of
 * subclasses of this class
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * TODO: introduce generics
 */
public abstract class SearchFilterItem implements Serializable
{
	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	public static final int MATCHTYPE_CONTAINS = 1;
	public static final int MATCHTYPE_NOTCONTAINS = 2;
	public static final int MATCHTYPE_BEGINSWITH = 3;
	public static final int MATCHTYPE_ENDSWITH = 4;
	public static final int MATCHTYPE_EQUALS = 5;
	public static final int MATCHTYPE_NOTEQUALS = 6;
	public static final int MATCHTYPE_GREATER_THAN = 7;
	public static final int MATCHTYPE_LESS_THAN = 8;
	public static final int MATCHTYPE_CONTAINED = 9;
	public static final int MATCHTYPE_MATCHES = 10;
	public static final int MATCHTYPE_DEFAULT = MATCHTYPE_CONTAINS;

	private static final String[] MATCHTYPE_STRINGS = {
		null, // since it starts with 1, we need to declare this empty first entry
		"MATCHTYPE_CONTAINS", //$NON-NLS-1$
		"MATCHTYPE_NOTCONTAINS", //$NON-NLS-1$
		"MATCHTYPE_BEGINSWITH", //$NON-NLS-1$
		"MATCHTYPE_ENDSWITH", //$NON-NLS-1$
		"MATCHTYPE_EQUALS", //$NON-NLS-1$
		"MATCHTYPE_NOTEQUALS", //$NON-NLS-1$
		"MATCHTYPE_GREATER_THAN", //$NON-NLS-1$
		"MATCHTYPE_LESS_THAN", //$NON-NLS-1$
		"MATCHTYPE_CONTAINED", //$NON-NLS-1$
		"MATCHTYPE_MATCHES" //$NON-NLS-1$
	};

	public static String getLocalisedMatchType(int matchType)
	{
		if (matchType < 1 || matchType > MATCHTYPE_STRINGS.length)
			throw new IllegalArgumentException("Unknown matchType: " + matchType); //$NON-NLS-1$

		return Messages.getString(SearchFilterItem.class.getName() + '.' + MATCHTYPE_STRINGS[matchType]);
	}

	protected int matchType;
	protected String needle;

	@SuppressWarnings("unused")
	private SearchFilterItem() {
	}

	protected SearchFilterItem(int matchType, String needle) {
		this.matchType = matchType;
		this.needle = needle;
	}

	/**
	 * Should return the Object this filter item is connected to.
	 * @return
	 */
	public abstract Object getSearchField();

	/**
	 * Should return whether or not this item trims searched set.
	 * @return
	 */
	public abstract boolean isConstraint();

	/**
	 * {@link SearchFilter} can use this to handle different targets.
	 * @return
	 */
	public abstract Class<?> getItemTargetClass();

	/**
	 * Callback method to add this items criteria to
	 * the data used to build a {@link javax.jdo.Query}.
	 *
	 * @param itemIndex The index of the of this item within its filter.
	 * @param itemSubIndex The index of this items targetclass within the filter.
	 * @param imports Instances of {@link Class} added here will be imported in the Query.
	 * @param vars Buffer to add variable declarations to.
	 * @param filter Buffer to add filter statements to.
	 * @param params Buffer to add parameter declaration to.
	 * @param paramMap Map to add parameter values to.
	 */
	public abstract void appendSubQuery(
			int itemIndex,
			int itemSubIndex,
			Set<Class<?>> imports,
			StringBuffer vars, StringBuffer filter,
			StringBuffer params, Map<String, Object> paramMap);


	public int getMatchType() {
		return matchType;
	}

	public String getNeedle() {
		return needle;
	}
}
