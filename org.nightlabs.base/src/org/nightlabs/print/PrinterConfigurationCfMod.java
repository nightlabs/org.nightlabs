/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.print;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * ConfigModule to hold all existing {@link PrinterConfiguration}s.
 * Static helper methods ease accessing and setting the different configurations.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class PrinterConfigurationCfMod extends ConfigModule {

	private static final long serialVersionUID = 1L;

	private Map<String, PrinterConfiguration> printerConfigurations;
	
	/**
	 * @deprecated only used by JDO and the Config to create all needed Modules per reflection
	 * @see ConfigModule#ConfigModule()
	 */
	@Deprecated
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
	 * @param printerConfigurations the printerConfigurations to set
	 */
	public void setPrinterConfigurations(
			Map<String, PrinterConfiguration> printerConfigurations) {
		this.printerConfigurations = printerConfigurations;
	}
	
	/**
	 * Returns the {@link PrinterConfigurationCfMod} retrieved from
	 * the {@link Config}s sharedInstance.
	 * 
	 * @return The {@link PrinterConfigurationCfMod} retrieved from
	 * the {@link Config}s sharedInstance.
	 */
	public static PrinterConfigurationCfMod getPrinterConfigurationCfMod() {
		try {
			return Config.sharedInstance().createConfigModule(PrinterConfigurationCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the {@link PrinterConfiguration} for the given printerUseCaseID.
	 * Might return null if none was set yet for the given id.
	 * Uses {@link #getPrinterConfigurationCfMod()}.
	 * 
	 * @param printerUseCaseID The use case to search the configuration for.
	 * @return The corresponding configuration or null if none set.
	 */
	public static PrinterConfiguration getPrinterConfiguration(String printerUseCaseID) {
		PrinterConfigurationCfMod cfMod = getPrinterConfigurationCfMod();
		return cfMod.getPrinterConfigurations().get(printerUseCaseID);
	}
	
	/**
	 * Set the {@link PrinterConfiguration} for the given use case.
	 * After the value is set to the CfMod returned by {@link #getPrinterConfigurationCfMod()}
	 * the {@link Config} will be asked to save this module.
	 * 
	 * @param printerUseCaseID The printer use case to set the configuration for.
	 * @param printerConfiguration The configuration to set.
	 */
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
