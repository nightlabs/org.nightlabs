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
package org.nightlabs.editor2d.print;

import java.awt.print.PageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.editor2d.DrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintPreviewDialog 
extends CenteredDialog 
{
	/**
	 * @param parentShell
	 */
	public EditorPrintPreviewDialog(DrawComponent dc, Shell parentShell) {
		super(parentShell);
		this.dc = dc;
	}

	private DrawComponent dc = null;
	private EditorPrinterConfiguratorComposite printConfiguratorComp = null;
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		printConfiguratorComp = new EditorPrinterConfiguratorComposite(dc, parent, SWT.NONE);
		return printConfiguratorComp;
	}
	
	public PageFormat getPageFormat() {
		return printConfiguratorComp.getPageFormat();
	}
}
