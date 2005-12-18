package org.nightlabs.base.table;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Christian Soltenborn - http://www.eclipsezone.com/eclipse/forums/t59401.html
 */
public abstract class InvertableSorter extends ViewerSorter {
	public abstract int compare(Viewer viewer, Object e1, Object e2);
 
	abstract InvertableSorter getInverseSorter();
 
	public abstract int getSortDirection();
}
