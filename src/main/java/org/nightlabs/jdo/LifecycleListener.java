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

import javax.jdo.listener.AttachLifecycleListener;
import javax.jdo.listener.ClearLifecycleListener;
import javax.jdo.listener.CreateLifecycleListener;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.DetachLifecycleListener;
import javax.jdo.listener.DirtyLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.StoreLifecycleListener;
import javax.jdo.spi.PersistenceCapable;

import org.nightlabs.jdo.callbacks.AttachCallback;
import org.nightlabs.jdo.callbacks.ClearCallback;
import org.nightlabs.jdo.callbacks.CreateCallback;
import org.nightlabs.jdo.callbacks.DeleteCallback;
import org.nightlabs.jdo.callbacks.DetachCallback;
import org.nightlabs.jdo.callbacks.DirtyCallback;
import org.nightlabs.jdo.callbacks.LoadCallback;
import org.nightlabs.jdo.callbacks.StoreCallback;

/**
 * Delegates LifecycleEvents to the PersistenceCapable
 * classes themselves if they implement certain callbacks.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @see org.nightlabs.jdo.callbacks.AttachCallback
 * @see org.nightlabs.jdo.callbacks.ClearCallback
 * @see org.nightlabs.jdo.callbacks.CreateCallback
 * @see org.nightlabs.jdo.callbacks.DeleteCallback
 * @see org.nightlabs.jdo.callbacks.DetachCallback
 * @see org.nightlabs.jdo.callbacks.DirtyCallback
 * @see org.nightlabs.jdo.callbacks.LoadCallback
 * @see org.nightlabs.jdo.callbacks.StoreCallback
 *
 * @deprecated Callbacks for all seem to be part of the JDO standard by now.
 */
@Deprecated
public class LifecycleListener
implements
	AttachLifecycleListener,
	ClearLifecycleListener,
	CreateLifecycleListener,
	DeleteLifecycleListener,
	DetachLifecycleListener,
	DirtyLifecycleListener,
	LoadLifecycleListener,
	StoreLifecycleListener
{

	private PersistenceCapable getPC(InstanceLifecycleEvent event) {
		if (event.getSource() instanceof PersistenceCapable)
			return (PersistenceCapable)event.getSource();
		else throw new IllegalArgumentException("Source of Lifecycle event is not PersistenceCapable but "+event.getSource());
	}
	
	/**
	 * @see javax.jdo.AttachLifecycleListener#preAttach(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preAttach(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof AttachCallback)
			((AttachCallback)pc).nljdoPreAttach();
	}

	/**
	 * @see javax.jdo.AttachLifecycleListener#postAttach(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postAttach(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof AttachCallback)
			((AttachCallback)pc).nljdoPostAttach();
	}

	/**
	 * @see javax.jdo.ClearLifecycleListener#preClear(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preClear(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof ClearCallback)
			((ClearCallback)pc).nljdoPreClear();
	}

	/**
	 * @see javax.jdo.ClearLifecycleListener#postClear(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postClear(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof ClearCallback)
			((ClearCallback)pc).nljdoPostClear();
	}

	/**
	 * @see javax.jdo.CreateLifecycleListener#postCreate(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postCreate(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof CreateCallback)
			((CreateCallback)pc).nljdoPostCreate();
	}

	/**
	 * @see javax.jdo.DeleteLifecycleListener#preDelete(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preDelete(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DeleteCallback)
			((DeleteCallback)pc).nljdoPreDelete();
	}

	/**
	 * @see javax.jdo.DeleteLifecycleListener#postDelete(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postDelete(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DeleteCallback)
			((DeleteCallback)pc).nljdoPostDelete();
	}

	/**
	 * @see javax.jdo.DetachLifecycleListener#preDetach(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preDetach(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DetachCallback)
			((DetachCallback)pc).nljdoPreDetach();
	}

	/**
	 * @see javax.jdo.DetachLifecycleListener#postDetach(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postDetach(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DetachCallback)
			((DetachCallback)pc).nljdoPostDetach();
	}

	/**
	 * @see javax.jdo.DirtyLifecycleListener#preDirty(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preDirty(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DirtyCallback)
			((DirtyCallback)pc).nljdoPreDirty();
	}

	/**
	 * @see javax.jdo.DirtyLifecycleListener#postDirty(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postDirty(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof DirtyCallback)
			((DirtyCallback)pc).nljdoPostDirty();
	}

	/**
	 * @see javax.jdo.LoadLifecycleListener#postLoad(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postLoad(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof LoadCallback)
			((LoadCallback)pc).nljdoPostLoad();
	}

	/**
	 * @see javax.jdo.StoreLifecycleListener#preStore(javax.jdo.InstanceLifecycleEvent)
	 */
	public void preStore(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof StoreCallback)
			((StoreCallback)pc).nljdoPreStore();
	}

	/**
	 * @see javax.jdo.StoreLifecycleListener#postStore(javax.jdo.InstanceLifecycleEvent)
	 */
	public void postStore(InstanceLifecycleEvent event) {
		PersistenceCapable pc = getPC(event);
		if (pc instanceof StoreCallback)
			((StoreCallback)pc).nljdoPostStore();
	}
	
}
