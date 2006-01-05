/**
 * 
 */
package org.nightlabs.base.table;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.resource.SharedImages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class CheckboxCellEditorHelper {

	/**
	 * 
	 */
	public CheckboxCellEditorHelper() {
		super();
	}

	/**
	 * To be used by LabelProviders that whant to display a checked/unchecked icon for
	 * the CheckboxCellEditor that does not have a Control.
	 * 
	 * @param cellModifier The ICellModifier for the CellEditor to provide the value
	 * @param element The current element
	 * @param property The property the cellModifier should return the value from 
	 */
	public static Image getCellEditorImage(ICellModifier cellModifier, Object element, String property) {
		Boolean value = (Boolean)cellModifier.getValue(element, property);
		if (value)
			return SharedImages.getSharedImage(NLBasePlugin.getDefault(), CheckboxCellEditorHelper.class, "checked");
		else
			return SharedImages.getSharedImage(NLBasePlugin.getDefault(), CheckboxCellEditorHelper.class, "unchecked");
	}

}
