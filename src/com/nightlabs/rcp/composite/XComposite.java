/*
 * Created on Sep 2, 2005
 */
package com.nightlabs.rcp.composite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class XComposite extends Composite
{
	public static final int LAYOUT_MODE_ORDINARY_WRAPPER = 0;
	public static final int LAYOUT_MODE_TIGHT_WRAPPER = 1;

	public static final int LAYOUT_DATA_MODE_NONE = 0;
	public static final int LAYOUT_DATA_MODE_GRID_DATA = 1;

	/**
	 * Calls {@link #XComposite(Composite, int, int)} with
	 * <code>layoutMode = </code>{@link #LAYOUT_MODE_ORDINARY_WRAPPER}.
	 */
	public XComposite(Composite parent, int style)
	{
		this(parent, style, LAYOUT_MODE_ORDINARY_WRAPPER);
	}

	/**
	 * Calls {@link #XComposite(Composite, int, int, int)} with
	 * <code>layoutDataMode = </code>{@link #LAYOUT_DATA_MODE_GRID_DATA}.
	 */
	public XComposite(Composite parent, int style, int layoutMode)
	{
		this(parent, style, layoutMode, LAYOUT_DATA_MODE_GRID_DATA);
	}

	/**
	 * @param parent The Composite into which this newly created one will be embedded as child.
	 * @param style A combination of the SWT style flags.
	 * @param layoutMode One of the <code>LAYOUT_MODE_*</code> constants.
	 * @param layoutDataMode One of the <code>LAYOUT_DATA_MODE_*</code> constants.
	 *
	 * @see #LAYOUT_MODE_ORDINARY_WRAPPER
	 * @see #LAYOUT_MODE_TIGHT_WRAPPER
	 *
	 * @see #LAYOUT_DATA_MODE_NONE
	 * @see #LAYOUT_DATA_MODE_GRID_DATA
	 */
	public XComposite(Composite parent, int style, int layoutMode, int layoutDataMode)
	{
		super(parent, style);

		this.setForeground(parent.getForeground());
		this.setBackground(parent.getBackground());

		switch (layoutMode) {
			case LAYOUT_MODE_ORDINARY_WRAPPER:{
				GridLayout layout = new GridLayout();
				setLayout(layout);
				break;
			}
			case LAYOUT_MODE_TIGHT_WRAPPER: {
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = 0;
				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginWidth = 0;

				layout.marginLeft = 0;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;

				setLayout(layout);
				break;
			}
			default:
				throw new IllegalArgumentException("layoutMode = " + layoutMode + " is unknown!");
		}

		switch (layoutDataMode) {
			case LAYOUT_DATA_MODE_NONE:
				// nothing
				break;
			case LAYOUT_DATA_MODE_GRID_DATA:
				GridData gridData = new GridData(GridData.FILL_BOTH);
//				gridData.minimumHeight = 1;
//				gridData.minimumWidth = 1;
				setLayoutData(gridData);
				break;
			default:
				throw new IllegalArgumentException("layoutDataMode = " + layoutDataMode + " is unknown!");
		}
	}

	private static class ChildStatus
	{
		private boolean enabled = true;

		public boolean isEnabled()
		{
			return enabled;
		}
		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}
	}

	private Map childStatusByControl = new HashMap();
	private ChildStatus getChildStatus(Control control, boolean create)
	{
		ChildStatus cs = (ChildStatus) childStatusByControl.get(control);
		if (cs == null && create) {
			cs = new ChildStatus();
			childStatusByControl.put(control, cs);
			control.addDisposeListener(childDisposeListener);
		}
		return cs;
	}

	private DisposeListener childDisposeListener = new DisposeListener() {
		public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
			childStatusByControl.remove(e.getSource());
		}
	};

	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled)
	{
		if (enabled == isEnabled())
			return;

		Control[] children = getChildren();
		for (int i = 0; i < children.length; ++i) {
			Control child = children[i];
			if (enabled) {
				ChildStatus childStatus = getChildStatus(child, false);
				if (childStatus != null) {
					child.setEnabled(childStatus.isEnabled());
				}
			} else {
				ChildStatus childStatus = getChildStatus(child, true);
				childStatus.setEnabled(child.isEnabled());
				child.setEnabled(false);
			}
		}
		super.setEnabled(enabled);
	}

	/**
	 * This is a convenience method.
	 *
	 * @return Returns the same instance as <tt>Composite.getLayout()</tt>, but you don't need to cast anymore.
	 * @see Composite#getLayout()
	 */
	public GridLayout getGridLayout()
	{
		return (GridLayout)getLayout();
	}

	/**
	 * This is a conenience method.
	 *
	 * @return Returns the same instance as <tt>Control.getLayoutData()</tt>, but you don't need to cast anymore.
	 * @see org.eclipse.swt.widgets.Control#getLayoutData()
	 */
	public GridData getGridData()
	{
		return (GridData)getLayoutData();
	}
}
