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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * <p>Controller used by {@link EntityEditor} to hold the
 * {@link IEntityEditorPageController} for the displayed pages.</p>
 * 
 * <p>In its default implementation an {@link EntityEditor} will call
 * its controller's {@link #doSave(IProgressMonitor)} method
 * when it needs to be saved. The controller will then delegate to all
 * it's registered {@link IEntityEditorPageController}s.</p>
 * 
 * <p>Addtitionally {@link EntityEditorController} provides a pool of Jobs
 * that will be scheduled by the controller taking care that not more than 
 * a configurable number of jobs runs simultanously.</p> 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityEditorController
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EntityEditorController.class);

	/**
	 * The editor this controller is linked to.
	 */
	private EntityEditor editor;
	
	/**
	 * Map to hold the page controllers for the pages of this controller
	 * editor. All work of this controller (load save get) will be delegated
	 * to the page controllers. The key of the controllers is the pageID of
	 * the pageFactory registered by extension-point. 
	 */
	private Map<String, IEntityEditorPageController> pageControllers = new HashMap<String, IEntityEditorPageController>();
	/**
	 * Reverse lookup map for {@link #pageControllers}.
	 */
	private Map<IEntityEditorPageController, IFormPage> controllerPages = new HashMap<IEntityEditorPageController, IFormPage>();
	
	/**
	 * Currently the maximum number of simultan jobs is configured with this constant.
	 * The current value is 3.
	 */
	public static final int MAX_RUNNING_JOB_COUNT = 3;
	
	/**
	 * Map of one job per page controller that could be scheduled in background. 
	 * The number of allowed simulatanously running jobs can be configured by {@link #MAX_RUNNING_JOB_COUNT}.
	 * The jobs will be sorted by the index of the IFormPage associated to the page controller
	 * given as key.
	 */
	private Map<IEntityEditorPageController, Job> loadJobPool = new TreeMap<IEntityEditorPageController, Job>(
			new Comparator<IEntityEditorPageController>() {
				public int compare(IEntityEditorPageController o1, IEntityEditorPageController o2) {
					IFormPage page1 = getPage(o1);
					IFormPage page2 = getPage(o2);
					if (page1 == null || page2 == null)
						return 0;
					return new Integer(page1.getIndex()).compareTo(page2.getIndex());
				}
			}
		);
	
	/**
	 * Set of jobs that were in the pool and already scheduled.
	 */
	private Set<Job> processedJobs = new HashSet<Job>();
	/**
	 * Counter for scheduled and unfinished (un-done()) jobs.
	 */
	private int runningJobs = 0;
	
	/**
	 * Create an instance of this controller for
	 * an {@link EntityEditor} and load the data.
	 */
	public EntityEditorController(EntityEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * Add a new page controller to this editor controller.
	 * The controller can later be referenced by the given page.
	 * If the given pageFactory does not return a valid page controller
	 * nothing will be done.
	 * 
	 * @param page The page the controller should be created for.
	 * @param pageFactory The factory the controller for the page should be created from.
	 */
	public void addPageController(IFormPage page, IEntityEditorPageFactory pageFactory) {
		IEntityEditorPageController pageController = pageFactory.createPageController(editor);
		if (pageController == null)
			return;
		pageController.setPage(page);
		pageController.setEntityEditorController(this);
		pageControllers.put(page.getId(), pageController);
		controllerPages.put(pageController, page);
	}
	
	/**
	 * Returns the page controller registered to the given page.
	 * If no page controller was linked or none could be found <code>null</code>
	 * will be returned.
	 * 
	 * @param page The page a controller should be searched for.
	 * @return The page controller registered to the given page or <code>null</code> if none found.
	 */
	public IEntityEditorPageController getPageController(IFormPage page) {
		return pageControllers.get(page.getId());
	}
	
	/**
	 * Return the page the given controller is linked to.
	 * 
	 * @param pageController The page controller to search a page for.
	 * @return The page the given controller is linked to.
	 */
	protected IFormPage getPage(IEntityEditorPageController pageController) {
		return controllerPages.get(pageController);
	}
	

	/**
	 * Adds a new job to the pool, that might be scheduled instantly or 
	 * some time later, depending on the numer of already running jobs.
	 * 
	 * This method synchronized the job pool so other modifications
	 * must wait.
	 * 
	 * @param pageController The pageController the job to add is linked to.
	 * @param loadJob The job to add.
	 */
	public void putLoadJob(IEntityEditorPageController pageController, Job loadJob) {
		synchronized (loadJobPool) {
			loadJobPool.put(pageController, loadJob);		
			loadJob.addJobChangeListener(loadJobChangeListener);
			runPossibleJobs();
		}
	}
	
	/**
	 * Remove and return the job linked to the given page controller from
	 * the pool. Note that the job returned might be running (or finished) already.
	 * If the job is currently running (checked by the isAlive() method of its thread)
	 * the counter of running jobs will be decremented and 
	 * 
	 * @param pageController
	 * @return
	 */
	public Job popLoadJob(IEntityEditorPageController pageController) {
		synchronized (loadJobPool) {
			Job loadJob = loadJobPool.get(pageController);
			if (loadJob != null) {
				loadJobPool.remove(pageController);
				loadJob.removeJobChangeListener(loadJobChangeListener);				
				if (loadJob.getThread() != null && loadJob.getThread().isAlive()) {
					runningJobs--;
					runPossibleJobs();
				}
			}
			return loadJob;
		}
	}

	/**
	 * Schedules as many jobs that were not already scheduled
	 * and allowed according to the setting of maximal simultanous jobs.  
	 */
	private void runPossibleJobs() {
		synchronized (loadJobPool) {
			for (Job loadJob : loadJobPool.values()) {
				if (runningJobs >= MAX_RUNNING_JOB_COUNT)
					return;
				if (processedJobs.contains(loadJob))
					continue;
				loadJob.schedule();
				runningJobs++;
				processedJobs.add(loadJob);
			}
		}
	}
	
	/**
	 * Job listener to track finished jobs. 
	 */
	private IJobChangeListener loadJobChangeListener = new IJobChangeListener() {
		public void aboutToRun(IJobChangeEvent event) {
		}
		public void awake(IJobChangeEvent event) {
		}
		public void done(IJobChangeEvent event) {
			runningJobs--;
			runPossibleJobs();
		}
		public void running(IJobChangeEvent event) {
		}
		public void scheduled(IJobChangeEvent event) {
		}
		public void sleeping(IJobChangeEvent event) {
		}
	};
	
	/**
	 * Does nothing at the moment.
	 * @param monitor The progress monitor to use.
	 */
	public void doLoad(IProgressMonitor monitor)
	{
	}

	/**
	 * Delegates to the {@link IEntityEditorPageController#doLoad(IProgressMonitor)}
	 * method of all known {@link IEntityEditorPageController}s.
	 * 
	 * @param monitor The progress monitor to use.
	 */
	public void doSave(IProgressMonitor monitor)
	{
		logger.debug("Calling all page controllers doSave() method."); 
		for (IEntityEditorPageController controller : pageControllers.values()) {
			controller.doSave(monitor);
		}
	}

}
