/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.base.language;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.language.LanguageCf;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageTableLabelProvider
	extends LabelProvider
	implements ITableLabelProvider
{

	public LanguageTableLabelProvider()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		// TODO Here we should have the 16x16-language flag
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		String res = null;
		if (element instanceof LanguageCf) {
			LanguageCf language = (LanguageCf)element;
			if (columnIndex == 0) {
				res = language.getNativeName();
			}
		} // if (element instanceof Language) {

		if (res != null)
			return res;
		else
			return "";
	}

}
