package org.nightlabs.jdo.query;

import java.util.Collection;

import javax.jdo.PersistenceManager;

/**
 * A Decorator for a QueryCollection that contains JDO-capable queries. This decorator sets the 
 * given PersitenceManager for all JDO-capable queries.
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
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
}
