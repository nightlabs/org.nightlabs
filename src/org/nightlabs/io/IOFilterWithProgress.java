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
import java.util.List;
import java.util.Map;

public interface IOFilterWithProgress
extends IOFilter
{
	/**
	 * Get the totalWork.
	 * @return the total work as int
	 */
  public int getTotalWork();
  
  /**
   * Get the current work done relativ to the totalWork.
   * @return the current work
   */
  public int getCurrentWork();
  
  /**
   * @param pcl a PropertyChangeListener which gets notified if the work changed
   */
  public void addPropertyChangeListener(PropertyChangeListener pcl);
  
  /**
   * removes a PropertyChangeListener
   * @param pcl a previously added PropertyChangeListener
   */
  public void removePropertyChangeListener(PropertyChangeListener pcl);
  
  /**
   * Does this IO filter have sub tasks?
   * @return <code>true</code> if the IO filter has sub tasks -
   * 		<code>false</code> otherwise
   */
  public boolean hasSubTasks();
  
  /**
   * Get a List of Strings which contain all Names of the sub tasks in the
   * right order of processing.
   * @return Al existing sub tasks
   */
  public List<String> getSubTasks();
  
  /**
   * Get a Map which contains all work (Integer) for each sub task (String).
   * key: SubTask Name (same as in the List {@link #getSubTasks()})
   * value: the Work (int) for the SubTask.
   * @return A map from work to sub task
   */
  public Map<String, Integer> getSubTask2Work();
}
