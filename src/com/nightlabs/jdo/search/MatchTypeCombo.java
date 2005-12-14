/*
 * Created 	on Sep 6, 2005
 * 					by alex
 *
 */
package com.nightlabs.jdo.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.nightlabs.rcp.composite.XComposite;

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
		super(parent, wrapperStyle, XComposite.LAYOUT_MODE_TIGHT_WRAPPER, XComposite.LAYOUT_DATA_MODE_NONE);
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
