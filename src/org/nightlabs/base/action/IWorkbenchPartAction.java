/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IWorkbenchPartAction extends IUpdateAction {

	void setActivePart(IWorkbenchPart part);
}
