package org.nightlabs.jdo.query;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of {@link QueryProvider} maintains one Query instance per query class.
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DefaultQueryProvider<Q extends AbstractSearchQuery>
	implements QueryProvider<Q>
{
	private QueryMap<Q> queryMap;
	private Map<Class<? extends Q>, PropertyChangeSupport> listeners;
	private PropertyChangeListener queryChangeDelegator = new PropertyChangeListener()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt instanceof QueryEvent)
			{
				final QueryEvent event = (QueryEvent) evt;
				// cast is safe, since we only allow such kinds of queries to be added / retrieved!
				notifyListeners((Class<? extends Q>) event.getChangedQuery().getClass(), event);
			}
			else
			{
				// the query we attached this listener to, must have been at least of type Q
				final QueryEvent event = new QueryEvent((AbstractSearchQuery) evt.getSource(), 
					evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()
					);
				
				notifyListeners((Class<? extends Q>) event.getChangedQuery().getClass(), event);
			}
		}
	};
	
	/**
	 * @param queryResultClass the topmost class that all maintained queries have to return. 
	 */
	public DefaultQueryProvider(Class<?> queryResultClass)
	{
		queryMap = new QueryMap<Q>(queryResultClass);
		listeners = new HashMap<Class<? extends Q>, PropertyChangeSupport>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#getManagedQueries()
	 */
	@Override
	public QueryCollection<Q> getManagedQueries()
	{
		return new QueryCollection<Q>(queryMap);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#getQueryOfType(java.lang.Class)
	 */
	@Override
	public <ReqQuery extends Q> ReqQuery getQueryOfType(Class<ReqQuery> queryClass)
	{
		return getQueryOfType(queryClass, true);
	}
	
	public <ReqQuery extends Q> ReqQuery getQueryOfType(Class<ReqQuery> queryClass, boolean createIfNotExisting)
	{
		ReqQuery query = queryMap.getQueryOfType(queryClass, createIfNotExisting);
		if (query != null)
		{
			query.addQueryChangeListener(queryChangeDelegator);
		}
		
		return query; 
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#loadQueries(org.nightlabs.jdo.query.QueryCollection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void loadQueries(QueryCollection<?> newQueries)
	{
		if (newQueries != null && ! (getManagedQueries().getResultClass().equals(newQueries.getResultClass())) )
		{
			throw new IllegalStateException("The given QueryCollection is not defined with the same base return type ");
		}
		
		final QueryMap<Q> oldQueries = queryMap;
		queryMap = new QueryMap<Q>(oldQueries.getResultClass());
		
		// add new Queries
		if (newQueries != null)
		{
			// TODO: maybe we should check whether the given QueryCollection contains multiple queries of the same kind => they will override each other!
			queryMap.load(newQueries);
			queryMap.setFromInclude(newQueries.getFromInclude());
			queryMap.setToExclude(newQueries.getToExclude());
		}
		
		// notify all listeners of the old query types (with the new query if there is one)
		for (Q oldQuery : oldQueries)
		{
			// remove change propagation listener
			oldQuery.removeQueryChangeListener(queryChangeDelegator);
			
			// notify change listeners of that type
			PropertyChangeSupport queryTypeListeners = listeners.get(oldQuery.getClass());
			if (queryTypeListeners != null && queryTypeListeners.hasListeners(null))
			{
				final Q newQuery = queryMap.getQueryOfType((Class<? extends Q>)oldQuery.getClass(), false);
				final QueryEvent event = new QueryEvent(newQuery == null ? oldQuery : newQuery, 
					AbstractSearchQuery.PROPERTY_WHOLE_QUERY, oldQuery, newQuery);
				
				queryTypeListeners.firePropertyChange(event);
			}
		}
		
		Set<Class<? extends Q>> registeredListenerTypes = oldQueries.getManagedQueryTypes();
		// notify all listeners of new query types that haven't been handled yet 
		for (Q query : queryMap)
		{
			// add propagation listener
			query.addQueryChangeListener(queryChangeDelegator);
			
			// if this type of query was already handled -> skip it
			if (registeredListenerTypes.contains(query.getClass()))
				continue;
			
			PropertyChangeSupport queryTypeListeners = listeners.get(query.getClass());
			if (queryTypeListeners != null && queryTypeListeners.hasListeners(null))
			{
				final QueryEvent event = new QueryEvent(query, AbstractSearchQuery.PROPERTY_WHOLE_QUERY,
					null, query);
				
				queryTypeListeners.firePropertyChange(event);
			}
		}
	}
	
	/**
	 * Internal method used to notify the listeners of a given query type.
	 * @param <T> the type of Query for which all registered listeners shall be notified.
	 * @param queryType the runtime type information of <code>T</code>.
	 */
	protected void notifyListeners(Class<? extends Q> queryType, QueryEvent event)
	{
		PropertyChangeSupport queryListeners = listeners.get(queryType);
		if (queryListeners == null)
		{
			return;
		}

		queryListeners.firePropertyChange(event);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#addModifyListener(java.lang.Class, java.beans.PropertyChangeListener)
	 */
	@Override
	public void addModifyListener(Class<? extends Q> targetQueryKind,
		PropertyChangeListener listener)
	{
		PropertyChangeSupport queryListeners = listeners.get(targetQueryKind);
		if (queryListeners == null)
		{
			queryListeners = new PropertyChangeSupport(this);
			
			listeners.put(targetQueryKind, queryListeners);
		}
		
		if (! Arrays.asList(queryListeners.getPropertyChangeListeners()).contains(listener))
		{
			queryListeners.addPropertyChangeListener(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#removeModifyListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removeModifyListener(PropertyChangeListener listener)
	{
		for (Class<? extends Q> queryType : listeners.keySet())
		{
			removeModifyListener(queryType, listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jdo.query.QueryProvider#removeModifyListener(java.lang.Class, java.beans.PropertyChangeListener)
	 */
	@Override
	public void removeModifyListener(Class<? extends Q> targetQueryKind,
		PropertyChangeListener listener)
	{
		PropertyChangeSupport queryListeners = listeners.get(targetQueryKind);
		if (queryListeners != null)
		{
			queryListeners.removePropertyChangeListener(listener);
		}
	}

}
