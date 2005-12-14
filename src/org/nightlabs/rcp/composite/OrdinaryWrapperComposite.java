/*
 * Created 	on Jun 1, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.composite;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


/**
 * A Composite for wrapping other composites but with
 * insets. Use this as your default base Composite when
 * you do not have to do tight wrapping.
 *
 * @see org.nightlabs.rcp.composite.TightWrapperComposite
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @deprecated Use {@link org.nightlabs.rcp.composite.XComposite} directly!
 */
public class OrdinaryWrapperComposite extends XComposite {
	/**
	 * This constructor calls {@link #OrdinaryWrapperComposite(Composite, int, boolean)}
	 * with <code>setLayoutData = true</code>.
	 */
	public OrdinaryWrapperComposite(Composite parent, int style) {
		this(parent, style, true);
	}

	/**
	 * @param parent The Composite into which this newly created one will be embedded as child.
	 * @param style A combination of the SWT style flags.
	 * @param setLayoutData If <code>true</code>, a {@link GridData} will be created and assigned. 
	 */
	public OrdinaryWrapperComposite(Composite parent, int style, boolean setLayoutData) {
		super(
				parent, style,
				LAYOUT_MODE_ORDINARY_WRAPPER,
				setLayoutData ? LAYOUT_DATA_MODE_GRID_DATA : LAYOUT_DATA_MODE_NONE);
	}

}
