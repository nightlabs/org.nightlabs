/*
 * Created on Apr 19, 2005
 */
package com.nightlabs.rcp.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import com.nightlabs.notification.NotificationEvent;
import com.nightlabs.notification.NotificationListener;
import com.nightlabs.notification.SubjectCarrier;
import com.nightlabs.util.RWLock;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SelectionManager extends NotificationManager
{
	private static SelectionManager _sharedInstance = null;

	public static SelectionManager sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new SelectionManager();

		return _sharedInstance;
	}

	protected SelectionManager()
	{
	}

	/**
	 * key: String zone<br/>
	 * value: Map {<br/>
	 *		key: Class searchClass<br/>
	 *		value: NotificationEvent event<br/>
	 * }
	 */
	private Map eventsByZone = new HashMap();
	private RWLock eventsByZoneMutex = new RWLock("eventsByZoneMutex");

	protected void registerEvent(NotificationEvent event)
	{
		eventsByZoneMutex.acquireWriteLock();
		try {
			String zone = event.getZone();
			Set zones = new HashSet();
			if (zone != null) {
				zones.add(null);
				zones.add(zone);
			}
			else {
				zones.add(null);
				zones.addAll(eventsByZone.keySet());
			}

			for (Iterator itZones = zones.iterator(); itZones.hasNext(); ) {
				zone = (String)itZones.next();

				Map eventsByClass = (Map) eventsByZone.get(zone);
				if (eventsByClass == null) {
					eventsByClass = new HashMap();
					eventsByZone.put(zone, eventsByClass);
				}

				for (Iterator itSubjectCarriers = event.getSubjectCarriers().iterator(); itSubjectCarriers.hasNext(); ) {
					SubjectCarrier carrier = (SubjectCarrier) itSubjectCarriers.next();
					for (Iterator itClasses = carrier.getSubjectClasses().iterator(); itClasses.hasNext(); ) {
						Class clazz = (Class) itClasses.next();
						
						if (carrier.isInheritanceIgnored())
							eventsByClass.put(clazz, event);
						else {
							do {
								eventsByClass.put(clazz, event);
								clazz = clazz.getSuperclass();
							} while (clazz != null);
						}
					}
				} // for (Iterator itSubjectCarriers = event.getSubjectCarriers().iterator(); itSubjectCarriers.hasNext(); ) {

			} // for (Iterator itZones = zones.iterator(); itZones.hasNext(); ) {

//			for (Iterator it = event.getSubjects().iterator(); it.hasNext(); ) {
//				Object subject = it.next();
//				Class clazz = subject.getClass();
//				do {
//					eventsByClass.put(clazz, event);
//					clazz = clazz.getSuperclass();
//				} while (clazz != null);
//			}
//			for (Iterator it = event.getSubjectClassesToClear().iterator(); it.hasNext(); ) {
//				Class clazz = (Class) it.next();
//				do {
//					eventsByClass.put(clazz, event);
//					clazz = clazz.getSuperclass();
//				} while (clazz != null);
//			}

		} finally {
			eventsByZoneMutex.releaseLock();
		}
	}

	/**
	 * @see com.nightlabs.notification.NotificationManager#addNotificationListener(java.lang.String, java.lang.Class, com.nightlabs.notification.NotificationListener)
	 */
	public void addNotificationListener(
			String zone, Class subjectClass, final NotificationListener listener)
	{
		super.addNotificationListener(zone, subjectClass, listener);

		eventsByZoneMutex.acquireReadLock();
		try {
			Map eventsByClass = (Map) eventsByZone.get(zone);
			if (eventsByClass == null) {
				if (zone != null) {
					eventsByClass = (Map) eventsByZone.get(null);
					if (eventsByClass == null)
						return;
				}
				else
					return;
			}

			final NotificationEvent event = (NotificationEvent) eventsByClass.get(subjectClass);
			if (event == null)
				return;

			Display.getDefault().asyncExec(
					new Runnable() {
						public void run()
						{
							SelectionManager.this.notify(event, listener);
						}
					});
		} finally {
			eventsByZoneMutex.releaseLock();
		}
	}

	/**
	 * @see com.nightlabs.notification.NotificationManager#intercept(com.nightlabs.notification.NotificationEvent)
	 */
	protected NotificationEvent intercept(NotificationEvent event)
	{
		event = super.intercept(event);
		registerEvent(event);
		return event;
	}

}
