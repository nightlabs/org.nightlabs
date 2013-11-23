package org.nightlabs.jdo.query;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A collection specifically made for Queries so that wrong results due to an incorrect execution
 * order or wrong minInclude, maxExclude bounds are prevented.
 * <p>Note: The execution order might furthermore be enhanced to start with the smallest tables
 * first to improve execution speed.</p>
 *
 * @param <Q>
 *          the type of the Query this collection will contain.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class QueryCollection<Q extends AbstractSearchQuery>
	extends AbstractCollection<Q>
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QueryCollection.class);

	/**
	 * The list of all queries combined by this one.
	 */
	private List<Q> queryList;

	/**
	 * The runtime information of the result class that all contained queries have to return.
	 */
	private transient Class<?> resultClass;
	private String resultClassName;

	/**
	 * Should only be used  for deserialization.
	 * @deprecated only for deserialization
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private QueryCollection() {}

	/**
	 * <p>
	 * <b>Note:</b> This constructor is only used by the XMLEncoder and should <b>NOT</b> be used
	 * programmatically!
	 * </p>
	 *
	 * @param resultClassName
	 *          The fully qualified class name of the result type.
	 */
	public QueryCollection(String resultClassName)
	{
		assert resultClassName != null && resultClassName.length() > 0;
		try
		{
			Class<?> resultClass = Class.forName(resultClassName);
			setResultClass(resultClass);
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(
				"Given classname is not known by this classloader! given className=" + resultClassName);
		}
	}

	public QueryCollection(Class<?> resultClass)
	{
		assert resultClass != null;
		setResultClass(resultClass);
	}

	public QueryCollection(Class<?> resultClass, Q firstQuery)
	{
		this(resultClass);
		add(firstQuery);
	}

	public QueryCollection(QueryCollection<? extends Q> original)
	{
		this(original.getResultClass());
		addAll(original.getManagedQueries());
		this.fromInclude = original.getFromInclude();
		this.toExclude = original.getToExclude();
	}

	protected Collection<Q> getManagedQueries()
	{
		checkCreateCollection();
		return queryList;
	}

	protected void checkCreateCollection()
	{
		if (queryList != null)
			return;

		queryList = new LinkedList<Q>();
	}

	public Collection<?> executeQueries()
	{
		checkCreateCollection();
		Collection<?> result = null;

		Collection<Q> queries = getManagedQueries();
		List<Q> queriesToExecute = new LinkedList<Q>();
		if (queries.size() > 1) {
			for (Q q : queries) {
				if (q.isConstraint()) {
					queriesToExecute.add(q);
				}
			}
			if (queriesToExecute.isEmpty())
				queriesToExecute.add(queries.iterator().next());
		}
		else
			queriesToExecute.addAll(queries);

		for (Iterator<Q> it = queriesToExecute.iterator(); it.hasNext();)
		{
			Q query = it.next();
			long executeQueryStartTimestamp = System.currentTimeMillis();

			// clear the bounds of all queries except the last one, otherwise incorrect results would be
			// the consequence.
			if (it.hasNext())
			{
				query.setFromInclude(0);
				query.setToExclude(Long.MAX_VALUE);
			} else
			{
				query.setFromInclude(getFromInclude());
				query.setToExclude(getToExclude());
			}

			if (result != null)
				query.setCandidates(result);

			result = query.getResult();

			if (logger.isDebugEnabled())
				logger.debug("executeQueries: query " + query.getClass().getName() + " took " + (System.currentTimeMillis() - executeQueryStartTimestamp) + " msec.");
		}

		if (result == null)
			return Collections.emptySet();

		return result;
	}

	@Override
	public boolean add(Q newQuery)
	{
		checkCreateCollection();

		if (!getResultClass().isAssignableFrom(newQuery.getResultClass()))
		{
			// This case seems to be impossible, but if the type information is ignored are cast to the
			// wrong type this case might occur!
			throw new IllegalStateException("The given query class does not deliver the expected Result "
				+ "of typ:" + getResultClass().getName() + " instead it yields elements of type:"
				+ newQuery.getResultClass().getName() + " !");
		}

		return getManagedQueries().add(newQuery);
	}

	@Override
	public Iterator<Q> iterator()
	{
		checkCreateCollection();
		return getManagedQueries().iterator();
	}

	@Override
	public int size()
	{
		checkCreateCollection();
		return getManagedQueries().size();
	}

	private long fromInclude = 0;
	private long toExclude = Long.MAX_VALUE;

	/**
	 * Returns the range beginning including the given value.
	 *
	 * @return the 0-based inclusive start index.
	 */
	public long getFromInclude()
	{
		return fromInclude;
	}

	/**
	 * sets the the range beginning including the given value.
	 *
	 * @param fromInclude
	 *          0-based inclusive start index.
	 */
	public void setFromInclude(long fromInclude)
	{
		this.fromInclude = fromInclude;
	}

	/**
	 * Returns the range end excluding the given value.
	 *
	 * @return the 0-based exclusive end index, or {@link Long#MAX_VALUE} for no limit.
	 */
	public long getToExclude()
	{
		return toExclude;
	}

	/**
	 * Sets the range end excluding the given value.
	 *
	 * @param toExclude
	 *          0-based exclusive end index, or {@link Long#MAX_VALUE} for no limit.
	 */
	public void setToExclude(long toExclude)
	{
		this.toExclude = toExclude;
	}

	/**
	 * @return The runtime information of the result class that all contained queries have to return.
	 */
	public Class<?> getResultClass()
	{
		if (resultClass == null)
		{
			if (resultClassName == null)
				return null;

			try
			{
				// This can only be class<R> since we don't allow any other class object to be set!
				resultClass = Class.forName(resultClassName);
			} catch (ClassNotFoundException e)
			{
				// FIXME: What to do if the result class is not available via this threads classloader?
				// (marius)
				throw new RuntimeException(e);
			}
		}

		return resultClass;
	}

	public void setResultClass(Class<?> resultClass)
	{
		assert resultClass != null;
		this.resultClass = resultClass;
		this.resultClassName = resultClass.getName();
	}

	/**
	 * @return The fully qualified class name of {@link #resultClass}.
	 */
	public String getResultClassName()
	{
		return resultClassName;
	}

}
