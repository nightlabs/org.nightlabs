/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.dialog;

import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PrintModeDialog 
extends Dialog 
{

	private Button tile, fitPage, fitWidth, fitHeight;

	public PrintModeDialog(Shell shell) {
		super(shell);
	}

	protected void cancelPressed() {
		setReturnCode(-1);
		close();
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("PrintDialog");
		super.configureShell(newShell);
	}

	protected Control createDialogArea(Composite parent) 
	{
		Composite composite = (Composite)super.createDialogArea(parent);
		
		tile = new Button(composite, SWT.RADIO);
		tile.setText("Tile");
		tile.setSelection(true);
		
		fitPage = new Button(composite, SWT.RADIO);
		fitPage.setText("Fit Page");

		fitWidth = new Button(composite, SWT.RADIO);
		fitWidth.setText("Fit Width");

		fitHeight = new Button(composite, SWT.RADIO);
		fitHeight.setText("Fit Height");

		return composite;
	}

	protected void okPressed() {
		int returnCode = -1;
		if (tile.getSelection())
			returnCode = PrintFigureOperation.TILE;
		else if (fitPage.getSelection())
			returnCode = PrintFigureOperation.FIT_PAGE;
		else if (fitHeight.getSelection())
			returnCode = PrintFigureOperation.FIT_HEIGHT;
		else if (fitWidth.getSelection())
			returnCode = PrintFigureOperation.FIT_WIDTH;
		setReturnCode(returnCode);
		close();
	}
}
