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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * Configuration module for the {@link DelegatingDocumentPrinter} by
 * which the default behaviour (Java API AUTOSENSE) can be overwritten
 * for specific file types by their extension.
 *
 * The configuration is initialized using a properties
 * "documentprinterdelegates.properties" file org.nightlabs.print package.
 * See the documentation in the sample "documentprinterdelegates.properties"
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DelegatingDocumentPrinterCfMod extends ConfigModule {

//	private static final Logger logger = Logger.getLogger(DelegatingDocumentPrinter.class);

	/**
	 * Used to configure an invocation of an external Java print
	 * engine to print documents.
	 * <b>className</b> is used to instantiate the engine that
	 * should implement {@link DocumentPrinter}.
	 */
	public static class ExternalEngineDelegateConfig implements DocumentPrinterDelegateConfig {
		private String className;

		/**
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @param className the className to set
		 */
		public void setClassName(String className) {
			this.className = className;
		}

	}

	/**
	 * Used to configure a system call for printing.
	 * <b>commandPattern</b> is split on spaces and will
	 * be separate argumets for {@link Runtime} to exec a process.
	 * <b>parameterPatter</b> will ge a single argument following
	 * the commandPattern ones. The Printer allows the usage
	 * of variables in the form ${VARNAME} that will be replaced
	 * appropiately. See var name constants in {@link DelegatingDocumentPrinter}.
	 */
	public static class SystemCallDelegateConfig implements DocumentPrinterDelegateConfig {
		private String commandPattern;
		private String parameterPattern;
		private int expectedReturnValue = 0;

		/**
		 * @return the commandPattern
		 */
		public String getCommandPattern() {
			return commandPattern;
		}
		/**
		 * @param commandPattern the commandPattern to set
		 */
		public void setCommandPattern(String commandPattern) {
			this.commandPattern = commandPattern;
		}
		/**
		 * @return the parameterPattern
		 */
		public String getParameterPattern() {
			return parameterPattern;
		}
		/**
		 * @param parameterPattern the parameterPattern to set
		 */
		public void setParameterPattern(String parameterPattern) {
			this.parameterPattern = parameterPattern;
		}
		/**
		 * @return the expectedReturnValue
		 */
		public int getExpectedReturnValue() {
			return expectedReturnValue;
		}
		/**
		 * @param expectedReturnValue the expectedReturnValue to set
		 */
		public void setExpectedReturnValue(int expectedReturnValue) {
			this.expectedReturnValue = expectedReturnValue;
		}
	}
	private static final long serialVersionUID = 1L;


	private Map<String, DocumentPrinterDelegateConfig> printConfigs;

	private Set<String> knownExtensions;

	@Override
	public void init() throws InitException {
		super.init();
		if (printConfigs == null) {
			printConfigs = new HashMap<String, DocumentPrinterDelegateConfig>();
		}
		if (knownExtensions == null) {
			knownExtensions = new HashSet<String>();
		}
	}

	public Set<String> getKnownExtensions() {
	    return knownExtensions;
    }

	public void addKnownExtension(String fileExt) {
		knownExtensions.add(fileExt);
		setKnownExtensions(knownExtensions);
	}

	public void setKnownExtensions(Set<String> knownExtensions) {
	    this.knownExtensions = knownExtensions;
	    setChanged();
    }

//	private void initFromProperties() {
//		if (this.getClass().getResource("documentprinterdelegates.properties") == null) {
//			logger.warn("Could not init initialize SystemCallPrinterCfMod from Properties as no resource documentprinterdelegates.properties could be found in the package org.nightlabs.print");
//		}
//		Properties props = new Properties();
//		try {
//			props.load(this.getClass().getResourceAsStream("documentprinterdelegates.properties"));
//		} catch (IOException e) {
//			logger.error("Could not initialize SystemCallPrinterCfMod from Properties. Loading properties failed", e);
//			printConfigs.clear();
//		}
//		Map<String, String> fileExts = new HashMap<String, String>();
//		for (Entry<Object, Object> entry : props.entrySet()) {
//			if (entry.getKey() instanceof String) {
//				String key = (String)entry.getKey();
//				if (key.indexOf('.') < 0)
//					fileExts.put(key, (String)entry.getValue());
//			}
//		}
//		for (Entry<String, String> configEntry : fileExts.entrySet()) {
//			String type = configEntry.getValue();
//			if (type.equalsIgnoreCase("systemcall")) {
//				String commandPattern = props.getProperty(configEntry+".commandPattern");
//				String parameterPattern = props.getProperty(configEntry+".parameterPattern");
//				SystemCallDelegateConfig config = new SystemCallDelegateConfig();
//				config.setCommandPattern((commandPattern != null) ? commandPattern : "");
//				config.setParameterPattern((parameterPattern != null) ? parameterPattern : "");
//				printConfigs.put(configEntry.getKey(), config);
//			}
//			else if (type.equalsIgnoreCase("externalengine")) {
//				String className = props.getProperty(configEntry+".className");
//				ExternalEngineDelegateConfig config = new ExternalEngineDelegateConfig();
//				config.setClassName((className != null) ? className : "");
//				printConfigs.put(configEntry.getKey(), config);
//			}
//		}
//	}

	public Map<String, DocumentPrinterDelegateConfig> getPrintConfigs() {
		return printConfigs;
	}

	public DocumentPrinterDelegateConfig getPrintConfig(String fileExt) {
		return getPrintConfigs().get(fileExt);
	}

	public void setPrintConfig(String fileExt, DocumentPrinterDelegateConfig printConfig) {
		printConfigs.put(fileExt, printConfig);
		setChanged();
	}

	public void setPrintConfigs(Map<String, DocumentPrinterDelegateConfig> printConfigs) {
		this.printConfigs = printConfigs;
		setChanged();
	}

	/**
	 * Returns the ConfigModule using the {@link Config}s sharedInstance.
	 * If the sharedInstance of {@link Config} was not created yet, <code>null</code>
	 * will be returned.
	 *
	 * @return The ConfigModule using the {@link Config}s sharedInstance or null if the {@link Config} was not created yet.
	 * @throws RuntimeException if something fails.
	 */
	public static DelegatingDocumentPrinterCfMod sharedInstance() {
		if (!Config.isSharedInstanceExisting()) {
			return null;
//			DelegatingDocumentPrinterCfMod cfMod = new DelegatingDocumentPrinterCfMod();
//			try {
//				cfMod.init();
//			} catch (InitException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return cfMod;
		}
		try {
			return Config.sharedInstance().createConfigModule(DelegatingDocumentPrinterCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
}
