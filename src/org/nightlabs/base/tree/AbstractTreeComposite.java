/*
 * Created 	on Sep 3, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.tree;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

import org.nightlabs.base.composite.XComposite;

public abstract class AbstractTreeComposite extends XComposite {

	private TreeViewer treeViewer;
	
	public static int DEFAULT_STYLE_SINGLE = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	public static int DEFAULT_STYLE_MULTI = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	
	public AbstractTreeComposite(Composite parent) {
		this(parent, DEFAULT_STYLE_SINGLE, true, true, true);
	}
	
	public AbstractTreeComposite(Composite parent, boolean init) {
		this(parent, DEFAULT_STYLE_SINGLE, true, init, true);
	}
	
	public AbstractTreeComposite(Composite parent, int style, boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER, setLayoutData ? XComposite.LAYOUT_DATA_MODE_GRID_DATA:  XComposite.LAYOUT_DATA_MODE_NONE);
		treeViewer = new TreeViewer(this, style);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setHeaderVisible(headerVisible);
		if (init)
			init();
	}
	
	public void init() {
		setTreeProvider(treeViewer);
		createTreeColumns(treeViewer.getTree());
	}
	
	public abstract void setTreeProvider(TreeViewer treeViewer);
	
	public abstract void createTreeColumns(Tree tree);
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public Tree getTree() {
		if (treeViewer != null)
			return treeViewer.getTree();
		return null;
	}
	
	public void refresh() {
		treeViewer.refresh();
	}

	public void refresh(boolean updateLabels) {
		treeViewer.refresh(updateLabels);
	}

	public void refresh(Object element, boolean updateLabels) {
		treeViewer.refresh(element, updateLabels);
	}

	public void refresh(Object element) {
		treeViewer.refresh(element);
	}

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Object getInput() {
		return treeViewer.getInput();
	}

	public ISelection getSelection() {
		return treeViewer.getSelection();
	}
	
	
}
