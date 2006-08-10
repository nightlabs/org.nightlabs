/**
 * 
 */
package org.nightlabs.base.print;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.print.PrinterConfigurationRegistry.ConfiguratorFactoryEntry;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditPrinterConfigurationComposite extends XComposite {

	private String printerUseCaseID;
	private PrinterUseCase printerUseCase;
	private PrinterConfiguration printerConfiguration;
	private PrinterConfiguratorFactory configuratorFactory;
	private PrinterConfigurator configurator;
	private Composite configuratorComposite;
	
	private XComposite configuratorWrapper;
	private ConfiguratorCombo configuratorCombo;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditPrinterConfigurationComposite(Composite parent, int style, XComposite.LayoutMode layoutMode, String printerUseCaseID) {
		super(parent, style, layoutMode);
		this.printerUseCaseID = printerUseCaseID;		
		printerUseCase = PrinterConfigurationRegistry.sharedInstance().getPrinterUseCase(printerUseCaseID);
		if (printerUseCase == null)
			throw new RuntimeException("The PrinterUseCase to be edited is not registered: "+printerUseCaseID);
		printerConfiguration = PrinterConfigurationCfMod.getPrinterConfiguration(printerUseCaseID);

		configuratorCombo = new ConfiguratorCombo(this, SWT.READ_ONLY);
		configuratorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		// TODO: Set default configurator for usecase
		configuratorWrapper = new XComposite(this, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		
		configuratorCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {				
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateConfigurator();
			}
		});
		
		updateConfigurator();
	}

	private void updateConfigurator() {
		ConfiguratorFactoryEntry selected = configuratorCombo.getSelectedElement();
		if (selected != null) {
			
			if (configurator != null && (configuratorComposite != null && (!configuratorComposite.isDisposed())))
				printerConfiguration = configurator.readPrinterConfiguration();
			
			configuratorFactory = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorFactory(selected.getId());
			if (configuratorFactory == null)
				throw new IllegalStateException("Could not find configuratorFactory for registered printerConfiguration ?!?");
			configurator = configuratorFactory.createPrinterConfigurator();
			configurator.init(printerConfiguration);
		}
		
		if (configuratorComposite != null && (!configuratorComposite.isDisposed()))
			configuratorComposite.dispose();

		if (selected != null) {
			configuratorComposite = configurator.showComposite(configuratorWrapper);
			configuratorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		
		this.layout(true, true);
	}
}
