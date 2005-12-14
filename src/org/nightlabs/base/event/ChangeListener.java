/*
 * Created 	on Mar 15, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.event;

import java.util.EventListener;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @deprecated Please use the new NightLabs Notification Framework
 */
public interface ChangeListener extends EventListener {
	public void stateChanged(ChangeEvent event);
}
