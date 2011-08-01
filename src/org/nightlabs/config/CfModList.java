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
package org.nightlabs.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.nightlabs.concurrent.RWLockable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This implementation of List should be used within a config module instead of a usual
 * ArrayList, because it notifies its owner ConfigModule, if it gets changed. Furthermore,
 * it uses the RWLockable ability of its owner to ensure thread safety and implements
 * RWLockable itself (delegating locking to the config module).
 * <p>
 * Warning: You still need to think about synchronization using RWLockable when using
 * iterator() or similar methods that return an object which should only be used while having
 * a read lock set! These methods don't acquire a read lock, because it would be in vain anyway.
 * Read the documentation of the methods you use to avoid thread problems.
 *
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 */
public class CfModList<E> extends ArrayList<E> implements RWLockable
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CfModList.class);

	protected ConfigModule ownerCfMod = null;

	public CfModList() { }

	public CfModList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public CfModList(Collection<? extends E> c)
	{
		super(c);
	}

	public CfModList(ConfigModule ownerCfMod)
	{
		this.ownerCfMod = ownerCfMod;
	}

	/**
	 * @return Returns the ownerCfMod.
	 */
	public ConfigModule getOwnerCfMod()
	{
		return ownerCfMod;
	}
	/**
	 * @param ownerCfMod The ownerCfMod to set.
	 */
	public void setOwnerCfMod(ConfigModule ownerCfMod)
	{
		this.ownerCfMod = ownerCfMod;
	}

	private transient long creationTimestamp = System.currentTimeMillis();

	private void logDebugWarn(String message)
	{
		if (creationTimestamp == 0)
			creationTimestamp = System.currentTimeMillis();

		if (System.currentTimeMillis() - creationTimestamp < 100)
			logger.debug(message);
		else
			logger.warn(message);
	}

	protected void setChanged()
	{
		if (ownerCfMod == null) {
			logDebugWarn("Owner ConfigModule is null! Cannot notify change!");
			return;
		}
		ownerCfMod.setChanged();
	}

	public void acquireReadLock()
	{
		if (ownerCfMod == null) {
			logDebugWarn("Owner ConfigModule is null! Cannot lock! Operation is not thread-safe!!!");
			return;
		}
		ownerCfMod.acquireReadLock();
	}

	public void acquireWriteLock()
	{
		if (ownerCfMod == null) {
			logDebugWarn("Owner ConfigModule is null! Cannot lock! Operation is not thread-safe!!!");
			return;
		}
		ownerCfMod.acquireReadLock();
	}

	public void releaseLock()
	{
		if (ownerCfMod == null) {
			logDebugWarn("Owner ConfigModule is null! Cannot lock! Operation is not thread-safe!!!");
			return;
		}
		ownerCfMod.releaseLock();
	}

	@Override
	public void add(int index, E element)
	{
		acquireWriteLock();
		try {
			super.add(index, element);
			setChanged();
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean add(E o)
	{
		acquireWriteLock();
		try {
			boolean res = super.add(o);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		acquireWriteLock();
		try {
			boolean res = super.addAll(c);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		acquireWriteLock();
		try {
			boolean res = super.addAll(index, c);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public void clear()
	{
		acquireWriteLock();
		try {
			super.clear();
			setChanged();
		} finally {
			releaseLock();
		}
	}

	@Override
	public E remove(int index)
	{
		acquireWriteLock();
		try {
			E res = super.remove(index);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex)
	{
		acquireWriteLock();
		try {
			super.removeRange(fromIndex, toIndex);
			setChanged();
		} finally {
			releaseLock();
		}
	}

	@Override
	public E set(int index, E element)
	{
		acquireWriteLock();
		try {
			E res = super.set(index, element);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public void trimToSize()
	{
		acquireWriteLock();
		try {
			super.trimToSize();
			setChanged();
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean remove(Object o)
	{
		acquireWriteLock();
		try {
			boolean res = super.remove(o);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		acquireWriteLock();
		try {
			boolean res = super.removeAll(c);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		acquireWriteLock();
		try {
			boolean res = super.retainAll(c);
			setChanged();
			return res;
		} finally {
			releaseLock();
		}
	}

	@Override
	public Object clone()
	{
		acquireReadLock();
		try {
			return super.clone();
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean contains(Object elem)
	{
		acquireReadLock();
		try {
			return super.contains(elem);
		} finally {
			releaseLock();
		}
	}

	@Override
	public void ensureCapacity(int minCapacity)
	{
		acquireReadLock();
		try {
			super.ensureCapacity(minCapacity);
		} finally {
			releaseLock();
		}
	}

	@Override
	public E get(int index)
	{
		acquireReadLock();
		try {
			return super.get(index);
		} finally {
			releaseLock();
		}
	}

	@Override
	public int indexOf(Object elem)
	{
		acquireReadLock();
		try {
			return super.indexOf(elem);
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean isEmpty()
	{
		acquireReadLock();
		try {
			return super.isEmpty();
		} finally {
			releaseLock();
		}
	}

	@Override
	public int lastIndexOf(Object elem)
	{
		acquireReadLock();
		try {
			return super.lastIndexOf(elem);
		} finally {
			releaseLock();
		}
	}

	@Override
	public int size()
	{
		acquireReadLock();
		try {
			return super.size();
		} finally {
			releaseLock();
		}
	}

	@Override
	public Object[] toArray()
	{
		acquireReadLock();
		try {
			return super.toArray();
		} finally {
			releaseLock();
		}
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		acquireReadLock();
		try {
			return super.toArray(a);
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean equals(Object o)
	{
		acquireReadLock();
		try {
			return super.equals(o);
		} finally {
			releaseLock();
		}
	}

	@Override
	public int hashCode()
	{
		acquireReadLock();
		try {
			return super.hashCode();
		} finally {
			releaseLock();
		}
	}

	/**
	 * This method does NOT acquire a read lock, because the returned Iterator
	 * must be thread protected the whole time it is used. Thus, it is your duty,
	 * to call acquireReadLock() and releaseLock()!
	 *
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator()
	{
		return super.iterator();
	}

	/**
	 * This method does NOT acquire a read lock, because YOU must protect
	 * the returned ListIterator the whole time, you use it. YOU MUST call
	 * acquireReadLock() before calling this method and releaseLock() after
	 * you finished using the returned ListIterator!
	 *
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<E> listIterator()
	{
		return super.listIterator();
	}
	/**
	 * This method does NOT acquire a read lock! YOU must call acquireReadLock()
	 * before calling this method and releaseLock() after you finished using the returned
	 * ListIterator!
	 *
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator(int index)
	{
		return super.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		acquireReadLock();
		try {
			return super.subList(fromIndex, toIndex);
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		acquireReadLock();
		try {
			return super.containsAll(c);
		} finally {
			releaseLock();
		}
	}

	@Override
	public String toString()
	{
		acquireReadLock();
		try {
			return super.toString();
		} finally {
			releaseLock();
		}
	}
}
