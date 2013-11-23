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

import java.lang.ref.WeakReference;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class NotificationListenerMeta
{
	private String zone;
	private Class<?> subjectClass;
	private WeakReference<NotificationListener> notificationListenerRef;
	private String notificationMode;
	private int counter;

//	/**
//	 * key: Class subjectClass
//	 * value: Integer counter
//	 */
//	private Map subjectCounters = new HashMap();
//
	public NotificationListenerMeta(String zone, Class<?> subjectClass, NotificationListener listener, String notificationMode)
	{
		this.zone = zone;
		this.subjectClass = subjectClass;
		this.notificationListenerRef = new WeakReference<NotificationListener>(listener);
		this.notificationMode = notificationMode;
	}
	/**
	 * @return Returns the zone.
	 */
	public String getZone()
	{
		return zone;
	}
	/**
	 * @return Returns the subjectClass.
	 */
	public Class<?> getSubjectClass()
	{
		return subjectClass;
	}
	/**
	 * @return Returns the notificationMode.
	 */
	public String getNotificationMode()
	{
		return notificationMode;
	}
	/**
	 * @return the notificationListener. Might return <tt>null</tt>, if weak reference has been disposed!
	 */
	public NotificationListener getNotificationListener()
	{
		return notificationListenerRef.get();
	}
	/**
	 * @return Returns the counter.
	 */
	public int getCounter()
	{
		return counter;
	}

	public int incCounter()
	{
		return ++counter;
	}

	public int decCounter()
	{
		return --counter;
	}

}
