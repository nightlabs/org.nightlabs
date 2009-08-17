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
package org.nightlabs.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

/**
 * Helper to wait for {@link Process}es started with {@link Runtime} and
 * to monitor their outputs. Create an {@link ObservedProcess} with a
 * {@link Process} obtained from Runtime similar to:
 * <pre>
 * ObservedProcess proc = new ObservedProcess(Runtime.getRuntime().exec(myCommand));
 * </pre>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ObservedProcess 
{
	private Executor executor;
	private Process proc;

	/**
	 * @author Marc Klinger - marc[at]nightlabs[dot]de
	 * This is only used since there are two implementations right now.
	 * It can be removed when the Writer implementation is removed.
	 */
	private static interface IStreamObserver extends Runnable
	{
		public boolean isDone();
	}
	
	/**
	 * Inner class used to observe a stream and redirect the output.
	 * @deprecated Use OutputStream implementation
	 */
	@Deprecated
	private static class StreamObserverWriter implements IStreamObserver, Runnable 
	{
		private InputStream is;
		private Writer output;
		private boolean done;

		/**
		 * Create a new StreamObserver. The invoker is responsible
		 * for closing the given stream/writer.
		 * @param is The stream to observer
		 * @param output The writer to redirect the stream output to
		 */
		public StreamObserverWriter(InputStream is, Writer output) 
		{
			this.is = is;
			this.output = output;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() 
		{
			done = false;
			try {
				BufferedInputStream bin = new BufferedInputStream(is);
				// read one character a time to keep line breaks
				int character = -1;
				try {
					while((character = bin.read()) >= 0)
						if(output != null)
							output.append((char)character);
				} catch (IOException e) {
					Logger.getLogger(StreamObserver.class).error("Error reading process output in StreamObserver: ", e);
				}
			} finally {
				done = true;
			}
		}
		
		public boolean isDone()
		{
			return done;
		}
	}

	/**
	 * Inner class used to observe a stream and redirect the output.
	 */
	private static class StreamObserver implements IStreamObserver, Runnable 
	{
		private InputStream is;
		private OutputStream output;
		private boolean done;

		/**
		 * Create a new StreamObserver. The invoker is responsible
		 * for closing the given stream/writer.
		 * @param is The stream to observer
		 * @param output The output stream to redirect the stream output to
		 */
		public StreamObserver(InputStream is, OutputStream output) 
		{
			this.is = is;
			this.output = output;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() 
		{
			done = false;
			try {
				try {
					IOUtil.transferStreamData(is, output);
				} catch (IOException e) {
					Logger.getLogger(StreamObserver.class).error("Error reading process output in StreamObserver: ", e);
				}
			} finally {
				done = true;
			}
		}
		
		public boolean isDone()
		{
			return done;
		}
	}	
	/**
	 * Create a new ObservedProcess. If you use many ObservedProcesses, consider
	 * using the {@link #ObservedProcess(Process, Executor)} constructor with a
	 * shared thread pool to improve performance.
	 * @param proc The process to observe
	 */
	public ObservedProcess(Process proc) 
	{
		this(proc, new Executor() {
			/* (non-Javadoc)
			 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
			 */
			@Override
			public void execute(Runnable command) {
				Thread t = new Thread(command);
				t.setPriority(Thread.NORM_PRIORITY);
				t.start();
			}
		});
	}

	/**
	 * Create a new ObservedProcess.
	 * @param proc The process to observe
	 * @param executor An executor to execute the observer threads.
	 * 		This may be a {@link java.util.concurrent.ThreadPoolExecutor} or similar.
	 */
	public ObservedProcess(Process proc, Executor executor) 
	{
		this.proc = proc;
		this.executor = executor;
	}
	
	/**
	 * Waits for the process this observer was created with.
	 * Additionally callers can pass {@link Writer} objects where
	 * the output end error-output of the running process will
	 * be written to.
	 * Return value is the exitValue of the running process.
	 * 
	 * @param output Writer for the process output.
	 * @param error Writer for the process error-output.
	 * @return The exitValue of the running process
	 * @throws InterruptedException When interrupted waiting for the actual process or the threads observing its output.
	 * @deprecated Use {@link #waitForProcess(OutputStream, OutputStream)} instead.
	 */
	@Deprecated
	public int waitForProcess(Writer output, Writer error)
	throws InterruptedException
	{
		return waitForProcess(
				new StreamObserverWriter(proc.getInputStream(), output), 
				new StreamObserverWriter(proc.getErrorStream(), error));
	}

	/**
	 * Waits for the process this observer was created with.
	 * Additionally callers can pass {@link OutputStream} objects where
	 * the output end error-output of the running process will
	 * be written to.
	 * Return value is the exitValue of the running process.
	 * 
	 * @param output OutputStream for the process output.
	 * @param error OutputStream for the process error-output.
	 * @return The exitValue of the running process
	 * @throws InterruptedException When interrupted waiting for the actual process or the threads observing its output.
	 */
	public int waitForProcess(OutputStream output, OutputStream error)
	throws InterruptedException
	{
		return waitForProcess(
				new StreamObserver(proc.getInputStream(), output), 
				new StreamObserver(proc.getErrorStream(), error));
	}
	
	/**
	 * Observer the process on two different threads. The given streams
	 * get filled with the data from stdout and stderr from the process.
	 * @param output OutputStream for the process output.
	 * @param error OutputStream for the process error-output.
	 */
	public void observeProcess(OutputStream output, OutputStream error)
	{
		observeProcess(
				new StreamObserver(proc.getInputStream(), output), 
				new StreamObserver(proc.getErrorStream(), error));
	}
	
	// method not needed anymore when IStreamObserver removed. Move code to stream method
	private void observeProcess(IStreamObserver outputObserver, IStreamObserver errorObserver)
	{
		executor.execute(outputObserver);
		executor.execute(errorObserver);
	}
	
	// method not needed anymore when IStreamObserver removed. Move code to stream method
	private int waitForProcess(IStreamObserver outputObserver, IStreamObserver errorObserver) throws InterruptedException
	{
		observeProcess(outputObserver, errorObserver);
		
		// wait for the process and check its exit status
		int exitVal = proc.waitFor();
		
		// now wait for the observer threads to finish
		while(!outputObserver.isDone() || !errorObserver.isDone())
			Thread.yield();
		
		// return the exit status
		return exitVal;		
	}

	/**
	 * Is the process running?
	 * @return <code>true</code> if the process is running - <code>false</code> otherwise.
	 */
	public boolean isRunning()
	{
		if(proc == null)
			return false;
		try {
			proc.exitValue();
			return false;
		} catch(IllegalThreadStateException e) {
			return true;
		}
	}
	
	/**
	 * Get the underlying process instance.
	 * @return The process instance
	 */
	public Process getProcess()
	{
		return proc;
	}
}
