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

package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;import org.eclipse.swt.widgets.Label;
;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @deprecated use {@link CComboComposite} instead and set a text.
 */
public class LabeledComboComposite extends XComposite {

	private Combo combo;
	private Label label;

	public LabeledComboComposite(Composite parent, int style, boolean setLayoutData) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER,
				setLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE);				
		label = new Label(this, SWT.NONE);
		combo = new Combo(this, style);
	}

	public Combo getCombo() {
		return combo;
	}

	public Label getLabel() {
		return label;
	}

}
