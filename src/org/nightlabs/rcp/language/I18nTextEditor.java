/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.rcp.language;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.language.LanguageCf;
import org.nightlabs.rcp.composite.XComposite;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class I18nTextEditor extends XComposite
{
	private I18nText i18nText;
	private LanguageChooser languageChooser;
	private LanguageCf textLanguage;
	private Text text;

	public I18nTextEditor(Composite parent)
	{
		this(parent, (LanguageChooser)null);
	}

	public I18nTextEditor(Composite parent, String caption)
	{
		this(parent, (LanguageChooser)null, caption);
	}

	public I18nTextEditor(Composite parent, LanguageChooser languageChooser)
	{
		this(parent, languageChooser, (String)null);
	}
	public I18nTextEditor(Composite parent, LanguageChooser languageChooser, String caption)
	{
		super(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);

		if (languageChooser == null)
			getGridLayout().numColumns = 2;

		if (caption != null) {
			Label l = new Label(this, SWT.WRAP);
			l.setText(caption);
			GridData gd = new GridData();
//			gd.grabExcessHorizontalSpace = true;
			gd.horizontalSpan = getGridLayout().numColumns;
			l.setLayoutData(gd);
		}

		if (languageChooser == null) {
			languageChooser = new LanguageChooserCombo(this, false);
			// TODO On the long run, the I18nTextEditor itself should be a combobox
			// in this mode, showing the language flag on the left.
		}

		this.languageChooser = languageChooser;

		getGridData().grabExcessVerticalSpace = false;

		text = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0)
			{
				storeText();
			}
		});
		
		languageChooser.addLanguageChangeListener(new LanguageChangeListener() {
			public void languageChanged(LanguageChangeEvent event)
			{
				storeText();
				loadText();
			}
		});

		text.addModifyListener(new ModifyListener() {
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e)
			{
				storeText();

				if (modifyListeners == null)
					return;

				for (Iterator it = modifyListeners.iterator(); it.hasNext(); ) {
					((ModifyListener)it.next()).modifyText(e);
				}
			}
		});
	}

	private LinkedList modifyListeners = null;

	public void addModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			modifyListeners = new LinkedList();

		modifyListeners.add(l);
	}
	public void removeModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			return;

		modifyListeners.remove(l);
	}

	/**
	 * @return Returns the currently edited text (the one that is visible).
	 */
	public String getEditText()
	{
		return text.getText();
	}

	public I18nText getI18nText()
	{
		storeText();
		return i18nText;
	}

	public void setI18nText(I18nText newI18nText)
	{
		storeText();
		i18nText = newI18nText;
		loadText();
		text.setEditable(i18nText != null);
	}

	private String orgText = "";

	private void loadText()
	{
		String txt = null;
		
		if (i18nText != null) {
			textLanguage = languageChooser.getLanguage();
			txt = i18nText.getText(textLanguage.getLanguageID());
		}
		if (txt == null) txt = "";
		text.setText(txt);
		orgText = txt;
	}

	/**
	 * This method stores the currently edited text into the "backend" {@link I18nText}
	 * object. This method is automatically called whenever the <tt>I18nTextEditor</tt>
	 * is changed by the user. This means, the I18nText is kept synchronous with the
	 * displayed and edited text. 
	 * <p>
	 * This method has been changed from public to private.
	 */
	private void storeText()
	{
		if (i18nText == null)
			return;

		String newText = text.getText();
		if (!newText.equals(orgText)) {
			i18nText.setText(textLanguage.getLanguageID(), newText);
			orgText = newText;
		}
	}

	public LanguageChooser getLanguageChooser()
	{
		return languageChooser;
	}
}
