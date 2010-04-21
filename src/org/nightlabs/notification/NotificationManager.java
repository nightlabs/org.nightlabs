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
package org.nightlabs.notification;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.nightlabs.concurrent.RWLock;



/**
 * <b>Warning:</b> The notification framework works with weak references. Therefore,
 * all listeners must be non-anonymous fields! They may be anonymous classes, but
 * if you don't assign them to a member in your object, they'll be quickly
 * released by the garbage collector and you'll probably never receive any events!
 *
 * <p>
 * TODO: use generics.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class NotificationManager
{
	private static final boolean USE_WEAK_REFERENCES = true;

	/**
	 * This class is intended to be extended and to be used with one shared instance
	 * per subclass. Hence, the constructor is protected.
	 */
	protected NotificationManager()
	{
	}

	private LinkedList<Interceptor> interceptors = new LinkedList<Interceptor>();
	private LinkedList<Interceptor> _interceptors = null;
	private RWLock interceptorsMutex = new RWLock("interceptorsMutex");

	public void addInterceptor(Interceptor m)
	{
		interceptorsMutex.acquireWriteLock();
		try {
			interceptors.add(m);
			_interceptors = null;
		} finally {
			interceptorsMutex.releaseLock();
		}
	}

	public void addInterceptorAsFirst(Interceptor m)
	{
		interceptorsMutex.acquireWriteLock();
		try {
			interceptors.addFirst(m);
			_interceptors = null;
		} finally {
			interceptorsMutex.releaseLock();
		}
	}

	public void addInterceptorAsLast(Interceptor m)
	{
		interceptorsMutex.acquireWriteLock();
		try {
			interceptors.addLast(m);
			_interceptors = null;
		} finally {
			interceptorsMutex.releaseLock();
		}
	}

	public void removeInterceptor(Interceptor m)
	{
		interceptorsMutex.acquireWriteLock();
		try {
			interceptors.remove(m);
			_interceptors = null;
		} finally {
			interceptorsMutex.releaseLock();
		}
	}

	/**
	 * If you extend the <tt>NotificationManager</tt> to support additional
	 * notification modes, you need to extend this method and add your listener types
	 * here.
	 * @return The collection of valid listener types for this NotificationManager
	 */
	protected Collection<Class<? extends NotificationListener>> getValidListenerTypes() {
		Collection<Class<? extends NotificationListener>> result = new HashSet<Class<? extends NotificationListener>>();
		result.add(NotificationListenerCallerThread.class);
		result.add(NotificationListenerWorkerThreadAsync.class);
		result.add(NotificationListenerAWTThreadSync.class);
		result.add(NotificationListenerAWTThreadAsync.class);
		return result;
	}

	/**
	 * Checks if the given listeners class can be assigned to a {@link NotificationListener} registered in {@link #getValidListenerTypes()}.
	 * If a suitable listener type can be found, it will be returned.
	 * @return The listener class the given listener matches.
	 */
	protected Class<? extends NotificationListener> checkListenerType(NotificationListener listener) {
		for (Class<? extends NotificationListener> listenerClass : getValidListenerTypes()) {
			if (listenerClass.isAssignableFrom(listener.getClass()))
				return listenerClass;
		}
		return null;
	}

	/**
	 * This method checks whether a suitable way to notify this listener can be found ({@link #checkListenerType(NotificationListener)}).
	 * If not it will throw a {@link ClassCastException} stating that the given listener is not supported.
	 * If a match can be found in the valid listener types {@link #getValidListenerTypes()} the name of the found match
	 * will be returned as identfier for this way to notify.
	 */
	protected String getNotificationModeForListener(NotificationListener listener)
	{
		Class<?> listenerMatch = checkListenerType(listener);
		if (listenerMatch == null)
			throw new ClassCastException("Listener is not supported! It must implement one of the sub-interfaces of NotificationListener! You passed: "+listener.getClass().getName());

		return listenerMatch.getName();
	}

