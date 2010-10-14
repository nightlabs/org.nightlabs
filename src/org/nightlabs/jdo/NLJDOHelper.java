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

package org.nightlabs.jdo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.listener.DeleteCallback;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.spi.PersistenceCapable;

import org.apache.log4j.Logger;
import org.nightlabs.datastructure.IdentityHashSet;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.reflect.ReflectUtil;

/**
 * @author Marco Schulze - Marco at NightLabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public final class NLJDOHelper
{
	private NLJDOHelper() { }

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(NLJDOHelper.class);

	public static final int MAX_FETCH_DEPTH_NO_LIMIT = -1;

	private static ThreadLocal<LinkedList<PersistenceManager>> persistenceManagerThreadLocal = new ThreadLocal<LinkedList<PersistenceManager>>();

	/**
	 * Sets the {@link PersistenceManager} of the current {@link Thread}.
	 *
	 * @param persistenceManager the {@link PersistenceManager} to be bound to the current <code>Thread</code>.
	 */
	public static void setThreadPersistenceManager(final PersistenceManager persistenceManager) {
		if (persistenceManager == null)
			throw new IllegalArgumentException("persistenceManager must not be null!");

		LinkedList<PersistenceManager> pmStack = persistenceManagerThreadLocal.get();
		if (pmStack == null) {
			pmStack = new LinkedList<PersistenceManager>();
			persistenceManagerThreadLocal.set(pmStack);
		}

		// clean up all closed PMs before adding our new one
		for (final Iterator<PersistenceManager> it = pmStack.iterator(); it.hasNext(); ) {
			if (it.next().isClosed())
				it.remove();
		}

		// add the current PM as FIRST one
		pmStack.addFirst(persistenceManager);
	}

	/**
	 * Get the {@link PersistenceManager} of the current {@link Thread}. This method
	 * returns the instance that has been passed to {@link #setThreadPersistenceManager(PersistenceManager)},
	 * but only, if it has not yet been closed; it never returns an instance of
	 * <code>PersistenceManager</code> where {@link PersistenceManager#isClosed()} is
	 * <code>true</code>.
	 * <p>
	 * This method delegates to {@link #getThreadPersistenceManager(boolean)} with
	 * <code>throwExceptionIfNotAvailable = true</code>.
	 * </p>
	 *
	 * @return the {@link PersistenceManager} bound to the current {@link Thread}.
	 * @throws IllegalStateException if there is no {@link PersistenceManager} bound to the current thread.
	 */
	public static PersistenceManager getThreadPersistenceManager() {
		return getThreadPersistenceManager(true);
	}

	/**
	 * Get the {@link PersistenceManager} of the current {@link Thread}. This method
	 * returns the instance that has been passed to {@link #setThreadPersistenceManager(PersistenceManager)},
	 * but only, if it has not yet been closed; it never returns an instance of
	 * <code>PersistenceManager</code> where {@link PersistenceManager#isClosed()} is
	 * <code>true</code>.
	 *
	 * @param throwExceptionIfNotAvailable whether to throw an <code>Exception</code> (rather than returning <code>null</code>), if there is no open <code>PersistenceManager</code> available in the current thread.
	 * @return the {@link PersistenceManager} bound to the current {@link Thread} or <code>null</code>, if there is none and <code>throwExceptionIfNotAvailable == false</code>.
	 * @throws IllegalStateException if there is no {@link PersistenceManager} bound to the current thread and
	 *		<code>throwExceptionIfNotAvailable</code> is <code>true</code>.
	 */
	public static PersistenceManager getThreadPersistenceManager(final boolean throwExceptionIfNotAvailable) {
		PersistenceManager pm = null;
		final LinkedList<PersistenceManager> pmStack = persistenceManagerThreadLocal.get();
		if (pmStack != null) {
			for (final Iterator<PersistenceManager> it = pmStack.iterator(); it.hasNext(); ) {
				pm = it.next();
				if (pm.isClosed()) {
					it.remove();
					pm = null;
				}
				if (pm != null)
					break;
			}
		}

		if (throwExceptionIfNotAvailable && pm == null)
			throw new IllegalStateException("There is no PersistenceManager bound to this thread or it has already been closed! Thread=" + Thread.currentThread());

		return pm;
	}

	/**
	 * Backup a fetch-plan in order to restore it later (after some client-code may have modified it).
	 *
	 * @param fetchPlan The fetch-plan to be backed-up - normally passed by: <code>myPersistenceManager.getFetchPlan()</code>
	 * @return a backup of the <code>fetchPlan</code>
	 * @see #restoreFetchPlan(FetchPlan, FetchPlanBackup)
	 */
	public static FetchPlanBackup backupFetchPlan(final FetchPlan fetchPlan)
	{
		return new FetchPlanBackup(fetchPlan);
	}
	/**
	 * Restore a previously backed-up fetch-plan.
	 *
	 * @param fetchPlan The fetch-plan to be modified - normally passed by: <code>myPersistenceManager.getFetchPlan()</code>
	 * @param fetchPlanBackup The backup-object created by {@link #backupFetchPlan(FetchPlan)}
	 * @see #backupFetchPlan(FetchPlan)
	 */
	public static void restoreFetchPlan(final FetchPlan fetchPlan, final FetchPlanBackup fetchPlanBackup)
	{
		fetchPlan.setDetachmentOptions(fetchPlanBackup.getDetachmentOptions());
		fetchPlan.setGroups(fetchPlanBackup.getGroups());
		fetchPlan.setFetchSize(fetchPlanBackup.getFetchSize());
		fetchPlan.setMaxFetchDepth(fetchPlanBackup.getMaxFetchDepth());

//		Caused by: org.jpox.exceptions.JPOXUserException: Detachment roots cannot be changed once set until commit is reached.
//		at org.jpox.FetchPlan.setDetachmentRoots(FetchPlan.java:253)
//		at org.jpox.jdo.JDOFetchPlan.setDetachmentRoots(JDOFetchPlan.java:238)
//		at org.nightlabs.jdo.NLJDOHelper.restoreFetchPlan(NLJDOHelper.java:84)
//		fetchPlan.setDetachmentRootClasses(fetchPlanBackup.getDetachmentRootClasses());
//		fetchPlan.setDetachmentRoots(fetchPlanBackup.getDetachmentRoots());
	}

	/**
	 * Checks whether an object exists in the datastore.
	 *
	 * @param pm The <tt>PersistenceManager</tt> to use.
	 * @param object The object to check.
	 * @return Returns <tt>true</tt>, if <tt>object</tt> has already been persisted into
	 *		and currently exists in the datastore
	 *		specified by <tt>pm</tt>. <tt>false</tt> otherwise.
	 */
	public static boolean exists(final PersistenceManager pm, final Object object)
	{
		if (pm == null)
			throw new IllegalArgumentException("pm must not be null!");

		if (object == null)
			throw new IllegalArgumentException("object must not be null!");

		final Object objectID = JDOHelper.getObjectId(object);
		if (objectID == null)
			return false;

//		pm.getExtent(object.getClass());
//		try {
//			pm.getObjectById(objectID);
//
//			return true;
//		} catch (JDOObjectNotFoundException x) {
//			return false;
//		}

//		// pm.getObjectById returns an object already when called in a jdoPreStore callback, hence we use a query instead
//		Query q = pm.newQuery(object.getClass());
//		q.setResult("count(this)");
//		q.setFilter("this == :object");
//		Long count = (Long) q.execute(object);
//		if (count.longValue() == 0)
//			return false;
//		else if (count.longValue() == 1)
//			return true;
//		else
//			throw new IllegalStateException("Query returned an impossible count: " + count.longValue());

		// just had an NPE - trying it without count(...) // TODO investigate this later some day
//		Query q = pm.newQuery(object.getClass());
//		q.setFilter("this == :object");
//		Collection<?> c = (Collection<?>) q.execute(object);
//		if (c.size() == 0)
//			return false;
//		else if (c.size() == 1)
//			return true;
//		else
//			throw new IllegalStateException("Query returned an impossible number of elements: " + c.size());

// Just had the below error with the above code.
//		12:29:42,899 INFO  [JDO] Exception thrown
//		Parameter for query ("org.nightlabs.jfire.simpletrade.store.SimpleProductType@12d03f6[reseller.jfire.org,corkscrew-stainless-steel]") is bound to a different manager than this query!
//		org.datanucleus.exceptions.NucleusUserException: Parameter for query ("org.nightlabs.jfire.simpletrade.store.SimpleProductType@12d03f6[reseller.jfire.org,corkscrew-stainless-steel]") is bound to a different manager than this query!
//		        at org.datanucleus.store.rdbms.query.JDOQLQueryCompiler.compile(JDOQLQueryCompiler.java:196)
//		        at org.datanucleus.store.rdbms.query.JDOQLQueryCompiler.compile(JDOQLQueryCompiler.java:223)
//		        at org.datanucleus.store.rdbms.query.JDOQLQuery.compileInternal(JDOQLQuery.java:169)
//		        at org.datanucleus.store.query.Query.executeQuery(Query.java:1384)
//		        at org.datanucleus.store.rdbms.query.JDOQLQuery.executeQuery(JDOQLQuery.java:226)
//		        at org.datanucleus.store.query.Query.executeWithArray(Query.java:1325)
//		        at org.datanucleus.store.query.Query.execute(Query.java:1275)
//		        at org.datanucleus.jdo.JDOQuery.execute(JDOQuery.java:237)
//		        at org.nightlabs.jdo.NLJDOHelper.exists(NLJDOHelper.java:224)
//		        at org.nightlabs.jfire.simpletrade.SimpleTradeManagerBean.storeProductType(SimpleTradeManagerBean.java:344)
//		        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//		        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
//		        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
//		        at java.lang.reflect.Method.invoke(Method.java:597)
//		        at org.jboss.invocation.Invocation.performCall(Invocation.java:359)
//		        at org.jboss.ejb.StatelessSessionContainer$ContainerInterceptor.invoke(StatelessSessionContainer.java:237)
//		        at org.nightlabs.jfire.jboss.transaction.ForceRollbackOnExceptionInterceptor.invoke(ForceRollbackOnExceptionInterceptor.java:40)
//		        at org.jboss.resource.connectionmanager.CachedConnectionInterceptor.invoke(CachedConnectionInterceptor.java:158)
//		        at org.jboss.ejb.plugins.StatelessSessionInstanceInterceptor.invoke(StatelessSessionInstanceInterceptor.java:169)
//		        at org.jboss.ejb.plugins.CallValidationInterceptor.invoke(CallValidationInterceptor.java:63)
//		        at org.jboss.ejb.plugins.AbstractTxInterceptor.invokeNext(AbstractTxInterceptor.java:121)
//		        at org.jboss.ejb.plugins.TxInterceptorCMT.runWithTransactions(TxInterceptorCMT.java:350)
//		        at org.jboss.ejb.plugins.TxInterceptorCMT.invoke(TxInterceptorCMT.java:181)
//		        at org.jboss.ejb.plugins.SecurityInterceptor.invoke(SecurityInterceptor.java:168)
//		        at org.jboss.ejb.plugins.LogInterceptor.invoke(LogInterceptor.java:205)
//		        at org.jboss.ejb.plugins.ProxyFactoryFinderInterceptor.invoke(ProxyFactoryFinderInterceptor.java:138)
//		        at org.jboss.ejb.SessionContainer.internalInvoke(SessionContainer.java:648)
//		        at org.jboss.ejb.Container.invoke(Container.java:960)
//		        at sun.reflect.GeneratedMethodAccessor126.invoke(Unknown Source)
//		        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
//		        at java.lang.reflect.Method.invoke(Method.java:597)
//		        at org.jboss.mx.interceptor.ReflectedDispatcher.invoke(ReflectedDispatcher.java:155)
//		        at org.jboss.mx.server.Invocation.dispatch(Invocation.java:94)
//		        at org.jboss.mx.server.Invocation.invoke(Invocation.java:86)
//		        at org.jboss.mx.server.AbstractMBeanInvoker.invoke(AbstractMBeanInvoker.java:264)
//		        at org.jboss.mx.server.MBeanServerImpl.invoke(MBeanServerImpl.java:659)
//		        at org.jboss.invocation.unified.server.UnifiedInvoker.invoke(UnifiedInvoker.java:231)
//		        at sun.reflect.GeneratedMethodAccessor194.invoke(Unknown Source)
//		        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
//		        at java.lang.reflect.Method.invoke(Method.java:597)
//		        at org.jboss.mx.interceptor.ReflectedDispatcher.invoke(ReflectedDispatcher.java:155)
//		        at org.jboss.mx.server.Invocation.dispatch(Invocation.java:94)
//		        at org.jboss.mx.server.Invocation.invoke(Invocation.java:86)
//		        at org.jboss.mx.server.AbstractMBeanInvoker.invoke(AbstractMBeanInvoker.java:264)
//		        at org.jboss.mx.server.MBeanServerImpl.invoke(MBeanServerImpl.java:659)
//		        at javax.management.MBeanServerInvocationHandler.invoke(MBeanServerInvocationHandler.java:288)
//		        at $Proxy16.invoke(Unknown Source)
//		        at org.jboss.remoting.ServerInvoker.invoke(ServerInvoker.java:769)
//		        at org.jboss.remoting.transport.socket.ServerThread.processInvocation(ServerThread.java:573)
//		        at org.jboss.remoting.transport.socket.ServerThread.dorun(ServerThread.java:387)
//		        at org.jboss.remoting.transport.socket.ServerThread.run(ServerThread.java:166)

		final Query q = pm.newQuery(object.getClass());
		q.setFilter("JDOHelper.getObjectId(this) == :objectID");
		final Collection<?> c = (Collection<?>) q.execute(objectID);
		if (c.size() == 0)
			return false;
		else if (c.size() == 1)
			return true;
		else {
			// WORKAROUND DataNucleus sometimes retuns collection with size 2 and same objects
			final HashSet<?> set = new HashSet(c);
			if (set.size() == 1)
				return true;
			else
				throw new IllegalStateException("Query returned an impossible number of elements: " + c.size());
		}
	}

	/**
	 * @param pm The PersistenceManager to use.
	 * @param object The Object to store.
	 * @param get Whether or not to retrieve and detach a copy of the given object. If <tt>false</tt>, this method returns <tt>null</tt>.
	 * @param groups Either <tt>null</tt> or a <tt>String</tt> array of groups to be used for detaching the object (if <tt>get</tt> is <tt>true</tt>).
	 * @return Returns a detached copy of <tt>object</tt>, if <tt>get</tt> is <tt>true</tt>; otherwise <tt>null</tt>.
	 */
	public static <T> T storeJDO(
			final PersistenceManager pm, T object, final boolean get, final String[] fetchGroups, final int maxFetchDepth)
	{
		pm.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
		if (fetchGroups != null)
			pm.getFetchPlan().setGroups(fetchGroups);

		object = pm.makePersistent(object);

		if (!get)
			return null;

		return pm.detachCopy(object);
	}

	/**
	 * @param pm The PersistenceManager to use.
	 * @param objects The {@link Collection} of objects to persist.
	 * @param get Whether or not to retrieve and detach a copy of the given object. If <tt>false</tt>, this method returns <tt>null</tt>.
	 * @param groups Either <tt>null</tt> or a <tt>String</tt> array of groups to be used for detaching the object (if <tt>get</tt> is <tt>true</tt>).
	 * @return Returns a detached copy of <tt>objects</tt>, if <tt>get</tt> is <tt>true</tt>; otherwise <tt>null</tt>.
	 */
	public static <T> Collection<T> storeJDOCollection(
			final PersistenceManager pm, Collection<T> objects, final boolean get, final String[] fetchGroups, final int maxFetchDepth)
	{
		pm.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
		if (fetchGroups != null)
			pm.getFetchPlan().setGroups(fetchGroups);

		objects = pm.makePersistentAll(objects);

		if (!get)
			return null;

		final Collection<T> c = pm.detachCopyAll(objects);
		assertHasObjectIDAssigned(c);
		return c;
	}

	/**
	 * Returns the number of objects known by the given PersistenceMangager of the
	 * given class. If includeSubclasses is true the number of instances of all
	 * sublcasses will be added the result. Optionally a filter can be applied to
	 * instances of the queried class to constrain the result.
	 *
	 * @param pm The persitsenceManager to ask
	 * @param objectClass The Class instances of which should be counted
	 * @param filter A jdoql filter to constrain the result, might be null or empty
	 * @param memberName The name of one pk-field if a composite primary field, null otherwise
	 * @param includeSubclasses Whether to include the number of subclass-instances
	 * @return The number of knonw objects of the given class.
	 */
	public static long getObjectCount(
			final PersistenceManager pm,
			final Class<?> objectClass,
			final String filter,
			final String memberName,
			final boolean includeSubclasses
	) {
		final Query query = pm.newQuery(pm.getExtent(objectClass, includeSubclasses));
		// WORKAROUND: Remove membername and switch to count(this) when JPOX bug is fixed
		query.setResult("count("+ ((memberName != null) ? memberName : "this") +")");
		if (filter != null && !"".equals(filter))
			query.setFilter(filter);
		return ((Long)query.execute()).longValue();
//		Collection result = (Collection)query.execute();
//		if (result.isEmpty())
//		throw new IllegalStateException("count(this) returned empty result for object on Extent");
//		return ((Long)result.iterator().next()).longValue();
	}

	/**
	 * Returns the object count with no filter, that means the count of all
	 * objects of the given type.
	 *
	 * @param pm The PersistenceManager to use
	 * @param objectClass The Class instances of which should be counted
	 * @param includeSubclasses Whether to include the number of subclass-instances
	 * @see #getObjectCount(PersistenceManager, Class, String, String, boolean)
	 */
	public static long getObjectCount(
			final PersistenceManager pm,
			final Class<?> objectClass,
			final boolean includeSubclasses
	) {
		return getObjectCount(pm, objectClass, null, null, includeSubclasses);
	}

