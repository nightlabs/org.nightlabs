/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.tree;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.composite.XComposite;

/**
 * A composite with a {@link TreeViewer} to be used as base for tree-composites.
 * It will create a treeViewer and trigger callback-methods for its configuration
 * (see {@link #setTreeProvider(TreeViewer)}) and {@link #createTreeColumns(Tree)}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractTreeComposite extends XComposite {

	private TreeViewer treeViewer;
	
	public static int DEFAULT_STYLE_SINGLE = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	public static int DEFAULT_STYLE_MULTI = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	
	/**
	 * Convenience parameter with 
	 * {@link #DEFAULT_STYLE_SINGLE}, a GridData, 
	 * directly inited and visible headers for the tree.
	 * @see #AbstractTreeComposite(Composite, int, boolean, boolean, boolean)
	 * 
	 * @param parent The parent to use.
	 */
	public AbstractTreeComposite(Composite parent) {
		this(parent, DEFAULT_STYLE_SINGLE, true, true, true);
	}
	
	/**
	 * Creates a new tree composite with the given parent.
	 * The init parameter controls whether or not the tree providers
	 * and colums should be configured already.
	 * 
	 * @param parent The parent to use.
	 * @param init Whether to call {@link #init()}
	 */
	public AbstractTreeComposite(Composite parent, boolean init) {
		this(parent, DEFAULT_STYLE_SINGLE, true, init, true);
	}
	
	/**
	 * See {@link #AbstractTreeComposite(Composite, boolean)}. The other
	 * parameters are used to control the trees look.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use for treeViewer. The style of the wrapping Composite will be SWT.NONE.
	 * @param setLayoutData Whether to set a LayoutData (of fill both) for the wrapping Composite.
	 * @param init Whether to call init directly.
	 * @param headerVisible Whether the header of the TreeViewer should be visible.
	 */
	public AbstractTreeComposite(Composite parent, int style, boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER, setLayoutData ? XComposite.LayoutDataMode.GRID_DATA:  XComposite.LayoutDataMode.NONE);
		treeViewer = new TreeViewer(this, style);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setHeaderVisible(headerVisible);
		if (init)
			init();
	}
	
	/**
	 * Init calls {@link #setTreeProvider(TreeViewer)} and {@link #createTreeColumns(Tree)}
	 * with the appropriate parameters.
	 */
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
	
	/**
	 * Selects the given elements in the list if they exist.
	 * @param elements the elements to be selected. 
	 */
	public void setSelection(List elements, boolean reveal)
	{
		if (elements == null || elements.size() == 0)
			return;
		getTreeViewer().setSelection(new StructuredSelection(elements), true);
	}
	
	/**
	 * Sets and reveals the selection to the given elements.
	 * 
	 * @param elements The elements to select.
	 * @see #setSelection(List, boolean)
	 */
	public void setSelection(List elements) {
		setSelection(elements, true);
	}
	
	/**
	 * Selects the given element in the tree. (Shortcut to #setSelection(List)).
	 * @param element the element to be selected
	 */
	public void setSelection(Object element)
	{
		getTreeViewer().setSelection(new StructuredSelection(element));
	}
	
	public void setInput(Object input) {
		treeViewer.setInput(input);
	}
}
