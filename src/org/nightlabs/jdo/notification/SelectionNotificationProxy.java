/* *****************************************************************************
 * org.nightlabs.jdo - NightLabs Eclipse utilities for JDO                     *
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

package org.nightlabs.jdo.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.spi.PersistenceCapable;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.nightlabs.base.notification.SelectionManager;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.SubjectCarrier;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public class SelectionNotificationProxy implements ISelectionChangedListener
{
	protected Object source;
	protected String zone;
	protected boolean ignoreInheritance;
	protected boolean clearOnEmptySelection;

	/**
	 * Constructor.
	 *
	 * @param source Which will be the source of all notifications?
	 * @param zone The zone in which selections occur.
	 * @param ignoreInheritance If <code>true</code>, all classes will be managed separately as if inheritance doesn't exist.
	 *		If <code>false</code>, the superclasses (recursively) of the selected objects will be assigned with the new selection. 
	 * @param clearOnEmptySelection If <code>true</code>, an instance will track all classes that have been selected
	 *		and in case of an empty selection, it will clear all selections that are assigned to these collected classes.
	 *		If <code>false</code>, empty selections are ignored and do not have any effect (do not modify the state of assigned selections).
	 */
	public SelectionNotificationProxy(
			Object source,
			String zone,
			boolean ignoreInheritance,
			boolean clearOnEmptySelection)
	{
		this.source = source;
		this.zone = zone;
		this.ignoreInheritance = ignoreInheritance;
		this.clearOnEmptySelection = clearOnEmptySelection;
	}

	private Set<Class> managedClasses = new HashSet<Class>();

	private static final List EMPTY_LIST = Collections.unmodifiableList(new LinkedList<Class>());

	public void selectionChanged(SelectionChangedEvent event) 
	{
		ISelection selection = event.getSelection();
		if(selection.isEmpty()) {
			// we clean the selection for our zone and for all classes that we have learned about so far
			if (clearOnEmptySelection && !managedClasses.isEmpty())
				SelectionManager.sharedInstance().notify(
						new NotificationEvent(source, zone, EMPTY_LIST, managedClasses));	
		}
		else {
			NotificationEvent e;
			if(selection instanceof IStructuredSelection)
			{
				List<Object> subjects = new ArrayList<Object>();
				Iterator i = ((IStructuredSelection)selection).iterator();
				while(i.hasNext())
				{
					Object o = i.next();
					if (clearOnEmptySelection &&
							o != null && !managedClasses.contains(o.getClass()))
						managedClasses.add(o.getClass());

					PersistenceCapable pc = getPersistenceCapable(o);
					if (pc != null) {
						Object oid = JDOHelper.getObjectId(pc);
						if (clearOnEmptySelection &&
								oid != null && !managedClasses.contains(oid.getClass()))
							managedClasses.add(oid.getClass());
						subjects.add(oid);
					}
					else
						subjects.add(o);
				}
				SubjectCarrier[] subjectCarriers = new SubjectCarrier[subjects.size()];
				for (int j = 0; j < subjects.size(); j++) {
					subjectCarriers[j] = new SubjectCarrier(subjects.get(j));
					subjectCarriers[j].setInheritanceIgnored(ignoreInheritance);
				}
				e = new NotificationEvent(source, zone, subjectCarriers);

//				e = new NotificationEvent(source, zone, subjects.toArray());
			}
			else { // TODO How can a PersistenceCapable be an ISelection???
				if(selection instanceof PersistenceCapable)
					e = new NotificationEvent(source, zone, JDOHelper.getObjectId(selection));
				else
					e = new NotificationEvent(source, zone, selection);
			}
			SelectionManager.sharedInstance().notify(e);
		}
	}

	/**
	 * Returns the PersistenceCapable extracted out of the given 
	 * selectionObject or null if none can be extracted.
	 */
	protected PersistenceCapable getPersistenceCapable(Object selectionObject) {
		if (selectionObject instanceof PersistenceCapable)
			return (PersistenceCapable)selectionObject;
		return null;
	}
}
