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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to synchronize a block for multiple reads and exclusive
 * write. The default java way to synchronize a block is exclusive which slows
 * down the program in some cases absolutely unnecessarily. Thus, you might want
 * to use this class instead of a synchronized-block if you
 * perform expensive multi-threaded read operations (e.g. in a server).
 * <p>
 * A thread trying to acquire a write lock obtains higher priority than threads
 * trying to aquire read locks.
 * <p>
 * Use code like the following to perform thread protected read operations:
 * <p><blockquote><pre>
 * RWLock rwLock = new RWLock();
 *
 * :
 * public void myMethod() {
 *   :
 *   :
 *   rwLock.acquireRead();
 *   try {
 *     :
 *     :
 *   } finally {
 *     rwLock.releaseLock();
 *   }
 *   :
 *   :
 * }
 * </pre></blockquote><p>
 * <b>Never ever forget the releaseLock() and always use try-finally-blocks!!!</b>
 * <p>
 * The RWLock supports cascaded reads and writes. There is only one thing that
 * you should be aware of: If a thread acquires a read lock, first, and without
 * releasing it, it acquires a write lock, the protected data might be changed by another thread
 * while the method <code>acquireWriteLock()</code> is waiting for the write access.
 * This behaviour is to prevent dead locks. A corruption of the data is not
 * possible, because only one is allowed to write at the same time and the
 * other threads are suspended at the line where they execute
 * <code>acquireWriteLock()</code>.
 * <p><blockquote><pre>
 * TestObject testObject = new TestObject();
 * RWLock rwLock = new RWLock();
 *
 * public void myMethod()
 * {
 *   rwLock.acquireReadLock();
 *   try {
 *     int tmpVal = testObject.getTmpVal();
 *     int tmpVal2;
 *     :
 *     :
 *     rwLock.acquireWriteLock(); // here the read locks get temporarily released to prevent dead locks
 *     try {
 *       if (tmpVal != testObject.getTmpVal())
 *         System.out.println("This can happen!!!");
 *       :
 *       :
 *       tmpVal2 = testObject.getTmpVal();
 *     } finally {
 *       rwLock.releaseLock(); // here the temporarily released read locks get re-established.
 *     }
 *     if (tmpVal2 != testObject.getTmpVal())
 *       throw new IllegalStateException("This can never happen, because the releaseLock() does not allow other threads to write.");
 *     :
 *     :
 *   } finally {
 *     rwLock.releaseLock();
 *   }
 * }
 * </pre></blockquote><p>
 *
 * @author Marco Schulze
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 * @version 1.0
 */

public class RWLock implements RWLockable
{
  /**
   * Contains 0 if there is no lock existing, &gt; 0 for read locks and &lt; 0 for write
   * locks. Because one thread may call acquireWriteLock multiple times, it
   * is necessary to count the write locks even though only one thread can
   * hold the write lock at the same time.
   */
  private int currentLocks = 0;

  /**
   * This method returns the current lock status. It equals 0, if there is
   * no lock existing. It is &gt; 0, if there are read locks existing and it is
   * &lt; 0, if there are write locks existing. Because locks can be cascaded and
   * the same thread is allowed to execute <code>acquireWriteLock()</code> multiple
   * times, there are values possible below -1.
   *
   * @return 0 if there is no lock; &lt; 0 if there is a write lock; &gt; 0 if there are read locks.
   */
  public int getCurrentLockStatus()
  {
    synchronized (mutex) { return currentLocks; }
  }

  /**
   * If there is a write lock existing (currentLocks &lt; 0), this variable stores
   * the thread that holds the lock.
   */
  private Thread currentWriteThread = null;

  /**
   * This map is used to allow cascaded reads without the danger of dead locks.
   * Because writes have priority, it is necessary to allow a thread who already
   * has acquired a read lock, to acquire more. Only new reading threads should
   * be forced to wait.
   * <p>
   * key: Thread reader<br/>
   * value: ReadLockCount readLockCount
   */
  private HashMap<Thread, ReadLockCount> currentReadThreads = new HashMap<Thread, ReadLockCount>();

  /**
   * This map is used to save the read locks while releasing them when
   * aquiring a write lock. To prevent dead locks, acquireWrite() releases
   * all read locks of the current thread and releaseLock() acquires them again.
   */
  private HashMap<Thread, ReadLockCount> savedReadLocks = new HashMap<Thread, ReadLockCount>();


  /**
   * This class exists to allow to put a changeable int into a Map.
   * For each reading thread, we store how many read locks this thread owns.
   * This is necessary to temporarily release all read locks when acquiring
   * a write lock - and of course, to restore the read locks when releasing
   * the write lock, too.
   */
  private static class ReadLockCount
  {
    public int readLockCount = 0;
  }