//	/**
//	 * Override this method in case you want to add new notification modes.
//	 *
//	 * @param notificationMode The notification mode which shall be checked for validity.
//	 * @throws IllegalArgumentException If the notificationMode is invalid.
//	 */
//	protected void assertValidNotificationMode(String notificationMode)
//	{
//		if (!NOTIFICATION_MODE_CALLER_THREAD.equals(notificationMode) &&
//				!NOTIFICATION_MODE_WORKER_THREAD_ASYNC.equals(notificationMode) &&
//				!NOTIFICATION_MODE_AWT_THREAD_SYNC.equals(notificationMode) &&
//				!NOTIFICATION_MODE_AWT_THREAD_ASYNC.equals(notificationMode))
//			throw new IllegalArgumentException("Invalid notificationMode: " + notificationMode);
//	}

	/**
	 * key: String zone<br/>
	 * value: Map subjectClasses {<br/>
	 *		key: Class subjectClass<br/>
	 *		value: (Weak)HashMap listeners {<br/>
	 *			NotificationListener listener<br/>
	 *				HashMap modes {<br/>
	 *				String notificationMode<br/>
	 *				NotificationListenerMeta listenerMeta<br/>
	 *			}<br/>
	 *		}<br/>
	 * }
	 */
	private Map<String, Map<Class<?>, Map<NotificationListener, Map<String, NotificationListenerMeta>>>> notificationListenersByZone = new HashMap<String, Map<Class<?>,Map<NotificationListener,Map<String,NotificationListenerMeta>>>>();
	private RWLock notificationListenersByZoneMutex = new RWLock("notificationListenersByZoneMutex");

	/**
	 * A cache for zones;
	 */
	private Collection<String> zones = null;

	/**
	 * @return a <tt>Collection</tt> of <tt>String</tt>.
	 */
	protected Collection<String> getZones()
	{
		if (zones == null) {
			notificationListenersByZoneMutex.acquireReadLock();
			try {
				zones = Collections.unmodifiableSet(new HashSet<String>(notificationListenersByZone.keySet()));
			} finally {
				notificationListenersByZoneMutex.releaseLock();
			}
		}
		return zones;
	}

	/**
	 * Convenience method calling {@link #removeNotificationListener(String, Class, NotificationListener)}
	 * with <tt>zone = null</tt>.
	 */
	public void removeNotificationListener(Class<?> subjectClass, NotificationListener listener)
	{
		removeNotificationListener((String)null, subjectClass, listener);
	}

	public void removeNotificationListener(String zone, Class<?> subjectClass, NotificationListener listener)
	{
		notificationListenersByZoneMutex.acquireWriteLock();
		try {
			zones = null;

			String notificationMode = getNotificationModeForListener(listener);

			Map<Class<?>, Map<NotificationListener, Map<String, NotificationListenerMeta>>> notificationListenersBySubjectClass = notificationListenersByZone.get(zone);
			if (notificationListenersBySubjectClass != null) {
				Map<NotificationListener, Map<String, NotificationListenerMeta>> mapListeners = notificationListenersBySubjectClass.get(subjectClass);
				if (mapListeners != null) {

					Map<String, NotificationListenerMeta> mapModes = mapListeners.get(listener);
					if (mapModes != null) {

						NotificationListenerMeta meta = mapModes.get(notificationMode);
						if (meta != null) {
							if (meta.decCounter() < 1) {
								mapModes.remove(notificationMode);
								meta = null;
							}
						} // if (meta != null) {

						if (mapModes.isEmpty()) {
							mapListeners.remove(listener);
						}

					} // if (mapModes != null) {

					if (mapListeners.isEmpty()) {
						notificationListenersBySubjectClass.remove(subjectClass);
					}
				} // if (mapListeners != null) {

				if (notificationListenersBySubjectClass.isEmpty()) {
					notificationListenersByZone.remove(zone);
				}
			} // if (notificationListenersBySubjectClass != null) {

		} finally {
			notificationListenersByZoneMutex.releaseLock();
		}
	}

	/**
	 * Convenience method calling {@link #addNotificationListener(String, Class, NotificationListener)}
	 * with <tt>zone = null</tt>.
	 */
	public void addNotificationListener(Class<?> subjectClass, NotificationListener listener)
	{
		addNotificationListener((String)null, subjectClass, listener);
	}

	public void addNotificationListener(String zone, Class<?> subjectClass, NotificationListener listener)
	{
		notificationListenersByZoneMutex.acquireWriteLock();
		try {
			zones = null;

			String notificationMode = getNotificationModeForListener(listener);

			Map<Class<?>, Map<NotificationListener, Map<String, NotificationListenerMeta>>> notificationListenersBySubjectClass = notificationListenersByZone.get(zone);
			if (notificationListenersBySubjectClass == null) {
				notificationListenersBySubjectClass = new HashMap<Class<?>, Map<NotificationListener,Map<String,NotificationListenerMeta>>>();
				notificationListenersByZone.put(zone, notificationListenersBySubjectClass);
			}

			Map<NotificationListener, Map<String, NotificationListenerMeta>> mapListeners = notificationListenersBySubjectClass.get(subjectClass);
			if (mapListeners == null) {
				if (USE_WEAK_REFERENCES)
					mapListeners = new WeakHashMap<NotificationListener, Map<String,NotificationListenerMeta>>();
				else
					mapListeners = new HashMap<NotificationListener, Map<String,NotificationListenerMeta>>();

				notificationListenersBySubjectClass.put(subjectClass, mapListeners);
			}

			Map<String, NotificationListenerMeta> mapModes = mapListeners.get(listener);
			if (mapModes == null) {
				mapModes = new HashMap<String, NotificationListenerMeta>();
				mapListeners.put(listener, mapModes);
			}

			NotificationListenerMeta meta = mapModes.get(notificationMode);
			if (meta != null && meta.getNotificationListener() == null)
				meta = null;

			if (meta == null) {
				meta = new NotificationListenerMeta(zone, subjectClass, listener, notificationMode);
				mapModes.put(notificationMode, meta);
			}
			meta.incCounter();

		} finally {
			notificationListenersByZoneMutex.releaseLock();
		}
	}

	public void addNotificationListener(Collection<? extends Class<?>> subjectClasses, NotificationListener listener)
	{
		addNotificationListener((String)null, subjectClasses, listener);
	}
	public void addNotificationListener(String zone, Collection<? extends Class<?>> subjectClasses, NotificationListener listener)
	{
		for (Iterator<? extends Class<?>> it = subjectClasses.iterator(); it.hasNext(); )
			addNotificationListener(zone, it.next(), listener);
	}

	public void addNotificationListener(Class<?>[] subjectClasses, NotificationListener listener)
	{
		addNotificationListener((String)null, subjectClasses, listener);
	}
	public void addNotificationListener(String zone, Class<?>[] subjectClasses, NotificationListener listener)
	{
		for (Class<?> element : subjectClasses)
			addNotificationListener(zone, element, listener);
	}

	public void removeNotificationListener(Collection<? extends Class<?>> subjectClasses, NotificationListener listener)
	{
		removeNotificationListener((String)null, subjectClasses, listener);
	}
	public void removeNotificationListener(String zone, Collection<? extends Class<?>> subjectClasses, NotificationListener listener)
	{
		for (Iterator<? extends Class<?>> it = subjectClasses.iterator(); it.hasNext(); )
			removeNotificationListener(zone, it.next(), listener);
	}

	public void removeNotificationListener(Class<?>[] subjectClasses, NotificationListener listener)
	{
		removeNotificationListener((String)null, subjectClasses, listener);
	}
	public void removeNotificationListener(String zone, Class<?>[] subjectClasses, NotificationListener listener)
	{
		for (Class<?> element : subjectClasses)
			removeNotificationListener(zone, element, listener);
	}

