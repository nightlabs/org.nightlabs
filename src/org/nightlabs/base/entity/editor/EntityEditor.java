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

package org.nightlabs.base.entity.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.composite.Fadeable;
import org.nightlabs.base.editor.CommitableFormEditor;
import org.nightlabs.base.entity.EntityEditorRegistry;
import org.nightlabs.base.job.FadeableCompositeJob;

/**
 * An abstract base class for entity editors. It provides
 * the method {@link #addPages()} using the EntityEditorRegistry
 * to add pages registered via extension point.
 * 
 * Each {@link EntityEditor} will have a {@link EntityEditorController} that
 * holds one {@link IEntityEditorPageController} for each page it displays.
 * 
 * This means that combined with {@link IEntityEditorPageFactory}s that return
 * controllers this class can be used as is and registered as editor (of course with an unique id).
 * All work will be delegated to the pages (visible representation) and the
 * page controller (model loading and saving) that were created by the pageFactory. 
 * 
 * However {@link EntityEditor} can be subclassed to configure its appearance (title, tooltip). 
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityEditor extends CommitableFormEditor
{
	/**
	 * This editor's controller, that will delegate
	 * loading and saving of the enity to the 
	 * page controllers of the registered pages.
	 * 
	 */
	private EntityEditorController controller;
	
	public EntityEditor()
	{	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		try {
			List<EntityEditorPageSettings> pageSettings = EntityEditorRegistry.sharedInstance().getPageSettingsOrdered(getEditorID());
			for (EntityEditorPageSettings pageSetting : pageSettings) {
				IEntityEditorPageFactory factory = pageSetting.getPageFactory();
				IFormPage page = factory.createPage(this);
				controller.addPageController(page, factory);
				addPage(page);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the editor id.
	 * @return The editor id
	 */
	public String getEditorID()
	{
		return getEditorSite().getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
		// Save as not supported by entity editor 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * This implementation additionally creates this editor's controller.
	 */
	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		super.init(arg0, arg1);
		controller = new EntityEditorController(this);
	}

	private IRunnableWithProgress saveRunnable = new IRunnableWithProgress() {
		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			controller.doSave(monitor);
			Thread.sleep(1000);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					editorDirtyStateChanged();
				}
			});
		}
		
	};
	
	/**
	 * {@inheritDoc}
	 * 
	 * This implementation will start a job to save the
	 * editor. It will first let all pages commit and then 
	 * call its controllers doSave() method. This will
	 * cause all page controllers to save their model.
	 * If the active page appears to be {@link Fadeable} it will
	 * be faded until the save operation is finished.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (controller != null)
			controller.checkDirtyPageControllers();
		super.doSave(monitor);
		int active = getActivePage();
		Job saveJob = null;
		if (active >= 0) {
			IFormPage page = getFormPages()[active];
			if (page instanceof Fadeable)
				saveJob = new FadeableCompositeJob("Async save", ((Fadeable)page), this) {
					@Override
					public IStatus run(IProgressMonitor monitor, Object source) {
						try {
							saveRunnable.run(monitor);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return Status.OK_STATUS;
					}
			};
		}
		if (saveJob == null) {
			saveJob = new Job("Async save") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						saveRunnable.run(monitor);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return Status.OK_STATUS;
				}
			};
		}
		
		saveJob.schedule();
	}
	
	/**
	 * Return the controller assoctiated with this editor.
	 * The controller is created in {@link #init(IEditorSite, IEditorInput)}.
	 * 
	 * See {@link #getPageController(IFormPage)} on how to access a single page's controller.
	 * 
	 * @return The controller assoctiated with this editor.
	 */
	public EntityEditorController getController() {
		return controller;
	}
	
	/**
	 * Convenience method to obtain the page controller for the given page.
	 * 
	 * Note that page controllers should not be accessed from their associated
	 * pages in their constructor, as the controller registration
	 * will be initialized immediately after the page was created. 
	 * 
	 * @param page The page to search the page controller for.
	 * @return The page controller for the given page, or <code>null</code> if none found
	 */
	public IEntityEditorPageController getPageController(IFormPage page) {
		return getController().getPageController(page);
	}

	/**
	 * Returns a collection of all pageControllers associated with this'
	 * editor {@link EntityEditorController}.
	 * 
	 * @return All page controllers.
	 */
	public Collection<IEntityEditorPageController> getPageControllers() {
		return getController().getPageControllers().values();
	}
}
