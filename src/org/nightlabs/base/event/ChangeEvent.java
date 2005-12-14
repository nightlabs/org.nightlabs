/*
 * Created 	on Mar 15, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.event;

import org.eclipse.swt.events.TypedEvent;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @deprecated Please use the new NightLabs Notification Framework
 */
public class ChangeEvent extends TypedEvent {

	private boolean changeState;
	/**
	 * @param object
	 */
	public ChangeEvent(Object object, boolean changeState) {
		super(object);
		this.changeState = changeState;
	}

//	/**
//	 * @param e
//	 */
//	public ChangeEvent(Event e) {
//		super(e);
//	}

}
