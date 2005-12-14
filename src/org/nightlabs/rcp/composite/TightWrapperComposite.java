/*
 * Created 	on Nov 30, 2004
 * 					by alex
 *
 */
package org.nightlabs.rcp.composite;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite for wrapping other composites and no insets.
 * 
 * @see org.nightlabs.rcp.composite.OrdinaryWrapperComposite
 * @see TightWrapperComposite#TightWrapperComposite(Composite, int, boolean)
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @deprecated Use {@link org.nightlabs.rcp.composite.XComposite} directly!
 */
public class TightWrapperComposite extends XComposite
{
	/**
	 * This constructor calls {@link #TightWrapperComposite(Composite, int, boolean)}
	 * with <code>setLayoutData = true</code>.
	 */
	public TightWrapperComposite(Composite parent, int style) {
		this(parent, style, true);
	}

	/**
	 * Creates a new Composite with a {@link GridLayout} which wastes a minimum of space.
	 * If <code>setLayoutData == true</code>, the newly created Composite
	 * will fit itself into another {@link GridLayout} by setting its layoutData.
	 *
	 * @param parent The Composite into which this newly created one will be embedded as child.
	 * @param style A combination of the SWT style flags.
	 * @param setLayoutData If <code>true</code>, a {@link GridData} will be created and assigned. 
	 */
	public TightWrapperComposite(Composite parent, int style, boolean setLayoutData) {
		super(
				parent, style,
				LAYOUT_MODE_TIGHT_WRAPPER,
				setLayoutData ? LAYOUT_DATA_MODE_GRID_DATA : LAYOUT_DATA_MODE_NONE);
	}

}
