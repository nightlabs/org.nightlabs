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

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
 * will either operate directly on the {@link I18nText} passed in
 * the {@link #setI18nText(I18nText)} or work with an own buffer,
 * so editing will not affect the original {@link I18nText}. This
 * behaviour can be controlled by
 * {@link #setEditMode(org.nightlabs.base.language.I18nTextEditor.EditMode)}.
 * 
 * Use {@link I18nText#copyFrom(I18nText)} with {@link #getI18nText()}
 * as paramteer to reflect the changes in your {@link I18nText}.
 * You can also call {@link #copyToOriginal()} to let that be done for you.
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class I18nTextEditor extends XComposite implements II18nTextEditor
{
	private I18nText original;
	private I18nText work;
	private I18nTextBuffer buffer = null;

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
	public I18nTextEditor(Composite parent, LanguageChooser _languageChooser, String caption)
	{
		super(parent, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		getGridData().grabExcessVerticalSpace = false;

		if (_languageChooser == null)
			getGridLayout().numColumns = 2;

		setEditMode(EditMode.DIRECT);

		if (caption != null) {
			Label l = new Label(this, SWT.NONE);
			l.setText(caption);
			GridData gd = new GridData();			
//			gd.grabExcessHorizontalSpace = true;
			gd.horizontalSpan = getGridLayout().numColumns;
			l.setLayoutData(gd);
		}

		final LanguageChangeListener languageChangeListener = new LanguageChangeListener() {
			public void languageChanged(LanguageChangeEvent event) {
				storeText();
				loadText();
			}
		};
		
		if (_languageChooser == null) {
			this.languageChooser = new LanguageChooserCombo(this); //, false);
			// TODO On the long run, the I18nTextEditor itself should be a combobox
			// in this mode, showing the language flag on the left.
		} else {
			this.languageChooser = _languageChooser;
			addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					I18nTextEditor.this.languageChooser.removeLanguageChangeListener(languageChangeListener);
				}
			});
		}

//		text = new Text(this, SWT.BORDER | SWT.READ_ONLY); // TODO: READ_ONLY default? Not neccessary any more with refactor to buffer
		text = new Text(this, SWT.BORDER); 
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0)
			{
				storeText();
			}
		});

		if (this.languageChooser != null)
			this.languageChooser.addLanguageChangeListener(languageChangeListener);

		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				storeText();

				if (loadingText)
					return;
				if (modifyListeners == null)
					return;

				Object[] listeners = modifyListeners.getListeners();
				for (int i = 0; i < listeners.length; ++i) {
					((ModifyListener)listeners[i]).modifyText(e);
				}
			}
		});
	}

	/**
	 * List of modifyListeners. They will only be triggered
	 * if the text is actually modified by the user, not
	 * when the edit changes its text.
	 */
	private ListenerList modifyListeners = null;

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			modifyListeners = new ListenerList();

		modifyListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#removeModifyListener(org.eclipse.swt.events.ModifyListener)
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

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#getOriginal()
	 */
	public I18nText getOriginal()
	{
		storeText(); // if we work in buffered mode, this method doesn't make much sense, but we can call it without harm.
		return original;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#getI18nText()
	 */
	public I18nText getI18nText()
	{
		storeText();
		return work;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#copyToOriginal()
	 */
	public void copyToOriginal() {
		if (original != null && work != original)
			work.copyTo(original);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText)
	 */
	public void setI18nText(I18nText i18nText)
	{
		setI18nText(i18nText, null);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText, org.nightlabs.base.language.I18nTextEditor.EditMode)
	 */
	public void setI18nText(I18nText i18nText, EditMode editMode)
	{
		storeText();
		_setI18nText(i18nText, editMode);
		loadText();
	}

	protected void _setI18nText(I18nText i18nText, EditMode editMode)
	{
		original = i18nText;

		if (editMode != null)
			setEditMode(editMode);
		else {
			if (original instanceof I18nTextBuffer)
				setEditMode(EditMode.DIRECT);
			else
				setEditMode(EditMode.BUFFERED);
		}
	}

	/**
	 * @see I18nTextEditor#setEditMode(org.nightlabs.base.language.I18nTextEditor.EditMode)
	 */
	public static enum EditMode {
		DIRECT,
		BUFFERED
	}

	private EditMode editMode;

	/**
	 * If the {@link EditMode} is {@link EditMode#DIRECT}, this editor will
	 * work directly with the {@link I18nText} that has been passed to
	 * {@link #setI18nText(I18nText)}. That means, every modification is
	 * directly forwarded to the original {@link I18nText} object (on
	 * focus lost or when calling {@link #getI18nText()}). This behaviour is
	 * useful when you create a new object (e.g. in a wizard page). In this case,
	 * it's not necessary to call {@link #copyToOriginal()}.
	 * <p>
	 * <b>Important:</b> The value set here is overridden when a new {@link I18nText}
	 * is set using {@link #setI18nText(I18nText)}.
	 * <p>
	 * If you edit an existing object, it is (in most cases) not desired to modify it
	 * directly, but instead to transfer all data only from the UI to the object, if
	 * the user explicitely applies his changes (in order to transfer them to the server
	 * as well). Therefore, you can use the {@link EditMode#BUFFERED}, which will
	 * cause an internal {@link I18nTextBuffer} to be created. 
	 * </p>
	 *
	 * @param editMode The new {@link EditMode}.
	 */
	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;

		switch (editMode) {
		case DIRECT:
			work = original;
			break;
		case BUFFERED:
			if (buffer == null)
				buffer = new I18nTextBuffer();

			buffer.clear();
			work = buffer;
			break;
		default:
			throw new IllegalArgumentException("Unknown editMode: " + editMode);
		}

		if (work != original && work != null && original != null)
			work.copyFrom(original);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#getEditMode()
	 */
	public EditMode getEditMode()
	{
		return editMode;
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

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#refresh()
	 */
	public void refresh()
	{
		loadText();
	}

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

			if (work != null) {
				textLanguage = languageChooser.getLanguage();
				txt = work.getText(textLanguage.getLanguageID(), false);
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
			if (work == null) {
				if (original == null) {
					_setI18nText(new I18nTextBuffer(), null);
				}
				else
					throw new IllegalStateException("work == null, but original != null - how's that possible?!");
			}

			if (textLanguage == null)
				textLanguage = languageChooser.getLanguage();

			work.setText(textLanguage.getLanguageID(), newText);
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

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}

	/**
	 * Sets the selection in the text box of the editor.
	 * @param start the beginning index of the selection
	 * @param end the end index of the selection (exclusive)
	 */
	public void setSelection(int start, int end) {
		text.setSelection(start, end);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#reset()
	 */
	public void reset() {
		setI18nText(null);
	}
}
