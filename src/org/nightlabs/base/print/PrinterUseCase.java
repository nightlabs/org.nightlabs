/**
 * 
 */
package org.nightlabs.base.print;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterUseCase {

	public static final String DEFAULT_USE_CASE = "PrinterUseCase-Default";
	
	private String id;
	private String name;
	private String description;
	private String defaultConfiguratorID;
	private PrinterConfiguratorFactory defaultConfiguratorFactory;
	
	/**
	 * 
	 */
	public PrinterUseCase() {}

	/**
	 * @return the defaultConfiguratorFactory
	 */
	public PrinterConfiguratorFactory getDefaultConfiguratorFactory() {
		return defaultConfiguratorFactory;
	}

	/**
	 * @param defaultConfiguratorFactory the defaultConfiguratorFactory to set
	 */
	public void setDefaultConfiguratorFactory(
			PrinterConfiguratorFactory defaultConfiguratorFactory) {
		this.defaultConfiguratorFactory = defaultConfiguratorFactory;
	}

	/**
	 * @param defaultConfiguratorFactory the defaultConfiguratorFactory to set
	 */
	public void setDefaultConfiguratorID(
			String defaultConfiguratorID) {
		this.defaultConfiguratorID = defaultConfiguratorID;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	void validate() {
		this.defaultConfiguratorFactory = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorFactory(defaultConfiguratorID);
	}

}
