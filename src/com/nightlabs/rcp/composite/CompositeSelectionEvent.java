/*
 * Created 	on Jan 24, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.composite;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class CompositeSelectionEvent {
	private boolean selected;
	private SelectableComposite source;
	private int stateMask;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public SelectableComposite getSource() {
		return source;
	}
	public void setSource(SelectableComposite source) {
		this.source = source;
	}	
	public int getStateMask() {
		return stateMask;
	}
	public void setStateMask(int stateMask) {
		this.stateMask = stateMask;
	}
}
