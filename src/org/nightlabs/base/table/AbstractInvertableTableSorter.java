package org.nightlabs.base.table;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

/**
 * @author Christian Soltenborn - http://www.eclipsezone.com/eclipse/forums/t59401.html
 */
public abstract class AbstractInvertableTableSorter extends InvertableSorter {
	private final InvertableSorter inverse = new InvertableSorter() {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			return (-1)*AbstractInvertableTableSorter.this.compare(viewer, e1, e2);
		}

		InvertableSorter getInverseSorter() {
			return AbstractInvertableTableSorter.this;
		}

		public int getSortDirection() {
			return SWT.DOWN;
		}
	};

	InvertableSorter getInverseSorter() {
		return inverse;
	}

	public int getSortDirection() {
		return SWT.UP;
	}
}
