/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.nightlabs.base.composite.FadeableComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.part.PartVisibilityListener;
import org.nightlabs.base.part.PartVisibilityTracker;
import org.nightlabs.base.util.RCPUtil;

/**
 * A ViewController handles the visibility of a set of Views. It accepts 
 * {@link org.nightlabs.base.view.ControllableView}s whose real content
 * can be hidden by the Controller. When hidden the real contents can 
 * either be sent to background or disposed 
 * (see {@link #updateViews()}, {@link #disposeViewsContents()}).
 * 
 * The visibility of a view id determined by their 
 * {@link org.nightlabs.base.view.ControllableView#canDisplayView()} method.
 * 
 * Implementors have to provide a Composite that will be displayed when
 * the real contents are hidden.
 * 
 * Views are managed when they register themselves by {@link #registerView(ControllableView)}.
 * The registratin of a View should be done in its constructor. See {@link org.nightlabs.base.view.ControllableView}
 * for more information on how to use them with a ViewController. 
 * 
 * A ViewController is responsible for somehow creating or listening to event 
 * that might cause the visibility of its registered views to change and 
 * use the ViewController API like {@link #updateViews()} to update the registered views.
 * 
 * Note that ViewController is also linked to the {@link PartVisibilityTracker}. Whenever the registered
 * View's real content was created it will call {@link PartVisibilityListener#partVisible(org.eclipse.ui.IWorkbenchPartReference)}
 * on all registrations that implement {@link PartVisibilityListener}. {@link PartVisibilityListener#partHidden(org.eclipse.ui.IWorkbenchPartReference)}
 * is called accordingly when the "ConditionUnsatisfiedComposite" is created. Both times the 
 * partReference parameter for the listeners callback method might be null.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @see #createNewConditionUnsatisfiedComposite(Composite)
 * @see org.nightlabs.base.view.ControllableView
 */
public abstract class ViewController {

	protected class ControlledView {
		
		private ControllableView view;
		
		private StackLayout stackLayout;
		private XComposite wrapper;
		private FadeableComposite fadeableWrapper;
		private XComposite conditionWrapper;
		
		private boolean contentsCreated = false;
		private boolean internalPartsCreated = false;
		
		public ControlledView(ControllableView view) {
			this.view = view;
		}
		
