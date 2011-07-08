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
package org.nightlabs.concurrent;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class is used by the RWLocks. There exists exactly one instance
 * of RWLockMan which keeps track of which thread holds which locks and which
 * thread waits for wich locks. It allows to detect dead locks. Never use this
 * object directly! Use RWLock.
 *
 * @see RWLock
 *
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version 1.0
 */

public class RWLockMan
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(RWLockMan.class.getName());
	
  private static RWLockMan rwLockMan;
  public static synchronized RWLockMan getRWLockMan()
  {
    if (rwLockMan == null)
      rwLockMan = new RWLockMan();
    return rwLockMan;
  }

  protected RWLockMan()
  {
  }

  private Object mutex = new Object();

  private static class ThreadCounter
  {
    private Thread thread;
    public Thread getThread() { return thread; }

    public int threadCount = 0;

    public ThreadCounter(Thread _thread)
    {
      this.thread = _thread;
    }
  }

  private static class RWLockCounter
  {
    private RWLock rwLock;
    public RWLock getRWLock() { return rwLock; }

    public int rwLockCountRead = 0;
    public int rwLockCountWrite = 0;

    public RWLockCounter(RWLock _rwLock)
    {
      this.rwLock = _rwLock;
    }

    @Override
		public String toString()
    {
      StringBuffer sb = new StringBuffer();
      sb.append("RWLockCounter{");
      sb.append(rwLock.toString());
      sb.append(",reads=");
      sb.append(rwLockCountRead);
      sb.append(",writes=");
      sb.append(rwLockCountWrite);
      sb.append('}');
      return sb.toString();
    }

//    public int hashCode()
//    {
//      return rwLock.hashCode();
//    }
//
//    public boolean equals(Object obj)
//    {
//      return rwLock.equals(obj);
//    }

  }

  /**
   * Key: RWLock rwLock<br/>
   * Value: HashMap threads (HashMap key: Thread, value: ThreadCounter)
   */
  private Map<RWLock, Map<Thread, ThreadCounter>> heldLocksToThreadsMap = new HashMap<RWLock, Map<Thread, ThreadCounter>>();


  /**
   * Key: Thread thread<br/>
   * Value: HashMap heldRWLocks (HashMap key: RWLock, value: RWLockCounter)
   */
  private Map<Thread, Map<RWLock, RWLockCounter>> threadsToHeldLocksMap = new HashMap<Thread, Map<RWLock, RWLockCounter>>();


  /**
   * Key: Thread thread<br/>
   * Value: HashMap waitingForRWLocks (HashMap key: RWLock, value: RWLockCounter)
   */
  private Map<Thread, Map<RWLock, RWLockCounter>> waitingThreadsToLocksMap = new HashMap<Thread, Map<RWLock, RWLockCounter>>();

  public static final byte MODE_READ = 120;
  public static final byte MODE_WRITE = 121;

  public static String modeToString(byte mode)
  {
    if (mode == MODE_READ)
      return "MODE_READ";
    else if (mode == MODE_WRITE)
      return "MODE_WRITE";
    else
      throw new IllegalArgumentException("unknown mode: "+mode);
  }


  public void beginWaitForLock(RWLock rwLock, byte mode)
  throws DeadLockException
  {
    try {
      if (mode != MODE_READ && mode != MODE_WRITE)
        throw new IllegalArgumentException("unknown mode!");

      Thread currentThread = Thread.currentThread();

      synchronized (mutex) {

        // *** DeadLock-Pruefung ***

        // Welche locks besitzen wir selbst? Dieses Set brauchen wir spaeter...
        Map<RWLock, RWLockCounter> ourHeldLocks = threadsToHeldLocksMap.get(currentThread);

        // Wir sind gezwungen, auf ein Lock zu warten. Also schauen wir mal,
        // wer es besitzt und uns somit zum warten zwingt.
        Map<Thread, ThreadCounter> threads = heldLocksToThreadsMap.get(rwLock);
        if (threads != null) {

          // Nun koennte es sein, dass wir selbst etwas besitzen, worauf diese Threads
          // gerade warten.
          // Also iterieren wir alle gefunden Threads und deren Warte-Locks durch
        	for (Thread thread : threads.keySet()) {
            if (thread != currentThread) {

              // Auf welche Locks wartet dieser Thread?
              Map<RWLock, RWLockCounter> waitingForRWLocks = waitingThreadsToLocksMap.get(thread);
              if (waitingForRWLocks != null) {

                if (ourHeldLocks != null) { // wenn wir keine eigenen Locks besitzen, brauchen wir nicht pruefen

                  // Nun koennte es sein, dass wir eines dieser Locks besitzen, was zu einem Deadlock fuehren wuerde.
                	for (RWLockCounter rwLockCounter : waitingForRWLocks.values()) {
                    RWLockCounter ourHeldLockCounter = ourHeldLocks.get(rwLockCounter.getRWLock());

                    if (ourHeldLockCounter != null) {
                      // Wenn wir selbst eine Schreibsperre auf etwas haben, auf das ein
                      // anderer wartet, von dem wir abhaengig sind, dann ist das auf jeden
                      // Fall ein Dead-Lock.

                      // Es ist auch ein DeadLock, wenn der andere eine Schreibsperre will, und wir eine
                      // Lese-Sperre darauf gesetzt haben.
                      if (
                        ourHeldLockCounter.rwLockCountWrite > 0 ||
                        rwLockCounter.rwLockCountWrite > 0
                      )
                        throw new DeadLockException(currentThread+": We are about to wait for lock "+rwLock+"("+modeToString(mode)+") which is held by thread "+thread+". This thread is waiting for lock "+rwLockCounter+" which is held by us: "+ourHeldLockCounter);

                    } // if (ourHeldLockCounter != null) {


                  } // for (Iterator itLocks = waitingForRWLocks.iterator(); itLocks.hasNext(); ) {
                } // if (ourHeldLocks != null) {

              } // if (waitingForRWLocks != null) {

            } // if (thread != currentThread) {

          } // for (Iterator itThreads = threads.keySet().iterator(); itThreads.hasNext(); ) {
        } // if (threads != null) {

        // *** / DeadLock-Pruefung ***


        // *** wait registrieren ***

        Map<RWLock, RWLockCounter> waitingForRWLocks = waitingThreadsToLocksMap.get(currentThread);
        if (waitingForRWLocks == null) {
          waitingForRWLocks = new HashMap<RWLock, RWLockCounter>();
          waitingThreadsToLocksMap.put(currentThread, waitingForRWLocks);
        }

        RWLockCounter rwLockCounter = waitingForRWLocks.get(rwLock);
        if (rwLockCounter == null) {
          rwLockCounter = new RWLockCounter(rwLock);
          waitingForRWLocks.put(rwLock, rwLockCounter);
        } // if (rwLockCounter == null) {

        if (mode == MODE_READ)
          rwLockCounter.rwLockCountRead++;
        else
          rwLockCounter.rwLockCountWrite++;

        // *** / wait registrieren ***

      } // synchronized (mutex) {

    } catch (RuntimeException x) {
    	logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), x);
      throw x;
    } catch (Throwable t) {
    	logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), t);
      throw new RuntimeException(t);
    }
  }

  public void endWaitForLock(RWLock rwLock, byte mode)
  {
    try {
      if (mode != MODE_READ && mode != MODE_WRITE)
        throw new IllegalArgumentException("unknown mode!");

      Thread currentThread = Thread.currentThread();

      synchronized (mutex) {

        Map<RWLock, RWLockCounter> waitingForRWLocks = waitingThreadsToLocksMap.get(currentThread);
        if (waitingForRWLocks == null)
          throw new IllegalStateException(currentThread+": No waiting locks registered for this thread! Unable to end wait for lock: "+rwLock);

        RWLockCounter rwLockCounter = waitingForRWLocks.get(rwLock);
        if (rwLockCounter == null)
          throw new IllegalStateException(currentThread+": No waiting lockCounter registered for this thread! Unable to end wait for lock: "+rwLock);


        if (mode == MODE_READ)
          rwLockCounter.rwLockCountRead--;
        else
          rwLockCounter.rwLockCountWrite--;


        if (rwLockCounter.rwLockCountRead == 0 && rwLockCounter.rwLockCountWrite == 0) {
          // References loeschen, damit der Garbage Collector den Muell
          // wegraeumen kann.

          waitingForRWLocks.remove(rwLock);

          if (waitingForRWLocks.isEmpty())
            waitingThreadsToLocksMap.remove(currentThread);

        } // if (rwLockCounter.rwLockCountRead == 0 && rwLockCounter.rwLockCountWrite == 0) {
      } // synchronized (mutex) {

    } catch (RuntimeException x) {
    	logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), x);
      throw x;
    } catch (Throwable t) {
      logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), t);
      throw new RuntimeException(t);
    }
  }

  public void acquireLock(RWLock rwLock, byte mode, int count)
  {
    try {
      if (mode != MODE_READ && mode != MODE_WRITE)
        throw new IllegalArgumentException("unknown mode!");

      Thread currentThread = Thread.currentThread();

      synchronized (mutex) {
        Map<Thread, ThreadCounter> threads = heldLocksToThreadsMap.get(rwLock);
        if (threads == null) {
          threads = new HashMap<Thread, ThreadCounter>();
          heldLocksToThreadsMap.put(rwLock, threads);
        }

        ThreadCounter threadCounter = threads.get(currentThread);
        if (threadCounter == null) {
          threadCounter = new ThreadCounter(currentThread);
          threads.put(currentThread, threadCounter);
        } // if (threadCounter == null) {

        threadCounter.threadCount += count;



        Map<RWLock, RWLockCounter> heldLocks = threadsToHeldLocksMap.get(currentThread);
        if (heldLocks == null) {
          heldLocks = new HashMap<RWLock, RWLockCounter>();
          threadsToHeldLocksMap.put(currentThread, heldLocks);
        }

        RWLockCounter rwLockCounter = heldLocks.get(rwLock);
        if (rwLockCounter == null) {
          rwLockCounter = new RWLockCounter(rwLock);
          heldLocks.put(rwLock, rwLockCounter);
        } // if (rwLockCounter == null) {

        if (mode == MODE_READ)
          rwLockCounter.rwLockCountRead += count;
        else
          rwLockCounter.rwLockCountWrite += count;

      } // synchronized (mutex) {

    } catch (RuntimeException x) {
      logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), x);
      throw x;
    } catch (Throwable t) {
    	logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), t);
      throw new RuntimeException(t);
    }
  }

  public void releaseLock(RWLock rwLock, byte mode, int count)
  {
    try {
      if (mode != MODE_READ && mode != MODE_WRITE)
        throw new IllegalArgumentException("unknown mode!");

      Thread currentThread = Thread.currentThread();

      synchronized (mutex) {

        Map<Thread, ThreadCounter> threads = heldLocksToThreadsMap.get(rwLock);
        if (threads == null)
          throw new IllegalStateException(currentThread+": No possessing threads registered for this lock! Unable to release lock: "+rwLock);

        ThreadCounter threadCounter = threads.get(currentThread);
        if (threadCounter == null)
          throw new IllegalStateException(currentThread+": No thread counter registered for this lock & current thread! Unable to release lock: "+rwLock);




        Map<RWLock, RWLockCounter> heldLocks = threadsToHeldLocksMap.get(currentThread);
        if (heldLocks == null)
          throw new IllegalStateException(currentThread+": No held locks registered for this thread! Unable to release lock: "+rwLock);

        RWLockCounter rwLockCounter = heldLocks.get(rwLock);
        if (rwLockCounter == null)
          throw new IllegalStateException(currentThread+": No lock counter registered for this thread & lock! Unable to release lock: "+rwLock);


        threadCounter.threadCount -= count;

        if (mode == MODE_READ)
          rwLockCounter.rwLockCountRead -= count;
        else
          rwLockCounter.rwLockCountWrite -= count;

        // Muell wegraeumen

        if (threadCounter.threadCount == 0) {

          threads.remove(currentThread);

          if (threads.isEmpty())
            heldLocksToThreadsMap.get(rwLock);

        } // if (threadCounter.threadCount == 0) {

        if (rwLockCounter.rwLockCountRead == 0 && rwLockCounter.rwLockCountWrite == 0) {

          heldLocks.remove(rwLock);

          if (heldLocks.isEmpty())
            threadsToHeldLocksMap.remove(currentThread);

        } // if (rwLockCounter.rwLockCountRead == 0 && rwLockCounter.rwLockCountWrite == 0) {

      } // synchronized (mutex) {

    } catch (RuntimeException x) {
    	logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), x);
      throw x;
    } catch (Throwable t) {
      logger.log(Level.FATAL, "Exception in thread "+Thread.currentThread(), t);
      throw new RuntimeException(t);
    }
  }

}
