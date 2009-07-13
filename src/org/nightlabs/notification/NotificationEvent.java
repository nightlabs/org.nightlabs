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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class NotificationEvent
extends EventObject
// implements Cloneable
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	private transient NotificationManager notificationManager;

	private String zone = null;

	private List<SubjectCarrier> subjectCarriers = new ArrayList<SubjectCarrier>();

	/**
	 * What has been changed (or references to it - e.g. IDs).
	 * <p>
	 * This is created dynamically out of {@link #subjectCarriers}.
	 */
	private transient Set<?> subjects = null;

	/**
	 * <tt>null</tt> cannot be put into the subjects, hence there's no way to clear e.g. a
	 * selection by using solely <tt>subjects</tt>. To make this possible,
	 * <tt>subjectClassesToClear</tt> can contain classes for the control
	 * <p>
	 * This is created dynamically out of {@link #subjectCarriers}.
	 */
	private transient List<Class<?>> subjectClassesToClear = null;

	protected void clearCache()
	{
		subjects = null;
		subjectClassesToClear = null;
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Object)}
	 * with <tt>zone = null</tt>.
	 */
	public NotificationEvent(Object source, Object subject)
	{
		this(source, (String)null, subject);
	}

	/**
	 * This is a convenience constructor calling {@link #NotificationEvent(Object, String, Object, Class)}
	 * with <tt>subjectClassToClear = null</tt>.
	 *
	 * @param source The source of the event (e.g. a composite that caused the notification).
	 * @param zone The zone in which to notify. If <tt>null</tt> all listeners in all zones will be notified.
	 * @param subject A non-<tt>null</tt> <tt>Object</tt> - e.g. a JDO object-id.
	 */
	public NotificationEvent(Object source, String zone, Object subject)
	{
		this(source, zone, subject, null);
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Object, Class)}
	 * with <tt>zone = null</tt>.
	 */
	public NotificationEvent(Object source, Object subject, Class<?> subjectClassToClear)
	{
		this(source, (String)null, subject, subjectClassToClear);
	}

	/**
	 * @param source The source of the event (e.g. a composite that caused the notification).
	 * @param zone The zone in which to notify. If <tt>null</tt> all listeners in all zones will be notified.
	 * @param subject Either <tt>null</tt> (then the <tt>subjectClassToClear</tt> must be defined) or a non-<tt>null</tt> <tt>Object</tt> - e.g. a JDO object-id.
	 * @param subjectClassToClear The class of a subject which should represent <tt>null</tt>. This class must be defined if <tt>subject</tt> is <tt>null</tt>.
	 * Both, <tt>subject</tt> and <tt>subjectClassToClear</tt> can be non-<tt>null</tt> indicating a real subject and a null-subject in one event.
	 */
	public NotificationEvent(Object source, String zone, Object subject, Class<?> subjectClassToClear)
	{
		super(source);
		this.zone = zone;
		if (subject == null && subjectClassToClear == null)
			throw new NullPointerException("subject and subjectClass are both null! At least one of them must be defined!");

		if (subject != null)
			subjectCarriers.add(new SubjectCarrier(subject));

		if (subjectClassToClear != null)
			subjectCarriers.add(new SubjectCarrier(subjectClassToClear));
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Object[])}
	 * with <tt>zone = null</tt>.
	 */
	public NotificationEvent(Object source, Object[] subjects)
	{
		this(source, (String)null, subjects);
	}

	/**
	 * This is a convenience constructor calling {@link #NotificationEvent(Object, String, Object[], Class[])} with
	 * <tt>subjectClassesToClear = null</tt>.
	 */
	public NotificationEvent(Object source, String zone, Object[] subjects)
	{
		this(source, zone, subjects, (Class[])null);
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Object[], Class[])}
	 * with <tt>zone = null</tt>.
	 */
	public NotificationEvent(Object source, Object[] subjects, Class<?>[] subjectClassesToClear)
	{
		this(source, (String)null, subjects, subjectClassesToClear);
	}

	/**
	 * @param source The source of the event (e.g. a composite that caused the notification).
	 * @param zone The zone in which to notify. If <tt>null</tt> all listeners in all zones will be notified.
	 * @param subjects An array of non-<tt>null</tt> objects about which to notify (<tt>null</tt> objects are silently ignored). This parameter may be <tt>null</tt> or empty, if <tt>subjectClassesToClear</tt> is defined and not empty.
	 * @param subjectClassesToClear An array of non-<tt>null</tt> classes, representing <tt>null</tt> objects that cannot be declared
	 * in the <tt>subjects</tt> array, because the system doesn't know how to dispatch <tt>null</tt> notifications (as the class is unknown).
	 */
	public NotificationEvent(Object source, String zone, Object[] subjects, Class<?>[] subjectClassesToClear)
	{
		super(source);
		this.zone = zone;
		if (subjects == null && subjectClassesToClear == null)
			throw new NullPointerException("subjects and subjectClassesToClear are both null! At least one of them must be defined!");

		if (subjects != null) {
			for (Object element : subjects) {
	//			if (subjects[i] == null)
	//				throw new NullPointerException("Object array 'subjects' must not contain null!");

				if (element != null)
					this.subjectCarriers.add(new SubjectCarrier(element));
			}
		} // if (subjects != null) {

		if (subjectClassesToClear != null) {
			for (Class<?> element : subjectClassesToClear) {
				if (element == null)
					throw new NullPointerException("Class array 'subjectClassesToClear' must not contain null!");

				this.subjectCarriers.add(new SubjectCarrier(element));
			}
		} // if (subjectClassesToClear != null) {

		if (this.subjectCarriers.isEmpty())
			throw new IllegalArgumentException("subjects and subjectClassesToClear are both empty! Need at least one entry in either of them!");
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Collection)}
	 * with <tt>zone = null</tt>.
	 */
	public NotificationEvent(Object source, Collection<?> subjects)
	{
		this(source, (String)null, subjects);
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Collection, Collection)}
	 * with <tt>subjectClassesToClear = null</tt>.
	 */
	public NotificationEvent(Object source, String zone, Collection<?> subjects)
	{
		this(source, zone, subjects, (Collection)null);
	}

	/**
	 * Convenience constructor calling {@link #NotificationEvent(Object, String, Collection, Collection)}
	 * with <tt>zone = null</tt>
	 */
	public NotificationEvent(Object source, Collection<?> subjects, Collection<? extends Class<?>> subjectClassesToClear)
	{
		this(source, (String)null, subjects, subjectClassesToClear);
	}

	/**
	 * This constructor does the same as {@link #NotificationEvent(Object, String, Object[], Class[])},
	 * but uses <tt>Collection</tt>s instead of arrays.
	 */
	public NotificationEvent(Object source, String zone, Collection<?> subjects, Collection<? extends Class<?>> subjectClassesToClear)
	{
		this(source, zone, subjects, subjectClassesToClear, null);
	}

	public NotificationEvent(Object source, String zone, Collection<?> subjects, Collection<? extends Class<?>> subjectClassesToClear, Collection<SubjectCarrier> _subjectCarriers)
	{
		super(source);
		this.zone = zone;
		if (subjects == null && subjectClassesToClear == null && _subjectCarriers == null)
			throw new NullPointerException("subjects and subjectClassesToClear and subjectCarriers are all null! At least one of them must be defined!");

		if (subjects != null) {
			for (Object subject : subjects) {
				if (subject != null)
					this.subjectCarriers.add(new SubjectCarrier(subject));
			}
		} // if (subjects != null) {

		if (subjectClassesToClear != null) {
			for (Class<?> subjectClassToClear : subjectClassesToClear) {
				if (subjectClassToClear == null)
					throw new NullPointerException("Class collection 'subjectClassesToClear' must not contain null!");

				this.subjectCarriers.add(new SubjectCarrier(subjectClassToClear));
			}
		} // if (subjectClassesToClear != null) {

		if (_subjectCarriers != null) {
			for (SubjectCarrier carrier : _subjectCarriers) {
				if (carrier != null)
					this.subjectCarriers.add(carrier);
			}
		}

		if (this.subjectCarriers.isEmpty())
			throw new IllegalArgumentException("subjects and subjectClassesToClear are both empty! Need at least one entry in either of them!");
	}

	public NotificationEvent(Object source, String zone, SubjectCarrier subjectCarrier)
	{
		super(source);
		this.zone = zone;
		if (subjectCarrier != null)
			subjectCarriers.add(subjectCarrier);
	}

	public NotificationEvent(Object source, String zone, SubjectCarrier[] subjectCarriers)
	{
		super(source);
		this.zone = zone;
		if (subjectCarriers != null)
			for (SubjectCarrier element : subjectCarriers) {
				addSubjectCarrier(element);
			}
	}

	/**
	 * @return Returns the zone.
	 */
	public String getZone()
	{
		return zone;
	}

	/**
	 * @return Returns a <b>read-only</b> <tt>Set</tt> of subjects.
	 */
	public Set getSubjects()
	{
		if (subjects == null) {
			Set<Object> s = new HashSet<Object>();
			for (Object element : subjectCarriers) {
				SubjectCarrier carrier = (SubjectCarrier) element;
				if (carrier.getSubject() != null)
					s.add(carrier.getSubject());
			}
			subjects = Collections.unmodifiableSet(s);
		}
		return subjects;
	}

	/**
	 * @return Returns the first subject or <tt>null</tt> if none available. This method
	 *		is mainly intended to be used when the NotificationManager is used for selections.
	 */
	public Object getFirstSubject()
	{
		if (subjectCarriers.isEmpty())
			return null;

		SubjectCarrier sc = subjectCarriers.get(0);
		return sc.getSubject();
	}

	/**
	 * @return Returns a <b>read-only</b> <tt>List</tt> with instances of {@link Class}.
	 */
	public List<Class<?>> getSubjectClassesToClear()
	{
		if (subjectClassesToClear == null) {
			ArrayList<Class<?>> l = new ArrayList<Class<?>>();
			for (Object element : subjectCarriers) {
				SubjectCarrier carrier = (SubjectCarrier) element;
				for (Object element0 : carrier.getSubjectClasses()) {
					Class<?> clazz = (Class<?>) element0;
					l.add(clazz);
				}
			}
			subjectClassesToClear = Collections.unmodifiableList(l);
		}

		return subjectClassesToClear;
	}

	public void addSubjectCarrier(SubjectCarrier subjectCarrier)
	{
		subjectCarriers.add(subjectCarrier);
		clearCache();
	}

	public void removeSubjectCarrier(int index)
	{
		subjectCarriers.remove(index);
		clearCache();
	}

	public void removeSubjectCarrier(SubjectCarrier subjectCarrier)
	{
		subjectCarriers.remove(subjectCarrier);
		clearCache();
	}

	private transient List<SubjectCarrier> subjectCarriersReadOnly = null;

	/**
	 * @return Returns a <b>read-only</b> <tt>List</tt> with instances of type {@link SubjectCarrier}.
	 */
	public List<SubjectCarrier> getSubjectCarriers()
	{
		if (subjectCarriersReadOnly == null)
			subjectCarriersReadOnly = Collections.unmodifiableList(subjectCarriers);

		return subjectCarriersReadOnly;
	}

	public NotificationManager getNotificationManager()
	{
		return notificationManager;
	}
	protected void setNotificationManager(NotificationManager notificationManager)
	{
		this.notificationManager = notificationManager;
	}

//	/**
//	 * This method does NOT clone the source. See {@link SubjectCarrier#clone()} for details
//	 * about what is cloned within the <tt>SubjectCarrier</tt>s.
//	 *
//	 * @see java.lang.Object#clone()
//	 */
//	public Object clone()
//	{
//		List newSubjectCarriers = new ArrayList();
//		for (Iterator it = subjectCarriers.iterator(); it.hasNext(); ) {
//			SubjectCarrier carrier = (SubjectCarrier) it.next();
//			newSubjectCarriers.add(carrier.clone());
//		}
//		NotificationEvent n = new NotificationEvent(
//				source, zone, null, null, newSubjectCarriers);
//		return n;
//	}
}
