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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * A thread for starting jobs. When to start which jobs is configured
 * using a {@link JobConfig} instance or a collection of {@link JobConfigEntry}s.
 * This class may be used with a {@link ConfigModule} framework or without.
 *
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class JobExecuter extends Thread
{
	/**
	 * The logger for this class.
	 */
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(JobExecuter.class);

	/**
	 * Instance counter for job executers.
	 */
	private static int instances = 0;

	/**
	 * The jobs list.
	 */
	private Collection<JobExecuterEntry> jobs = null;

	/**
	 * The pre-calculated next execution times for jobs.
	 */
	private Map<JobExecuterEntry, Date> nextExecutions = null;

	/**
	 * Mutex for {@link #runningInstances}.
	 */
	private Object runningInstancesMutex = new Object();

	/**
	 * Which job instances are running right now.
	 */
	private Map<JobExecuterEntry, Collection<JobRunner>> runningInstances = null;

	/**
	 * The job thread pool.
	 */
	private ExecutorService threadPool;


	/**
	 * Get the name for this job executer thread using the {@link #instances} counter.
	 * @return The name for this thread.
	 */
	private static synchronized String getThreadName()
	{
		String name = "job-executer";
		if(instances > 0)
			name += "-" + instances;
		instances++;
		return name;
	}

	/**
	 * Create a job executer by a collection of {@link JobConfigEntry}.
	 * @param configEntries The config entries
	 * @return The newly created job executer
	 */
	public static JobExecuter createByConfigEntries(Collection<JobConfigEntry> configEntries)
	{
		Collection<JobExecuterEntry> myJobs = new HashSet<JobExecuterEntry>(configEntries.size());
		for (JobConfigEntry configEntry : configEntries) {
			try {
				myJobs.add(new JobExecuterEntry(configEntry));
			} catch (TimePatternFormatException e) {
				log.error("Creating time pattern failed for job "+configEntry.getClassName()+". Ignoring job.", e);
			} catch (ClassNotFoundException e) {
				log.error("Getting job class failed for job "+configEntry.getClassName()+". Ignoring job.", e);
			}
		}
		return new JobExecuter(myJobs);
	}

	/**
	 * Create a new JobExecuter with the given jobs.
	 * @param jobs The jobs to use.
	 */
	public JobExecuter(Collection<JobExecuterEntry> jobs)
	{
		super(getThreadName());
		setDaemon(true);
		if(jobs == null)
			throw new NullPointerException("jobs");
		this.jobs = jobs;
		log.debug("Have "+jobs.size()+" jobs");
	}

	/**
	 * Get the thread pool. If no thread pool is set,
	 * a new cached thread pool will be created.
	 * @return The thread pool.
	 */
	public ExecutorService getThreadPool()
	{
		if(threadPool == null)
			threadPool = Executors.newCachedThreadPool();
		return threadPool;
	}

	/**
	 * Set a thread pool. This thread pool will be used
	 * for creating new job threads. Setting a thread pool is optional.
	 * @see #getThreadPool()
	 * @param threadPool The thread pool to set.
	 */
	public void setThreadPool(ExecutorService threadPool)
	{
		this.threadPool = threadPool;
	}

	/**
	 * Calculate the fist execution time for jobs. Do nothing
	 * if execution times are already calculated.
	 */
	private synchronized void calculateFirstExecutions()
	{
		if(nextExecutions != null)
			return;
		nextExecutions = new HashMap<JobExecuterEntry, Date>(jobs.size());

		for (JobExecuterEntry entry : jobs) {
			Date nextMatchTime = entry.getTimePatternSet().getNextMatchTime((Date) null);
			if(log.isDebugEnabled()) log.debug("First execution of job "+entry.getJobClass().getName()+": "+nextMatchTime);
			if(nextMatchTime != null)
				nextExecutions.put(entry, nextMatchTime);
		}
	}

	/**
	 * Calculate the next execution time for executed jobs
	 * @param executedJobs the jobs for which to claculate the next execution time.
	 */
	private synchronized void calculateNextExecutions(Collection<Map.Entry<JobExecuterEntry, Date>> executedJobs)
	{
		if(log.isDebugEnabled()) log.debug("Calculating next job executions for "+executedJobs.size()+" jobs...");
		for (Map.Entry<JobExecuterEntry, Date> entry : executedJobs) {
			Class<Job> job = entry.getKey().getJobClass();
			TimePatternSet tps = entry.getKey().getTimePatternSet();
			Date lastExecution = entry.getValue();
			if(log.isDebugEnabled()) {
				if(lastExecution == null)
					log.debug("Job "+job.getName()+" was never executed before");
				else
					log.debug("Job "+job.getName()+" last execution was: "+lastExecution);
			}
			Date nextMatchTime = tps.getNextMatchTime(lastExecution);
			if(log.isDebugEnabled()) log.debug("Next execution of job "+job.getName()+": "+nextMatchTime);
			if(nextMatchTime != null) {
				if(nextMatchTime.equals(lastExecution)) {
					log.error("****************************");
					log.error("THIS SHOULD NEVER HAPPEN!");
					log.error("Next execution time equals last execution time: "+nextMatchTime);
					log.error("Job "+job.getName()+" will not be executed anymore!");
					log.error("****************************");
				} else
					nextExecutions.put(entry.getKey(), nextMatchTime);
			}
		}
		if(log.isDebugEnabled()) log.debug("Calculating next job executions done.");
	}

	private volatile boolean forceInterrupt = false;

	@Override
	public void interrupt() {
		this.forceInterrupt = true;
		super.interrupt();
	}

	@Override
	public boolean isInterrupted() {
		return super.isInterrupted() || forceInterrupt;
	}

	/**
	 * Shuts down this {@link JobExecuter}. If <code>waitTimeoutMillis</code> has been specified &gt; 0, this method blocks
	 * until all tasks have completed execution, or the timeout occurs, or the current thread is interrupted, whichever happens first.
	 * If <code>waitTimeoutMillis == 0</code>, this method blocks forever or the current thread is interrupted.
	 * If <code>waitTimeoutMillis == -1</code>, this method returns immediately.
	 *
	 * @param waitTimeoutMillis Whether and how long to wait for the <code>JobExecuter</code> to shut down. A value of 0 means to wait forever,
	 *		a value of -1 means to return immediately (no wait) and a value &gt; 0 means to wait this time in milliseconds.
	 * @return <code>true</code> if this executor terminated and <code>false</code> if the timeout elapsed before termination
	 *		(i.e. either this <code>JobExecuter</code>-thread is still alive or the internal thread-pool was not yet shut down).
	 *		In this case, you might want to call this <code>shutdown</code> method again.
	 * @throws InterruptedException if the current thread was interrupted while waiting for the shutdown to be completed.
	 */
	public boolean shutdown(long waitTimeoutMillis)
	throws InterruptedException
	{
		boolean result = false;

		if (waitTimeoutMillis < -1)
			throw new IllegalArgumentException("waitTimeout < -1");

		if (waitTimeoutMillis == 0)
			waitTimeoutMillis = Long.MAX_VALUE;

		interrupt();
		getThreadPool().shutdown();

		if (waitTimeoutMillis > 0) {
			long shutdownStart = System.currentTimeMillis();

			// first wait for the thread-pool
			result = getThreadPool().awaitTermination(waitTimeoutMillis, TimeUnit.MILLISECONDS);

			// now wait for the JobExecuter itself
			if (result) {
				long remainingTimeout = waitTimeoutMillis - (System.currentTimeMillis() - shutdownStart);
				if (waitTimeoutMillis == Long.MAX_VALUE)
					remainingTimeout = waitTimeoutMillis;

				if (remainingTimeout > 0)
					join(remainingTimeout);

				result = !isAlive();
			}
		}

		return result;
	}


	public static void main(String[] args)
	{
		Thread t = new Thread() {
			@Override
			public void run() {
				long lastScheduledRunTimestamp = (System.currentTimeMillis() / 1000 / 60) * 60000;
				while(!isInterrupted()) {

					long nextScheduledRunTimestamp = lastScheduledRunTimestamp + 60000;

					long sleep;
					while ((sleep = nextScheduledRunTimestamp - System.currentTimeMillis()) > 0){
						try {
							System.out.println(new Date().toString() + " - Going to sleep for " + sleep + " msec.");
							Thread.sleep(sleep);
						} catch (InterruptedException e1) {
							if (isInterrupted())
								return;
						}
					}

					lastScheduledRunTimestamp = (System.currentTimeMillis() / 1000 / 60) * 60000;



					System.out.println(new Date().toString() + " - Simulating: Executing jobs.");

					try {
						long testSleep = (long) (Math.random() * 150000);
						System.out.println(new Date().toString() + " - Simulating: Sleeping " + testSleep + " msec.");
						sleep(testSleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		t.setDaemon(true);
		t.start();

		try {
			t.join(600000);
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}


	/**
	 * Start the job executer. The first execution times will
	 * be calculated for all configured jobs. Whenever the execution
	 * time for a job has come, it will be executed if its maximum
	 * concurrent execution count is not yet reached.
	 */
	@Override
	public void run()
	{
		calculateFirstExecutions();

		// In order to run the jobs always at the full minute (i.e. when the seconds are 00),
		// we calculate the last scheduled run timestamp, i.e. the timestamp when it should have
		// been run the last time and the next time it should run.
		long lastScheduledRunTimestamp = (System.currentTimeMillis() / 1000 / 60) * 60000;
		while(!isInterrupted()) {
			try {

				// Calculate, when it should run the next time (i.e. 1 minute later than the last run)
				long nextScheduledRunTimestamp = lastScheduledRunTimestamp + 60000;

				long sleep;
				while ((sleep = nextScheduledRunTimestamp - System.currentTimeMillis()) > 0){
					try {
						System.out.println(new Date().toString() + " - Going to sleep for " + sleep + " msec.");
						Thread.sleep(sleep);
					} catch (InterruptedException e1) {
						if (isInterrupted())
							return;
					}
				}

				// Update the last scheduled run - which is the second 00 of the current minute.
				lastScheduledRunTimestamp = (System.currentTimeMillis() / 1000 / 60) * 60000;

				if (isInterrupted())
					return;

				Date now = new Date();
				Collection<Map.Entry<JobExecuterEntry, Date>> jobsToExecute = new HashSet<Map.Entry<JobExecuterEntry,Date>>();

				for (Map.Entry<JobExecuterEntry, Date> entry : nextExecutions.entrySet())
					if(entry.getValue().compareTo(now) <= 0)
						jobsToExecute.add(entry);

				if(jobsToExecute.size() > 0) {
					if(log.isDebugEnabled()) log.debug("Have "+jobsToExecute.size()+" job(s) to execute...");

					for (Map.Entry<JobExecuterEntry, Date> entry : jobsToExecute) {
						JobExecuterEntry jobEntry = entry.getKey();
						int instanceCount = getJobRunnerInstanceCount(jobEntry);
						if(instanceCount >= jobEntry.getMaxConcurrentExecutions()) {
							log.info("Too many instances of job "+jobEntry.getJobClass().getName()+" are running. Job will not be started. Running instances: "+instanceCount+" - Max: "+jobEntry.getMaxConcurrentExecutions());
						} else {
							Class<Job> jobClass = jobEntry.getJobClass();
							try {
								if(log.isDebugEnabled()) log.debug("Starting job "+jobClass.getName()+"...");
								Job job = jobClass.newInstance();
								if(jobEntry.getJobConfig() != null)
									job.setConfig(jobEntry.getJobConfig());
								getThreadPool().execute(new JobRunner(job, jobEntry));
								if(log.isDebugEnabled()) log.debug("Back from starting job "+jobClass.getName()+".");
							} catch(Throwable e) {
								log.error("Execution of job "+jobClass.getName()+" failed. Ignoring.", e);
							}
						}
					}

					calculateNextExecutions(jobsToExecute);
				}

			} catch (Throwable t) {
				log.error("run: " + t.getClass().getName() + ": " + t.getMessage(), t);
				// TODO notify registrable error handlers
			}
		}
	}

	/**
	 * Job runners notify the executer about beginning jobs by calling
	 * this method.
	 * @param jobRunner The notifying job runner
	 */
	private void jobRunnerBegin(JobRunner jobRunner)
	{
		synchronized(runningInstancesMutex) {
			if(runningInstances == null)
				runningInstances = new HashMap<JobExecuterEntry, Collection<JobRunner>>(1);
			if(!runningInstances.containsKey(jobRunner.entry))
				runningInstances.put(jobRunner.entry, new HashSet<JobRunner>());
			runningInstances.get(jobRunner.entry).add(jobRunner);
		}
	}

	/**
	 * Job runners notify the executer about done jobs by calling
	 * this method.
	 * @param jobRunner The notifying job runner
	 */
	private void jobRunnerDone(JobRunner jobRunner)
	{
		synchronized(runningInstancesMutex) {
			if(runningInstances == null)
				return;
			Collection<JobRunner> jobRunners = runningInstances.get(jobRunner.entry);
			if(jobRunners == null)
				return;
			jobRunners.remove(jobRunner);
			if(jobRunners.isEmpty())
				runningInstances.remove(jobRunner.entry);
			if(runningInstances.isEmpty())
				runningInstances = null;
		}
	}

	/**
	 * Get the number of running jobs.
	 * @param entry The job
	 * @return The number of running jobs.
	 */
	private int getJobRunnerInstanceCount(JobExecuterEntry entry)
	{
		synchronized(runningInstancesMutex) {
			if(runningInstances == null)
				return 0;
			Collection<JobRunner> jobRunners = runningInstances.get(entry);
			if(jobRunners == null)
				return 0;
			return jobRunners.size();
		}
	}

	/**
	 * A {@link Runnable} implementation for jobs.
	 * Job runners are invoked using the Java 5 concurrency
	 * mechanisms.
	 */
	private class JobRunner implements Runnable
	{
		/**
		 * The job to run.
		 */
		private final Job job;

		/**
		 * The jobs configuration.
		 */
		private final JobExecuterEntry entry;

		/**
		 * Create a new JobRunner.
		 * @param job The job to run
		 * @param entry The job's configuration
		 */
		public JobRunner(Job job, JobExecuterEntry entry)
		{
			this.job = job;
			this.entry = entry;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			jobRunnerBegin(this);
			try {
				job.start();
			} catch(Throwable e) {
				log.error("Unhandled exception in job", e);
			} finally {
				jobRunnerDone(this);
			}
		}
	}
}
