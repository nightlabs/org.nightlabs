/*
 * Created 	on Sep 13, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.part;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import org.nightlabs.base.util.RCPUtil;

/**
 * PartVisibilityTracker is used to determine the visibility sate of {@link org.eclipse.ui.IWorkbenchPart}s.
 * It can be asked whether a Part is visble (see. {@link #isPartVisible(IWorkbenchPart)}).
 * Addtionally {@link org.nightlabs.base.part.PartVisibilityListener} can be 
 * registered that will be notified when the visibility status of a part changes
 * (see {@link #addVisibilityListener(IWorkbenchPart, PartVisibilityListener)}).  
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PartVisibilityTracker {
	
	public static final int PART_STATUS_NO_VISIBILITY_CHANGE = 1;
	public static final int PART_STATUS_VISIBLE = 2;
	public static final int PART_STATUS_HIDDEN = 4;
	
	protected static Logger LOGGER = Logger.getLogger(PartVisibilityTracker.class);
	
	/**
	 * An instance of this listener will to the active WorkbenchPage and tracks
	 * status changes of the parts within that page.
	 * @see PartVisibilityTracker#initialize() 
	 */
	protected static class Listener implements IPartListener2 {
	
		private PartVisibilityTracker tracker;
		
		public Listener(PartVisibilityTracker tracker) {
			this.tracker = tracker;
		}

		/**
		 * Set the status for the part referenced by the given partRef 
		 */
		private void setPartStatus(IWorkbenchPartReference partRef, int newStatus) {
			PartStatus status = tracker.getPartStatus(partRef);
			if (status != null)
				status.setStatus(newStatus);
		}		
		
		private PartStatus getPartStatus(IWorkbenchPartReference partRef) {
			return tracker.getPartStatus(partRef);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partActivated(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partActivated  partRef: "+partRef.getPartName());
			if (getPartStatus(partRef).getStatus() == PART_STATUS_HIDDEN)
				setPartStatus(partRef, PART_STATUS_VISIBLE);
			else
				setPartStatus(partRef, PART_STATUS_NO_VISIBILITY_CHANGE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partBroughtToTop  partRef: "+partRef.getPartName());
			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partClosed  partRef: "+partRef.getPartName());
			setPartStatus(partRef, PART_STATUS_HIDDEN);
			getPartStatus(partRef).removeAllListeners();
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partDeactivated(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partDeactivated  partRef: "+partRef.getPartName());
			setPartStatus(partRef, 
					partRef.getPage().isPartVisible(partRef.getPart(false)) ? PART_STATUS_NO_VISIBILITY_CHANGE: PART_STATUS_HIDDEN);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partOpened(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partOpened  partRef: "+partRef.getPartName());
			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partHidden(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partHidden  partRef: "+partRef.getPartName());
			setPartStatus(partRef, PART_STATUS_HIDDEN);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partVisible  partRef: "+partRef.getId());
			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partInputChanged(IWorkbenchPartReference partRef) {
//			LOGGER.debug("partInputChanged  partRef: "+partRef.getPartName());
			setPartStatus(partRef, PART_STATUS_NO_VISIBILITY_CHANGE);
		}
	}

//	public static final int PART_STATUS_CLOSED = 4;
	
	/**
	 * Class to hold the status of a IWorkbenchPart and notify visibility listners.
	 */
	protected static class PartStatus {
		private IWorkbenchPart part;
		private IWorkbenchPartReference partRef;
		private int status;
		private List listeners = new LinkedList();
		
		private boolean haveDelayedHideStatusChanges = false;
		private boolean doNotification = false;
		
		public PartStatus(IWorkbenchPart part) {
			this.part = part;
			this.partRef = null;
		}
		
		public PartStatus(IWorkbenchPart part, IWorkbenchPartReference partRef) {
			this.partRef = partRef;
			this.part = part;
		}

		/**
		 * @return Returns the status.
		 */
		public int getStatus() {
			return status;
		}
		
		public IWorkbenchPartReference getPartRef() {
			return partRef;
		}
		
		public void setPartRef(IWorkbenchPartReference partRef) {
			this.partRef = partRef;
		}
		
		public Runnable delayedHiddenNotifyer = new Runnable() {
			public void run() {
//				LOGGER.debug("delayedHiddenNotifyer: have visible, set haveDelayedHideStatusChanges to false status: "+status+" part: "+part+" have..."+haveDelayedHideStatusChanges);
				if (haveDelayedHideStatusChanges) {
					status = PART_STATUS_HIDDEN;
					notifyListeners(status);
				}
			}
		};

		/**
		 * @param status The status to set.
		 */
		public void setStatus(int newStatus) {
			if (newStatus == PART_STATUS_NO_VISIBILITY_CHANGE)
				return;
//			LOGGER.debug("setStatus: newStatus: "+newStatus+" part: "+part);
			if (haveDelayedHideStatusChanges) {
				if (newStatus == PART_STATUS_VISIBLE) {
					haveDelayedHideStatusChanges = false;
				}
			}
			if (status != newStatus) {
				if (newStatus == PART_STATUS_HIDDEN) {
					haveDelayedHideStatusChanges = true;
//					LOGGER.debug("setStatus: have hidden scedule delayed notification newStatus: "+newStatus+" part: "+part);
					Display.getDefault().timerExec(200, delayedHiddenNotifyer);
				}
				else {
//					LOGGER.debug("setStatus: have visible, set haveDelayedHideStatusChanges to false newStatus: "+newStatus+" part: "+part);
					haveDelayedHideStatusChanges = false;
					status = newStatus;
					notifyListeners(status);
				}
			}
		}
		
		/**
		 * Notify all listeners of the new satus
		 *
		 */
		public void notifyListeners(int newStatus) {
			for (Iterator iter = new LinkedList(listeners).iterator(); iter.hasNext();) {
				PartVisibilityListener listener = (PartVisibilityListener) iter.next();
				switch (newStatus) {
					case PART_STATUS_HIDDEN:
						listener.partHidden(partRef);
						break;
					case PART_STATUS_VISIBLE:
						listener.partVisible(partRef);
						break;
				}
				
			}
		}
		
		public void asyncInitStatus(int status) {
			this.status = status;
			notifyListeners(status);
		}

		public IWorkbenchPart getPart() {
			return part;
		}
		
		public void addListener(PartVisibilityListener listener) {
			listeners.add(listener);
		}
		
		public void removeListener(PartVisibilityListener listener) {
			listeners.remove(listener);
		}
		
		public void removeAllListeners() {
			listeners.clear();
		}
	}
	
	/**
	 * key: IWorkbenchPart part<br/>
	 * value: PartStatus satus
	 */
	private Map partStati = new HashMap();

	/**
	 * 
	 */
	public PartVisibilityTracker() {
		super();
	}
	
	/**
	 * Get the PartStatus for the part referenced by the given partRef.
	 * Calls {@link #getPartStatus(IWorkbenchPartReference, IWorkbenchPart)}
	 * with partRef and partRef.getPart()
	 */
	private PartStatus getPartStatus(IWorkbenchPartReference partRef) {
		return getPartStatus(partRef, partRef.getPart(false));
	}
	
	/**
	 * Get the PartStatus for the given part. If not found a new PartStatus
	 * will be created for the given part and partRef.
	 */
	private PartStatus getPartStatus(IWorkbenchPartReference partRef, IWorkbenchPart part) {
		PartStatus status = (PartStatus)partStati.get(part);
		if (status == null) {			
			status = new PartStatus(part, partRef);
			partStati.put(part, status);
		}
		else if (status.getPartRef() == null)
			status.setPartRef(partRef);
		return status;
	}

	/**
	 * Calls {@link #getPartStatus(IWorkbenchPartReference, IWorkbenchPart)}
	 * with null as partRef parameter.
	 */
	private PartStatus getPartStatus(IWorkbenchPart part) {
		return getPartStatus(null, part);
	}

	/**
	 * Add a visibility Listener to this tracker.
	 * The passed listener will be notified of visibility changes of the given 
	 * part. You can add a listeners at any time but only to a non null 
	 * {@link IWorkbenchPart}. If at listener registration the WorkbenchWindow
	 * was not already created this listener will be asyncronously triggered with 
	 * {@link PartVisibilityListener#partVisible(IWorkbenchPartReference)}.  
	 * Note that all listeners to a part will be removed when this part is 
	 * closed.
	 *  
	 * @param part The IWorkbenchPart which visibility changes should be notified for.
	 * @param listener The listener to be notified.
	 */
	public void addVisibilityListener(final IWorkbenchPart part, final PartVisibilityListener listener) {
		if (part == null)
			throw new IllegalArgumentException("Cannot add listener for null-part. Parameter part must not be null");
		if (listener == null)
			throw new IllegalArgumentException("Parameter listener must not be null");
		
		if (!initialized)
			initialize();
		final PartStatus status = getPartStatus(part);
		if (status == null) {
//			LOGGER.warn("Autocreation of PartStatus failed status is null, can not add Listener "+listener);
			return;
		}
		status.addListener(listener);
//		LOGGER.debug("Added PartVisibiltyListener "+listener);
		
		if (!initialized)
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					status.asyncInitStatus(PART_STATUS_VISIBLE);
//					LOGGER.debug("Invoked asyncInitStatus()");
				}
			});
	}
		
	/**
	 * Remove a PartVisibilityListener listener for given part.
	 */
	public void removeVisibilityListener(IWorkbenchPart part, PartVisibilityListener listener) {
		PartStatus status = getPartStatus(part);
		if (status == null) {
			LOGGER.warn("Autocreation of PartStatus failed status is null, can not remove Listener "+listener);
			return;
		}
		status.removeListener(listener);
	}
	
	/**
	 * Get the visibility status of the given part. Will return one out of
	 * {@link #PART_STATUS_VISIBLE} or {@link #PART_STATUS_HIDDEN}.
	 *  
	 * @param part The part the visibilty should be returned
	 */
	public int getPartVisibilityStatus(IWorkbenchPart part) {
		PartStatus status = getPartStatus(part);
		if (status != null)
			return status.getStatus();
		return PART_STATUS_HIDDEN;
	}
	
	private boolean initialized = false;
	public void initialize() {
		if (initialized)
			return;
		if (RCPUtil.getWorkbenchPage() == null)
			return;
		Listener listener = new Listener(this);
		RCPUtil.getWorkbenchPage().addPartListener(listener);
		initialized = true;
	}
	
	
	private static PartVisibilityTracker sharedInstance;
	
	/**
	 * Shared instance of PartVisibilityTracker. Should be initialized once.
	 * @see #initialize() 
	 */
	public static PartVisibilityTracker sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PartVisibilityTracker();
		return sharedInstance;
	}
	
	/**
	 * Check whether the given part is visible.
	 * Be sure that at some point of time {@link #initialize()} was called before
	 * you call this method.  
	 * Calls {@link #getPartVisibilityStatus(IWorkbenchPart)} for the sharedInstance.
	 */
	public static boolean isPartVisible(IWorkbenchPart part) {
		return sharedInstance().getPartVisibilityStatus(part) == PART_STATUS_VISIBLE;
	}
	
	
	
}
