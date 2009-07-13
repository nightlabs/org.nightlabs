package org.nightlabs.datastructure;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Set} implementation that - like Sun's implementation of {@link IdentityHashSet} - wraps a {@link Map} implementation.
 * In contrast to {@link IdentityHashSet} (which wraps a {@link IdentityHashMap}), this implementation wraps an {@link IdentityIdentityHashMap}.
 * Use this whenever you need reference handling (instead of equality-based handling).
 *
 * @author marco
 */
public class IdentityHashSet<E>
implements Set<E>, Cloneable, java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private IdentityHashMap<E, Object> map = new IdentityHashMap<E, Object>();

	@Override
	public boolean add(E e) {
		if (map.containsKey(e))
			return false;

		map.put(e, IdentityHashSet.class);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c)
			changed |= add(e);

		return changed;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (c.size() != map.size())
			return false;

		for (Object element : c) {
			if (!map.containsKey(element))
				return false;
		}

		return true;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed |= remove(o);

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll(...) is not yet supported by IdentityHashSet! Feel free to implement it and contribute it!");
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Object[] toArray() {
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.keySet().toArray(a);
	}
}

