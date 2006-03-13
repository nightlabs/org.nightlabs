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

package org.nightlabs.base.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class CenteredDialog 
extends Dialog 
{

	/**
	 * @param parentShell
	 */
	public CenteredDialog(Shell parentShell) {
		super(parentShell);	
	}

	/**
	 * @param parentShell
	 */
	public CenteredDialog(IShellProvider parentShell) {
		super(parentShell);	
	}

	public void create() 
	{
		super.create();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point shellSize = getShell().getSize();
		int diffWidth = screenSize.width - shellSize.x;
		int diffHeight = screenSize.height - shellSize.y;
		getShell().setLocation(diffWidth/2, diffHeight/2);
	}	
}
