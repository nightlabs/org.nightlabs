package org.nightlabs.jdo.search;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public interface ISearchFilterItem {

//	/**
//	 * Should return whether or not this item trims the searched domain, i.e. whether it does contain any criteria
//	 * that will reduce the amount of returned results.
//	 *
//	 * @return whether or not this item trims the searched set.
//	 */
//	public abstract boolean isConstraint();

	/**
	 * Callback method to add this items criteria to
	 * the data used to build a {@link javax.jdo.Query}.
	 *
	 * @param itemIndex The index of this item within the list of items of its filter.
	 * @param imports Instances of {@link Class} added here will be imported in the Query.
	 * @param vars Buffer to add variable declarations to.
	 * @param filter Buffer to add filter statements to.
	 * @param params Buffer to add parameter declaration to.
	 * @param paramMap Map to add parameter values to.
	 */
	public void appendSubQuery(int itemIndex, Set<Class<?>> imports, StringBuffer vars, StringBuffer filter, StringBuffer params,
			Map<String, Object> paramMap);
	
	/**
	 * Returns an {@link EnumSet} containing all {@link MatchType}s that are supported by this
	 * {@link ISearchFilterItem} instance.
	 * 
	 * @return An {@link EnumSet} containing all supported {@link MatchType}s.
	 */
	public EnumSet<MatchType> getSupportedMatchTypes();
}