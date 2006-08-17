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

package org.nightlabs.base.print;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * Dialog to edit the {@link PrinterConfiguration} of a certain
 * {@link PrinterUseCase}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditPrinterConfigurationDialog extends CenteredDialog {

	private EditPrinterConfigurationComposite configurationComposite;
	private String printerUseCaseID;
	
	/**
	 * @param parentShell
	 */
	public EditPrinterConfigurationDialog(Shell parentShell, String printerUseCaseID) {
		super(parentShell);
		this.printerUseCaseID = printerUseCaseID;
		setShellStyle(getShellStyle() | SWT.RESIZE);				
	}
	
	protected void configureShell(Shell newShell) 
	{
		super.configureShell(newShell);		
		newShell.setText(NLBasePlugin.getResourceString("dialog.printerConfiguration.title"));
		newShell.setSize(500, 500);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		configurationComposite = new EditPrinterConfigurationComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER, printerUseCaseID);
		configurationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return configurationComposite;
	}
	
	@Override
	protected void okPressed() {
		PrinterConfiguration configuration = configurationComposite.getCurrentPrinterConfiguration();
		if (configuration != null)
			PrinterConfigurationCfMod.setPrinterConfiguration(printerUseCaseID, configuration);
		super.okPressed();
	}
	
	/**
	 * Opens a new {@link EditPrinterConfigurationDialog} for the given
	 * printerUseCaseID.
	 * 
	 * @param printerUseCaseID The use case the configuration should be edited for.
	 * @return The dialogs result.
	 */
	public static int openDialog(String printerUseCaseID) {
		EditPrinterConfigurationDialog dlg = new EditPrinterConfigurationDialog(
				RCPUtil.getActiveWorkbenchShell(),
				printerUseCaseID
			);
		return dlg.open();
	}
	
}