  /**
   * This integer counts, how many threads are currently trying to acquire a
   * write lock. This is used to give writers priority. This means: If a
   * waiting writer exists, noone can acquire new read locks - except if the
   * thread already owns at least one read or write lock.
   */
  private int waitingWriters = 0;

  /**
   * This is a counter to know how many threads are currently trying to acquire
   * a read lock. This is only used for statistics and debugging.
   */
  private int waitingReaders = 0;

  /**
   * All the methods of this class use this object to synchronize.
   */
  private Object mutex = new Object();

  private RWLockMan rwLockMan;

  private String rwLockName = null;

  public RWLock()
  {
    rwLockMan = RWLockMan.getRWLockMan();
  }

  /**
   * Use this constructor to define a name for the lock. This name provides
   * no functionality, but in case of a dead lock exception, you might be
   * thankful to get names instead of memory addresses.
   *
   * @param _rwLockName The name for this lock.
   */
  public RWLock(String _rwLockName)
  {
    this();
    this.rwLockName = _rwLockName;
  }

  /**
   * Use this constructor to define a name for the lock. This name provides
   * no functionality, but in case of a dead lock exception, you might be
   * thankful to get names instead of memory addresses.
   * <p>
   * If you create semi-static rwLockNames (e.g. use the class name of the protected
   * object), you might want to have the memory address additionally. Thus,
   * you can set appendMemAddr true.
   *
   * @param _rwLockName The name for this lock.
   * @param appendMemAddr Whether or not to append the memory address.
   */
  public RWLock(String _rwLockName, boolean appendMemAddr)
  {
    this();
    if (appendMemAddr) {
      StringBuffer sb = new StringBuffer(_rwLockName.length()+10);
      sb.append(_rwLockName);
      sb.append('@');
      sb.append(Integer.toHexString(hashCode()));
      this.rwLockName = sb.toString();
    }
    else
      this.rwLockName = _rwLockName;
  }

  /**
   * Use this method to find out, how many threads are currently waiting for
   * a write lock.
   *
   * @return Number of threads trying to acquire a write lock.
   */
  public int getWaitingWriters()
  {
    synchronized (mutex) { return waitingWriters; }
  }

  /**
   * Use this method to find out, how many threads are currently waiting for
   * a read lock.
   *
   * @return Number of threads trying to acquire a read lock.
   */
  public int getWaitingReaders()
  {
    synchronized (mutex) { return waitingReaders; }
  }

  /**
   * Use this method to find out, who owns the write lock. If there is no write
   * lock existing, this method returns <code>null</code>
   *
   * @return The thread that owns the write lock or <code>null</code>.
   */
  public Runnable getCurrentWriteThread()
  {
    synchronized (mutex) { return currentWriteThread; }
  }


  /**
   * Use this method to acquire a read lock. <b>Never forget to release the lock!!!</b>
   * The best thing is to use try..finally-blocks to ensure the release.
   *
   * @throws DeadLockException If the try to acquire a lock would lead into a
   *   dead lock, a DeadLockException is thrown. Dead locks are detected immediately.
   *   Because DeadLockException is inherited from RuntimeException, you don't have
   *   to declare it.
   *
   * @see #releaseLock()
   */
  public void acquireReadLock()
  throws DeadLockException
  {
    ReadLockCount rlc = null;

    synchronized(mutex) {
      Thread currentThread = Thread.currentThread();

      waitingReaders++;
      try {

        // Wenn der aktuelle Thread ein WriteLock besitzt, darf natuerlich nicht
        // geprueft und gewartet werden. In dem Fall muss er natuerlich sofort
        // ein ReadLock zugestanden bekommen.
        if (currentWriteThread != currentThread) {

          // wenn der aktuelle Thread bereits ein Read-Lock hat, kann
          // es kein *fremdes* Write-Lock geben (eigenes schon) und wir duerfen nicht
          // testen & warten.
          // Dies wuerde naemlich zu einem Deadlock fuehren, falls der gleiche
          // Thread mehr readLocks will, aber ein anderer Thread auf einen WriteLock
          // wartet, weil WriteLocks Prioritaet haben.
          rlc = currentReadThreads.get(currentThread);
          if (rlc == null || rlc.readLockCount == 0) {

            if ((currentLocks < 0) || (waitingWriters != 0)) {
              rwLockMan.beginWaitForLock(this, RWLockMan.MODE_READ); // here, a DeadLockException might be thrown.
              try {

                try {
                  while ((currentLocks < 0) || (waitingWriters != 0))
                    mutex.wait();
                } catch (InterruptedException x) {
                  throw new IllegalThreadStateException("Waiting interrupted. Can't continue work in this situation, because threads my collide!");
                }

              } finally {
                rwLockMan.endWaitForLock(this, RWLockMan.MODE_READ);
              }
            } // if ((currentLocks < 0) || (waitingWriters != 0)) {

            if (rlc == null) {
              rlc = new ReadLockCount();
              currentReadThreads.put(currentThread, rlc);
            } // if (rlc == null) {
          } // if (rlc == null || rlc.readLockCount == 0) {

        } // if (currentWriteThread != currentThread) {

      } finally {
        waitingReaders--;
      }

      rwLockMan.acquireLock(this, RWLockMan.MODE_READ, 1);

      // register the current read lock to be able to release it.
      List<Boolean> lockStack = lockStacksByThread.get(currentThread);
      if (lockStack == null) {
        lockStack = new ArrayList<Boolean>();
        lockStacksByThread.put(currentThread, lockStack);
      }
      lockStack.add(new Boolean(false)); // Boolean isWriteLock

//      if (currentWriteThread != currentThread) {
      if (currentLocks >= 0) {
        currentLocks++;
        rlc.readLockCount++;
      }
      else {
        rlc = savedReadLocks.get(currentThread);
        if (rlc == null) {
          rlc = new ReadLockCount();
          savedReadLocks.put(currentThread, rlc);
        }
        rlc.readLockCount++;
      } // if (currentWriteThread == currentThread) {

    } // synchronized(mutex) {
  }

