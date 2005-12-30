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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;

/**
 * ControllableViews are used together with {@link org.nightlabs.base.view.ViewController}s.
 * When implementing a ControllableView register it (best in its constructor)
 * to a ViewController. 
 * Delegate the {@link org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)}
 * method to this Controller by calling its method {@link org.nightlabs.base.view.ViewController#createViewControl(ControllableView, Composite)}.
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