//	/**
//	* @deprecated Use {@link #getDetachedObjectList(PersistenceManager, Object[], Object, String[])} instead.
//	*/
//	public static Collection getObjectsByIDs(PersistenceManager pm, Object[] objectIDs, String[] groups, int maxFetchDepth)
//	{
//	if (groups != null)
//	pm.getFetchPlan().setGroups(groups);
//	Collection ret = new HashSet();
//	for(int i = 0; i < objectIDs.length; i++)
//	ret.add(pm.getObjectById(objectIDs[i]));
//	return pm.detachCopyAll(ret);
//	}

//	/**
//	 * @param pm The accessor to the datastore.
//	 * @param objectIDs Instance of JDO object IDs.
//	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
//	 * @return Returns the detached JDO objects that correspond to the given <code>objectIDs</code>.
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> List<T> getDetachedObjectList(PersistenceManager pm, Object[] objectIDs, Object classes, String[] groups, int maxFetchDepth)
//	{
//		return (List<T>) getDetachedObjectList(pm, CollectionUtil.array2ArrayList(objectIDs), classes, groups, maxFetchDepth);
//	}

	/**
	 * This is more-or-less a temporary check, since DataNucleus seems to sometimes return detached objects not having an object-id assigned.
	 * TODO remove this workaround, when it is not necessary anymore!
	 */
	private static void assertHasObjectIDAssigned(final Collection<?> c)
	{
		for (final Object object : c) {
			if (JDOHelper.getObjectId(object) == null)
				throw new IllegalStateException("Object has no object-id assigned: " + object);
		}
	}

