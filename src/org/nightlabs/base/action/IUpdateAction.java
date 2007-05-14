/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.IAction;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek <!-- daniel [AT] nightlabs [DOT] de -->
 */
public interface IUpdateAction
extends IAction
{
	/**
	 * returns if the action should be enabled
	 * @return if the action should be enabled
	 */
	boolean calculateEnabled();
	
	/**
	 * returns if the action should be visible
	 * @return if the action should be visible
	 */
	boolean calculateVisible();
}
