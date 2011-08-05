package org.nightlabs.util;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A benchmark &amp; performance testing tool. Use an instance of <code>Stopwatch</code> to find out
 * how long certain tasks take in your application and how many recursions appear with which depth. One instance of <code>Stopwatch</code> can be used to measure the times of multiple tasks and
 * {@link #createHumanReport(boolean) generate a single report} for all of them.
 * </p>
 * <p>
 * Instances of this class are throw-away objects that should be subject to garbage collection as soon
 * as they are not used anymore (hence you should think twice before keeping an instance in a static field!).
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class Stopwatch
{
	private static final Logger logger = LoggerFactory.getLogger(Stopwatch.class);

	private static class TaskRecursion
	{
		public TaskRecursion(Task task, int recursionLevel) {
			this.task = task;
			this.recursionLevel = recursionLevel;
		}

		private Task task;
		private int recursionLevel;
		private long accumulatedTime = 0;
		private long minTime = Long.MAX_VALUE;
		private long maxTime = Long.MIN_VALUE;
		private int invocationCount;

		public String getTaskIdentifier() {
			return task.getTaskIdentifier();
		}
		public Task getAccumulationSummary() {
			return task;
		}
		public int getRecursionLevel() {
			return recursionLevel;
		}
		public long getAccumulatedTime() {
			return accumulatedTime;
		}
		public long getMinTime() {
			return minTime;
		}
		public long getMaxTime() {
			return maxTime;
		}
		public int getInvocationCount() {
			return invocationCount;
		}

		private long startTimestamp = 0;

		public void start()
		{
			if (startTimestamp != 0)
				throw new IllegalStateException("start already called! taskIdentifier=" + getTaskIdentifier() + " recursionLevel=" + recursionLevel);

			startTimestamp = System.currentTimeMillis();
		}

		public void stop()
		{
			if (startTimestamp == 0)
				throw new IllegalStateException("start not called or stop already called! taskIdentifier=" + getTaskIdentifier() + " recursionLevel=" + recursionLevel);

			long duration = System.currentTimeMillis() - startTimestamp;
			startTimestamp = 0;
			++invocationCount;
			accumulatedTime += duration;
			minTime = Math.min(minTime, duration);
			maxTime = Math.max(maxTime, duration);
		}
	}

	private static class Task
	{
		public Task(String taskIdentifier) {
			this.taskIdentifier = taskIdentifier;
		}

		private Map<Integer, TaskRecursion> recursionLevel2accumulationSummaryRecursive = new TreeMap<Integer, TaskRecursion>();

		protected TaskRecursion createAccumulationSummaryRecursive(int recursionLevel)
		{
			TaskRecursion taskRecursion = recursionLevel2accumulationSummaryRecursive.get(recursionLevel);
			if (taskRecursion == null) {
				taskRecursion = new TaskRecursion(this, recursionLevel);
				recursionLevel2accumulationSummaryRecursive.put(recursionLevel, taskRecursion);
			}
			return taskRecursion;
		}

		private String taskIdentifier;
		private int invocationCount;
		private int recursionLevel = -1;

		public String getTaskIdentifier() {
			return taskIdentifier;
		}
		public int getInvocationCount() {
			return invocationCount;
		}

		public void start()
		{
			createAccumulationSummaryRecursive(++recursionLevel).start();
		}

		public void stop()
		{
			if (recursionLevel < 0)
				throw new IllegalStateException("recursionLevel < 0");

			recursionLevel2accumulationSummaryRecursive.get(recursionLevel--).stop();
			++invocationCount;
		}

		protected void appendToReport(StringBuilder sb)
		{
			for (TaskRecursion taskRecursion : recursionLevel2accumulationSummaryRecursive.values()) {
				sb.append("        ");
				sb.append(getIndentByRecursionLevel(taskRecursion.getRecursionLevel()));

				sb.append("recursionLevel=");
				sb.append(taskRecursion.getRecursionLevel());

				sb.append(" invocationCount=");
				sb.append(taskRecursion.getInvocationCount());

				sb.append(" accumulatedTime=");
				sb.append(taskRecursion.getAccumulatedTime());

				sb.append(" minTime=");
				sb.append(taskRecursion.getMinTime());

				sb.append(" maxTime=");
				sb.append(taskRecursion.getMaxTime());

				sb.append('\n');
			}
		}
	}

	private static String getIndentByRecursionLevel(int recursionLevel)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < recursionLevel; ++i)
			sb.append("  ");
		return sb.toString();
	}


	private Map<String, Task> taskIdentifier2task = new TreeMap<String, Task>();

	protected Task createTask(String taskIdentifier)
	{
		Task task = taskIdentifier2task.get(taskIdentifier);
		if (task == null) {
			task = new Task(taskIdentifier);
			taskIdentifier2task.put(taskIdentifier, task);
		}
		return task;
	}

	/**
	 * Mark the beginning of a task (imagine this as clicking the "start" button on your stopwatch for a certain task).
	 * Note, that there should exist a corresponding {@link #stop(String)} invocation!
	 * <p>
	 * The {@link Stopwatch} supports recursion. You can therefore call <code>start(String)</code> inside a method that calls
	 * itself (directly or indirectly) without any problem. You only have to ensure that there is a stop for each start.
	 * </p>
	 *
	 * @param taskIdentifier the identifier of the task. You must pass the same value to the corresponding {@link #stop(String)} invocation!
	 * @see #stop(String)
	 */
	public void start(String taskIdentifier)
	{
		try {
			createTask(taskIdentifier).start();
		} catch (Exception x) {
			taskIdentifier2task.remove(taskIdentifier); // prevent corrupt data by removing it.
			logger.warn("start(taskIdentifier='" + taskIdentifier + "'): " + x, x);
			return;
		}
	}

	/**
	 * Mark the end of a task (imagine this as clicking the "stop" button on your stopwatch for a certain task).
	 * Note, that there should exist one <code>stop(String)</code> invocation for each {@link #start(String)}!
	 *
	 * @param taskIdentifier the identifier of the task. You must pass the same value to the corresponding {@link #start(String)} invocation!
	 * @see #start(String)
	 */
	public void stop(String taskIdentifier)
	{
		Task task = taskIdentifier2task.get(taskIdentifier);
		if (task == null) {
			Exception x = new IllegalArgumentException("There is no task with the taskIdentifier \"" + taskIdentifier + "\"! You must call start(...) before stop(...) with the same taskIdentifier!");
			logger.warn("stop(taskIdentifier='" + taskIdentifier + "'): " + x, x);
			return;
		}

		try {
			task.stop();
		} catch (Exception x) {
			taskIdentifier2task.remove(taskIdentifier); // prevent corrupt data by removing it.
			logger.warn("stop(taskIdentifier='" + taskIdentifier + "'): " + x, x);
			return;
		}
	}

	/**
	 * Clear all data that was collected so far.
	 */
	public void clear()
	{
		taskIdentifier2task.clear();
	}

	/**
	 * Generate a report in human-readable form.
	 *
	 * @param clear whether to call {@link #clear()} afterwards to forget all data (and allow for garbage collection).
	 * @return a human-readable report of the data gathered before.
	 */
	public String createHumanReport(boolean clear)
	{
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for (Task task : taskIdentifier2task.values()) {
			sb.append("    ");
			sb.append(task.getTaskIdentifier());
			sb.append(" invocationCount=");
			sb.append(task.getInvocationCount());
			sb.append('\n');
			task.appendToReport(sb);
		}

		if (clear)
			clear();

		return sb.toString();
	}
}
