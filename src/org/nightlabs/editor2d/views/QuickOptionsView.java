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
package org.nightlabs.editor2d.views;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.config.QuickOptionsConfigModule;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class QuickOptionsView 
extends ViewPart 
{
	public static final String ID = QuickOptionsView.class.getName(); 
	
	public QuickOptionsView() 
	{
		super();
	}

	protected void init() 
	{
		setPartName(EditorPlugin.getResourceString("quickOptionsView.name"));
		initConfigModule();
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
	
	private XFormToolkit toolkit = null;
	private ScrolledForm form = null;	
	
	public void createPartControl(Composite parent) 
	{
		toolkit = new XFormToolkit(parent.getDisplay());
//		toolkit.setCurrentMode(TOOLKIT_MODE.COMPOSITE);
		
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);		
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
//		form.setText(EditorPlugin.getResourceString("quickOptionsView.name"));		
		
		Section sectionDuplicate = toolkit.createSection(form.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.EXPANDED);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		sectionDuplicate.setLayoutData(td);		
		
		sectionDuplicate.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
		  }
		});		
		sectionDuplicate.setText(EditorPlugin.getResourceString("quickOptions.section.clone.text"));
		sectionDuplicate.setDescription(EditorPlugin.getResourceString("quickOptions.section.clone.description"));
		Composite sectionClientDuplicate = toolkit.createComposite(sectionDuplicate);	
		sectionClientDuplicate.setLayout(new GridLayout(2, false));				
		createEntry(sectionClientDuplicate, EditorPlugin.getResourceString("quickOptions.distanceCloneX.label"), CLONE_X);
		createEntry(sectionClientDuplicate, EditorPlugin.getResourceString("quickOptions.distanceCloneY.label"), CLONE_Y);
		sectionDuplicate.setClient(sectionClientDuplicate);
		
		Section sectionMove = toolkit.createSection(form.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.EXPANDED);
		TableWrapData tdMove = new TableWrapData(TableWrapData.FILL_GRAB);
		sectionMove.setLayoutData(tdMove);
		
		sectionMove.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
		  }
		});		
		sectionMove.setText(EditorPlugin.getResourceString("quickOptions.section.move.text"));
		sectionMove.setDescription(EditorPlugin.getResourceString("quickOptions.section.move.description"));
		Composite sectionClientMove = toolkit.createComposite(sectionMove);
		sectionClientMove.setLayout(new GridLayout(2, false));				
		createEntry(sectionClientMove, EditorPlugin.getResourceString("quickOptions.distanceMoveX.label"), MOVE_X);
		createEntry(sectionClientMove, EditorPlugin.getResourceString("quickOptions.distanceMoveY.label"), MOVE_Y);
		sectionMove.setClient(sectionClientMove);
	}

	protected static final int CLONE_X = 1; 
	protected static final int CLONE_Y = 2;
	protected static final int MOVE_X = 3;
	protected static final int MOVE_Y = 4;	
	
	protected void createEntry(Composite parent, String labelText, int identifier) 
	{
		toolkit.paintBordersFor(parent);
		
		Label l = toolkit.createLabel(parent, labelText);		
		Spinner spinner = toolkit.createSpinner(parent, SWT.NONE);
		
//		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		spinner.setSelection(getValue(identifier));				
		spinner.addSelectionListener(textListener);
		spinner.addFocusListener(focusListener);
		spinner.addDisposeListener(disposeListener);
		
		spinner2Identifier.put(spinner, new Integer(identifier));		
	}	
	
	public void setFocus() 
	{
		form.setFocus();
	}

	protected Map<Spinner, Integer> spinner2Identifier = new HashMap<Spinner, Integer>();
	
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
