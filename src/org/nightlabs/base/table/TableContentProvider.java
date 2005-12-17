/*
 * Created 	on Jun 14, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.table;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Adapter for ContentProviders for Tables.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class TableContentProvider implements IStructuredContentProvider {

	/**
	 * 
	 */
	public TableContentProvider() {
		super();
	}

	/**
	 * This basic implementation of IStructuredContentProvider accepts
	 * a {@link Collection} as <code>inputElement</code> and calls
	 * {@link Collection#toArray()}. If your inputElement has another type,
	 * you must override this method. 
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Collection)
			return ((Collection)inputElement).toArray();
		else
			throw new IllegalArgumentException("This basic implementation of getElements(...) (from " + TableContentProvider.class + ") does not support the inputElement type " + (inputElement == null ? null : inputElement.getClass().getName()));
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
