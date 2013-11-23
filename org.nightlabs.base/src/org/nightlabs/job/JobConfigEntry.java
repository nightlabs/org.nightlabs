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
package org.nightlabs.job;

import java.io.Serializable;

/**
 * A job's configuration. This class is analogue to {@link JobExecuterEntry}.
 * The main difference is, that this class uses Strings representations instead
 * of real objects. To create a {@link JobExecuter} with JobConfigEntries use
 * the {@link JobExecuter#createByConfigEntries(java.util.Collection)} method.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class JobConfigEntry implements Serializable
{
	/**
	 * The serial version for this class.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The parent config module.
	 */
	JobConfig config;
	
	/**
	 * The job's class name.
	 */
	private String className;
	
	/**
	 * The time pattern String representation for this job.
	 */
	private String timePattern;
	
	/**
	 * How many instances of this job might be executed concurrently.
	 * A value if &lt;= 0 means an unlimited number of concurrent
	 * executions.
	 */
	private int maxConcurrentExecutions = 0;

	/**
	 * A configuration object used by the job.
	 */
	private Object jobConfig = null;
	
	/**
	 * Create a new empty Entry.
	 */
	public JobConfigEntry()
	{
	}
	
	/**
	 * Create a new Entry with no concurrent execution limitations.
	 * @param className the job's class
	 * @param timePattern the execution timepattern
	 */
	public JobConfigEntry(String className, String timePattern)
	{
		this(className, timePattern, 0);
	}
	
	/**
	 * Create a new Entry.
	 * @param className the job's class
	 * @param timePattern the execution timepattern
	 * @param maxConcurrentExecutions how many instances of this job
	 * 		might be executed concurrently.
	 */
	public JobConfigEntry(String className, String timePattern, int maxConcurrentExecutions)
	{
		this.className = className;
		this.timePattern = timePattern;
		this.maxConcurrentExecutions = maxConcurrentExecutions;
	}
	
	/**
	 * Get the maxConcurrentExecutions.
	 * @return the maxConcurrentExecutions
	 */
	public int getMaxConcurrentExecutions()
	{
		return maxConcurrentExecutions;
	}

	/**
	 * Set the maxConcurrentExecutions. A value &lt;=0 indicates
	 * no execution limits and is the default.
	 * @param maxConcurrentExecutions the maxConcurrentExecutions to set -
	 * 		a value &lt;=0 for no limits.
	 */
	public void setMaxConcurrentExecutions(int maxConcurrentExecutions)
	{
		this.maxConcurrentExecutions = maxConcurrentExecutions;
		if(config != null)
			config.setChanged();
	}

	/**
	 * Get the className.
	 * @return the className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Set the className.
	 * @param className the className to set
	 */
	public void setClassName(String className)
	{
		this.className = className;
		if(config != null)
			config.setChanged();
	}

	/**
	 * Get the timePattern.
	 * @return the timePattern
	 */
	public String getTimePattern()
	{
		return timePattern;
	}

	/**
	 * Set the timePattern.
	 * @param timePattern the timePattern to set
	 */
	public void setTimePattern(String timePattern)
	{
		this.timePattern = timePattern;
		if(config != null)
			config.setChanged();
	}

	/**
	 * Get the jobConfig.
	 * @return the jobConfig
	 */
	public Object getJobConfig()
	{
		return jobConfig;
	}

	/**
	 * Set the jobConfig.
	 * @param jobConfig the jobConfig to set
	 */
	public void setJobConfig(Object jobConfig)
	{
		this.jobConfig = jobConfig;
		if(config != null)
			config.setChanged();
	}
}