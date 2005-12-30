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

package org.nightlabs.base.exceptionhandler.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.dialog.ExpandableAreaDialog;

/**
 * @author Alexander Bieber
 * @deprecated is this still in use?? see org.nightlabs.base.exceptionhandler.DefaultErrorDialog
 */
public class ThrowableHandlerDialog extends ExpandableAreaDialog {

	private Throwable error = null;
	public ThrowableHandlerDialog(Shell parent, Throwable error) {
		super(parent, NLBasePlugin.getResourceString("extensionpoint.throwable.dialog.title"), NLBasePlugin.getResourceString("extensionpoint.throwable.dialog.expandText"));
		this.error = error;
	}
	
	
	protected Composite createStaticArea(Composite parent) {
		ThrowableHandlerMessageArea messageArea = new ThrowableHandlerMessageArea(parent,SWT.NONE);
		messageArea.setThrowable(error);
		return messageArea;
	}
	
	
	protected Composite createExpandableArea(Composite parent) {
		ThrowableHandlerDetailArea detailArea = new ThrowableHandlerDetailArea(parent,SWT.NONE);
		detailArea.setThrowable(error);
		return detailArea;
	}
}
