/*
 * Created on Apr 27, 2005
 *
 */
package org.nightlabs.ipanema.rcp.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	public SelectionNotificationProxy(Object source)
	{
		this(source, null, false);
	}
	
	public SelectionNotificationProxy(Object source, String zone, boolean ignoreInheritance)
	{
		this.source = source;
		this.zone = zone;
		this.ignoreInheritance = ignoreInheritance;
	}
	
	public void selectionChanged(SelectionChangedEvent event) 
	{
		ISelection selection = event.getSelection();
		if(!selection.isEmpty())
		{
			NotificationEvent e;
			if(selection instanceof IStructuredSelection)
			{
				List<Object> subjects = new ArrayList<Object>();
				Iterator i = ((IStructuredSelection)selection).iterator();
				while(i.hasNext())
				{
					Object o = i.next();
					PersistenceCapable pc = getPersistenceCapable(o);
					if (pc != null)
						subjects.add(JDOHelper.getObjectId(pc));
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
