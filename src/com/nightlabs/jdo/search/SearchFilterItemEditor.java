/*
 * Created 	on Dec 16, 2004
 * 					by alex
 *
 */
package com.nightlabs.jdo.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.nightlabs.jdo.search.SearchFilterItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class SearchFilterItemEditor {
	
	/**
	 * After the first call this method should always
	 * return the same control. So from the second call
	 * the parent parameter should be neglected.
	 * 
	 * @param parent
	 * @return
	 */
	public abstract Control getControl(Composite parent);
	
	/**
	 * Should return the SearchFilterItem this 
	 * editor has build.
	 * 
	 * @return
	 */
	public abstract SearchFilterItem getSearchFilterItem();
	
	/**
	 * Will be called when the
	 * editor is closed. It should be
	 * used for cleanup (removing listeners), 
	 * not for disposing widgets.
	 */
	public abstract void close();
	
	/**
	 * Creates a new instance of the current class.
	 * 
	 * @return
	 */
	public SearchFilterItemEditor newInstance() {
		SearchFilterItemEditor newEditor = null;
		try {
			newEditor = (SearchFilterItemEditor) this.getClass().newInstance();
		} catch (Throwable t) {
			IllegalStateException ill = new IllegalStateException("Could not create new instance of SearchFilterItemEditor "+this);
			ill.initCause(t);
			throw ill;
		}
		return newEditor;
	}
	
}