  /**
   * Use this method to acquire a write lock. <b>Never forget to release the lock!!!</b>
   * The best thing is to use try..finally-blocks to ensure the release.
   *
   * @throws DeadLockException If the try to acquire a lock would lead into a
   *   dead lock, a DeadLockException is thrown. Dead locks are detected immediately.
   *   Because DeadLockException is inherited from RuntimeException, you don't have
   *   to declare it.
   *
   * @see #releaseLock()
   */
  public void acquireWriteLock()
  throws DeadLockException
  {
    synchronized(mutex) {
      Thread currentThread = Thread.currentThread();

      waitingWriters++;
      try {

        if (currentThread != currentWriteThread) {

          // A dead lock exception might be thrown by rwLockMan.beginWaitForLock(...).
          // Thus, we have to execute this method, before releasing the read locks
          // to make sure, our lock doesn't get corrupted by the DeadLockException.
          boolean endWaitInRWLockMan = false;
          if (currentLocks != 0) {
            rwLockMan.beginWaitForLock(this, RWLockMan.MODE_WRITE);
            endWaitInRWLockMan = true;
          }
          try { // finally ends wait in rwLockMan

            // aktuelle ReadLocks sichern und temporaer releasen
            ReadLockCount rlc = currentReadThreads.get(currentThread);
            if (rlc != null && rlc.readLockCount > 0) {
              savedReadLocks.put(currentThread, rlc);
              currentReadThreads.remove(currentThread);
              currentLocks -= rlc.readLockCount;
              if (currentLocks < 0)
                throw new IllegalStateException(currentThread+": currentLocks < 0!!!");

              rwLockMan.releaseLock(this, RWLockMan.MODE_READ, rlc.readLockCount);

              mutex.notifyAll();
            }

            if (currentLocks != 0) {
              try {
                while (currentLocks != 0)
                  mutex.wait();
              } catch (InterruptedException x) {
                throw new IllegalThreadStateException(currentThread+": Waiting interrupted. Can't continue work in this situation, because threads my collide!");
              }
            } // if (currentLocks != 0) {

          } finally {
            if (endWaitInRWLockMan)
              rwLockMan.endWaitForLock(this, RWLockMan.MODE_WRITE);
          }

          currentWriteThread = currentThread;
        } // if (currentThread != currentWriteThread) {

      } finally {
        waitingWriters--;
      }

      rwLockMan.acquireLock(this, RWLockMan.MODE_WRITE, 1);

      List<Boolean> lockStack = lockStacksByThread.get(currentThread);
      if (lockStack == null) {
        lockStack = new ArrayList<Boolean>();
        lockStacksByThread.put(currentThread, lockStack);
      }
      lockStack.add(new Boolean(true)); // Boolean isWriteLock

      currentLocks--;
    } // synchronized(mutex) {
  }

  /**
   * This map is needed to know in releaseLock(), whether we have to release
   * a read or a write lock, because they can be cascaded.
   * <p>
   * key: Thread theTread<br/>
   * value: Vector of (Boolean isWriteLock)
   * <p>
   * The last entry in the vector is always the newest.
   */
  private HashMap<Thread, List<Boolean>> lockStacksByThread = new HashMap<Thread, List<Boolean>>();