//	protected static class _SubjectCarrier {
//		public _SubjectCarrier(Class subjectClass) {
//			this.subjectClass = subjectClass;
//		}
//
//		public _SubjectCarrier(Object subject) {
//			this.subject = subject;
//			this.subjectClass = subject.getClass();
//		}
//
//		public Class subjectClass;
//		public Object subject = null;
//	}

	protected static class SubjectBundle
	{
		public Set<SubjectCarrier> subjectCarriers = new HashSet<SubjectCarrier>();
//		public List subjects = new ArrayList();
//		public List subjectClassesToClear = new ArrayList();
	}

	public void notify(NotificationEvent event)
	{
		if (event == null)
			throw new IllegalArgumentException("Parameter 'event' must not be null!");

		event.setNotificationManager(this);

		event = intercept(event);

		notify(event, null);
	}

	protected NotificationEvent intercept(NotificationEvent event)
	{
		LinkedList<Interceptor> i;
		interceptorsMutex.acquireReadLock();
		try {
			if (interceptors.isEmpty())
				return event;

			if (_interceptors == null)
				_interceptors = new LinkedList<Interceptor>(interceptors);

			i = _interceptors;
		} finally {
			interceptorsMutex.releaseLock();
		}

		for (Iterator<Interceptor> it = i.iterator(); it.hasNext(); ) {
			Interceptor interceptor = it.next();
			NotificationEvent newEvent = interceptor.intercept(event);
			if (newEvent != null)
				event = newEvent;
		}
		return event;
	}

	private static List<Class<?>> getAllInterfaces(Class<?> clazz)
	{
		List<Class<?>> interfaces = new LinkedList<Class<?>>();
		Set<Class<?>> seen = new HashSet<Class<?>>();
		populateInterfaces(interfaces, seen, clazz);
		return interfaces;
	}
	private static void populateInterfaces(List<Class<?>> interfaces, Set<Class<?>> seen, Class<?> clazz)
	{
		Class<?>[] ifs = clazz.getInterfaces();
		for (Class<?> iface : ifs) {
			if (seen.add(iface)) {
				interfaces.add(iface);
				populateInterfaces(interfaces, seen, iface);
			}
		}
	}

	/**
	 * @param event The event to fire.
	 * @param onlyThisNotificationListener This is a fiter. That means: If it is <tt>null</tt>
	 *		all matching listeners will be triggered. If not
	 *		<tt>null</tt>, only this listener will be triggered. This feature is needed for
	 *		re-fireing of old events after a new listener has been
	 *		registered. The new listener must already be registered when calling this method
	 *		and it will only be triggered, if it would be triggered with this param being
	 *		<tt>null</tt>.
	 */
	protected void notify(NotificationEvent event, NotificationListener onlyThisNotificationListener)
	{
		if (event == null)
			throw new NullPointerException("Parameter 'event' must not be null!");

		event.setNotificationManager(this);

		notificationListenersByZoneMutex.acquireReadLock();
		try {

			Collection<String> zones;
			if (event.getZone() == null) {
				// if the event has zone=null, we send the event to all zones
				zones = notificationListenersByZone.keySet();
			}
			else {
				// otherwise, we send the event to the given zone and
				// the listeners that are registered with zone=null.
				zones = new LinkedList<String>();
				zones.add(event.getZone());
				zones.add(null);
			}

//			List subjectCarriers = new LinkedList();
//			for (Iterator it = event.getSubjects().iterator(); it.hasNext(); ) {
//				Object subject = it.next();
//				subjectCarriers.add(new _SubjectCarrier(subject));
//			}
//			for (Iterator it = event.getSubjectClassesToClear().iterator(); it.hasNext(); ) {
//				Class subjectClass = (Class)it.next();
//				subjectCarriers.add(new _SubjectCarrier(subjectClass));
//			}

			// key: NotificationListener listener
			// value: Map {
			//   String notificationMode
			//   SubjectBundle subjects
			// }
			Map<NotificationListener, Map<String, SubjectBundle>> listenerSubjects = new HashMap<NotificationListener, Map<String,SubjectBundle>>();

			for (Iterator<String> itZones = zones.iterator(); itZones.hasNext(); ) {
				String zone = itZones.next();

				Map<Class<?>, Map<NotificationListener, Map<String, NotificationListenerMeta>>> notificationListenersBySubjectClass = notificationListenersByZone.get(zone);
				if (notificationListenersBySubjectClass == null)
					continue;

				for (Object element : event.getSubjectCarriers()) {
					SubjectCarrier subjectCarrier = (SubjectCarrier) element;

					for (Class<?> subjectClass : subjectCarrier.getSubjectClasses()) {
						boolean inheritanceIgnored = subjectCarrier.isInheritanceIgnored();
						boolean interfacesIgnored = subjectCarrier.isInterfacesIgnored();
						boolean breakOnFirstFound = subjectCarrier.isBreakOnFirstFound();
						boolean doBreak = false;
						Class<?> clazz = subjectClass;
						do {
							Map<NotificationListener, Map<String, NotificationListenerMeta>> mapListeners = notificationListenersBySubjectClass.get(clazz);
							if (mapListeners != null)
								mapListeners = Collections.unmodifiableMap(mapListeners);

							boolean copiedMapListeners = false;
							if (!interfacesIgnored) {
								Collection<Class<?>> interfaces = getAllInterfaces(clazz);
								for (Class<?> element1 : interfaces) {
									Map<NotificationListener, Map<String, NotificationListenerMeta>> mapListenersForInterface = notificationListenersBySubjectClass.get(element1);
									if (mapListenersForInterface != null) {
										if (mapListeners == null)
											mapListeners = Collections.unmodifiableMap(mapListenersForInterface);
										else {
											if (!copiedMapListeners) {
												mapListeners = new HashMap<NotificationListener, Map<String,NotificationListenerMeta>>(mapListeners);
												copiedMapListeners = true;
											}

											mapListeners.putAll(mapListenersForInterface);
										}
									}
								}
							}
							if (mapListeners != null) {
								for (Iterator<Map<String, NotificationListenerMeta>> itListeners = mapListeners.values().iterator(); itListeners.hasNext(); ) {
									Map<String, NotificationListenerMeta> mapModes = itListeners.next();
									if (doBreak)
										break;
									for (Iterator<NotificationListenerMeta> itModes = mapModes.values().iterator(); itModes.hasNext(); ) {
										NotificationListenerMeta meta = itModes.next();
										NotificationListener listener = meta.getNotificationListener();

										if (onlyThisNotificationListener != null &&
												onlyThisNotificationListener != listener)
											listener = null;

										if (listener != null) {
											Map<String, SubjectBundle> m = listenerSubjects.get(listener);
											if (m == null) {
												m = new HashMap<String, SubjectBundle>();
												listenerSubjects.put(listener, m);
											}
											SubjectBundle subjectBundle = m.get(meta.getNotificationMode());
											if (subjectBundle == null) {
												subjectBundle = new SubjectBundle();
												m.put(meta.getNotificationMode(), subjectBundle);
											}
											subjectBundle.subjectCarriers.add(subjectCarrier);
											if (breakOnFirstFound) {
												doBreak = true;
												break;
											}
//											if (subjectCarrier.getSubject() == null)
//												subjectBundle.subjectClassesToClear.add(subjectClass);
//											else
//												subjectBundle.subjects.add(subjectCarrier.getSubject());
										} // if (listener != null) {
									} // for (Iterator itListeners = mapListeners.values().iterator(); itListeners.hasNext(); ) {
								} // for (Iterator itModeListeners = mapModeListeners.values().iterator(); itModeListeners.hasNext(); ) {
							} // if (mapModeListeners != null) {

							if (doBreak)
								break;
							if (inheritanceIgnored)
								clazz = null;
							else
								clazz = clazz.getSuperclass();
						} while (clazz != null);

					} // for (Iterator itSubjectClasses = subjectCarrier.getSubjectClasses().iterator(); itSubjectClasses.hasNext()) {
				} // for (Iterator itSubjects = event.getSubjects().iterator(); itSubjects.hasNext(); ) {

				for (Iterator<Map.Entry<NotificationListener, Map<String, SubjectBundle>>> itListeners = listenerSubjects.entrySet().iterator(); itListeners.hasNext(); ) {
					Map.Entry<NotificationListener, Map<String, SubjectBundle>> meListener = itListeners.next();
					NotificationListener listener = meListener.getKey();
					Map<String, SubjectBundle> mapModeSubjects = meListener.getValue();
					for (Iterator<Map.Entry<String, SubjectBundle>> itModes = mapModeSubjects.entrySet().iterator(); itModes.hasNext(); ) {
						Map.Entry<String, SubjectBundle> meMode = itModes.next();
						String notificationMode = meMode.getKey();
						SubjectBundle subjectBundle = meMode.getValue();

						NotificationEvent newEvent;
						if (subjectBundle.subjectCarriers.size() == event.getSubjectCarriers().size())
							newEvent = event;
						else
							newEvent = createNotificationEvent(
									event.getSource(), event.getZone(),
									null, null, subjectBundle.subjectCarriers);

						performNotification(notificationMode, listener, newEvent);
					}
				}
			} // for (Iterator itZones = zones.iterator(); itZones.hasNext(); ) {

		} finally {
			notificationListenersByZoneMutex.releaseLock();
		}
	}

	protected NotificationEvent createNotificationEvent(
			Object source, String zone, Collection<?> subjects, Collection<? extends Class<?>> subjectClassesToClear, Collection<SubjectCarrier> _subjectCarriers)
	{
		return new NotificationEvent(
				source, zone,
				subjects, subjectClassesToClear, _subjectCarriers);
	}

	/**
	 * If you want to implement additional notification modes, you must override this method.
	 * It is supposed to call {@link NotificationListener#notify(NotificationEvent)}
	 * on the thread which is defined by notification mode.
	 */
	protected void performNotification(
			String notificationMode,
			final NotificationListener listener, final NotificationEvent event)
	{
		listener.setActiveNotificationEvent(event);
		if (NotificationListenerCallerThread.class.getName().equals(notificationMode)) {
			listener.notify(event);
		}
		else if (NotificationListenerWorkerThreadAsync.class.getName().equals(notificationMode)) {
			Thread worker = new Thread() {
				@Override
				public void run() {
					listener.notify(event);
				}
			};
			worker.start();
		}
		else if (NotificationListenerAWTThreadSync.class.getName().equals(notificationMode)) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						listener.notify(event);
					}
				});
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		else if (NotificationListenerAWTThreadAsync.class.getName().equals(notificationMode)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.notify(event);
				}
			});
		}
		else
			throw new IllegalArgumentException("unknown notificationMode: " + notificationMode);
	}
}
