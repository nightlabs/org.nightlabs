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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class XComposite extends Composite
{
	private ChildStatusController childStatusController = new ChildStatusController();

	public static enum LayoutMode {
		ORDINARY_WRAPPER, TIGHT_WRAPPER, TOP_BOTTOM_WRAPPER, LEFT_RIGHT_WRAPPER 
	}

	public static enum LayoutDataMode {
		NONE, GRID_DATA, GRID_DATA_HORIZONTAL
	}

	/**
	 * returns a GridLayout for the given layoutMode
	 * @return a GridLayout for the given layoutMode
	 */
	public static GridLayout getLayout(LayoutMode layoutMode)
	{
		return getLayout(layoutMode, null);
	}
	
	/**
	 * modifies a GridLayout to the appropriate {@link LayoutMode}
	 * 
	 * @param layoutMode the layoutMode to set
	 * @param layout the GridLayout to modify
	 * @return the modified GridLayout
	 */
	public static GridLayout getLayout(LayoutMode layoutMode, GridLayout layout)
	{
		if (layout == null)
			layout = new GridLayout();
		switch (layoutMode) 
		{
			case ORDINARY_WRAPPER:
				return layout;
			case TIGHT_WRAPPER:
				layout.horizontalSpacing = 0;
				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;
				return layout;
			case TOP_BOTTOM_WRAPPER:
				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;
				return layout;
			case LEFT_RIGHT_WRAPPER:
				layout.horizontalSpacing = 0;
				layout.marginWidth = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				return layout;				
			default:
				throw new IllegalArgumentException("layoutMode = " + layoutMode + " is unknown!");
		}		
	}	
	
	public static void setLayoutDataMode(LayoutDataMode layoutDataMode, Control c) 
	{
		switch (layoutDataMode) 
		{
			case NONE:
				// nothing
				break;
			case GRID_DATA:
				GridData gridData = new GridData(GridData.FILL_BOTH);
				c.setLayoutData(gridData);
				break;
			case GRID_DATA_HORIZONTAL:
				GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
				c.setLayoutData(gridData2);
				break;				
			default:
				throw new IllegalArgumentException("layoutDataMode = " + layoutDataMode + " is unknown!");
		}	 
	}
	
	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode)} with
	 * <code>layoutMode = </code> {@link LayoutMode#ORDINARY_WRAPPER}
	 */
	public XComposite(Composite parent, int style)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER);
	}
	
//	/**
//	 * Calls {@link #XComposite(Composite, int, int)} with
//	 * <code>layoutMode = </code>{@link #LAYOUT_MODE_ORDINARY_WRAPPER}.
//	 */
//	public XComposite(Composite parent, int style)
//	{
//		this(parent, style, LAYOUT_MODE_ORDINARY_WRAPPER);
//	}
//
//	/**
//	 * Calls {@link #XComposite(Composite, int, int, int)} with
//	 * <code>layoutDataMode = </code>{@link #LAYOUT_DATA_MODE_GRID_DATA}.
//	 */
//	public XComposite(Composite parent, int style, int layoutMode)
//	{
//		this(parent, style, layoutMode, LAYOUT_DATA_MODE_GRID_DATA);
//	}
//
//	/**
//	 * @param parent The Composite into which this newly created one will be embedded as child.
//	 * @param style A combination of the SWT style flags.
//	 * @param layoutMode One of the <code>LAYOUT_MODE_*</code> constants.
//	 * @param layoutDataMode One of the <code>LAYOUT_DATA_MODE_*</code> constants.
//	 *
//	 * @see #LAYOUT_MODE_ORDINARY_WRAPPER
//	 * @see #LAYOUT_MODE_TIGHT_WRAPPER
//	 *
//	 * @see #LAYOUT_DATA_MODE_NONE
//	 * @see #LAYOUT_DATA_MODE_GRID_DATA
//	 */
//	public XComposite(Composite parent, int style, int layoutMode, int layoutDataMode)
//	{
//		this(parent, style, int2LayoutMode(layoutMode), int2LayoutDataMode(layoutDataMode));	
//	}

	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode, LayoutDataMode)}
	 * with <code>layoutDataMode = </code> {@link LayoutDataMode#GRID_DATA}
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode)
	{
		this(parent, style, layoutMode, LayoutDataMode.GRID_DATA);
	}

	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode, LayoutDataMode)}
	 * with <code>layoutMode = </code> {@link LayoutMode#ORDINARY_WRAPPER}
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutDataMode the LayoutDataMode to set
	 *
	 * @see LayoutMode
	 * @see LayoutDataMode 
	 */
	public XComposite(Composite parent, int style, LayoutDataMode layoutDataMode)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER, layoutDataMode);
	}

	/**
	 * creates a Composite with the appropriate layoutMode and layoutDataMode
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the LayoutDataMode to set
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode  
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style);

		this.setForeground(parent.getForeground());
		this.setBackground(parent.getBackground());

		setLayout(getLayout(layoutMode));
		setLayoutDataMode(layoutDataMode, this);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled)
	{
		if (enabled == isEnabled())
			return;
		childStatusController.setEnabled(this, enabled);
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
