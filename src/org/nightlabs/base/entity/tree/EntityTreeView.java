/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.entity.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.entity.EntityEditorRegistry;
import org.nightlabs.base.part.ControllablePart;
import org.nightlabs.base.part.PartController;
import org.nightlabs.base.tree.TreeContentProvider;

/**
 * The tree view for entity tree categories and their
 * entity elements. This view displays all categories
 * registered via extension point and opens the registered
 * editor for them upon user request.
 * 
 * This class is intended to be used in two ways.
 * <ol>
 * <li>Register this class as is as a View. Then it will display all categories registered
 * to the view-id you have assigned to it, but will not be controlled (see {@link #getPartController()}, {@link PartController})</li>
 * <li>Create a subclass and override {@link #getPartController()} and {@link #canDisplayPart()} in order to have the new View
 * correctly controlled by a {@link PartController}. Then proceed as above and register the new class as view.</li>
 * </ol>
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber - <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityTreeView 
extends ViewPart 
implements IEntityTreeCategoryChangeListener, IOpenListener, ControllablePart
{

	/**
	 * The tree viewer in this view
	 */
	private TreeViewer treeViewer;

	/**
	 * This map is used to find out wich tree object belongs 
	 * to wich category for asking the category for labels
	 * and icons.
	 */
	private Map<Object, IEntityTreeCategory> childCategories;

	/**
	 * Default constructor registers this view to 
	 * the controller returned by {@link #getPartController()}
	 */
	public EntityTreeView()
	{
		PartController controller = getPartController();
		// Regsiter to the PartControler if available
		if (controller != null)
			controller.registerPart(this);
	}

	/**
	 * The default implementation of createPartControl first checks the
	 * {@link PartController} of this View by {@link #getPartController()}.
	 * If it is not null, work will be delegated to the controller, 
	 * otherwise the contents will be created directly by {@link #createPartContents(Composite)}.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		PartController controller = getPartController();
		if (controller != null)
			// Delegate this to the view-controller, to let him decide what to display
			controller.createPartControl(this, parent);
		else
			// Directly create contents
			createPartContents(parent);
	}

	/**
	 * This method is cunsulted in the constructor
	 * and creatPartControl, to delegate the work 
	 * a {@link PartController} if neccessary.
	 * The default implementation returns null, so
	 * the View will not be controlled. If you want
	 * it to be controlled override and return 
	 * the appropriate controller.
	 */
	protected PartController getPartController() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartContents(Composite parent)
	{
		treeViewer = new TreeViewer(parent, SWT.NONE);
		EntityTreeContentProvider entityTreeContentProvider = new EntityTreeContentProvider();
		treeViewer.setContentProvider(entityTreeContentProvider);
		EntityTreeLabelProvider entityTreeLabelProvider = new EntityTreeLabelProvider();
		treeViewer.setLabelProvider(entityTreeLabelProvider);
		IEntityTreeCategory[] categories = EntityEditorRegistry.sharedInstance().getCategories(getViewSite().getId());
		treeViewer.setInput(categories);
		for (IEntityTreeCategory category : categories) {
			//System.err.println("category: "+category.getName());
			category.addEntityTreeCategoryChangeListener(this);
		}
		treeViewer.addOpenListener(this);

		// needed with LSDView to fill - not needed stand alone
		treeViewer.getTree().setLayout(new GridLayout());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * The default implementation returns true.
	 * If you use this with a {@link PartController}, you have
	 * to override and return the appropriate value.
	 * @see #getPartController()
	 * @see org.nightlabs.base.part.ControllablePart#canDisplayPart()
	 */
	public boolean canDisplayPart()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * The content provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	class EntityTreeContentProvider extends TreeContentProvider 
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return (IEntityTreeCategory[])inputElement;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement)
		{
			//System.err.println("GET CHILDREN FOR "+parentElement);
			IEntityTreeCategory category = (IEntityTreeCategory)parentElement;
			Object[] children = category.getChildren();
			for (Object object : children)
				addChildCategory(object, category);
			return children;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element)
		{
			return element instanceof IEntityTreeCategory;
		}
	}

	/**
	 * The label provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	class EntityTreeLabelProvider extends LabelProvider 
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element)
		{
			if(element instanceof IEntityTreeCategory)
				return ((IEntityTreeCategory)element).getImage();
			else {
				IEntityTreeCategory category = getChildCategory(element);
				if(category != null)
					return category.getImage(element);
				else
					return super.getImage(element);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			if(element instanceof IEntityTreeCategory)
				return ((IEntityTreeCategory)element).getName();
			else {
				IEntityTreeCategory category = getChildCategory(element);
				if(category != null)
					return category.getText(element);
				else
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

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategoryChangeListener#categoryChanged(org.nightlabs.jfire.base.admin.entityeditor.EntityTreeCategory)
	 */
	public void categoryChanged(final EntityTreeCategory category)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh(category);
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
						if(treeViewer.getExpandedState(element))
							treeViewer.collapseToLevel(element, 1);
						else
							treeViewer.expandToLevel(element, 1);
					} else {
						IEntityTreeCategory category = getChildCategory(element);
						if(category != null) {
							IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(category.getEditorID());
							try {
								getSite().getPage().openEditor(category.getEditorInput(element), editorDescriptor.getId());
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		// remove listeners
		IEntityTreeCategory[] categories = EntityEditorRegistry.sharedInstance().getCategories(getViewSite().getId());
		for (IEntityTreeCategory category : categories)
			category.removeEntityTreeCategoryChangeListener(this);
		treeViewer.removeOpenListener(this);
		// clean childCategories
		childCategories = null;
		// call super
		super.dispose();
	}
}
