/**
 * 
 */
package org.nightlabs.base.language;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

/**
 * Simple extendable Wizardpage showing one I18nTextEditor.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class I18nTextEditorWizardPage extends WizardHopPage {

	/**
	 * @param pageName
	 */
	public I18nTextEditorWizardPage(String pageName, String editorCaption) {
		super(pageName);
		setEditorCaption(editorCaption);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public I18nTextEditorWizardPage(String pageName, String title, String editorCaption) {
		super(pageName, title);
		setEditorCaption(editorCaption);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public I18nTextEditorWizardPage(String pageName, String title,
			ImageDescriptor titleImage, String editorCaption) {
		super(pageName, title, titleImage);
		setEditorCaption(editorCaption);
	}
	
	private XComposite wrapper;
	private I18nTextEditor textEditor;
	private String editorCaption;
	

	public void setEditorCaption(String editorCaption) {
		this.editorCaption = (editorCaption != null) ? this.editorCaption: "";
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		textEditor = new I18nTextEditor(wrapper, editorCaption);
		textEditor.setI18nText(new I18nTextBuffer());
		textEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
		createAdditionalContents(wrapper);
		return wrapper;
	}
	
	/**
	 * This is called after the I18TextEditor is created to allow 
	 * subclasses to add additional Controls to the page.
	 * 
	 * @param wrapper
	 */
	protected void createAdditionalContents(Composite wrapper) {}
	
	
	public I18nText getI18nText() {
		return textEditor.getI18nText();
	}

}
