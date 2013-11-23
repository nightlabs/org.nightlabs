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
package org.nightlabs.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.nightlabs.util.CollectionUtil;

/**
 * @author marco schulze - marco at nightlabs dot de
 *
 * @deprecated Not necessary anymore! Never use this class - it was used internally before by {@link CollectionUtil#castCollection(Collection)},
 * and will soon be removed.
 */
@Deprecated
public class DelegatingCollection<E>
implements Collection<E>, Serializable
{
	private static final long serialVersionUID = 1L;
	private Collection<E> delegate;

	public DelegatingCollection(Collection<E> delegate)
	{
		this.delegate = delegate;
	}

	public boolean add(E o)
	{
		return delegate.add(o);
	}

	@SuppressWarnings("unchecked")
	public boolean addAll(Collection c)
	{
		return delegate.addAll(c);
	}

	public void clear()
	{
		delegate.clear();
	}

	public boolean contains(Object o)
	{
		return delegate.contains(o);
	}

	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection c)
	{
		return delegate.containsAll(c);
	}

	@Override
	public boolean equals(Object o)
	{
		return delegate.equals(o);
	}

	@Override
	public int hashCode()
	{
		return delegate.hashCode();
	}

	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return delegate.iterator();
	}

	public boolean remove(Object o)
	{
		return delegate.remove(o);
	}

	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection c)
	{
		return delegate.removeAll(c);
	}

	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection c)
	{
		return delegate.retainAll(c);
	}

	public int size()
	{
		return delegate.size();
	}

	public Object[] toArray()
	{
		return delegate.toArray();
	}

	public <U> U[] toArray(U[] a)
	{
		return delegate.toArray(a);
	}

}
