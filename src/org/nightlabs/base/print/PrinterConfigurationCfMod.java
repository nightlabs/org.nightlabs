/**
 * 
 */
package org.nightlabs.base.print;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfigurationCfMod extends ConfigModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, PrinterConfiguration> printerConfigurations;
	
	/**
	 * 
	 */
	public PrinterConfigurationCfMod() {
	}
	
	@Override
	public void init() throws InitException {
		super.init();
		if (printerConfigurations == null)
			printerConfigurations = new HashMap<String, PrinterConfiguration>();
	}

	/**
	 * @return the printerConfigurations
	 */
	public Map<String, PrinterConfiguration> getPrinterConfigurations() {
		return printerConfigurations;
	}

	/**
	 * @param printerConfigurationAWTs the printerConfigurations to set
	 */
	public void setPrinterConfigurations(
			Map<String, PrinterConfiguration> printerConfigurations) {
		this.printerConfigurations = printerConfigurations;
	}
	
	public static PrinterConfigurationCfMod getPrinterConfigurationCfMod() {
		try {
			return (PrinterConfigurationCfMod)Config.sharedInstance().createConfigModule(PrinterConfigurationCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	public static PrinterConfiguration getPrinterConfiguration(String printerUseCaseID) {
		PrinterConfigurationCfMod cfMod = getPrinterConfigurationCfMod();
		return cfMod.getPrinterConfigurations().get(printerUseCaseID);
	}
	
	public static void setPrinterConfiguration(String printerUseCaseID, PrinterConfiguration printerConfiguration) {
		PrinterConfigurationCfMod cfMod = getPrinterConfigurationCfMod();
		cfMod.getPrinterConfigurations().put(printerUseCaseID, printerConfiguration);
		cfMod.setChanged();
		try {
			Config.sharedInstance().save();
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
}
