/*
 * Created 	on Sep 6, 2005
 * 					by alex
 *
 */
package com.nightlabs.jdo.search;

import org.eclipse.swt.widgets.Composite;

import com.nightlabs.j2ee.InitialContextProvider;

/**
 * Default implementation of an ItemBased SearchFilterProviderComposite.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class ItemBasedSearchFilterProviderComposite extends
		AbstractItemBasedSearchFilterProviderComposite {

	/**
	 * @param parent
	 * @param style
	 * @param searchFilterProvider
	 * @param listMutator
	 * @param resultFetcher
	 * @param login
	 */
	public ItemBasedSearchFilterProviderComposite(Composite parent, int style,
			SearchFilterProvider searchFilterProvider,
			SearchFilterItemListMutator listMutator,
			SearchResultFetcher resultFetcher, InitialContextProvider login) {
		super(parent, style, searchFilterProvider, listMutator, resultFetcher,
				login);
	}

	/**
	 * @see com.nightlabs.jdo.search.AbstractItemBasedSearchFilterProviderComposite#createSearchFilterItemList(org.eclipse.swt.widgets.Composite, int)
	 */
	public SearchFilterItemList createSearchFilterItemList(Composite parent,
			int style) {
		return new SearchFilterItemList(parent, style);
	}

}
