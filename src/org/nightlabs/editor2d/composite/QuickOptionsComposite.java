/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.composite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.config.QuickOptionsConfigModule;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class QuickOptionsComposite 
extends XComposite 
{

	/**
	 * @param parent
	 * @param style
	 */
	public QuickOptionsComposite(Composite parent, int style) 
	{
		super(parent, style);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public QuickOptionsComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		init();
	}

	protected void init() 
	{
		initConfigModule();
		createComposite(this);
	}
	
	protected QuickOptionsConfigModule confMod = null;
	protected QuickOptionsConfigModule getConfigModule() 
	{
		if (confMod == null)
			initConfigModule();
		
		return confMod;
	}
	
	protected void initConfigModule() 
	{
		try {
			confMod = (QuickOptionsConfigModule) Config.sharedInstance().createConfigModule(QuickOptionsConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		} 
	}
	
	protected void createComposite(Composite parent) 
	{
		Group cloneGroup = new Group(parent, SWT.NONE);
		cloneGroup.setText(EditorPlugin.getResourceString("quickOptionsView.group.clone.name"));
		cloneGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cloneGroup.setLayout(new GridLayout());
		createEntry(cloneGroup, EditorPlugin.getResourceString("quickOptions.distanceCloneX.label"), CLONE_X);
		createEntry(cloneGroup, EditorPlugin.getResourceString("quickOptions.distanceCloneY.label"), CLONE_Y);
		
		Group moveGroup = new Group(parent, SWT.NONE);
		moveGroup.setText(EditorPlugin.getResourceString("quickOptionsView.group.move.name"));
		moveGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		moveGroup.setLayout(new GridLayout());
		createEntry(moveGroup, EditorPlugin.getResourceString("quickOptions.distanceMoveX.label"), MOVE_X);
		createEntry(moveGroup, EditorPlugin.getResourceString("quickOptions.distanceMoveY.label"), MOVE_Y);
	}
	
	protected static final int CLONE_X = 1; 
	protected static final int CLONE_Y = 2;
	protected static final int MOVE_X = 3;
	protected static final int MOVE_Y = 4;
	
	protected Map<Spinner, Integer> spinner2Identifier = new HashMap<Spinner, Integer>();
	
	protected void createEntry(Composite parent, String labelText, int identifier) 
	{
		XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(2, true);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l = new Label(comp, SWT.NONE);
		l.setText(labelText);
		Spinner spinner = new Spinner(comp, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		spinner.setSelection(getValue(identifier));		
		
		spinner.addSelectionListener(textListener);
		spinner.addFocusListener(focusListener);
		spinner.addDisposeListener(disposeListener);
		
		spinner2Identifier.put(spinner, new Integer(identifier));
	}
	
	protected SelectionListener textListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			Spinner t = (Spinner) e.getSource();
			int identifier = ((Integer) spinner2Identifier.get(t)).intValue();
			int value = t.getSelection();	
			setValue(identifier, value);
		}	
	};
		
	protected void setValue(int identifier, int value) 
	{
		switch (identifier) 
		{
			case (CLONE_X):
				getConfigModule().setCloneDistanceX(value);
				break;
			case (CLONE_Y):
				getConfigModule().setCloneDistanceY(value);
				break;
			case (MOVE_X):
				getConfigModule().setMoveTranslationX(value);
				break;
			case (MOVE_Y):
				getConfigModule().setMoveTranslationY(value);
				break;								
		}
	}
	
	protected int getValue(int identifier) 
	{
		switch (identifier) 
		{
			case (CLONE_X):
				return getConfigModule().getCloneDistanceX();
			case (CLONE_Y):
				return getConfigModule().getCloneDistanceY();
			case (MOVE_X):
				return getConfigModule().getMoveTranslationX();
			case (MOVE_Y):
				return getConfigModule().getMoveTranslationY();
		}
		return 0;
	}
	
	protected FocusListener focusListener = new FocusListener()
	{	
		public void focusLost(FocusEvent e) 
		{
//			Spinner t = (Spinner) e.getSource();
//			int identifier = ((Integer) spinner2Identifier.get(t)).intValue();
//			int value = t.getSelection();	
//			setValue(identifier, value);			
		}	
		public void focusGained(FocusEvent e) {
			
		}	
	};
	
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			Spinner t = (Spinner) e.getSource();
			t.removeSelectionListener(textListener);
			t.removeFocusListener(focusListener);
		}	
	};
	
}