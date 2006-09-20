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

package org.nightlabs.base.entity.tree;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.entity.EntityEditorRegistry;
import org.nightlabs.base.tree.AbstractTreeComposite;
import org.nightlabs.base.tree.TreeContentProvider;
import org.nightlabs.base.util.RCPUtil;

/**
 * Displays a tree of {@link IEntityTreeCategory}s regsitered to 
 * a given viewID and and opens the registered
 * editor for them upon user request.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityTree 
extends AbstractTreeComposite 
implements IEntityTreeCategoryChangeListener, IOpenListener, DisposeListener 
{
	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(EntityTree.class);

//	/**
//	 * This is the id the categories to display with this composite
//	 * should be registered to.
//	 */
//	private String viewID;
	
	/**
	 * Member to hold the categories displayed by this tree Composite
	 */
	private IEntityTreeCategory[] categories;
	
	/**
	 * This map is used to find out wich tree object belongs 
	 * to wich category for asking the category for labels
	 * and icons.
	 */
	private Map<Object, IEntityTreeCategory> childCategories;

	/**
	 * @param parent
	 */
	public EntityTree(Composite parent, String viewID) {
		super(parent, false);
//		this.viewID = viewID;
		init();
		categories = EntityEditorRegistry.sharedInstance().getCategories(viewID);
		getTreeViewer().setInput(categories);
		for (IEntityTreeCategory category : categories) {
			//System.err.println("category: "+category.getName());
			category.addEntityTreeCategoryChangeListener(this);
		}
		getTreeViewer().addOpenListener(this);
		getTree().setHeaderVisible(false);
		addDisposeListener(this);
		
	}

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 * @param init
	 * @param headerVisible
	 */
	public EntityTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new EntityTreeContentProvider());
		treeViewer.setLabelProvider(new EntityTreeLabelProvider());
	}

	
	/**
	 * The content provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	class EntityTreeContentProvider extends TreeContentProvider 
	{
		private Map<IEntityTreeCategory, ITreeContentProvider> contentProviders = new HashMap<IEntityTreeCategory, ITreeContentProvider>();

		private ITreeContentProvider getContentProvider(IEntityTreeCategory category) {
			if (category == null)
				return null;
			if (contentProviders.containsKey(category))
				return contentProviders.get(category);
			ITreeContentProvider result = category.getContentProvider();
			contentProviders.put(category, result);
			return result;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			// either the input is set to a IEntityTreeCategory[]
			if (inputElement instanceof IEntityTreeCategory[])
				return (IEntityTreeCategory[])inputElement;
			// or it is something the delegate content provider has to deal with (using node-objects one still could have go-into)
			else {
				ITreeContentProvider contentProvider = getContentProvider(getChildCategory(inputElement));
				if (contentProvider != null)
					return contentProvider.getElements(inputElement);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement)
		{
			IEntityTreeCategory parentCategory = null;
			// check the parent category. Its either the element itself (as published so in getElements)
			if (parentElement instanceof IEntityTreeCategory)
				parentCategory = (IEntityTreeCategory)parentElement;
			// or it is somewhere up the tree
			else 
				parentCategory = getChildCategory(parentElement);
			Object[] children = null;
			ITreeContentProvider contentProvider = getContentProvider(parentCategory);
			if (contentProvider != null)
				// if the direct parent is a category call getElements on its ContentProvider
				if (parentElement instanceof IEntityTreeCategory)
					children = contentProvider.getElements(parentElement);
				// if we're deeper check the getChildren method
				else if (contentProvider != null)
					children = contentProvider.getChildren(parentElement);
			
			if (children != null)
				for (Object object : children)
					addChildCategory(object, parentCategory);
			return children;
			
			//System.err.println("GET CHILDREN FOR "+parentElement);
//			IEntityTreeCategory category = (IEntityTreeCategory)parentElement;
//			Object[] children = category.getChildren();
//			for (Object object : children)
//				addChildCategory(object, category);
//			return children;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element)
		{
			// if direct category, baby, say yeah
			if (element instanceof IEntityTreeCategory)				
				return true;
			// else delegate
			else {
				IEntityTreeCategory category = getChildCategory(element);
				ITreeContentProvider contentProvider = category != null ? getContentProvider(category) : null;
				return (contentProvider != null) ? contentProvider.hasChildren(element) : false;
			}
		}
		
		@Override
		public Object getParent(Object element) {
			// here as well delegate when not at top level 
			if (element instanceof IEntityTreeCategory)
				return null;
			ITreeContentProvider contentProvider = getContentProvider(getChildCategory(element));
			if (contentProvider != null)
				return contentProvider.getParent(element);
			return super.getParent(element);
		}
	}

	/**
	 * The label provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	class EntityTreeLabelProvider extends LabelProvider implements ITableLabelProvider 
	{
		private Map<IEntityTreeCategory, ITableLabelProvider> labelProviders = new HashMap<IEntityTreeCategory, ITableLabelProvider>();
		
		private ITableLabelProvider getLabelProvider(IEntityTreeCategory category) {
			if (category == null)
				return null;			
			if (labelProviders.containsKey(category))
				return labelProviders.get(category);
			ITableLabelProvider labelProvider = category.getLabelProvider();
			labelProviders.put(category, labelProvider);
			return labelProvider;			
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element)
		{
			return getColumnImage(element, 0);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIdx) {
			if(element instanceof IEntityTreeCategory)
				return ((IEntityTreeCategory)element).getImage();
			else {
				IEntityTreeCategory category = getChildCategory(element);
				ITableLabelProvider labelProvider = getLabelProvider(category);
				Image image = null;
				if(labelProvider != null)
					image = labelProvider.getColumnImage(element, columnIdx);
				if (image != null)
					return image;
				return category.getImage();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIdx) {
			if(element instanceof IEntityTreeCategory)
				return ((IEntityTreeCategory)element).getName();
			else {
				ITableLabelProvider labelProvider = getLabelProvider(getChildCategory(element));
				if(labelProvider != null)
					return labelProvider.getColumnText(element, columnIdx);				
				return super.getText(element);
			}
		}
	}

	/**
	 * Add a child for a category. This is used internally
	 * to find out wich tree object belongs to wich category
	 * for asking the category for labels and icons.
	 * @param child The child element to add
	 * @param category The category the child belongs to
	 */
	protected void addChildCategory(Object child, IEntityTreeCategory category)
	{
		if(childCategories == null)
			childCategories = new HashMap<Object, IEntityTreeCategory>();
		childCategories.put(child, category);
	}

	/**
	 * Add a child for a category. This is used internally
	 * to find out wich tree object belongs to wich category
	 * for asking the category for labels and icons.
	 * @param child The child element to get the category for
	 * @return The category of the child element or <code>null</code>
	 * 		if the child is unknown.
	 */
	protected IEntityTreeCategory getChildCategory(Object child)
	{
		return childCategories==null ? null : childCategories.get(child);
	}
	
	/**
	 * Returns the {@link IEntityTreeCategory} of the given element
	 * regardless whether the element is itself a categroy or
	 * a child provided by a category.
	 * 
	 * @param element The element to search the category for.
	 * @return The {@link IEntityTreeCategory} of the given element
	 */
	public IEntityTreeCategory getElementCategory(Object element) {
		if (element instanceof IEntityTreeCategory)
			return (IEntityTreeCategory) element;
		return getChildCategory(element);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategoryChangeListener#categoryChanged(org.nightlabs.jfire.base.admin.entityeditor.EntityTreeCategory)
	 */
	public void categoryChanged(final EntityTreeCategory category)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().refresh(category);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Expand/collapse all categories in the selection
	 * and open the registered editors for all selected
	 * category elements.
	 */
	public void open(OpenEvent event)
	{
		final ISelection _selection = event.getSelection();
		if(_selection.isEmpty())
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Object[] selectedElements = ((IStructuredSelection)_selection).toArray();
				for (Object element : selectedElements) {
					if(element instanceof IEntityTreeCategory) {
						if(getTreeViewer().getExpandedState(element))
							getTreeViewer().collapseToLevel(element, 1);
						else
							getTreeViewer().expandToLevel(element, 1);
					} else {
						IEntityTreeCategory category = getChildCategory(element);
						if(category != null) {
							IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(category.getEditorID());
							if (editorDescriptor == null) {
								logger.warn("Could not obtain editorDescriptor to open editor for category "+category.getName()+" and editorID "+category.getEditorID());
								return;
							}
							try {
								IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
								if (page != null)
									page.openEditor(category.getEditorInput(element), editorDescriptor.getId());
								else
									logger.warn("Could not get active workbench page, no editor was opened.");
							} catch (PartInitException e) {
								throw new RuntimeException("Opening editor failed", e);
							}
						} else {
							System.err.println("Unknown entity category for element "+element);
						}
					}
				}
			}
		});
	}

	public void widgetDisposed(DisposeEvent arg0) {
		getTreeViewer().removeOpenListener(this);
		for (IEntityTreeCategory category : categories) {
			category.removeEntityTreeCategoryChangeListener(this);
		}
	}
	
}
