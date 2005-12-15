/*
 * Created 	on Dec 16, 2004
 * 					by alex
 *
 */
package org.nightlabs.jdo.search;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface SearchFilterItemListMutator {
	
	/**
	 * Will be called when a new 
	 * SearchFilterItemEditor has to be created
	 * and added to the list.<br/>
	 * Most implementations will look like:
	 * <pre>
	 * 	list.addItemEditor(new MyInheritorOfSearchFilterItemEditor())
	 * </pre>
	 * 
	 * @param list The list the editor should be added
	 */
	public void addItemEditor(SearchFilterItemList list);
	
}