//	/**
//	 * This method calls {@link #getDetachedObjectList(PersistenceManager, Collection, Object, String[], int, boolean)} with
//	 * <code>silentlyIgnoreMissingObjects == true</code>.
//	 *
//	 * @param pm The accessor to the datastore.
//	 * @param objectIDs Instance of JDO object IDs.
//	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
//	 * @return Returns the detached JDO objects that correspond to the given <code>objectIDs</code>.
//	 */
//	public static <T> List<T> getDetachedObjectList(PersistenceManager pm, Collection<?> objectIDs, Object classes, String[] fetchGroups, int maxFetchDepth)
//	{
//		return getDetachedObjectList(pm, objectIDs, classes, fetchGroups, maxFetchDepth, true);
//	}

//	/**
//	 * This method calls {@link #getDetachedObjectSet(PersistenceManager, Collection, Object, String[], int, boolean)} with
//	 * <code>silentlyIgnoreMissingObjects == true</code>.
//	 *
//	 * @param pm The accessor to the datastore.
//	 * @param objectIDs Instance of JDO object IDs.
//	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
//	 * @return Returns the detached JDO objects that correspond to the given <code>objectIDs</code>.
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> Set<T> getDetachedObjectSet(PersistenceManager pm, Collection objectIDs, Object classes, String[] fetchGroups, int maxFetchDepth)
//	{
//		return getDetachedObjectSet(pm, objectIDs, classes, fetchGroups, maxFetchDepth, true);
//	}

	/**
	 * @param pm The accessor to the datastore.
	 * @param objectIDs Instance of JDO object IDs.
	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
	 * @param silentlyIgnoreMissingObjects If <code>true</code>, occuring {@link JDOObjectNotFoundException}s will be silently ignored and the
	 *		failed objects will simply be missing in the result. If <code>false</code>, occuring {@link JDOObjectNotFoundException}s will be escalated.
	 * @return Returns the detached JDO objects that correspond to the given <code>objectIDs</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getDetachedObjectList(final PersistenceManager pm, final Collection objectIDs, final Object classes, final String[] fetchGroups, final int maxFetchDepth, final QueryOption ... options)
	{
		pm.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
		if (fetchGroups != null)
			pm.getFetchPlan().setGroups(fetchGroups);

		final Collection c = pm.detachCopyAll(getObjectList(pm, objectIDs, classes, options));
		assertHasObjectIDAssigned(c);
		if (c instanceof List)
			return (List<T>)c;
		else
			return new ArrayList<T>(c);
	}

	/**
	 * @param pm The accessor to the datastore.
	 * @param objectIDs Instance of JDO object IDs.
	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
	 * @param silentlyIgnoreMissingObjects If <code>true</code>, occuring {@link JDOObjectNotFoundException}s will be silently ignored and the
	 *		failed objects will simply be missing in the result. If <code>false</code>, occuring {@link JDOObjectNotFoundException}s will be escalated.
	 * @return Returns the detached JDO objects that correspond to the given <code>objectIDs</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getDetachedObjectSet(final PersistenceManager pm, final Collection objectIDs, final Object classes, final String[] fetchGroups, final int maxFetchDepth, final QueryOption ... options)
	{
		pm.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
		if (fetchGroups != null)
			pm.getFetchPlan().setGroups(fetchGroups);

		final Collection c = pm.detachCopyAll(getObjectSet(pm, objectIDs, classes, options));
		assertHasObjectIDAssigned(c);
		if (c instanceof Set)
			return (Set<T>)c;
		else
			return new HashSet<T>(c);
	}

	@SuppressWarnings("unchecked")
	private static void _loadObjects(final Extent extent, final Collection objects)
	{
		for (final Iterator iter = extent.iterator(); iter.hasNext();)
			objects.add(iter.next());
	}

	@SuppressWarnings("unchecked")
	private static HashSet<Class<?>> _getValidClassesSet(final Object classes)
	{
		HashSet<Class<?>> res; // = new HashSet<Class>();
		if (classes instanceof Class) {
			res = new HashSet<Class<?>>(1);
			res.add((Class) classes);
//			Class clazz = (Class) classes;
//			while (clazz != null) {
//			res.add(clazz);
//			clazz = clazz.getSuperclass();
//			}
		}
		else if (classes instanceof Collection) {
			res = new HashSet<Class<?>>();
			final Collection<Class<?>> clazzes = (Collection<Class<?>>) classes;
			for (final Class clazz : clazzes) {
				res.add(clazz);
//				while (clazz != null) {
//				res.add(clazz);
//				clazz = clazz.getSuperclass();
//				}
			}
		}
		else
			throw new IllegalStateException("classes is neither an instance of Class nor Collection!");

		return res;
	}

	private static boolean _isClassValid(final Set<Class<?>> validClasses, final Class<?> classOrInterface)
	{
		Class<?> clazz = classOrInterface;
		while (clazz != null) {
			if (validClasses.contains(clazz))
				return true;

			// check interfaces
			for (final Class<?> iface : clazz.getInterfaces()) {
				if (_isClassValid(validClasses, iface))
					return true;
			}

			clazz = clazz.getSuperclass();
		}

		return false;
	}

	private static void _checkObjectClass(final Set<Class<?>> validClasses, final Object instance)
	{
		if (instance == null)
			return;

		// If we have a small number of classes, it's probably faster to iterate them and ask the JVM whether the instance is really an instance
		// of one of the classes. But if there are more classes, we traverse the instance's class-graph with all interfaces and check whether the set contains one of them.
		if (validClasses.size() <= 5) {
			for (final Class<?> vc : validClasses) {
				if (vc.isInstance(instance))
					return;
			}
		}
		else {
			if (_isClassValid(validClasses, instance.getClass()))
				return;
//			Class<?> clazz = instance.getClass();
//			while (clazz != null) {
//				if (validClasses.contains(clazz))
//					return;
//
//				clazz = clazz.getSuperclass();
//			}
		}

		final StringBuffer vcb = new StringBuffer();
		for (final Class<?> vc : validClasses) {
			if (vcb.length() > 0)
				vcb.append('|');

			vcb.append(vc.getName());
		}

		throw new IllegalStateException("Object is not an instance of a valid class (expected " + vcb.toString() + " but found " + instance.getClass().getName() + "): " + JDOHelper.getObjectId(instance));
	}

//	/**
//	 * This method calls {@link #getObjectList(PersistenceManager, Collection, Object, boolean)} with
//	 * <code>silentlyIgnoreMissingObjects == true</code>.
//	 *
//	 * @param pm The accessor to the datastore.
//	 * @param objectIDs Instance of JDO object IDs. Can be <code>null</code>, if <code>classes</code> is specified, which means to retrieve ALL objects of the given classes (including their subclasses).
//	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
//	 * @return Returns the JDO objects that correspond to the given <code>objectIDs</code>.
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> List<T> getObjectList(PersistenceManager pm, Collection<?> objectIDs, Object classes)
//	{
//		return getObjectList(pm, objectIDs, classes, true);
//	}

	/**
	 * @param pm The accessor to the datastore.
	 * @param objectIDs Instance of JDO object IDs. Can be <code>null</code>, if <code>classes</code> is specified, which means to retrieve ALL objects of the given classes (including their subclasses).
	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
	 * @param silentlyIgnoreMissingObjects If <code>true</code>, occuring {@link JDOObjectNotFoundException}s will be silently ignored and the
	 *		failed objects will simply be missing in the result. If <code>false</code>, occuring {@link JDOObjectNotFoundException}s will be escalated.
	 * @return Returns the JDO objects that correspond to the given <code>objectIDs</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getObjectList(final PersistenceManager pm, final Collection<?> objectIDs, final Object classes, final QueryOption ... options)
	{
		if (objectIDs == null && classes == null)
			throw new IllegalArgumentException("objectIDs and classes cannot both be null!");

		final Set<QueryOption> optionSet = CollectionUtil.array2HashSet(options, false);

		final List<T> res = objectIDs == null ? new ArrayList<T>() : new ArrayList<T>(objectIDs.size());
		Set<Class<?>> validClasses = null;
		if (classes != null) {
			if (classes instanceof Collection) {
				for (final Iterator<?> iter = ((Collection<?>)classes).iterator(); iter.hasNext();) {
					final Class<?> clazz = (Class<?>) iter.next();
					Extent<?> extent = null;
					if (PersistenceCapable.class.isAssignableFrom(clazz))
						extent = pm.getExtent(clazz);

					if (objectIDs == null && extent != null)
						_loadObjects(extent, res);
				}
			}
			else if (classes instanceof Class) {
				Extent<?> extent = null;
				final Class<?> clazz = (Class<?>)classes;
				if (PersistenceCapable.class.isAssignableFrom(clazz))
					extent = pm.getExtent(clazz);

				if (objectIDs == null && extent != null)
					_loadObjects(extent, res);
			}
			else
				throw new IllegalArgumentException(
						"Param classes is neither an instance of Class nor of Collection, but: " + classes.getClass().getName());

			validClasses = _getValidClassesSet(classes);
		}

		Set<Class<?>> knownValidClasses = null;

		// check if only one result type is wanted, if so force the PersitenceManager to only
		// search for this kind of class. If this check is omitted, then there might occur Problems
		// in class hierarchies such as A <- B, where A and B have the same primary key.
		// In this case when there is an A object 'a' with key 'k(a)' and you search for a B object
		// with the same key then pm.getObjectById(key(a)) will return a instead of null!
		// Hence, this fix will only prohibit this error when only one class is given, if there are
		// several we might still run into this kind of problems! (marius)
		boolean onlyOneResultClass = false;
		Class<?> resultClass = null;
		if (validClasses != null && validClasses.size() == 1)
		{
			onlyOneResultClass = true;
			resultClass = validClasses.iterator().next();

			if (!PersistenceCapable.class.isAssignableFrom(resultClass)) {
				onlyOneResultClass = false;
				resultClass = null;
			}
		}

		if (objectIDs != null) {
			iterateObjectIDs: for (final Iterator<?> iter = objectIDs.iterator(); iter.hasNext();) {
				final Object oID = iter.next();
				T o;
				try {
					if (validClasses != null && onlyOneResultClass)
						o = (T)pm.getObjectById(resultClass, oID.toString()); // need to pass the string-representation of the objectid here unfortunately, see jdo javadoc
					else
						o = (T)pm.getObjectById(oID);
				} catch (final JDOObjectNotFoundException x) {
					if (optionSet.contains(QueryOption.throwExceptionOnMissingObject))
						throw x;
					else
						continue iterateObjectIDs;
				}
				if (validClasses != null) {
					if (knownValidClasses == null)
						knownValidClasses = new HashSet<Class<?>>();

					if (o != null && !knownValidClasses.contains(o.getClass())) {
						_checkObjectClass(validClasses, o); // throws an exception, if not valid
						knownValidClasses.add(o.getClass());
					}
				}
				res.add(o);
			} // iterateObjectIDs
		}
		return res;
	}

//	/**
//	 * This method calls {@link #getObjectSet(PersistenceManager, Collection, Object, boolean)} with
//	 * <code>silentlyIgnoreMissingObjects == true</code>.
//	 *
//	 * @param pm The accessor to the datastore.
//	 * @param objectIDs Instance of JDO object IDs. Can be <code>null</code>, if <code>classes</code> is specified, which means to retrieve ALL objects of the given classes (including their subclasses).
//	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
//	 * @return Returns the JDO objects that correspond to the given <code>objectIDs</code>.
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> Set<T> getObjectSet(PersistenceManager pm, Collection<?> objectIDs, Object classes)
//	{
//		return getObjectSet(pm, objectIDs, classes, true);
//	}

	/**
	 * @param pm The accessor to the datastore.
	 * @param objectIDs Instance of JDO object IDs. Can be <code>null</code>, if <code>classes</code> is specified, which means to retrieve ALL objects of the given classes (including their subclasses).
	 * @param classes Can be <code>null</code>! Otherwise either a single instance of {@link java.lang.Class} or a {@link Collection} of {@link java.lang.Class}.
	 * @param silentlyIgnoreMissingObjects If <code>true</code>, occuring {@link JDOObjectNotFoundException}s will be silently ignored and the
	 *		failed objects will simply be missing in the result. If <code>false</code>, occuring {@link JDOObjectNotFoundException}s will be escalated.
	 * @return Returns the JDO objects that correspond to the given <code>objectIDs</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getObjectSet(final PersistenceManager pm, Collection<?> objectIDs, final Object classes, final QueryOption ... options)
	{
		if (objectIDs == null && classes == null)
			throw new IllegalArgumentException("objectIDs and classes cannot both be null!");

		final Set<QueryOption> optionSet = CollectionUtil.array2HashSet(options, false);

		final Set<T> res = objectIDs == null ? new HashSet<T>() : new HashSet<T>(objectIDs.size());
		Set<Class<?>> validClasses = null;
		if (classes != null) {
			if (classes instanceof Collection) {
				for (final Iterator iter = ((Collection)classes).iterator(); iter.hasNext();) {
					final Class clazz = (Class) iter.next();
					final Extent extent = pm.getExtent(clazz);
					if (objectIDs == null)
						_loadObjects(extent, res);
				}
			}
			else if (classes instanceof Class) {
				final Extent extent = pm.getExtent((Class)classes);
				if (objectIDs == null)
					_loadObjects(extent, res);
			}
			else
				throw new IllegalArgumentException(
						"Param classes is neither an instance of Class nor of Collection, but: " + classes.getClass().getName());

			validClasses = _getValidClassesSet(classes);
		}

		if (objectIDs != null) {
			if (!(objectIDs instanceof Set))
				objectIDs = new HashSet<Object>(objectIDs);

			Set<Class> knownValidClasses = null;

			iterateObjectIDs: for (final Iterator iter = objectIDs.iterator(); iter.hasNext();) {
				final Object oID = iter.next();
				T o;
				try {
					o = (T)pm.getObjectById(oID);
				} catch (final JDOObjectNotFoundException x) {
					if (optionSet.contains(QueryOption.throwExceptionOnMissingObject))
						throw x;
					else
						continue iterateObjectIDs;
				}
				if (validClasses != null) {
					if (knownValidClasses == null)
						knownValidClasses = new HashSet<Class>();

					if (o != null && !knownValidClasses.contains(o.getClass())) {
						_checkObjectClass(validClasses, o); // throws an exception, if not valid
						knownValidClasses.add(o.getClass());
					}
				}
				res.add(o);
			} // iterateObjectIDs
		}
		return res;
	}

	/**
	 * Get a {@link List} containing the ObjectIDs for all the given
	 * {@link PersistenceCapable} objects in the given collection.
	 * Note, that for all non-{@link PersistenceCapable} objects found
	 * in the given collection the original object will be put into
	 * the result list.
	 *
	 * @param <T> The type of ObjectID expected.
	 * @param objects The collection of Objects to get the ids for.
	 * @return Get a {@link List} containing the ObjectIDs for all the given {@link PersistenceCapable} objects.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getObjectIDList(final Collection<?> objects)
	{
		final List<T> res = new ArrayList<T>(objects.size());
		for (final Iterator iter = objects.iterator(); iter.hasNext();) {
			final Object o = iter.next();
			// TODO WORKAROUND: This is a workaround for a situation when this method is called with instances of ObjectID. Thus, the code should be changed NOT to call this method and this check should be reverted (no instanceof check!).
			if (o instanceof PersistenceCapable)
				res.add((T)JDOHelper.getObjectId(o));
			else
				res.add((T)o);
		}
		return res;
	}

	/**
	 * Get a {@link Set} containing the ObjectIDs for all the given
	 * {@link PersistenceCapable} objects in the given collection.
	 * Note, that for all non-{@link PersistenceCapable} objects found
	 * in the given collection the original object will be put into
	 * the result set.
	 *
	 * @param <T> The type of ObjectID expected.
	 * @param objects The collection of Objects to get the ids for.
	 * @return Get a {@link Set} containing the ObjectIDs for all the given {@link PersistenceCapable} objects.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getObjectIDSet(final Collection<?> objects)
	{
		final Set<T> res = new HashSet<T>(objects.size());
		for (final Iterator iter = objects.iterator(); iter.hasNext();) {
			final Object o = iter.next();
			// TODO WORKAROUND: This is a workaround for a situation when this method is called with instances of ObjectID. Thus, the code should be changed NOT to call this method and this check should be reverted (no instanceof check!).
			if (o instanceof PersistenceCapable)
				res.add((T)JDOHelper.getObjectId(o));
			else
				res.add((T)o);
		}
		return res;
	}

	/**
	 * Returns a serialisable result of a JDO {@link Query}.
	 * @param <T> the Type of the result
	 * @param pm the {@link PersistenceManager} used to detach the given object if necessary
	 * @param queryResult the result of a JDO {@link Query}.
	 * @returns a serialisable Collection with the content of the given <code>queryResult</code>.
	 */
	public static <T> List<T> getDetachedQueryResultAsList(final PersistenceManager pm, final T queryResult) {
		return (List<T>) getDetachedQueryResultInternal(pm, queryResult, new ArrayList<T> (1));
	}

	/**
	 * Returns a serialisable result of a JDO {@link Query}.
	 * @param <T> the Type of the result
	 * @param pm the {@link PersistenceManager} used to detach the given object if necessary
	 * @param queryResult the result of a JDO {@link Query}.
	 * @returns a serialisable Collection with the content of the given <code>queryResult</code>.
	 */
	public static <T> Set<T> getDetachedQueryResultAsSet(final PersistenceManager pm, final T queryResult) {
		return (Set<T>) getDetachedQueryResultInternal(pm, queryResult, new HashSet<T> (1));
	}

	/**
	 * Returns a serialisable {@link List} with the contents of the given <code>queryResult</code>.
	 * @param <T> the Type of Elements in the queryResult
	 * @param pm the {@link PersistenceManager} used to detach the given Objects if necessary
	 * @param queryResult the result of a JDO {@link Query}.
	 * @returns a serialisable Collection with the contents of the given <code>queryResult</code>.
	 */
	public static <T> List<T> getDetachedQueryResultAsList(final PersistenceManager pm, final Collection<T> queryResult) {
		if (queryResult == null  || queryResult.isEmpty())
			return new ArrayList<T>();
		return (List<T>) getDetachedQueryResultInternal(pm, queryResult, new ArrayList<T> (queryResult.size()));
	}

	/**
	 * Returns a serialisable {@link List} with the contents of the given <code>queryResult</code>.
	 * @param <T> the Type of Elements in the queryResult
	 * @param pm the {@link PersistenceManager} used to detach the given Objects if necessary
	 * @param queryResult the result of a JDO {@link Query}.
	 * @returns a serialisable Collection with the contents of the given <code>queryResult</code>. Never returns <code>null</code>.
	 */
	public static <T> Set<T> getDetachedQueryResultAsSet(final PersistenceManager pm, final Collection<T> queryResult) {
		if (queryResult == null  || queryResult.isEmpty())
			return new HashSet<T>();
		return (Set<T>) getDetachedQueryResultInternal(pm, queryResult, new HashSet<T> (queryResult.size()));
	}

	/**
	 * Get a {@link Collection} populated with the detached <code>queryResult</code> - i.e. one single element.
	 * If <code>queryResult</code> is <code>null</code>, the result contains one element being <code>null</code>.
	 *
	 * @param <T> the type of the <code>queryResult</code>.
	 * @param pm the gate to the datastore.
	 * @param queryResult the result as returned by a query.
	 * @param result the resulting collection. It will be populated and returned.
	 * @return the result after it has been populated.
	 */
	@SuppressWarnings("unchecked")
	protected static <T> Collection<T> getDetachedQueryResultInternal(final PersistenceManager pm, final T queryResult, final Collection<T> result) {
		if (queryResult == null) {
			result.add(null);
			return result;
		}

		final Class<T> clazz = (Class<T>) queryResult.getClass();
		if (clazz.isArray()) {
			final int size = Array.getLength(queryResult);
//			For information about this Warning see:
//			http://www.angelikalanger.com/GenericsFAQ/FAQSections/ParameterizedTypes.html#How%20can%20I%20work%20around%20the%20restriction%20that%20there%20are%20no%20arrays%20whose%20component%20type%20is%20a%20concrete%20instantiation%20of%20a
			final T newArray = (T) Array.newInstance(clazz.getComponentType(), size); // Warning is ok, it is still type safe
			for (int i = 0; i < size; i++) {
				if (Array.get(queryResult, i) instanceof PersistenceCapable)
					Array.set(newArray, i, clazz.getComponentType().cast(pm.detachCopy(Array.get(queryResult, i))));
				else
					Array.set(newArray, i, Array.get(queryResult, i));
			}
			result.add(newArray);
		} else { // T is no array
			if (queryResult instanceof PersistenceCapable)
				result.add((clazz.cast(pm.detachCopy(queryResult))));
			else
				result.add(queryResult);
		}
		return result;
	}

	/**
	 * Get a {@link Collection} populated with the detached elements of <code>queryResult</code>.
	 *
	 * @param <T> the type of the <code>queryResult</code>.
	 * @param pm the gate to the datastore.
	 * @param queryResult the result as returned by a query.
	 * @param result the resulting collection. It will be populated and returned.
	 * @return the result after it has been populated.
	 */
	@SuppressWarnings("unchecked")
	protected static <T> Collection<T> getDetachedQueryResultInternal(final PersistenceManager pm, final Collection<T> queryResult, final Collection<T> result)
	{
		if (queryResult == null || queryResult.isEmpty())
			return result;

//		Class<T> innerClass = (Class<T>) queryResult.iterator().next().getClass();

		for (final Iterator<T> iter = queryResult.iterator(); iter.hasNext();) {
			final T element = iter.next();
			if (element == null)
				result.add(null);
			else {
				final Class<T> innerClass = (Class<T>) element.getClass();
				if (innerClass.isArray()) {
					final int size = Array.getLength(element);
	//				For information about this Warning see:
	//				http://www.angelikalanger.com/GenericsFAQ/FAQSections/ParameterizedTypes.html#How%20can%20I%20work%20around%20the%20restriction%20that%20there%20are%20no%20arrays%20whose%20component%20type%20is%20a%20concrete%20instantiation%20of%20a
					final T newArray = (T) Array.newInstance(innerClass.getComponentType(), size); // Warning is ok, it is still type safe
					for (int i = 0; i < size; i++) {
						if (Array.get(element, i) instanceof PersistenceCapable)
							Array.set(newArray, i, innerClass.getComponentType().cast(pm.detachCopy(Array.get(element, i))));
						else
							Array.set(newArray, i, Array.get(element, i));
					}
					result.add(newArray);
				} else { // T is no array
					if (element instanceof PersistenceCapable) {
						result.add(innerClass.cast(pm.detachCopy(element)));
					} else {
						result.add(element);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Mark an entire object graph dirty.
	 * <p>
	 * This method is mainly useful for replication of objects from one datastore to another. It recursively
	 * walks through the object graph and marks all fields of all {@link PersistenceCapable} objects dirty.
	 * </p>
	 *
	 * @param object the root object of the graph. This may either be a {@link PersistenceCapable}, a {@link Collection},
	 *		a {@link Map} or an array. Objects of other types are silently ignored.
	 */
	public static void makeDirtyAllFieldsRecursively(final Object object)
	{
		if (object == null)
			throw new IllegalArgumentException("object must not be null!");

		if (logger.isDebugEnabled())
			logger.debug("makeDirtyAllFieldsRecursively: object=" + JDOHelper.getObjectId(object) + " isDetached=" + JDOHelper.isDetached(object));

		internalMakeDirtyAllFieldsRecursively(object, new IdentityHashSet<Object>());
	}

	private static void internalMakeDirtyAllFieldsRecursively(final Object object, final IdentityHashSet<Object> alreadyProcessedObjects)
	{
		if (object == null)
			return;

		if (!alreadyProcessedObjects.add(object))
			return;

		final boolean trace = logger.isTraceEnabled();
		if (trace)
			logger.trace("internalMakeDirtyAllFieldsRecursively: object=" + JDOHelper.getObjectId(object));

		if (object instanceof Collection) {
			final Collection<?> c = (Collection<?>) object;
			Iterator<?> iterator;
			try {
				iterator = c.iterator();
			} catch (final UnsupportedOperationException x) {
				iterator = null;
				// ignore
			}

			if (iterator != null) {
				while (iterator.hasNext()) {
					final Object element = iterator.next();
					internalMakeDirtyAllFieldsRecursively(element, alreadyProcessedObjects);
				}
			}

			return;
		} else if (object instanceof Map) {
			final Map<?, ?> m = (Map<?, ?>) object;
			Iterator<? extends Map.Entry<?, ?>> iterator;
			try {
				iterator = m.entrySet().iterator();
			} catch (final UnsupportedOperationException x) {
				iterator = null;
				// ignore
			}

			if (iterator != null) {
				while (iterator.hasNext()) {
					final Map.Entry<?, ?> me = iterator.next();
					internalMakeDirtyAllFieldsRecursively(me.getKey(), alreadyProcessedObjects);
					internalMakeDirtyAllFieldsRecursively(me.getValue(), alreadyProcessedObjects);
				}
			}

			return;
		} else if (object instanceof Object[]) {
			final Object[] array = (Object[]) object;
			for (final Object element : array)
				internalMakeDirtyAllFieldsRecursively(element, alreadyProcessedObjects);

			return;
		}
		else if (!(object instanceof PersistenceCapable))
			return;

		try {
			final Class<?> clazz = object.getClass();
			for (final Field field : ReflectUtil.collectAllFields(clazz, true)) {
				final String fieldName = field.getName();

// TODO WORKAROUND for DataNucleus - BEGIN
// this is a hack to workaround this problem: http://www.jpox.org/servlet/forum/viewthread?thread=5086
// hmmmm... seems there is a bug concerning simple int fields not being updated during replication. it works fine with this workaround in place. marco. 2008-11-20.
				if (fieldName.equals("jdoDetachedState")) {
					field.setAccessible(true);
					final Object fieldValue = field.get(object);
					if (fieldValue instanceof Object[]) {
						final Object[] detachedState = (Object[]) fieldValue;
						if (detachedState.length >= 4) {
							if ((detachedState[2] instanceof BitSet) && (detachedState[3] instanceof BitSet)) {
								final BitSet detachedFields = (BitSet) detachedState[2];
								final BitSet dirtyFields = (BitSet) detachedState[3];
								dirtyFields.or(detachedFields);
							}
						}
					}
				}
// WORKAROUND for DataNucleus - END

				if (fieldName.startsWith("jdo")) {
					if (trace)
						logger.trace("internalMakeDirtyAllFieldsRecursively: ignoring internal JDO field: object=" + JDOHelper.getObjectId(object) + " fieldName=" + fieldName);

					continue; // ignore JDO internal fields
				}

//				String qualifiedFieldName = field.getDeclaringClass().getName() + '#' + fieldName; // maybe this '#' should be a '.', but at the moment both don't work :-( have to discuss with erik and andy (and maybe create a test case)
				final String qualifiedFieldName = field.getDeclaringClass().getName() + '.' + fieldName; // maybe this '#' should be a '.', but at the moment both don't work :-( have to discuss with erik and andy (and maybe create a test case)

				field.setAccessible(true);
				try {
					if (trace)
						logger.trace("internalMakeDirtyAllFieldsRecursively: calling JDOHelper.makeDirty with object=" + JDOHelper.getObjectId(object) + " qualifiedFieldName=" + qualifiedFieldName);

					JDOHelper.makeDirty(object, qualifiedFieldName);
				} catch (final JDODetachedFieldAccessException x) { // I'm not sure what the current version of the JDO 2.1 spec says about non-detached fields and makeDirty
					// ignore

					if (trace)
						logger.trace("internalMakeDirtyAllFieldsRecursively: JDOHelper.makeDirty failed with JDODetachedFieldAccessException.");
				}
				final Object fo = field.get(object);
				internalMakeDirtyAllFieldsRecursively(fo, alreadyProcessedObjects);
//				if (fo instanceof Collection) {
//					Collection<?> c = (Collection<?>) fo;
//					for (Object element : c)
//						internalMakeDirtyAllFieldsRecursively(element, alreadyProcessedObjects);
//				} else if (fo instanceof Map) {
//					Map<?, ?> m = (Map<?, ?>) fo;
//					for (Map.Entry<?, ?> me : m.entrySet()) {
//						internalMakeDirtyAllFieldsRecursively(me.getKey(), alreadyProcessedObjects);
//						internalMakeDirtyAllFieldsRecursively(me.getValue(), alreadyProcessedObjects);
//					}
//				} else if (fo instanceof Object[]) {
//					Object[] array = (Object[]) fo;
//					for (Object element : array)
//						internalMakeDirtyAllFieldsRecursively(element, alreadyProcessedObjects);
//				} else if (fo instanceof PersistenceCapable)
//					internalMakeDirtyAllFieldsRecursively(fo, alreadyProcessedObjects);
			}
		} catch (final RuntimeException x) {
			throw x;
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
	}

	private static final ThreadLocal<int[]> transactionSerializeReadObjectsReferenceCounterTL = new ThreadLocal<int[]>()
	{
		@Override
		protected int[] initialValue() {
			return new int[] { 0 };
		}
	};

	/**
	 * Enable the usage of SELECT ... FOR UPDATE for the current transaction.
	 * If you enable this feature, all subsequent queries
	 * of the current transaction (of the specified {@link PersistenceManager}) will lock the objects that are
	 * read from the underlying datastore, just like they are locked in an UPDATE. This ensures that data
	 * is not manipulated by other transactions and therefore renders the JDO object in memory out-dated (stale).
	 * <p>
	 * <b>Important:</b> You must call {@link #disableTransactionSerializeReadObjects(PersistenceManager)} for each call
	 * to this method, because there is a reference counting mechanism in place! Therefore, you have to use try-finally blocks
	 * like in this example:
	 * <br/>
	 * <pre>
	 * NLJDOHelper.enableTransactionSerializeReadObjects(pm);
	 * try {
	 *
	 *   // your code that requires "FOR UPDATE" to be used in queries.
	 *
	 * } finally {
	 *   NLJDOHelper.disableTransactionSerializeReadObjects(pm);
	 * }
	 * </pre>
	 * </p>
	 * <p>
	 * Warning! This feature currently works with DataNucleus only!
	 * </p>
	 * <p>
	 * You can toggle this feature multiple times within one transaction. Its current value affects all subsequent queries -
	 * both explicit (via {@link Query}) and implicit (when accessing a field, i.e. a 1-1 or 1-n relation).
	 * </p>
	 * <p>
	 * If you already have objects and want to lock them afterwards, you can use {@link PersistenceManager#refresh(Object)}
	 * or {@link PersistenceManager#refreshAll(Collection)} in conjunction with this method like in this code snippet:
	 * <br/>
	 * <code>
	 * Collection<MyObject> objects = getSomeObjectsFromSomewhere(pm);
	 * NLJDOHelper.setTransactionSerializeReadObjects(pm, true);
	 * pm.refreshAll(objects);
	 * NLJDOHelper.setTransactionSerializeReadObjects(pm, false);
	 * </code>
	 * </p>
	 *
	 * @param pm the door to the datastore (and accessor to the current transaction object).
	 * @param serializeReadObjects <code>true</code> means read operations will lock objects in the underlying datastore; <code>false</code>
	 *		means reading without locks.
	 */
	public static void enableTransactionSerializeReadObjects(final PersistenceManager pm) {
		final int[] refCount = transactionSerializeReadObjectsReferenceCounterTL.get();
		++refCount[0];
		setTransactionSerializeReadObjects(pm, true);
	}
	/**
	 * Disable the usage of "SELECT ... FOR UPDATE" for the current transaction.
	 * This method must be used <b>in a finally block</b> in conjunction with
	 * {@link #enableQuerySerializeReadObjects(Query)}.
	 *
	 * @param pm the door to the datastore (and accessor to the current transaction object).
	 */
	public static void disableTransactionSerializeReadObjects(final PersistenceManager pm) {
		final int[] refCount = transactionSerializeReadObjectsReferenceCounterTL.get();
		if (--refCount[0] < 0)
			throw new IllegalStateException("transactionSerializeReadObjectsReferenceCounterTL got negative!");

		if (refCount[0] == 0) {
			setTransactionSerializeReadObjects(pm, false);
			transactionSerializeReadObjectsReferenceCounterTL.remove();
		}
	}

	private static boolean forceDisableTransactionSerializeReadObjects = false;

	/**
	 * @param force <code>true</code> to disable the usage of SELECT ... FOR UPDATE.
	 *
	 * @deprecated This is a WORKAROUND for Derby not supporting "FOR UPDATE" in certain situations:
	 *
14:36:01,004 ERROR [LogInterceptor] RuntimeException in method: public abstract void org.nightlabs.jfire.timer.TimerManagerLocal.ejbTimeoutDelegate(org.nightlabs.jfire.timer.TimerParam) throws java.lang.Exception:
javax.jdo.JDODataStoreException: Error executing JDOQL query "SELECT 'org.nightlabs.jfire.timer.Task' AS NUCMETADATA,"this"."opt_version","this"."active_exec_id","this"."bean","this"."enabled","this"."executing","this"."last_exec_dt","this"."last_exec_duration_msec","this"."last_exec_failed","this"."last_exec_message","this"."last_exec_stack_trace","this"."method","this"."next_calculate_next_exec_dt","this"."next_exec_dt" AS JPOXORDER0,"this"."organisation_id","this"."param_object_idstr","this"."task_id","this"."task_type_id","this"."time_pattern_set_time_pattern_set_id_oid","this"."user_organisation_id_oid","this"."user_user_id_oid" FROM "jfirebase_task" "this" WHERE "this"."enabled" = 'Y' AND "this"."executing" = 'N' AND "this"."next_exec_dt" <= ? ORDER BY JPOXORDER0 FOR UPDATE" : FOR UPDATE is not permitted in this type of statement.  .
        at org.datanucleus.jdo.NucleusJDOHelper.getJDOExceptionForNucleusException(NucleusJDOHelper.java:379)
        at org.datanucleus.jdo.JDOQuery.execute(JDOQuery.java:246)
        at org.nightlabs.jfire.timer.Task.getTasksToDo(Task.java:123)
        at org.nightlabs.jfire.timer.TimerManagerBean.ejbTimeoutDelegate(TimerManagerBean.java:142)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.jboss.invocation.Invocation.performCall(Invocation.java:359)
        at org.jboss.ejb.StatelessSessionContainer$ContainerInterceptor.invoke(StatelessSessionContainer.java:237)
        at org.nightlabs.jfire.jboss.transaction.ForceRollbackOnExceptionInterceptor.invoke(ForceRollbackOnExceptionInterceptor.java:40)
        at org.jboss.resource.connectionmanager.CachedConnectionInterceptor.invoke(CachedConnectionInterceptor.java:158)
        at org.jboss.ejb.plugins.StatelessSessionInstanceInterceptor.invoke(StatelessSessionInstanceInterceptor.java:169)
        at org.jboss.ejb.plugins.CallValidationInterceptor.invoke(CallValidationInterceptor.java:63)
        at org.jboss.ejb.plugins.AbstractTxInterceptor.invokeNext(AbstractTxInterceptor.java:121)
        at org.jboss.ejb.plugins.TxInterceptorCMT.runWithTransactions(TxInterceptorCMT.java:404)
        at org.jboss.ejb.plugins.TxInterceptorCMT.invoke(TxInterceptorCMT.java:181)
        at org.jboss.ejb.plugins.SecurityInterceptor.invoke(SecurityInterceptor.java:168)
        at org.jboss.ejb.plugins.LogInterceptor.invoke(LogInterceptor.java:205)
        at org.jboss.ejb.plugins.ProxyFactoryFinderInterceptor.invoke(ProxyFactoryFinderInterceptor.java:138)
        at org.jboss.ejb.SessionContainer.internalInvoke(SessionContainer.java:648)
        at org.jboss.ejb.Container.invoke(Container.java:960)
        at org.jboss.ejb.plugins.local.BaseLocalProxyFactory.invoke(BaseLocalProxyFactory.java:430)
        at org.jboss.ejb.plugins.local.StatelessSessionProxy.invoke(StatelessSessionProxy.java:103)
        at $Proxy342.ejbTimeoutDelegate(Unknown Source)
        at org.nightlabs.jfire.timer.JFireTimerBean.ejbTimeout(JFireTimerBean.java:113)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.jboss.invocation.Invocation.performCall(Invocation.java:359)
        at org.jboss.ejb.StatelessSessionContainer$ContainerInterceptor.invoke(StatelessSessionContainer.java:237)
        at org.nightlabs.jfire.jboss.transaction.ForceRollbackOnExceptionInterceptor.invoke(ForceRollbackOnExceptionInterceptor.java:40)
        at org.jboss.resource.connectionmanager.CachedConnectionInterceptor.invoke(CachedConnectionInterceptor.java:158)
        at org.jboss.ejb.plugins.StatelessSessionInstanceInterceptor.invoke(StatelessSessionInstanceInterceptor.java:169)
        at org.jboss.ejb.plugins.CallValidationInterceptor.invoke(CallValidationInterceptor.java:63)
        at org.jboss.ejb.plugins.AbstractTxInterceptor.invokeNext(AbstractTxInterceptor.java:121)
        at org.jboss.ejb.plugins.TxInterceptorCMT.runWithTransactions(TxInterceptorCMT.java:350)
        at org.jboss.ejb.plugins.TxInterceptorCMT.invoke(TxInterceptorCMT.java:181)
        at org.jboss.ejb.plugins.SecurityInterceptor.invoke(SecurityInterceptor.java:168)
        at org.jboss.ejb.plugins.LogInterceptor.invoke(LogInterceptor.java:205)
        at org.jboss.ejb.plugins.ProxyFactoryFinderInterceptor.invoke(ProxyFactoryFinderInterceptor.java:138)
        at org.jboss.ejb.SessionContainer.internalInvoke(SessionContainer.java:648)
        at org.jboss.ejb.Container.invoke(Container.java:960)
        at org.jboss.ejb.txtimer.TimedObjectInvokerImpl.callTimeout(TimedObjectInvokerImpl.java:99)
        at org.jboss.ejb.txtimer.TimerImpl$TimerTaskImpl.run(TimerImpl.java:561)
        at java.util.TimerThread.mainLoop(Timer.java:512)
        at java.util.TimerThread.run(Timer.java:462)
NestedThrowablesStackTrace:
java.sql.SQLSyntaxErrorException: FOR UPDATE is not permitted in this type of statement.
        at org.apache.derby.impl.jdbc.SQLExceptionFactory40.getSQLException(Unknown Source)
        at org.apache.derby.impl.jdbc.Util.generateCsSQLException(Unknown Source)
        at org.apache.derby.impl.jdbc.TransactionResourceImpl.wrapInSQLException(Unknown Source)
        at org.apache.derby.impl.jdbc.TransactionResourceImpl.handleException(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedConnection.handleException(Unknown Source)
        at org.apache.derby.impl.jdbc.ConnectionChild.handleException(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedPreparedStatement.<init>(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedPreparedStatement20.<init>(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedPreparedStatement30.<init>(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedPreparedStatement40.<init>(Unknown Source)
        at org.apache.derby.jdbc.Driver40.newEmbedPreparedStatement(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedConnection.prepareStatement(Unknown Source)
        at org.apache.derby.impl.jdbc.EmbedConnection.prepareStatement(Unknown Source)
        at org.apache.derby.iapi.jdbc.BrokeredConnection.prepareStatement(Unknown Source)
        at org.jboss.resource.adapter.jdbc.BaseWrapperManagedConnection.doPrepareStatement(BaseWrapperManagedConnection.java:349)
        at org.jboss.resource.adapter.jdbc.BaseWrapperManagedConnection.prepareStatement(BaseWrapperManagedConnection.java:344)
        at org.jboss.resource.adapter.jdbc.WrappedConnection.prepareStatement(WrappedConnection.java:201)
        at org.datanucleus.store.rdbms.SQLController.getStatementForQuery(SQLController.java:311)
        at org.datanucleus.store.rdbms.query.RDBMSQueryUtils.getPreparedStatementForQuery(RDBMSQueryUtils.java:266)
        at org.datanucleus.store.rdbms.query.SQLEvaluator.evaluate(SQLEvaluator.java:114)
        at org.datanucleus.store.rdbms.query.JDOQLQuery.performExecute(JDOQLQuery.java:278)
        at org.datanucleus.store.query.Query.executeQuery(Query.java:1417)
        at org.datanucleus.store.rdbms.query.JDOQLQuery.executeQuery(JDOQLQuery.java:226)
        at org.datanucleus.store.query.Query.executeWithArray(Query.java:1325)
        at org.datanucleus.store.query.Query.execute(Query.java:1275)
        at org.datanucleus.jdo.JDOQuery.execute(JDOQuery.java:237)
        at org.nightlabs.jfire.timer.Task.getTasksToDo(Task.java:123)
        at org.nightlabs.jfire.timer.TimerManagerBean.ejbTimeoutDelegate(TimerManagerBean.java:142)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.jboss.invocation.Invocation.performCall(Invocation.java:359)
        at org.jboss.ejb.StatelessSessionContainer$ContainerInterceptor.invoke(StatelessSessionContainer.java:237)
        at org.nightlabs.jfire.jboss.transaction.ForceRollbackOnExceptionInterceptor.invoke(ForceRollbackOnExceptionInterceptor.java:40)
        at org.jboss.resource.connectionmanager.CachedConnectionInterceptor.invoke(CachedConnectionInterceptor.java:158)
        at org.jboss.ejb.plugins.StatelessSessionInstanceInterceptor.invoke(StatelessSessionInstanceInterceptor.java:169)
        at org.jboss.ejb.plugins.CallValidationInterceptor.invoke(CallValidationInterceptor.java:63)
        at org.jboss.ejb.plugins.AbstractTxInterceptor.invokeNext(AbstractTxInterceptor.java:121)
        at org.jboss.ejb.plugins.TxInterceptorCMT.runWithTransactions(TxInterceptorCMT.java:404)
        at org.jboss.ejb.plugins.TxInterceptorCMT.invoke(TxInterceptorCMT.java:181)
        at org.jboss.ejb.plugins.SecurityInterceptor.invoke(SecurityInterceptor.java:168)
        at org.jboss.ejb.plugins.LogInterceptor.invoke(LogInterceptor.java:205)
        at org.jboss.ejb.plugins.ProxyFactoryFinderInterceptor.invoke(ProxyFactoryFinderInterceptor.java:138)
        at org.jboss.ejb.SessionContainer.internalInvoke(SessionContainer.java:648)
        at org.jboss.ejb.Container.invoke(Container.java:960)
        at org.jboss.ejb.plugins.local.BaseLocalProxyFactory.invoke(BaseLocalProxyFactory.java:430)
        at org.jboss.ejb.plugins.local.StatelessSessionProxy.invoke(StatelessSessionProxy.java:103)
        at $Proxy342.ejbTimeoutDelegate(Unknown Source)
        at org.nightlabs.jfire.timer.JFireTimerBean.ejbTimeout(JFireTimerBean.java:113)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.jboss.invocation.Invocation.performCall(Invocation.java:359)
        at org.jboss.ejb.StatelessSessionContainer$ContainerInterceptor.invoke(StatelessSessionContainer.java:237)
        at org.nightlabs.jfire.jboss.transaction.ForceRollbackOnExceptionInterceptor.invoke(ForceRollbackOnExceptionInterceptor.java:40)
        at org.jboss.resource.connectionmanager.CachedConnectionInterceptor.invoke(CachedConnectionInterceptor.java:158)
        at org.jboss.ejb.plugins.StatelessSessionInstanceInterceptor.invoke(StatelessSessionInstanceInterceptor.java:169)
        at org.jboss.ejb.plugins.CallValidationInterceptor.invoke(CallValidationInterceptor.java:63)
        at org.jboss.ejb.plugins.AbstractTxInterceptor.invokeNext(AbstractTxInterceptor.java:121)
        at org.jboss.ejb.plugins.TxInterceptorCMT.runWithTransactions(TxInterceptorCMT.java:350)
        at org.jboss.ejb.plugins.TxInterceptorCMT.invoke(TxInterceptorCMT.java:181)
        at org.jboss.ejb.plugins.SecurityInterceptor.invoke(SecurityInterceptor.java:168)
        at org.jboss.ejb.plugins.LogInterceptor.invoke(LogInterceptor.java:205)
        at org.jboss.ejb.plugins.ProxyFactoryFinderInterceptor.invoke(ProxyFactoryFinderInterceptor.java:138)
        at org.jboss.ejb.SessionContainer.internalInvoke(SessionContainer.java:648)
        at org.jboss.ejb.Container.invoke(Container.java:960)
        at org.jboss.ejb.txtimer.TimedObjectInvokerImpl.callTimeout(TimedObjectInvokerImpl.java:99)
        at org.jboss.ejb.txtimer.TimerImpl$TimerTaskImpl.run(TimerImpl.java:561)
        at java.util.TimerThread.mainLoop(Timer.java:512)
        at java.util.TimerThread.run(Timer.java:462)
Caused by: java.sql.SQLException: FOR UPDATE is not permitted in this type of statement.
        at org.apache.derby.impl.jdbc.SQLExceptionFactory.getSQLException(Unknown Source)
        at org.apache.derby.impl.jdbc.SQLExceptionFactory40.wrapArgsForTransportAcrossDRDA(Unknown Source)
        ... 72 more
Caused by: ERROR 42Y90: FOR UPDATE is not permitted in this type of statement.
        at org.apache.derby.iapi.error.StandardException.newException(Unknown Source)
        at org.apache.derby.impl.sql.compile.CursorNode.bindStatement(Unknown Source)
        at org.apache.derby.impl.sql.GenericStatement.prepMinion(Unknown Source)
        at org.apache.derby.impl.sql.GenericStatement.prepare(Unknown Source)
        at org.apache.derby.impl.sql.conn.GenericLanguageConnectionContext.prepareInternalStatement(Unknown Source)
        ... 66 more

	 */
	@Deprecated
	public static void setForceDisableTransactionSerializeReadObjects(final boolean force)
	{
		forceDisableTransactionSerializeReadObjects = force;
	}

	private static void setTransactionSerializeReadObjects(final PersistenceManager pm, boolean serializeReadObjects) {
		if (forceDisableTransactionSerializeReadObjects)
			serializeReadObjects = false;

		// finally since JDO 2.3 this method is in the JDO Standard and therefore no dependency on DataNucleus is needed anymore
//		((org.datanucleus.jdo.JDOTransaction)pm.currentTransaction()).setSerializeRead(serializeReadObjects ? Boolean.TRUE : null);
		pm.currentTransaction().setSerializeRead(serializeReadObjects ? Boolean.TRUE : null);
	}

	/**
	 * Enable the usage of SELECT ... FOR UPDATE for a query.
	 * If you enable this feature, the specified query will lock the objects that are
	 * read from the underlying datastore, just like they are locked in an UPDATE. This ensures that data
	 * is not manipulated by other transactions and therefore renders the JDO object in memory out-dated (stale).
	 * <p>
	 * Warning! This feature currently works with DataNucleus only!
	 * </p>
	 *
	 * @param query the query for which to enable update-locks.
	 */
	public static void enableQuerySerializeReadObjects(final Query query)
	{
		query.addExtension("datanucleus.rdbms.query.useUpdateLock", "true");
	}

	/**
	 * Compare the object version of two JDO objects. Get a positive value, if
	 * one of the following conditions is <code>true</code>:
	 * <ul>
	 *		<li>Result 1: The version of <code>newObject</code> is higher than the version of <code>oldObject</code>.</li>
	 *		<li>Result 2: <code>oldObject</code> is <code>null</code>.</li>
	 *		<li>Result 3: At least one of the objects is either not persistence capable or does not have versioning enabled.</li>
	 * </ul>
	 * <p>
	 * A negative value is returned, if one of the following conditions is met:
	 * <ul>
	 *		<li>Result -1: The version of <code>oldObject</code> is higher than the version of <code>newObject</code>.</li>
	 *		<li>Result -2: <code>newObject</code> is <code>null</code>.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * An {@link IllegalArgumentException} is thrown, if the objects are persistence-capable and both have
	 * an object-id assigned and the object-id is different.
	 * </p>
	 *
	 * @param oldObject the old object - might be <code>null</code>.
	 * @param newObject the new object - must not be <code>null</code>.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static int compareObjectVersions(final Object oldObject, final Object newObject)
	{
		if (oldObject == null)
			return 2;

		if (newObject == null)
			return -2;

		final Object oldOid = JDOHelper.getObjectId(oldObject);
		final Object newOid = JDOHelper.getObjectId(newObject);

		if (oldOid != null && newOid != null && !oldOid.equals(newOid))
			throw new IllegalArgumentException("Comparing apples to pears! The object-ids of oldObject and newObject do not match! oldOid=\"" + oldOid + "\" newOid=\"" + newOid + "\"");

		final Object oldVersion = JDOHelper.getVersion(oldObject);
		final Object newVersion = JDOHelper.getVersion(newObject);
		if (oldVersion == null || newVersion == null)
			return 3;

		final Comparable<Object> oldVersionC = (Comparable<Object>) oldVersion;
		final Comparable<Object> newVersionC = (Comparable<Object>) newVersion;
		final int compareResult = newVersionC.compareTo(oldVersionC);
		return compareResult < 0 ? -1 : (compareResult > 0 ? 1 : 0);
	}

	private static Map<Class<?>, Boolean> cachedClass2PersistanceCapable = Collections.synchronizedMap(new HashMap<Class<?>, Boolean>());

	/**
	 * This method checks the class (not any super-class) of the given object implemnts {@link PersistenceCapable}.
	 *
	 * @param obj The object to check, might be <code>null</code>.
	 * @return Whether the exact class of the given object declares {@link PersistenceCapable}.
	 */
	public static boolean isPersistenceCapable(final Object obj) {
		if (obj == null)
			return false;
		final Boolean cachedResult = cachedClass2PersistanceCapable.get(obj.getClass());
		if (cachedResult != null)
			return cachedResult;
		Boolean result = null;
		for (final Class<?> iface : obj.getClass().getInterfaces()) {
			if (iface == PersistenceCapable.class) {
				result = Boolean.TRUE;
				break;
			}
		}
		if (result == null)
			result = Boolean.FALSE;
		cachedClass2PersistanceCapable.put(obj.getClass(), result);
		return result;
	}

	/**
	 * Delete one or more objects after the deletion of a primary object completed.
	 * This is usually used in a {@link DeleteCallback} for deleting objects that
	 * would otherwise become orphans but that cannot be directly deleted since the
	 * primary object still references them.
	 * <p>
	 * At the moment, there exists a DataNucleus bug, preventing dependend objects to be
	 * deleted and thus requiring this workaround being used.
	 * See: https://www.jfire.org/modules/bugs/view.php?id=1330
	 * </p>
	 * <p>
	 * Example code:
	 * <pre>
	 * public class A
	 * implements DeleteCallback
	 * {
	 *   &#64;Persistent(nullValue=NullValue.EXCEPTION)
	 *   private B someDependentField;
	 *
	 *   &#64;Override
	 *   public void jdoPreDelete() {
	 *   	NLJDOHelper.deleteAfterPrimaryObjectDeleted(this, someDependentField);
	 *   }
	 * }
	 * </pre>
	 * </p>
	 *
	 * @param primaryObject the primary object. <code>null</code> is not allowed for this argument.
	 * @param objectsToBeDeleted one or more objects to be deleted, after the primary object was deleted. <code>null</code> elements are silently ignored and the whole var-arg-array can be <code>null</code>, too, thus there's no need for null-checks.
	 */
	public static void deleteAfterPrimaryObjectDeleted(
			final Object primaryObject, final Object ... objectsToBeDeleted
	)
	{
		if (primaryObject == null)
			throw new IllegalArgumentException("primaryObject == null");

		final PersistenceManager pm = JDOHelper.getPersistenceManager(primaryObject);
		if (pm == null)
			throw new IllegalArgumentException("JDOHelper.getPersistenceManager(primaryObject) returned null!!!");

		pm.addInstanceLifecycleListener(new DeleteLifecycleListener() {
			@Override
			public void preDelete(final InstanceLifecycleEvent event) { }

			@Override
			public void postDelete(final InstanceLifecycleEvent event) {
				if (event.getPersistentInstance() != primaryObject)
					return;

				pm.removeInstanceLifecycleListener(this);
				pm.flush();

				if (objectsToBeDeleted != null) {
					for (final Object object : objectsToBeDeleted) {
						if (object != null) {
							pm.deletePersistent(object);
							pm.flush();
						}
					}
				}
			}
		}, primaryObject.getClass());
	}
}
