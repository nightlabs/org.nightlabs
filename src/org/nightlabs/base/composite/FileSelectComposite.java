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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class FileSelectComposite extends XComposite {

	/**
	 * @param parent
	 * @param style
	 */
	public FileSelectComposite(Composite parent, int style, String caption) {
		super(parent, style);
		createContents(caption);
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public FileSelectComposite(Composite parent, int style, LayoutMode layoutMode, 
			LayoutDataMode layoutDataMode, String caption) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createContents(caption);
	}	
	
	private Text fileTextControl;
	private Button browseButton;
	private String fileText = ""; //$NON-NLS-1$
	
	private boolean updating = false;
	
	private void createContents(String caption) {
		new Label(this, SWT.NONE).setText((caption != null) ? caption: ""); //$NON-NLS-1$

		XComposite fileComp = new XComposite(this, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);		
		fileComp.getGridLayout().numColumns = 3;
		fileTextControl = new Text(fileComp, SWT.BORDER);
		fileTextControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileTextControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				if (updating)
					return;
				fileText = fileTextControl.getText();
				FileSelectComposite.this.modifyText(e);
			}
		});

		browseButton = new Button(fileComp, SWT.PUSH);
		browseButton.setText(Messages.getString("composite.FileSelectComposite.browseButton.text")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell());
				fileDialog.setFilterExtensions(new String[]{"*.*"}); //$NON-NLS-1$
				fileDialog.setFilterNames(new String[]{Messages.getString("composite.FileSelectComposite.filterName_allFiles")}); //$NON-NLS-1$
				String selectedFile = fileDialog.open();
				if (selectedFile != null)
					fileTextControl.setText(selectedFile);
			}
		});
		
	}
	
	public String getFileText() {
		return fileText;
	}
	
	public File getFile() {
		return new File(getFileText());
	}
	
	public void setFileText(String fileText) {
		updating = true;
		try {
			this.fileText = fileText;
			if (fileTextControl != null && (!fileTextControl.isDisposed()))
				if (fileText != null)
					fileTextControl.setText(fileText);
		}
		finally {
			updating = false;
		}
	}
	
	protected void modifyText(ModifyEvent e) {	}

	public Text getFileTextControl() {
		return fileTextControl;
	}
}