  /**
   * Use this method to release a lock. The method knows whether it must
   * release a read or a write lock. You should always use try...finally-blocks
   * in the following way:
   * <p><blockquote><pre>
   * RWLock rwLock = new RWLock();
   * :
   * :
   * rwLock.acquireReadLock();
   * try {
   *   :
   *   :
   * } finally {
   *   rwLock.releaseLock();
   * }
   * </pre></blockquote>
   * <p>
   * Note, that there must be a call of releaseLock() for each corresponding acquire*Lock(),
   * if you cascade them.
   *
   * @see #acquireReadLock()
   * @see #acquireWriteLock()
   */
  public void releaseLock()
  {
    synchronized(mutex) {
      Thread currentThread = Thread.currentThread();

      if (currentLocks == 0)
        throw new IllegalStateException(currentThread+": currentLocks == 0: releaseLock called without previous acquire!");

      List<Boolean> lockStack = lockStacksByThread.get(currentThread);
      if (lockStack == null)
        throw new IllegalStateException(currentThread+": No lock stack registered for current thread!");

      if (lockStack.isEmpty())
        throw new IllegalStateException(currentThread+": lock stack of current thread is empty!");

      Boolean releaseWriteLock = lockStack.remove(lockStack.size()-1);

      if (releaseWriteLock.booleanValue()) { // we release a write lock

        if (currentLocks > 0)
          throw new IllegalStateException(currentThread+": currentLocks > 0, but we are trying to release a write lock!");

        currentLocks++;

        if (currentLocks == 0) {
          currentWriteThread = null;

          // gesicherte ReadLocks wiederherstellen
          ReadLockCount rlc = savedReadLocks.remove(currentThread);
          if (rlc != null) {
            if (rlc.readLockCount > 0) {
//              mutex.notifyAll(); // andere waiting writers wecken!
//
//              try {
//                while ((currentLocks < 0) || (waitingWriters != 0))
//                  mutex.wait();
//              } catch (InterruptedException x) {
//                throw new IllegalThreadStateException("Waiting interrupted. Can't continue work in this situation, because threads my collide!");
//              }
//
//              // restore, when it's save to do it.
// The above behaviour is not that logical and it is not necessary, thus we
// use the below.

              // we restore the read locks immediately, without giving other writers
              // the possibility to write, because it's more logical, that the
              // protected variable(s) can't change the value during a releaseLock.
              // It is necessary that others can write while one is trying to acquire
              // a write lock, because we might run into deadlocks otherwise, but
              // for the release lock it's not necessary.
              currentLocks += rlc.readLockCount;
              currentReadThreads.put(currentThread, rlc);

              rwLockMan.acquireLock(this, RWLockMan.MODE_READ, rlc.readLockCount);
            } // if (rlc.readLockCount > 0) {
          } // if (rlc != null) {

        } // if (currentLocks == 0) {

        rwLockMan.releaseLock(this, RWLockMan.MODE_WRITE, 1);

      }
      else { // we release a read lock

        if (currentLocks < 0) { // there is a write lock currently active! If everything is correct, this must be owned by the current thread!
          if (currentThread != currentWriteThread)
            throw new IllegalStateException(currentThread+": Current thread is not current write thread! Why are we here?");


          ReadLockCount rlc = savedReadLocks.get(currentThread);

          if (rlc == null || rlc.readLockCount == 0)
            throw new IllegalStateException(currentThread+": Current thread does not have a read lock set, but tries to release one!");

          rlc.readLockCount--;
        } // if (currentLocks < 0) {
        else {
          ReadLockCount rlc = currentReadThreads.get(currentThread);
          if (rlc == null || rlc.readLockCount == 0)
            throw new IllegalStateException(currentThread+": Current thread does not have a read lock set, but tries to release one!");

          currentLocks--;
          rlc.readLockCount--;
        } // if (currentLocks > 0) {

        rwLockMan.releaseLock(this, RWLockMan.MODE_READ, 1);

      }

      mutex.notifyAll();
    } // synchronized(mutex) {
  }

  /**
   * Create a <code>String</code>-representation of this object.
   * Overridden method.
   *
   * @see java.lang.Object#toString()
   * @return The <code>String</code>-representation of this object.
   */
  @Override
	public String toString()
  {
    synchronized(mutex) {
      StringBuffer sb = new StringBuffer();
      sb.append(this.getClass().getName());

      if (rwLockName == null) {
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append('{');
      }
      else {
        sb.append("{name=");
        sb.append(rwLockName);
        sb.append(',');
      }
      sb.append("status=");
      sb.append(currentLocks);
      sb.append(",waitingReaders=");
      sb.append(waitingReaders);
      sb.append(",waitingWriters=");
      sb.append(waitingWriters);
      sb.append(",currentWriteThread=");
      sb.append(currentWriteThread);
      sb.append('}');
      return sb.toString();
    } // synchronized(mutex) {
  }

}
