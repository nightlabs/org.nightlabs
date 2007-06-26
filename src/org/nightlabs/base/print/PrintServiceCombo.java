/**
 * 
 */
package org.nightlabs.base.print;

import java.util.ArrayList;

import javax.print.PrintService;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.CComboComposite;
import org.nightlabs.base.table.TableLabelProvider;
import org.nightlabs.print.PrintUtil;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrintServiceCombo extends CComboComposite<PrintService> {

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object arg0, int arg1) {
			return ((PrintService)arg0).getName();
		}
	}
	
	/**
	 * @param types
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	public PrintServiceCombo(Composite parent, int style) {
		super(
				new ArrayList<PrintService>(PrintUtil.lookupPrintServices()), 
				new LabelProvider(),
				parent, 
				style | SWT.READ_ONLY,
				(String) null
			);
	}	
	
}
