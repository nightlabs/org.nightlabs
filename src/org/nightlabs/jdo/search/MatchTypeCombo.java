/* *****************************************************************************
 * org.nightlabs.jdo - NightLabs Eclipse utilities for JDO                     *
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

package org.nightlabs.jdo.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class MatchTypeCombo extends XComposite {

	public static final int[] KNOWN_MATCH_TYPES = new int[] {
		SearchFilterItem.MATCHTYPE_CONTAINS,
		SearchFilterItem.MATCHTYPE_NOTCONTAINS,
		SearchFilterItem.MATCHTYPE_BEGINSWITH,
		SearchFilterItem.MATCHTYPE_ENDSWITH,
		SearchFilterItem.MATCHTYPE_EQUALS,
		SearchFilterItem.MATCHTYPE_NOTEQUALS,
		SearchFilterItem.MATCHTYPE_GREATER_THAN,
		SearchFilterItem.MATCHTYPE_LESS_THAN,
		SearchFilterItem.MATCHTYPE_CONTAINED
	};
	
	private Combo combo;
	
	private List displayingTypes = new ArrayList();
	
	/**
	 * @param parent
	 * @param wrapperStyle
	 * @param comboStyle
	 */
	public MatchTypeCombo(Composite parent, int wrapperStyle, int comboStyle) {
		super(parent, wrapperStyle, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		combo = new Combo(this, comboStyle);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (int i = 0; i < MatchTypeCombo.KNOWN_MATCH_TYPES.length; i++) {
			if (supportsMatchType(MatchTypeCombo.KNOWN_MATCH_TYPES[i])) {
				combo.add(getMatchTypeDescription(MatchTypeCombo.KNOWN_MATCH_TYPES[i]));
				displayingTypes.add(new Integer(MatchTypeCombo.KNOWN_MATCH_TYPES[i]));
			}			
		}
		if (combo.getItemCount() > 0)
			combo.select(0);
	}

	public abstract boolean supportsMatchType(int matchType);
	
	public abstract String getMatchTypeDescription(int matchType);
	
	public int getSelectedMatchType() {
		if (combo.getSelectionIndex() >= 0)
			return ((Integer)displayingTypes.get(combo.getSelectionIndex())).intValue();
		return -1;
	}
	
	public void setSelectedMatchType(int matchType) {
		for (int i = 0; i < displayingTypes.size(); i++) {
			Integer type = (Integer) displayingTypes.get(i);
			if (type.intValue() == matchType)
				combo.select(i);
		}
	}
	
	public Combo getCombo() {
		return combo;
	}

}
