/**
 * 
 */
package org.nightlabs.base.action.registry.editor;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
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
	
	/**
	 * 
	 */
	public XEditorActionBarContributor() {
	}

	private AbstractActionRegistry actionRegistry;
	
	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			updateActions();
		}
	};
	
	protected AbstractActionRegistry getActionRegistry() {
		if (getActiveEditor() == null)
			throw new IllegalStateException("Can not obtain actionRegistry, activeEditor == null!");
		if (actionRegistry == null) 
			actionRegistry = EditorActionBarContributorRegistry.sharedInstance().getActionRegistry(getActiveEditor().getEditorSite().getId());
		return actionRegistry;
	}
	
	protected void updateActions() {
		for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
			if (actionDescriptor.getAction() instanceof IUpdateAction) {
				actionDescriptor.getAction().setEnabled(((IUpdateAction)actionDescriptor.getAction()).calculateEnabled());
				actionDescriptor.setVisible(((IUpdateAction)actionDescriptor.getAction()).calculateVisible());
			}
		}
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		if (activeEditor != null)
			activeEditor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionChangedListener);
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
		activeEditor.getSite().getSelectionProvider().addSelectionChangedListener(selectionChangedListener);
		
	}
	
	public IEditorPart getActiveEditor() {
		return activeEditor;
	}
	
	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		updateActions();
		getActionRegistry().contributeToCoolBar(coolBarManager);
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		updateActions();
		getActionRegistry().contributeToToolBar(toolBarManager);
	}
	
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		updateActions();
		getActionRegistry().contributeToMenuBar(menuManager);
	}
	
	@Override
	public void dispose() {
		if (activeEditor != null)
			activeEditor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionChangedListener);
		super.dispose();
	}
	
}