		/**
		 * Creates the internal Controls hosting the real view
		 * content.
		 *  
		 * @param parent
		 */
		public void createViewControl(Composite parent) {
			wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
			stackLayout = new StackLayout();
			wrapper.setLayout(stackLayout);
			conditionWrapper = new XComposite(wrapper, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
			createNewConditionUnsatisfiedComposite(conditionWrapper);
			createFadeableWrapper();
			internalPartsCreated = true;			
		}
		
		public XComposite getWrapper() {
			return wrapper;
		}
		
		protected void createFadeableWrapper() {
			fadeableWrapper = new FadeableComposite(wrapper, SWT.NONE);
			fadeableWrapper.setLayout(new GridLayout());
		}
		
		/**
		 * Updates the view according to the current login-state.
		 */
		protected void updateView() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!internalPartsCreated)
						return;
					doCreateContents();
					if (view.canDisplayView()) { 
						stackLayout.topControl = fadeableWrapper;
						setViewActionsVisible(true);
						callPartVisibilityListener(view, PartVisibilityListenerMethod.partVisible);
					}
					else { 
						stackLayout.topControl = conditionWrapper;
						setViewActionsVisible(false);
						callPartVisibilityListener(view, PartVisibilityListenerMethod.partHidden);
					}
					wrapper.layout(true, true);
					
				}
			});
		}
		
		/**
		 * Creates the View contents if possible
		 * and neccesary
		 */
		protected void doCreateContents() {
			if (!contentsCreated) {
				if (view.canDisplayView()) {
					view.createViewContents(fadeableWrapper);
					contentsCreated = true;
				}
			}
		}
		
		/**
		 * Disposes and recreates the fadeable
		 * wrapper for the real view content. 
		 */
		protected void disposeViewContents() {
			if (!internalPartsCreated)
				return;
			fadeableWrapper.dispose();
			createFadeableWrapper();
			contentsCreated = false;
		}
		
		/**
		 * Show/Hide all Viewactions of a View
		 * @param visible
		 */
		protected void setViewActionsVisible(boolean visible) {
			RCPUtil.setViewActionsVisible(view, visible);
		}
		
		
	}
	
	/**
	 * Sublcasses should create a new Composite indicating that the conditions
	 * neccessary to show a View were not satisfied.
	 * 
	 * @param parent The parent the Composite shoulb be created for
	 * @return A new Composite
	 */
	protected abstract Composite createNewConditionUnsatisfiedComposite(Composite parent);
	
	/**
	 * key: ControllableView view
	 * value: ControlledView controlledView
	 */
	private Map<ControllableView, ControlledView> controlledViews = new HashMap<ControllableView, ControlledView>();
	
	/**
	 * Update all registered Views according to their satus returned by
	 * {@link ControllableView#canDisplayView()}.
	 * 
	 * @param view
	 */
	public void updateView(ControllableView view) {
		ControlledView controlledView = (ControlledView)controlledViews.get(view);
		controlledView.updateView();
	}

	/**
	 * Get the ControlledView for the given ControllableView if registrered.
	 * Will return null otherwise
	 */
	protected ControlledView getControlledView(ControllableView view) {
		return (ControlledView)controlledViews.get(view);
	}

	/**
	 * Update all registered Views according to their satus returned by
	 * {@link ControllableView#canDisplayView()}
	 */
	public void updateViews() {
		for (Iterator iter = controlledViews.keySet().iterator(); iter.hasNext();) {
			ControllableView view = (ControllableView) iter.next();
			updateView(view);
		}
	}
	
	/**
	 * Dispose the real contents of the given View.
	 */
	public void disposeViewContents(ControllableView view) {
		ControlledView controlledView = getControlledView(view);
		if (controlledView != null)
			controlledView.disposeViewContents();
	}
	
	/**
	 * Dispose the real contents of all registered Views.
	 */
	protected void disposeViewsContents() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (Iterator iter = controlledViews.keySet().iterator(); iter.hasNext();) {
					ControllableView view = (ControllableView) iter.next();
					disposeViewContents(view);
				}
			}
		});
	}
	
	/**
	 * Register a view to this controller.
	 * When done so make sure that the createPartControl(Composite)
	 * method of the passed View is delegated to 
	 * this Controllers {@link #createViewControl(ControllableView, Composite)} method.
	 * 
	 * @param view The view to be controlled by this Controller.
	 */
	public void registerView(ControllableView view) {
		ControlledView controlledView = new ControlledView(view);
		controlledViews.put(view, controlledView);
	}

	/**
	 * Unregister a controlled View from the Controller.
	 * 
	 * @param view The View to unregister.
	 */
	public void unregisterView(ControllableView view) {
		controlledViews.remove(view);
	}
	
	/**
	 * Should be called by controlled views in their 
	 * createPartControl(Composite) method.
	 * 
	 * @param view The controlled view.
	 * @param parent The parent passed to the createPartControl() method.
	 */
	public void createViewControl(final ControllableView view, Composite parent) {
		ControlledView controlledView = getControlledView(view);
		if (controlledView == null)
			throw new IllegalStateException("The ControlledView instance of view "+view.getTitle()+"("+view.getClass().getName()+") could not be found. Maybe it was not registered. Use ViewController#registerView() prior to this method.");
		if (view != null) {
			controlledView.createViewControl(parent);
			controlledView.getWrapper().addDisposeListener(
					new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							unregisterView(view);
						}
					}
			);
			controlledView.updateView();
		}
	}
	
	private enum PartVisibilityListenerMethod {
		partVisible,
		partHidden
	}
	
	private void callPartVisibilityListener(ControllableView listenerView, PartVisibilityListenerMethod listenerMethod) {
		if (listenerView instanceof IWorkbenchPart) {
			int partStatus = PartVisibilityTracker.sharedInstance().getPartVisibilityStatus((IWorkbenchPart)listenerView);
			if (partStatus != PartVisibilityTracker.PART_STATUS_HIDDEN)
				if (listenerView instanceof PartVisibilityListener) {
					IWorkbenchPartReference partRef = RCPUtil.searchPartReference((IWorkbenchPart)listenerView);						
					if (listenerMethod == PartVisibilityListenerMethod.partVisible)
						((PartVisibilityListener)listenerView).partVisible(partRef);
					if (listenerMethod == PartVisibilityListenerMethod.partHidden)
						((PartVisibilityListener)listenerView).partHidden(partRef);
				}
		}
	}
}
