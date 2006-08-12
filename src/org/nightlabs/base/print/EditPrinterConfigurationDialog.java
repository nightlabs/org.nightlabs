/**
 * 
 */
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
		newShell.setSize(350, 325);
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
