/*
 * Created 	on Sep 6, 2005
 * 					by alex
 *
 */
package org.nightlabs.jdo.search;

import org.eclipse.swt.widgets.Composite;

import org.nightlabs.j2ee.InitialContextProvider;

/**
 * Default implementation of a ItemBased SearchFilterProvider.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class ItemBasedSearchFilterProvider extends
		AbstractItemBasedSearchFilterProvider {

	public ItemBasedSearchFilterProvider(SearchFilterItemListMutator listMutator) {
		super(listMutator);
	}

	public AbstractItemBasedSearchFilterProviderComposite createProviderComposite(
			Composite parent, 
			int style, 
			SearchFilterProvider searchFilterProvider, 
			SearchFilterItemListMutator listMutator, 
			SearchResultFetcher resultFetcher, 
			InitialContextProvider login
		) {
		return new ItemBasedSearchFilterProviderComposite(
				parent,
				style,
				searchFilterProvider,
				listMutator,
				resultFetcher,
				login
			);
	}

}
