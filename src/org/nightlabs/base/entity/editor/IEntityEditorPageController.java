/**
 * 
 */
package org.nightlabs.base.entity.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * <p>A controller that will be associated to a page that is displayed
 * by an {@link EntityEditor}. Page controllers are created by
 * {@link IEntityEditorPageFactory}s registered by the "pageFactory" extension point.</p>
 * 
 * <p>The default implementation of {@link EntityEditor} will make use of an
 * {@link EntityEditorController} and delegate all work concerning
 * loading and saving of data to the page controllers (implementations of this interface).</p>
 * 
 * <p>Also some base classes of the entity-editor-framework with extra background-loading 
 * functionality use {@link IEntityEditorPageController}s to have a standardized access to
 * the data a page needs</p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageController {
	
	/**
	 * Set the page this controller is associated with.
	 * This will be called immediately after the controller is created.
	 * @param page the page this controller is associated with.
	 */
	public void setPage(IFormPage page);
	
	/**
	 * Set the {@link EntityEditorController} this page controller is
	 * registered to. This will also be called right after creation.
	 * 
	 * @param editorController the {@link EntityEditorController} this page controller is
	 * registered to.
	 */
	public void setEntityEditorController(EntityEditorController editorController);
	
	/**
	 * Load the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread.
	 * @param monitor The monitor to write status to.
	 */
	public void doLoad(IProgressMonitor monitor);
	
	/**
	 * Save the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread.
	 * @param monitor The monitor to write status to.
	 */
	public void doSave(IProgressMonitor monitor);
	
}
