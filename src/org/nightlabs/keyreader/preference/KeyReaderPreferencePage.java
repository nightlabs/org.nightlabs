package org.nightlabs.keyreader.preference;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class KeyReaderPreferencePage
		extends PreferencePage
		implements IWorkbenchPreferencePage
{
	private static final Logger logger = Logger.getLogger(KeyReaderPreferencePage.class);
	private IWorkbench workbench;
	private KeyReaderPreferenceComposite keyReaderPreferenceComposite;

	public void init(IWorkbench workbench)
	{
		this.workbench = workbench;
	}

	protected Control createContents(Composite parent)
	{
		keyReaderPreferenceComposite = new KeyReaderPreferenceComposite(parent, SWT.NONE);
//		getApplyButton().setEnabled(false);
		updateApplyButton();
		return keyReaderPreferenceComposite;
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		getDefaultsButton().setEnabled(false);
	}

	@Override
	protected void performDefaults()
	{
		logger.debug("performDefaults");
		super.performDefaults();
	}

	@Override
	public boolean performCancel()
	{
		logger.debug("performCancel");
		return super.performCancel();
	}
	@Override
	public boolean performOk()
	{
		logger.debug("performOk");
		keyReaderPreferenceComposite.save();
		return super.performOk();
	}

}
