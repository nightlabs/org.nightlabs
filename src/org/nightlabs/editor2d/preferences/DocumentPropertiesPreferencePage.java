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
package org.nightlabs.editor2d.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.composite.ComboComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.i18n.ResolutionUnitEP;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.base.print.page.PredefinedPageEP;
import org.nightlabs.editor2d.Editor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.config.DocumentConfigModule;
import org.nightlabs.editor2d.page.DocumentProperties;
import org.nightlabs.editor2d.page.DocumentPropertiesRegistry;
import org.nightlabs.editor2d.page.PageOrientationComposite;
import org.nightlabs.editor2d.page.PredefinedPageComposite;
import org.nightlabs.editor2d.page.ResolutionUnitComposite;
import org.nightlabs.editor2d.page.UnitComposite;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DocumentPropertiesPreferencePage 
extends PreferencePage
implements IWorkbenchPreferencePage 
{

	public DocumentPropertiesPreferencePage() {
		init();
	}

	protected void init() 
	{
		setTitle(EditorPlugin.getResourceString("preferences.document.properties.title"));
		setPreferenceStore(Preferences.getPreferenceStore());
	}
	
	private PredefinedPageComposite pageComp = null;
	private ResolutionUnitComposite resUnitComp = null;
	private UnitComposite unitComp = null;
	private Text resolutionText = null;
	private PageOrientationComposite orientationComp = null;
	private ComboComposite<Class> editorChooseCombo = null;
	
//	private DocumentConfigModule documentConfigModule = null;
	protected  DocumentConfigModule getDocumentConfigModule() {
//		if (documentConfigModule == null)
//			documentConfigModule = (DocumentConfigModule) Config.sharedInstance().createConfigModule(DocumentConfigModule.class);
//		return documentConfigModule;
		return DocumentPropertiesRegistry.sharedInstance().getDocumentConfModule();
	}
	
	private ISelectionChangedListener editorListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			if (!selection.isEmpty() && selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof Class) {
					DocumentProperties documentProperties = getDocumentConfigModule().getEditorClass2DocumentProperties().get((Class)firstElement);
					if (documentProperties != null) {
						setDocumentProperties(documentProperties);
					}
				}
			}
		}
	};
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected Control createContents(Composite parent) 
	{
		Composite content = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		content.setLayout(new GridLayout(2, false));
		content.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Editors
		Label editorSelectLabel = new Label(content, SWT.NONE);
		editorSelectLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.editorSelect.label"));
		editorChooseCombo = new ComboComposite<Class>(content, SWT.NONE, getEditorClasses(), editorLabelProvider);
		editorChooseCombo.selectElement(Editor.class);
		editorChooseCombo.addSelectionChangedListener(editorListener);
		editorChooseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label separator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.horizontalSpan = 2;
		separator.setLayoutData(separatorData);
		
		// Predefined Pages
		Label pageSelectLabel = new Label(content, SWT.NONE);
		pageSelectLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.pageSelect.label"));
		pageComp = new PredefinedPageComposite(content, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		pageComp.selectPage(PredefinedPageEP.sharedInstance().getPageRegistry().getPage(
				Preferences.getPreferenceStore().getString(Preferences.PREF_PREDEFINED_PAGE_ID)));
		pageComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
		// Page Orientation
		Label orientationLabel = new Label(content, SWT.NONE);
		orientationLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.pageOrientation.label"));
		orientationComp = new PageOrientationComposite(content, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		orientationComp.selectOrientation(Preferences.getPreferenceStore().getInt(
				Preferences.PREF_PAGE_ORIENTATION_ID));
		orientationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		// Resolution Unit
		Label resolutionUnitSelectLabel = new Label(content, SWT.NONE);
		resolutionUnitSelectLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.resolutionUnit.label"));
		resUnitComp = new ResolutionUnitComposite(content, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		resUnitComp.selectResolutionUnit(ResolutionUnitEP.sharedInstance().getResolutionUnitRegistry().getResolutionUnit(
				Preferences.getPreferenceStore().getString(Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID)));
		resUnitComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		

		// Resolution
		Label resolutionLabel = new Label(content, SWT.NONE);
		resolutionLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.resolution.label"));
		resolutionText = new Text(content, SWT.BORDER);
		resolutionText.setText(Preferences.getPreferenceStore().getString(Preferences.PREF_DOCUMENT_RESOLUTION));
		resolutionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Standard Unit
		Label unitSelectLabel = new Label(content, SWT.NONE);
		unitSelectLabel.setText(EditorPlugin.getResourceString("preferences.document.properties.standardUnit.label"));
		unitComp = new UnitComposite(content, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		unitComp.selectUnit(UnitRegistryEP.sharedInstance().getUnitRegistry().getUnit(
				Preferences.getPreferenceStore().getString(Preferences.PREF_STANDARD_UNIT_ID)));
		unitComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		return content;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

	@Override
	public void dispose() 
	{
		pageComp.dispose();
		resUnitComp.dispose();
		unitComp.dispose();
		super.dispose();
	}

	private double getResolution() 
	{
		double res = Preferences.getPreferenceStore().getDefaultDouble(Preferences.PREF_DOCUMENT_RESOLUTION);
		String value = resolutionText.getText();
		try {
			res = Double.parseDouble(value);			
		} catch (NumberFormatException e) {
			return res; 			
		}
		return res; 
	}

	private DocumentProperties getCurrentDocmuentProperties() 
	{
		return new DocumentProperties(pageComp.getSelectedPage(), orientationComp.getOrientation(),
				resUnitComp.getSelectedResolutionUnit(), getResolution());
	}
	
	private void setDocumentProperties(DocumentProperties documentProperties) 
	{
		pageComp.selectPage(documentProperties.getPredefinedPage());
		resUnitComp.selectResolutionUnit(documentProperties.getResolutionUnit());
		resolutionText.setText(""+documentProperties.getResolution());
		orientationComp.selectOrientation(documentProperties.getOrientation());
	}
	
	private List<Class> getEditorClasses() 
	{
		Map<Class, DocumentProperties> editorClass2DocumentProperties = getDocumentConfigModule().getEditorClass2DocumentProperties();
		List<Class> editorClasses = new ArrayList<Class>();
		for (Map.Entry<Class, DocumentProperties> entry : editorClass2DocumentProperties.entrySet()) {
			Class editorClass = entry.getKey();
			editorClasses.add(editorClass);
		}
		return editorClasses;
	}
	
//	private ILabelProvider getEditorLabelProvider() 
//	{
//  	IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
//  	String editorID = null;
//  	String editorName = editorRegistry.findEditor(editorID).;
//	}

	private ILabelProvider editorLabelProvider = new LabelProvider() 
	{
		@Override
		public String getText(Object element) 
		{			
			String editorID = getDocumentConfigModule().getEditorClass2EditorID().get((Class)element);
			IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(editorID);
			if (editorDescriptor != null) {
				return editorDescriptor.getLabel();
			}
			return super.getText(element);
		}		
	};
			
	@Override
	public boolean performOk() 
	{
//		Preferences.getPreferenceStore().setValue(Preferences.PREF_PREDEFINED_PAGE_ID, 
//				pageComp.getSelectedPage().getPageID());
//		Preferences.getPreferenceStore().setValue(Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID, 
//				resUnitComp.getSelectedResolutionUnit().getResolutionID());
		Preferences.getPreferenceStore().setValue(Preferences.PREF_STANDARD_UNIT_ID, 
				unitComp.getSelectedUnit().getUnitID());		
//		Preferences.getPreferenceStore().setValue(Preferences.PREF_DOCUMENT_RESOLUTION,
//				getResolution());
//		Preferences.getPreferenceStore().setValue(Preferences.PREF_PAGE_ORIENTATION_ID,
//				orientationComp.getOrientation());
		Map<Class, DocumentProperties> editorClass2DocumentProperties = getDocumentConfigModule().getEditorClass2DocumentProperties();
		editorClass2DocumentProperties.put(editorChooseCombo.getSelectedElement(), getCurrentDocmuentProperties());
		getDocumentConfigModule().setEditorClass2DocumentProperties(editorClass2DocumentProperties);
		
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() 
	{
//		pageComp.selectPage(PredefinedPageEP.sharedInstance().getPageRegistry().getPage(
//				Preferences.PREF_PREDEFINED_PAGE_ID_DEFAULT));
//		resUnitComp.selectResolutionUnit(ResolutionUnitEP.sharedInstance().getResolutionUnitRegistry().getResolutionUnit(
//				Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID_DEFAULT));
		unitComp.selectUnit(UnitRegistryEP.sharedInstance().getUnitRegistry().getUnit(
				Preferences.PREF_STANDARD_UNIT_ID_DEFAULT));
//		resolutionText.setText(""+Preferences.getPreferenceStore().getDefaultDouble(
//				Preferences.PREF_DOCUMENT_RESOLUTION));
//		orientationComp.selectOrientation(Preferences.getPreferenceStore().getDefaultInt(
//				Preferences.PREF_PAGE_ORIENTATION_ID));
		editorChooseCombo.selectElement(Editor.class);
		DocumentProperties documentProperties = getDocumentConfigModule().getEditorClass2DocumentProperties().get(Editor.class);
		setDocumentProperties(documentProperties);
		
		super.performDefaults();
	}
		
}
