/*
 * Created 	on Dec 15, 2004
 * 					by alex
 *
 */
package com.nightlabs.jdo.search;

import org.eclipse.swt.widgets.Composite;

import com.nightlabs.jdo.search.SearchFilter;

/**
 * Common interface to handle different scenarios of 
 * searching for persons.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface SearchFilterProvider {
	
	/**
	 * Should create and return a GUI-representation of this
	 * CriteriaBuilder as Composite. 
	 * 
	 * @param parent
	 * @return
	 */
	public Composite createComposite(Composite parent);
	
	/**
	 * Should return the Composite created in {@link #createComposite(Composite)}.
	 * @return
	 */
	public Composite getComposite();
	
	/**
	 * Return the PersonSearchFilter build up by this
	 * Criteria builder.
	 * 
	 * @return
	 */
	public SearchFilter getSearchFilter();
}
