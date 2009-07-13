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

import java.util.StringTokenizer;

import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;
import org.nightlabs.timepattern.TimePatternSetImpl;

/**
 * Configuration for jobs used by the {@link JobExecuter} class.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class JobExecuterEntry
{
	private final Class<Job> jobClass;
	private final TimePatternSet timePatternSet;
	private final int maxConcurrentExecutions;
	
	/**
	 * A configuration object used by the job.
	 */
	private Object jobConfig = null;
	
	/**
	 * Create a new JobExecuterEntry.
	 * @param jobClass The job's class
	 * @param timePatternSet When to execute the job
	 * @param maxConcurrentExecutions how many concurrent executions are allowed.
	 * 		A value of &lt;= 0 means no limit.
	 */
	public JobExecuterEntry(Class<Job> jobClass, TimePatternSet timePatternSet, int maxConcurrentExecutions, Object jobConfig)
	{
		super();
		if(jobClass == null)
			throw new NullPointerException("jobClass");
		this.jobClass = jobClass;
		if(timePatternSet == null)
			throw new NullPointerException("timePatternSet");
		this.timePatternSet = timePatternSet;
		this.maxConcurrentExecutions = maxConcurrentExecutions;
		this.jobConfig = jobConfig;
	}
	
	/**
	 * Create a new JobExecuterEntry from a {@link JobConfigEntry}.
	 * @param configEntry The job config entry
	 * @throws ClassNotFoundException If the class configured in the job config entry could not be found.
	 * @throws TimePatternFormatException If the time pattern configured in the job config entry is invalid
	 */
	public JobExecuterEntry(JobConfigEntry configEntry) throws ClassNotFoundException, TimePatternFormatException
	{
		this(
				getJobClass(configEntry.getClassName()),
				getTimePatternSet(configEntry.getTimePattern()),
				configEntry.getMaxConcurrentExecutions(),
				configEntry.getJobConfig());
	}

	/**
	 * Create a time pattern set from a time pattern string representation.
	 * @param pattern The time pattern string representation.
	 * @return The newly create time pattern set
	 * @throws TimePatternFormatException If the time pattern format is invalid.
	 */
	private static TimePatternSet getTimePatternSet(String pattern) throws TimePatternFormatException
	{
		StringTokenizer st = new StringTokenizer(pattern);
		if(st.countTokens() < 6)
			throw new TimePatternFormatException("Invalid time pattern: Need 6 fields");
		TimePatternSet timePatternSet = new TimePatternSetImpl();
		timePatternSet.createTimePattern(
				st.nextToken(),
				st.nextToken(),
				st.nextToken(),
				st.nextToken(),
				st.nextToken(),
				st.nextToken());
		return timePatternSet;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<Job> getJobClass(String className) throws ClassNotFoundException
	{
		return (Class<Job>)Class.forName(className);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof JobExecuterEntry))
			return false;
		JobExecuterEntry other = (JobExecuterEntry)obj;
		return
				jobClass.equals(other.jobClass) &&
				timePatternSet.equals(other.timePatternSet) &&
				maxConcurrentExecutions == other.maxConcurrentExecutions;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return
				jobClass.hashCode() ^ timePatternSet.hashCode() ^ maxConcurrentExecutions;
	}
	
	/**
	 * Get the job class.
	 * @return the job class
	 */
	protected Class<Job> getJobClass()
	{
		return jobClass;
	}
	
	/**
	 * Get the maxConcurrentExecutions. A value of &lt;= 0 represents
	 * unlimited concurrent execution.
	 * @return the maxConcurrentExecutions
	 */
	protected int getMaxConcurrentExecutions()
	{
		return maxConcurrentExecutions;
	}
	
	/**
	 * Get the timePatternSet.
	 * @return the timePatternSet
	 */
	protected TimePatternSet getTimePatternSet()
	{
		return timePatternSet;
	}

	/**
	 * Get the jobConfig.
	 * @return the jobConfig
	 */
	protected Object getJobConfig()
	{
		return jobConfig;
	}
}