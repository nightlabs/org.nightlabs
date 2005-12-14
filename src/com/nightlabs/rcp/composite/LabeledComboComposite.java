/*
 * Created 	on Mar 9, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;import org.eclipse.swt.widgets.Label;
;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LabeledComboComposite extends XComposite {

	private Combo combo;
	private Label label;

	public LabeledComboComposite(Composite parent, int style, boolean setLayoutData) {
		super(parent, style,
				XComposite.LAYOUT_MODE_TIGHT_WRAPPER,
				setLayoutData ? XComposite.LAYOUT_DATA_MODE_GRID_DATA : LAYOUT_DATA_MODE_NONE);
		label = new Label(this, SWT.NONE);
		combo = new Combo(this, SWT.NONE);
	}

	public Combo getCombo() {
		return combo;
	}

	public Label getLabel() {
		return label;
	}

}
