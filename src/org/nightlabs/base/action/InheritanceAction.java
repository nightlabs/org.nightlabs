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
package org.nightlabs.base.action;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.InheritanceToggleButton;
import org.nightlabs.base.resource.SharedImages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class InheritanceAction 
extends Action 
{
	public static final String ID = InheritanceAction.class.getName();
	
	public InheritanceAction() {
		super();
		setId(ID);
		setToolTipText("Inherit");
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				NLBasePlugin.getDefault(), InheritanceToggleButton.class));		
	}

	/**
	 * @param text
	 * @param style
	 */
	public InheritanceAction(String text, int style) {
		super(text, style);
	}

	private boolean selection = false;

	/**
	 * @return the selection
	 */
	public boolean isSelection() {
		return selection;
	}

	/**
	 * @param selection the selection to set
	 */
	public void setSelection(boolean selection) {
		this.selection = selection;
		if (selection)
			setImageDescriptor(SharedImages.getSharedImageDescriptor(NLBasePlugin.getDefault(), InheritanceToggleButton.class));
		else
			setImageDescriptor(SharedImages.getSharedImageDescriptor(NLBasePlugin.getDefault(), InheritanceToggleButton.class, "Unlink"));		
	}
	
}
