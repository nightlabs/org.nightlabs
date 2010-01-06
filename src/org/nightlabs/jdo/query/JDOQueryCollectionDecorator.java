package org.nightlabs.jdo.query;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;

/**
 * A Decorator for a QueryCollection that contains JDO-capable queries. This decorator sets the
 * given PersitenceManager for all JDO-capable queries.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class JDOQueryCollectionDecorator<Q extends AbstractSearchQuery>
	extends QueryCollection<Q>
{
	/**
	 * The serial version id.
	 */
	private static final long serialVersionUID = 1L;
	private QueryCollection<? extends Q> wrappedCollection;
	private transient PersistenceManager pm;

	public JDOQueryCollectionDecorator(QueryCollection<? extends Q> wrappedCollection)
	{
		super(wrappedCollection.getResultClass());
		this.wrappedCollection = wrappedCollection;
	}

	@Override
	public Collection<?> executeQueries()
	{
		for (Q query : wrappedCollection)
		{
			if (query instanceof AbstractJDOQuery)
			{
				final AbstractJDOQuery jdoQuery = (AbstractJDOQuery) query;
				jdoQuery.setPersistenceManager(getPersistenceManager());
			}
		}
		return wrappedCollection.executeQueries();
	}

	/**
	 * @return the PersitenceManager used for the JDO-capable queries in this collection.
	 */
	public PersistenceManager getPersistenceManager()
	{
		return pm;
	}

	/**
	 * @param pm the PersitenceManager used for the JDO-capable queries in this collection.
	 */
	public void setPersistenceManager(PersistenceManager pm)
	{
		assert pm != null;
		this.pm = pm;
	}

	@Override
	public boolean addAll(Collection<? extends Q> c) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public boolean contains(Object o) {
		return wrappedCollection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedCollection.containsAll(c);
	}

	@Override
	public long getFromInclude() {
		return wrappedCollection.getFromInclude();
	}

	@Override
	public long getToExclude() {
		return wrappedCollection.getToExclude();
	}

	@Override
	public boolean isEmpty() {
		return wrappedCollection.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Q> iterator() {
		return (Iterator<Q>) wrappedCollection.iterator();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public void setFromInclude(long fromInclude) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public void setResultClass(Class<?> resultClass) {
		if (getResultClass() != null) // this method is called by the constructor, hence we need to allow this first call.
			throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public void setToExclude(long toExclude) {
		throw new UnsupportedOperationException("The JDOQueryCollectionDecorator is read-only!");
	}

	@Override
	public int size() {
		return wrappedCollection.size();
	}

	@Override
	public Object[] toArray() {
		return wrappedCollection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrappedCollection.toArray(a);
	}

}
