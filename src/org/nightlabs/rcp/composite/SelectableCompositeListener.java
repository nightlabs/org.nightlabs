package org.nightlabs.rcp.composite;

/**
 * Listener interface to react on selection state changes
 * of SelecableComposites.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface SelectableCompositeListener {
	
	/**
	 * Will be called when the selection state of a
	 * SelectableComposite changes.
	 *  
	 * @param evt
	 */
	public void selectionStateChanged(CompositeSelectionEvent evt);
}
