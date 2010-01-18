/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jdo.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.query.AbstractJDOQuery;

/**
 * Abstract SearchFilter basically to hold {@link org.nightlabs.jdo.search.SearchFilterItem}s.<br/>
 * Subclasses have to execute the queries themselves.
 * 
 * @author Alexander Bieber
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public abstract class SearchFilter
	extends AbstractJDOQuery
{
	private static final long serialVersionUID = 1L;

	/**
	 * LOG4J logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(SearchFilter.class);

	public static final int CONJUNCTION_OR = 1;
	public static final int CONJUNCTION_AND = 2;
	public static final int CONJUNCTION_DEFAULT = CONJUNCTION_AND;
	
	private int conjunction;
	private List<ISearchFilterItem> filterItems = new ArrayList<ISearchFilterItem>();
	private List<SearchFilterListener> searchFilterListeners = new LinkedList<SearchFilterListener>();
	private Map<String, Object> paramMap;
	
	@SuppressWarnings("unused")
	private SearchFilter() {
	}
	
	protected SearchFilter(int conjunction) {
		this.conjunction = conjunction;
	}
	
	/**
	 * Is called by {@link #executeQuery(PersistenceManager)} to actually build the
	 * query before it is executed. Implementors should fill the parameters
	 * according to entries in {@link #getFilters()}. to form
	 * a functional query that can be executed with paramMap as parameters.
	 * 
	 * @param imports Instances of {@link Class} added here will be imported in the Query.
	 * @param vars Buffer to add variable declarations to.
	 * @param filter Buffer to add filter statements to.
	 * @param params Buffer to add parameter declaration to.
	 * @param paramMap Map to add parameter values to.
	 */
	protected abstract void prepareQuery(
			Set<Class<?>> imports,
			StringBuffer vars,
			StringBuffer filter,
			StringBuffer params,
			Map<String, Object> paramMap,
			StringBuffer result
		);

	/**
	 * 
	 * SearchFilters build  a search query here to
	 * find a collection of instances of a specific object.
	 * Executes {@link #doExecuteQuery(Set, StringBuffer, StringBuffer, StringBuffer)}
	 * to actually build the query.
	 * @param pm The PersistenceManager to execute the Query on
	 *
	 * @see #doExecuteQuery(Set, StringBuffer, StringBuffer, StringBuffer)
	 * @see #getExtentClass()
	 */
//	public Collection<?> executeQuery(PersistenceManager pm)
//	{
	@Override
	protected void prepareQuery(Query query)
	{
			Set<Class<?>> imports = new HashSet<Class<?>>();

			paramMap = new HashMap<String, Object>();
			StringBuffer vars = new StringBuffer();
			StringBuffer filter = new StringBuffer();
			StringBuffer params = new StringBuffer();
			StringBuffer result = new StringBuffer();
			// doExcecution
			prepareQuery(imports, vars, filter, params, paramMap, result);

			StringBuffer importsSB = new StringBuffer();
			for (Iterator<Class<?>> it = imports.iterator(); it.hasNext(); ) {
				Class<?> clazz = it.next();
				importsSB.append("import ");
				importsSB.append(clazz.getName());
				if (it.hasNext())
					importsSB.append("; ");
			}
			query.declareImports(importsSB.toString());
			query.declareVariables(vars.toString());
			query.declareParameters(params.toString());
			query.setFilter(filter.toString());
			if (result.length() > 0)
				query.setResult(result.toString());

			String parameters = "";
			for (Iterator<Map.Entry<String, Object>> iter = paramMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, Object> entry = iter.next();
				parameters += "("+entry.getKey()+", "+entry.getValue()+")";
			}
			logger.info("******************************************");
			logger.info(query.toString());
			logger.info("******************************************");
//			System.out.println(importsSB.toString()+"\n"+vars.toString()+"\n"+params.toString()+"\n"+parameters+"\n"+filter.toString());
//			return (Collection<?>)query.executeWithMap(paramMap);
//		} finally {
//			this.persistenceManager = null;
//		}
	}

	@Override
	protected Collection<?> executeQuery()
	{
		final Query q = createQuery();
		prepareQuery(q);
		return (Collection<?>) q.executeWithMap(paramMap);
	}

	
	/**
	 * Returns the list of {@link SearchFilterItem}
	 * @return
	 */
	public List<ISearchFilterItem> getFilters()
	{
		return filterItems;
	}
	
	/**
	 * Adds a {@link SearchFilterItem} to this list of filter items
	 * and notifies all SearchFilterListeners.
	 * 
	 * @param item
	 */
	public void addSearchFilterItem(ISearchFilterItem item) {
		filterItems.add(item);
		for (Iterator<SearchFilterListener> iter = searchFilterListeners.iterator(); iter.hasNext();) {
			SearchFilterListener listener = iter.next();
			listener.itemAdded(item);
		}
	}
	
	/**
	 * Removes a {@link SearchFilterItem} from this list of filter items
	 * and notifies all SearchFilterListeners.
	 * 
	 * @param item
	 */
	public void removeSearchFilterItem(ISearchFilterItem item) {
		filterItems.remove(item);
		for (Iterator<SearchFilterListener> iter = searchFilterListeners.iterator(); iter.hasNext();) {
			SearchFilterListener listener = iter.next();
			listener.itemRemoved(item);
		}
	}
	
	public void updateItem(ISearchFilterItem item) {
		for (Iterator<SearchFilterListener> iter = searchFilterListeners.iterator(); iter.hasNext();) {
			SearchFilterListener listener = iter.next();
			listener.updateItem(item);
		}
	}

	public int getConjunction() {
		return conjunction;
	}
	
	public void setConjunction(int conjunction) {
		if (conjunction != CONJUNCTION_AND && conjunction != CONJUNCTION_OR)
			throw new IllegalArgumentException("conjunction unknown! Must be CONJUNCTION_AND or CONJUNCTION_OR!");

		this.conjunction = conjunction;
	}
		
	/**
	 * Add a {@link SearchFilterListener} that will be notified
	 * if items were added or removed from this filter.
	 * 
	 * @param listener
	 */
	public void addChangeListener(SearchFilterListener listener) {
		searchFilterListeners.add(listener);
	}
	
	/**
	 * Removes the SearchFilterListener.
	 * 
	 * @param listener
	 */
	public void removeChangeListener(SearchFilterListener listener) {
		searchFilterListeners.remove(listener);
	}
	
	/**
	 * Removes all listeners and clears
	 * the list of filter items.
	 */
	public void clear() {
		searchFilterListeners.clear();
		filterItems.clear();
	}
}