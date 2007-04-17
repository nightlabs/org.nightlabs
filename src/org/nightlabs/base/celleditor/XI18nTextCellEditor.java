/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.base.celleditor;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.language.I18nTextEditor;
import org.nightlabs.i18n.I18nText;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class XI18nTextCellEditor 
extends XCellEditor 
{

	public XI18nTextCellEditor() {
		super();
	}

	/**
	 * @param parent
	 */
	public XI18nTextCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public XI18nTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param readOnly
	 */
	public XI18nTextCellEditor(Composite parent, int style, boolean readOnly) {
		super(parent, style, readOnly);
	}

	private I18nTextEditor i18nTextEditor;
	@Override
	protected Control createControl(Composite parent) {
		i18nTextEditor = new I18nTextEditor(parent);
		GridLayout layout = new GridLayout(2, false);
		layout = XComposite.getLayout(LayoutMode.TIGHT_WRAPPER, layout);
		i18nTextEditor.setLayout(layout);
		return i18nTextEditor;
	}

	@Override
	protected Object doGetValue() {
		return i18nTextEditor.getI18nText();
	}

	@Override
	protected void doSetFocus() {
		i18nTextEditor.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof I18nText)
			i18nTextEditor.setI18nText((I18nText)value);
	}

}
