/*
 * Created 	on Sep 1, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;

/**
 * ControllableViews are used together with {@link org.nightlabs.rcp.view.ViewController}s.
 * When implementing a ControllableView register it (best in its constructor)
 * to a ViewController. 
 * Delegate the {@link org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)}
 * method to this Controller by calling its method {@link org.nightlabs.rcp.view.ViewController#createViewControl(ControllableView, Composite)}.
 * Create the real contents of the view in the {@link #createViewContents(Composite)}
 * method that will be called by the ViewController if this view can be displayed.
 * ControllableViews themselves control whether they might be displayed by {@link #canDisplayView()}
 *   
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface ControllableView extends IViewPart {

	/**
	 * Create the real contents of a {@link org.eclipse.ui.part.ViewPart}
	 * here. Delegate the ViewParts 
	 * {@link org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)}
	 * method to the {@link ViewController#createViewControl(ControllableView, Composite)}
	 * method of the ViewController you have registered this View to.
	 * 
	 * @param parent The parent to create the contents to.
	 */
	void createViewContents(Composite parent);
	
	/**
	 * Should check whether this View can be displayed. If false is returned
	 * the associated ViewController will hide or even dispose the 
	 * contents of this View and display a default Composite.
	 * 
	 * @return Whether this View can be displayed.
	 */
	boolean canDisplayView();
	
	
}
