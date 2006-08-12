/**
 * 
 */
package org.nightlabs.base.print;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.ComboComposite;
import org.nightlabs.base.print.PrinterConfigurationRegistry.ConfiguratorFactoryEntry;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * Combo with a list of all Registered {@link PrinterConfiguratorFactory}.
 * As registrations are held as {@link ConfiguratorFactoryEntry}s this is
 * the actual type of entries in this combo.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ConfiguratorCombo extends ComboComposite<ConfiguratorFactoryEntry> {

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object arg0, int arg1) {
			return ((ConfiguratorFactoryEntry)arg0).getName();
		}
	}
	
	/**
	 * @param types
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	public ConfiguratorCombo(Composite parent, int style) {
		super(
				new ArrayList<ConfiguratorFactoryEntry>(PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorEntries()), 
				new LabelProvider(),
				parent, 
				style
			);
	}

}
