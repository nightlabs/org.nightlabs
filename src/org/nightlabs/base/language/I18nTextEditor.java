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

package org.nightlabs.base.language;

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
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.language.LanguageCf;

/**
 * Editor Composite for {@link I18nText}s. This will provide (or use)
 * a language chooser and make an i18n text editable. The editor
 * will operate on an own buffer, so editing will not have an affect
 * on the original {@link I18nText} passed to display.
 * 
 * Use {@link I18nText#copyFrom(I18nText)} with {@link #getI18nText()}
 * as paramteer to reflect the changes in your {@link I18nText}.
 * You can also call {@link #copyToOriginal()} to let that be done for you.
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class I18nTextEditor extends XComposite
{
	private I18nText i18nText;
	private I18nTextBuffer buffer = new I18nTextBuffer();
	private LanguageChooser languageChooser;
	private LanguageCf textLanguage;
	private Text text;

	/**
	 * Create a new {@link I18nTextEditor} with a new default (combo)
	 * {@link LanguageChooser} and no descriptive text.
	 * 
	 * @param parent The parent to use.
	 */
	public I18nTextEditor(Composite parent)
	{
		this(parent, (LanguageChooser)null);
	}

	/**
	 * Create a new {@link I18nTextEditor} with a new default (combo)
	 * {@link LanguageChooser} and the given caption as descriptive header.
	 * 
	 * @param parent The parent to use.
	 * @param caption The header to display for the editor.
	 */
	public I18nTextEditor(Composite parent, String caption)
	{
		this(parent, (LanguageChooser)null, caption);
	}

	/**
	 * Create a new {@link I18nTextEditor} listening to languagechanges
	 * of the given {@link LanguageChooser} and no descriptive header.
	 *  
	 * @param parent The parent to use.
	 * @param languageChooser The {@link LanguageChooser} to listen to.
	 */
	public I18nTextEditor(Composite parent, LanguageChooser languageChooser)
	{
		this(parent, languageChooser, (String)null);
	}

	/**
	 * Create a new {@link I18nTextEditor} listening to languagechanges
	 * of the given {@link LanguageChooser} and the given caption as descriptive header.
	 * 
	 * @param parent The parent to use.
	 * @param languageChooser The {@link LanguageChooser} to listen to.
	 * @param caption The header to display for the editor.
	 */
	public I18nTextEditor(Composite parent, LanguageChooser languageChooser, String caption)
	{
		super(parent, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		getGridData().grabExcessVerticalSpace = false;

		if (languageChooser == null)
			getGridLayout().numColumns = 2;

		if (caption != null) {
			Label l = new Label(this, SWT.NONE);
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

//		text = new Text(this, SWT.BORDER | SWT.READ_ONLY); // TODO: READ_ONLY default? Not neccessary any more with refactor to buffer
		text = new Text(this, SWT.BORDER); 
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

				if (loadingText)
					return;
				if (modifyListeners == null)
					return;

				for (Iterator it = modifyListeners.iterator(); it.hasNext(); ) {
					((ModifyListener)it.next()).modifyText(e);
				}
			}
		});
	}

	/**
	 * List of modifyListeners. They will only be triggered
	 * if the text is actually modified by the user, not
	 * when the edit changes its text.
	 */
	private LinkedList modifyListeners = null;

	/**
	 * Add a {@link ModifyListener} to this editor. The listener
	 * will get triggered only when the text was actually modified by the user, 
	 * not when the edit changes its text.
	 * 
	 * @param l The {@link ModifyListener} to add.
	 */
	public void addModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			modifyListeners = new LinkedList();

		modifyListeners.add(l);
	}
	
	/**
	 * Remove the given modify listener.
	 * @param l The listener to remove.
	 */
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

	/**
	 * Returns the {@link I18nText} buffer used by the editor
	 * reflecting all changes made since the last call to {@link #setI18nText(I18nText)}.
	 *  
	 * @return the {@link I18nText} buffer used by the editor.
	 */
	public I18nText getI18nText()
	{
		storeText();
		return buffer;
	}

	/**
	 * Copies all values from the buffer
	 * to the {@link I18nText} originally passed
	 * on the last call to {@link #setI18nText(I18nText)}.
	 */
	public void copyToOriginal() {
		if (i18nText != null)
			buffer.copyTo(i18nText);
	}

	/**
	 * Initialize the editor with the given {@link I18nText}.
	 * 
	 * @param newI18nText The {@link I18nText} that should be displayed and made editable 
	 */
	public void setI18nText(I18nText newI18nText)
	{
		storeText();
		i18nText = newI18nText;
		buffer.clear();
		if (i18nText != null)
			buffer.copyFrom(i18nText);
		loadText();
	}

	/**
	 * The orgiginal text of the current language's text after load. 
	 */
	private String orgText = "";

	/**
	 * Set to true when loading, so that modify-listeners will not react 
	 * when a loaded (not modified) text is displayed.
	 */
	private boolean loadingText = false;

	/**
	 * Loads the text out of the buffer and displays it 
	 * in the text field. ModifyListeners registered will
	 * not be triggered when this happens.
	 */
	private void loadText()
	{
		loadingText = true;
		try {
			String txt = null;

			if (buffer != null) {
				textLanguage = languageChooser.getLanguage();
				txt = buffer.getText(textLanguage.getLanguageID());
			}
			if (txt == null) txt = "";
			text.setText(txt);
			orgText = txt;
		} finally {
			loadingText = false;
		}
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
		String newText = text.getText();
		if (!newText.equals(orgText)) {
			buffer.setText(textLanguage.getLanguageID(), newText);
			orgText = newText;
		}
	}

	/**
	 * Return the {@link LanguageChooser} used by this editor.
	 * @return the {@link LanguageChooser} used by this editor.
	 */
	public LanguageChooser getLanguageChooser()
	{
		return languageChooser;
	}

	/**
	 * Sets whether the text can be edited. 
	 * @param editable the editable.
	 * @see org.eclipse.swt.widgets.Text#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}
	
	
}
