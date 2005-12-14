/*
 * Created 	on Jun 1, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.composite;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import org.nightlabs.base.NLBasePlugin;

/**
 * An ExpandableComposite with an ExpansionListener that
 * re-layouts the parent composite on expansion state 
 * changes. ExpandableWrapperComposite stores its expansion
 * state to the Eclipse PreferenceStore.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ExpandableWrapperComposite extends ExpandableComposite {

	private static final String EXPANDED_SUFFIX = ".expanded";
	
	private IExpansionListener expansionListener = new IExpansionListener(){
		public void expansionStateChanging(ExpansionEvent e) {
		}
		public void expansionStateChanged(ExpansionEvent e) {
			getParent().layout(true);
			getParent().redraw();
		}
	};
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ExpandableWrapperComposite(Composite parent, int style) {
		this(parent, style, ExpandableComposite.TWISTIE);
		setExpanded(restoreExpansionState());
	}

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param expansionStyle
	 */
	public ExpandableWrapperComposite(Composite parent, int style, int expansionStyle) {
		super(parent, style, expansionStyle);
		setBackground(parent.getBackground());
//	setForeground(parent.getForeground());
		addExpansionListener();
		setExpanded(restoreExpansionState());
	}
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param expansionStyle
	 */
	public ExpandableWrapperComposite(Composite parent, int style, int expansionStyle, String identifier) {
		super(parent, style, expansionStyle);
		setBackground(parent.getBackground());
//	setForeground(parent.getForeground());
		addExpansionListener();
		this.identifier = identifier;
		setExpanded(restoreExpansionState());
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				storeExpansionState();
			}
		});
	}
	
	public void addExpansionListener() {
		addExpansionListener(expansionListener);
	}

	public void removeExpansionListener() {
		removeExpansionListener(expansionListener);
	}
	
	private String identifier;
	private boolean restoreExpansionState() {
		if (identifier == null)
			return false;
		return NLBasePlugin.getDefault().getPreferenceStore().getBoolean(identifier+EXPANDED_SUFFIX);
	}
	
	private void storeExpansionState() {
		if (identifier == null)
			return;
		NLBasePlugin.getDefault().getPreferenceStore().setValue(identifier+EXPANDED_SUFFIX, isExpanded());
	}
}
