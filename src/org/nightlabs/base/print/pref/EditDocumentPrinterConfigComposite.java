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

package org.nightlabs.base.print.pref;

import java.awt.SystemColor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.LabeledText;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.print.DocumentPrinterDelegateConfig;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod.ExternalEngineDelegateConfig;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod.SystemCallDelegateConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditDocumentPrinterConfigComposite extends XComposite {

	private XComposite typeWrapper;
	private Group typeGroup;
	private Button typeSysCall;
	private Button typeExtEngine;
	
	private Composite editWrapper;
	private StackLayout stackLayout;
	private XComposite sysCalLEditComposite;
	private LabeledText commandPattern;
	private LabeledText parameterPattern;
	
	private XComposite extEngineEditComposite;
	private LabeledText className;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI();
	}

	protected void initGUI() {
		typeWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		typeGroup = new Group(typeWrapper, SWT.NONE);
		typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeGroup.setText(NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.configType.header"));
		typeGroup.setLayout(new RowLayout(SWT.VERTICAL));
		typeSysCall = new Button(typeGroup, SWT.RADIO);
		typeSysCall.setText(NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.configType.typeSysCall"));
		typeSysCall.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateTypeView();
			}
		});
		typeExtEngine = new Button(typeGroup, SWT.RADIO);
		typeExtEngine.setText(NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.configType.typeExtEngine"));
		typeExtEngine.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateTypeView();
			}
		});
		
		editWrapper = new Composite(this, SWT.NONE);
		stackLayout = new StackLayout();
		editWrapper.setLayout(stackLayout);
		editWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sysCalLEditComposite = new XComposite(editWrapper, SWT.NONE);
		commandPattern = new LabeledText(sysCalLEditComposite, NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.commandPattern"));
		parameterPattern = new LabeledText(sysCalLEditComposite, NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.parameterPattern"));
				
		extEngineEditComposite = new XComposite(editWrapper, SWT.NONE);
		className = new LabeledText(extEngineEditComposite, NLBasePlugin.getResourceString("preferencePage.documentPrinter.editConfig.className"));
		stackLayout.topControl = sysCalLEditComposite;
	}
	
	private void clearAll() {
		typeSysCall.setSelection(false);
		typeExtEngine.setSelection(false);
		commandPattern.getTextControl().setText("");
		parameterPattern.getTextControl().setText("");
		className.getTextControl().setText("");
	}
	
	private void updateTypeView() {
		if (typeSysCall.getSelection())
			stackLayout.topControl = sysCalLEditComposite;
		else if (typeExtEngine.getSelection())
			stackLayout.topControl = extEngineEditComposite;
		editWrapper.layout(true, true);
	}
	
	public void setDelegateConfig(DocumentPrinterDelegateConfig printerConfig) {
		clearAll();
		if (printerConfig != null) {
			if (printerConfig instanceof SystemCallDelegateConfig) {
				typeSysCall.setSelection(true);
				SystemCallDelegateConfig config = (SystemCallDelegateConfig)printerConfig;
				if (config.getCommandPattern() != null)
					commandPattern.getTextControl().setText(config.getCommandPattern());
				if (config.getParameterPattern() != null)
					parameterPattern.getTextControl().setText(config.getParameterPattern());
			}
			else if (printerConfig instanceof ExternalEngineDelegateConfig) {
				ExternalEngineDelegateConfig config = (ExternalEngineDelegateConfig) printerConfig;
				typeExtEngine.setSelection(true);
				if (config.getClassName() != null)
					className.getTextControl().setText(config.getClassName());
			}
			updateTypeView();
		}
	}
	
	public DocumentPrinterDelegateConfig readDelegateConfig() {
		DocumentPrinterDelegateConfig result = null;
		if (typeSysCall.getSelection()) {
			result = new SystemCallDelegateConfig();
			((SystemCallDelegateConfig)result).setCommandPattern(commandPattern.getTextControl().getText());
			((SystemCallDelegateConfig)result).setParameterPattern(parameterPattern.getTextControl().getText());
		}
		else if (typeExtEngine.getSelection()) {
			result = new ExternalEngineDelegateConfig();
			((ExternalEngineDelegateConfig)result).setClassName(className.getTextControl().getText());
		}
		return result;
	}
	
}
