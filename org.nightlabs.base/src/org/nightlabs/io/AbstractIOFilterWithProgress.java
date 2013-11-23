/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIOFilterWithProgress
extends AbstractIOFilter
implements IOFilterWithProgress
{
	/**
	 * the name of the property which is fired when the currentWork changes
	 */
	public static final String PROGRESS_CHANGED = "progress changed";
	
	/**
	 * the name of the property which is fired when a subTask finished
	 */
	public static final String SUBTASK_FINISHED = "SubTask finished";
	
	public AbstractIOFilterWithProgress() {
		super();
		pcs = new PropertyChangeSupport(this);
	}

	protected PropertyChangeSupport pcs = null;

	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}
	
	protected int currentWork = 0;
	
	/**
	 * Sets the currentWork.
	 * @param work the value to set
	 */
	protected void setCurrentWork(int work) {
		int oldWork = currentWork;
		currentWork = work;
		pcs.firePropertyChange(PROGRESS_CHANGED, oldWork, currentWork);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#getCurrentWork()
	 */
	public int getCurrentWork() {
		return currentWork;
	}
	
	protected int totalWork = 100;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#getTotalWork()
	 */
	public int getTotalWork() {
		return totalWork;
	}
		
	protected boolean hasSubTasks = false;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#hasSubTasks()
	 */
	public boolean hasSubTasks() {
		return hasSubTasks;
	}
	
	public void setHasSubTasks(boolean b) {
		hasSubTasks = b;
	}
	
	protected List<String> subTasks = new ArrayList<String>();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#getSubTasks()
	 */
	public List<String> getSubTasks() {
		return subTasks;
	}
		
	protected Map<String, Integer> subTask2Work = new HashMap<String, Integer>();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.io.IOFilterWithProgress#getSubTask2Work()
	 */
	public Map<String, Integer> getSubTask2Work() {
		return subTask2Work;
	}
	
	protected int totalSubTaskWork = 0;
	
	/**
	 * @param name the name of the subTask
	 * @param work the amount of work for the given subTask
	 */
	public void addSubTask(String name, int work)
	{
		if (hasSubTasks == false)
			hasSubTasks = true;
		
		totalSubTaskWork += work;
		if (totalSubTaskWork > totalWork)
			throw new IllegalArgumentException("Param work is too big, the addition of all subTasks is > then TotalWork!");
		
		subTasks.add(name);
		subTask2Work.put(name, Integer.valueOf(work));
	}
	
	/**
	 * processes the given subTask and adds it work to the currentWork
	 * @param name the name of the subTask
	 */
	public void processSubTask(String name)
	{
		int oldWork = currentWork;
		Integer subTaskWork = getSubTask2Work().get(name);
		if (subTaskWork != null) {
			int taskWork = subTaskWork.intValue();
			currentWork = taskWork;
			pcs.firePropertyChange(PROGRESS_CHANGED, oldWork, currentWork);
			pcs.firePropertyChange(SUBTASK_FINISHED, null, name);
		}
	}
	
	protected void flushSubTasks()
	{
		totalSubTaskWork = 0;
		hasSubTasks = false;
		subTask2Work = new HashMap<String, Integer>();
		subTasks = new ArrayList<String>();
	}
//	public void processSubTask(String name)
//	{
//		int oldWork = currentWork;
//		Integer subTaskWork = (Integer) getSubTask2Work().get(name);
//		if (subTaskWork != null) {
//			int taskWork = subTaskWork.intValue();
//			currentWork = taskWork;
//			pcs.firePropertyChange(PROGRESS_CHANGED, oldWork, currentWork);
//		}
//	}
	
}
