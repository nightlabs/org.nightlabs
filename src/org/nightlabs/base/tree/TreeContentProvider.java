/*
 * Created 	on Sep 3, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Simple abstract ContentProvider for Trees.
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class TreeContentProvider implements ITreeContentProvider {

	public TreeContentProvider() {
		super();
	}

	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}


	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return null;
	}


	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}


	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return false;
	}

	
	
}
