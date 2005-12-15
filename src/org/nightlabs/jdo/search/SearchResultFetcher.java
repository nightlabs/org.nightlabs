package org.nightlabs.jdo.search;

import org.nightlabs.j2ee.InitialContextProvider;


/**
 * Common interface to handle triggerment of searches
 * for persons.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface SearchResultFetcher {
	/**
	 * Will be called when a search is triggered.
	 * The criteriaBuilder will provide a SearchFilter.
	 * Fetchers have to perform the search themselves
	 * within this method. With the login passed fetchers 
	 * can have access to a j2ee server to perform the
	 * search based on the obtained search filter.
	 * 
	 * @param criteriaBuilder
	 * @param login
	 */
	public void searchTriggered(SearchFilterProvider filterProvider, InitialContextProvider login);
}
