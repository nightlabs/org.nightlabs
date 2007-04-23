package org.nightlabs.base.test;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.language.I18nTextEditorTable;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

public class TestView
		extends ViewPart
{
	private I18nTextEditorTable i18nTextEditorTable;
	private Button testButton;
	private I18nText i18nText = new I18nTextBuffer();

	@Override
	public void createPartControl(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		i18nTextEditorTable = new I18nTextEditorTable(page);
		i18nText.setText(Locale.getDefault().getLanguage(), "Test in my language.");
		i18nTextEditorTable.setI18nText(i18nText);

		testButton = new Button(page, SWT.PUSH);
		testButton.setText("Test");
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StringBuffer sb = new StringBuffer();
				Logger logger = Logger.getLogger(TestView.class);
				logger.info("***********************");
				logger.info("*** " + i18nText.getTexts().size() + " entries in I18nText:");
				sb.append(i18nText.getTexts().size() + " entries in I18nText:\n");
				for (Map.Entry<String, String> me : i18nText.getTexts()) {
					logger.info("*** " + me);
					sb.append("  " + me + '\n');
				}
				logger.info("***********************");
				MessageDialog.openInformation(getSite().getShell(), "Test", sb.toString());
			}
		});
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub
	}
}
