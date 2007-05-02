/**
 * 
 */
package org.nightlabs.base.action.registry.editor;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.base.action.ISelectionAction;
import org.nightlabs.base.action.IUpdateAction;
import org.nightlabs.base.action.IWorkbenchPartAction;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.action.registry.ActionDescriptor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class XEditorActionBarContributor extends EditorActionBarContributor {

	private IEditorPart activeEditor;
	private AbstractActionRegistry actionRegistry;
	
	public XEditorActionBarContributor() {
	}
	
//	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
//		public void selectionChanged(SelectionChangedEvent event) 
//		{
//			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
//				if (actionDescriptor.getAction() instanceof ISelectionAction) {
//					ISelectionAction selectionAction = (ISelectionAction) actionDescriptor.getAction();
//					selectionAction.setSelection(event.getSelection());
//				}
//			}
//			updateActions();
//		}
//	};

	private ISelectionListener selectionListener = new ISelectionListener(){	
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part.equals(activeEditor)) {
				for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
					if (actionDescriptor.getAction() instanceof ISelectionAction) {
						ISelectionAction selectionAction = (ISelectionAction) actionDescriptor.getAction();
						selectionAction.setSelection(selection);
					}
				}
				updateActions();				
			}
		}	
	};
	
//	protected AbstractActionRegistry getActionRegistry() {
//		if (getActiveEditor() == null)
//			throw new IllegalStateException("Can not obtain actionRegistry, activeEditor == null!");
//		if (actionRegistry == null) 
//			actionRegistry = EditorActionBarContributorRegistry.sharedInstance().getActionRegistry(getActiveEditor().getEditorSite().getId());
//		return actionRegistry;
//	}

	protected AbstractActionRegistry getActionRegistry() {
		if (actionRegistry == null && getActiveEditor() != null) 
			actionRegistry = EditorActionBarContributorRegistry.sharedInstance().getActionRegistry(getActiveEditor().getEditorSite().getId());
		return actionRegistry;
	}
	
	protected void updateActions() 
	{
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof IUpdateAction) {
					actionDescriptor.getAction().setEnabled(((IUpdateAction)actionDescriptor.getAction()).calculateEnabled());
//					actionDescriptor.setVisible(((IUpdateAction)actionDescriptor.getAction()).calculateVisible());
				}
			}			
		}
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		if (activeEditor != null)
//			activeEditor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionChangedListener);
			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
		
		if (targetEditor == null) {
			activeEditor = null;
			return;
		}
		
		super.setActiveEditor(targetEditor);
		this.activeEditor = targetEditor;
		for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
			if (actionDescriptor.getAction() instanceof IWorkbenchPartAction) {
				((IWorkbenchPartAction)actionDescriptor.getAction()).setActivePart(targetEditor);
			}
		}		
//		activeEditor.getSite().getSelectionProvider().addSelectionChangedListener(selectionChangedListener);		
		activeEditor.getSite().getPage().addSelectionListener(selectionListener);
		
		if (contributedToToolBar)
			contributeToToolBar(getActionBars().getToolBarManager());
		
		if (contributedToMenu)
			contributeToMenu(getActionBars().getMenuManager());		
	}
	
	public IEditorPart getActiveEditor() {
		return activeEditor;
	}
	
//	private boolean contributedToCoolBar = false;
	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) 
	{
//		contributedToCoolBar = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToCoolBar(coolBarManager);
	}
	
	private boolean contributedToToolBar = false;
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) 
	{
		contributedToToolBar = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToToolBar(toolBarManager);
	}
	
	private boolean contributedToMenu = false;
	@Override
	public void contributeToMenu(IMenuManager menuManager) 
	{
		contributedToMenu = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToMenuBar(menuManager);
	}
	
	@Override
	public void dispose() {
		if (activeEditor != null)
//			activeEditor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionChangedListener);
			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
		super.dispose();
	}
	
}
