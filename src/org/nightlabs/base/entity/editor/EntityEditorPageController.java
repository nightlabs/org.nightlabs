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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.progress.CompoundProgressMonitor;
import org.nightlabs.base.util.RCPUtil;

/**
 * <p>This implementation of {@link IEntityEditorPageController} can be used
 * as controllers for entity editor pages registered by the "pageFactory" element.
 * This base class allows to schedule a job when the controller is created, so that when the
 * page is shown and needs to access the controller's data it might be
 * already (partially) loaded.</p>
 * 
 * <p>The loading job can be started at any time via {@link #startLoadJob()} 
 * or you can use the constructor {@link #EntityEditorPageController(boolean)}</p>
 * 
 * <p>If a thread needs to access this controllers data it should 
 * use the {@link #load(IProgressMonitor)} method instead of invoking 
 * {@link IEntityEditorPageController#doLoad(IProgressMonitor)} directly.</p> 
 * 
 * <p>{@link EntityEditorPageController} extends {@link PropertyChangeSupport} and will
 * pass the {@link EntityEditor} this controller was created with as source to
 * all property change listeners.</p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class EntityEditorPageController
extends PropertyChangeSupport
implements IEntityEditorPageController 
{

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EntityEditorPageController.class);
	
	/**	
	 * The editor controller this page controller is registered to.
	 * Used to put the loadJob in the editor controllers job pool.
	 */
	private EntityEditorController editorController;

	/**
	 * The actual background loading job.
	 * This calls {@link IEntityEditorPageController#doLoad(IProgressMonitor)} 
	 * and notifies the wrapping page controller
	 * of its end by the controller's {@link EntityEditorPageController#mutex}.
	 */
	public static class LoadJob extends Job {
		
		private EntityEditorPageController controller;
		private CompoundProgressMonitor cMonitor;
		
		public LoadJob(EntityEditorPageController controller) {
			super("Loading entities ...");
			this.controller = controller;
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			cMonitor = new CompoundProgressMonitor(monitor);
			try {
				controller.doLoad(cMonitor);
				controller.setLoaded(true);
			} catch (Throwable t) {
				// Workaround as we can not get a grip of exceptions within jobs
				controller.setLoadException(t);
				controller.setLoaded(false);
				return Status.CANCEL_STATUS; // TODO: Write error status
			}
			synchronized (controller.getMutex()) {
				controller.getMutex().notifyAll();
			}
			return Status.OK_STATUS;
		}
		
		public CompoundProgressMonitor getCompoundProgressMonitor() {
			return cMonitor;
		}
	}
	
	/**
	 * The page this controller is associated with
	 */
	private IFormPage page;
	
	/**
	 * Stores the loading status
	 */
	private boolean loaded = false;
	
	/**
	 * Mutex used to synchronize the background load with
	 * other threads that have to wait for its end.
	 */
	private Object mutex = new Object();
	
	/**
	 * The load job member. If null, its assumed that no background
	 * loading will be done by this controller.
	 */
	private LoadJob loadJob = null;
	
	/**
	 * The error that might have occured during background load.
	 */
	private Throwable loadException = null;
	/**
	 * Whether the load job is currently running
	 */
	private boolean loadJobRunning = false;
	/**
	 * Whether the load job is already finished
	 */
	private boolean loadJobDone = false;
	
	/**
	 * Create a new page controller that
	 * will not do background loading. 
	 */
	public EntityEditorPageController(EntityEditor editor) {
		this(editor, false);
	}
	
	/**
	 * Create a new {@link EntityEditorController} and
	 * decide whether to start background loading instantly.
	 * 
	 * @param startBackgroundLoading Whether to start the load job instantly.
	 */
	public EntityEditorPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor);
		if (startBackgroundLoading)
			startLoadJob();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.entity.editor.IEntityEditorPageController#setEntityEditorController(org.nightlabs.base.entity.editor.EntityEditorController)
	 */
	public void setEntityEditorController(EntityEditorController editorController) {
		this.editorController = editorController;
		if (loadJob != null)
			editorController.putLoadJob(this, loadJob);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.entity.editor.IEntityEditorPageController#setPage(org.eclipse.ui.forms.editor.IFormPage)
	 */
	public void setPage(IFormPage page) {
		this.page = page;
	}
	
	/**
	 * Returns the page associated with this controller.
	 * 
	 * @return The page associated with this controller.
	 * @deprecated There is a 1-n relation from one controller to many pages. That's why, this method makes no sense and will
	 *		be removed. Instead, there should be a method getPages() which returns all pages that are associated with this
	 *		controller. Furthermore, this method should be part of the interface! Marco.
	 */
	public IFormPage getPage() {
		return page;
	}

	/**
	 * Returns the editor controller this page controller is linked to. 
	 * @return Tthe editor controller this page controller is linked to.
	 */
	public EntityEditorController getEntityEditorController() {
		return editorController;
	}

	/**
	 * Check whether this controller already loaded its data.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Set the loading status.
	 */
	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	/**
	 * Set the loading error.
	 * @param loadException Loading error.
	 */
	protected void setLoadException(Throwable loadException) {
		this.loadException = loadException;
	}
	/**
	 * Return the mutex used to synchronize the background loading.
	 * @return The mutex used to synchronize the background loading.
	 */
	protected Object getMutex() {
		return mutex;
	}

	/**
	 * Starts the load job for this controller's
	 * data if not already done.
	 */
	public void startLoadJob() {
		if (loadJob == null) {
			loadJob = new LoadJob(this);
			loadJob.addJobChangeListener(new IJobChangeListener() {
				public void aboutToRun(IJobChangeEvent arg0) {
				}
				public void awake(IJobChangeEvent arg0) {
				}
				public void done(IJobChangeEvent arg0) {
					loadJobDone = true;
				}
				public void running(IJobChangeEvent arg0) {
					loadJobRunning = true;
				}
				public void scheduled(IJobChangeEvent arg0) {
				}
				public void sleeping(IJobChangeEvent arg0) {
				}
			});
			// when not called from constructor 
			if (editorController != null)
				editorController.putLoadJob(this, loadJob);
		}
	}

	/**
	 * Runnable that waits until the mutex is notified.
	 * Called from different scopes
	 */
	private IRunnableWithProgress waitForLoadJob = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if (!loaded)
				loadJob.getCompoundProgressMonitor().addProgressMonitor(monitor);
				synchronized (mutex) {					
					while (!loaded)
						mutex.wait(200);
				}
		}
	};
	
	/**
	 * <p>Ensures that this controller's {@link IEntityEditorPageController#doLoad(IProgressMonitor)}
	 * method has fully run and thus the controller is ready for use.</p> 
	 * 
	 * <p>If {@link #isLoaded()} returns true this method immeditiately returns.
	 * If not if will first check if the data already began to load in the 
	 * background. If so, it will 'join' the loading job and wait until its finished.</p>
	 * 
	 * <p>If the background job was not created or started yet, the data will
	 * be loaded on the current thread.
	 * <p>Note that invoking this form the gui thread will cause a blocking 
	 * progress dialog to appear.</p>
	 */
	public synchronized void load(IProgressMonitor monitor)
	{
		if (isLoaded())
			return;
		try {
			if (loadJob != null) {
				editorController.popLoadJob(this);
				if (loadJobRunning && !loadJobDone) {
					if (RCPUtil.isDisplayThread()) {
						ProgressMonitorDialog dlg = new ProgressMonitorDialog(null);
						dlg.run(true, false, waitForLoadJob);
					}
					else
						waitForLoadJob.run(monitor);
				}
			}
			else {
				if (RCPUtil.isDisplayThread()) {
					ProgressMonitorDialog dlg = new ProgressMonitorDialog(null);
					dlg.run(true, false, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException	{
							monitor.beginTask("Loading user data", IProgressMonitor.UNKNOWN);
							doLoad(monitor);
							monitor.done();
							setLoaded(true);
						}
					});
				}
				else {
					doLoad(monitor);
					setLoaded(true);
				}
			}
		} catch (InvocationTargetException e) {
			logger.error("Loading entity failed", e);
			throw new RuntimeException("Loading entity failed", e);
		} catch (InterruptedException e) {
			logger.error("Loading entity failed", e);
			throw new RuntimeException("Loading entity failed", e);
		}
	}
}
	
