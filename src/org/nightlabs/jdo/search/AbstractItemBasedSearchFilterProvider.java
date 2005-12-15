/*
 * Created 	on Dec 16, 2004
 * 					by alex
 *
 */
package org.nightlabs.jdo.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.j2ee.InitialContextProvider;

/**
 * SearchFilterProvider providing a changable list of SearchFilterItems
 * for the search. The class has to be instatiated with a {@link org.nightlabs.jdo.search.SearchFilterItemListMutator}
 * in order to change the item list.
 * <br/>
 * Optionally one can set a {@link org.nightlabs.jdo.search.SearchResultFetcher} to
 * perform a search when the search button is hit. A SearchResultFetcher has to come
 * along with an InitialContextProvider to allow fetchers access to a j2ee server.
 * <br/>
 * Make sure to make the call to {@link #setSearchResultFetcher(SearchResultFetcher, InitialContextProvider)}
 * before you obtain this providers Composite as a SubComposite needs this 
 * values.
 * <br/>
 * Suclasses have to override {@link #createSearchFilter()}, so they have full control of
 * what type of SearchFilter is instatiated.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class AbstractItemBasedSearchFilterProvider implements SearchFilterProvider {

	
	protected SearchFilterItemListMutator listMutator;
	protected AbstractItemBasedSearchFilterProviderComposite providerComposite;
	protected SearchResultFetcher resultFetcher;
	protected InitialContextProvider login;
	
	/**
	 * Used to create custom instances of implementors of SearchFilter.<br/>
	 * A typical implementation would look like:
	 * <pre>
	 * 	return new MyInheritorOfSearchFilter();
	 * </pre>
	 * 
	 * @return
	 */
	protected abstract SearchFilter createSearchFilter();

	/**
	 * Creates new FilterProvider with listMutator used as callback
	 * for modifying the item list.<br/>
	 * Callback for the search button can be set with {@link #setSearchResultFetcher(SearchResultFetcher, InitialContextProvider)}
	 * 
	 * @param listMutator 
	 */
	public AbstractItemBasedSearchFilterProvider(SearchFilterItemListMutator listMutator) {
		this.listMutator = listMutator;
	}

	public void setSearchResultFetcher(SearchResultFetcher resultFetcher, InitialContextProvider login) {
		this.resultFetcher = resultFetcher;
		this.login = login;
	}
	
	public void setListMutator(SearchFilterItemListMutator listMutator) {
		this.listMutator = listMutator;
	}
	
	public abstract AbstractItemBasedSearchFilterProviderComposite createProviderComposite(
			Composite parent, 
			int style,
			SearchFilterProvider searchFilterProvider,
			SearchFilterItemListMutator listMutator,
			SearchResultFetcher resultFetcher,
			InitialContextProvider login
		);
	
	/**
	 * @see org.nightlabs.jdo.search.SearchFilterProvider#getComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createComposite(Composite parent) {
		providerComposite = createProviderComposite(
			parent,
			SWT.NONE,
			this,
			listMutator,
			resultFetcher,
			login
		);
		return providerComposite;
	}
	
	public Composite getComposite() {
		return providerComposite;
	}

	/**
	 * Calls createSearchFilter, clears the obtained filter
	 * and adds all item from the SearchFilterItemList.<br/>
	 * When overrided super() should be called, then SearchFilterItems
	 * can be added or removed to the returned filter.
	 * 
	 * @see org.nightlabs.jdo.search.SearchFilterProvider#getSearchFilter()
	 */
	public SearchFilter getSearchFilter() {
		SearchFilter filter = createSearchFilter();
		filter.clear();
		filter.setConjunction(providerComposite.getConjuction());
		providerComposite.getItemList().addItemsToFilter(filter);
		return filter;
	}

}
