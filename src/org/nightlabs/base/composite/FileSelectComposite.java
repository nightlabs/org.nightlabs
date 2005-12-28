/**
 * 
 */
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
import org.nightlabs.base.util.RCPUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
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
	 * @param layoutMode
	 */
	public FileSelectComposite(Composite parent, int style, int layoutMode, String caption) {
		super(parent, style, layoutMode);		
		createContents(caption);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public FileSelectComposite(Composite parent, int style, int layoutMode,
			int layoutDataMode, String caption) {
		super(parent, style, layoutMode, layoutDataMode);
		createContents(caption);
	}
	
	private Text fileTextControl;
	private Button browseButton;
	private String fileText = ""; 
	
	private void createContents(String caption) {
		new Label(this, SWT.NONE).setText((caption != null) ? caption: "");

		XComposite fileComp = new XComposite(this, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);		
		fileComp.getGridLayout().numColumns = 3;
		fileTextControl = new Text(fileComp, SWT.BORDER);
		fileTextControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileTextControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				fileText = fileTextControl.getText();
				FileSelectComposite.this.modifyText(e);
			}
		});

		browseButton = new Button(fileComp, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell());
				fileDialog.setFilterExtensions(new String[]{"*.*"});
				fileDialog.setFilterNames(new String[]{"All files"});
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
	
	protected void modifyText(ModifyEvent e) {	}

}
