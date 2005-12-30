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

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.l10n.Currency;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CurrencyEditComposite extends XComposite
{
	private Currency currency;
	private Text numberText;
	private long value;

	/**
	 * @param parent
	 */
	public CurrencyEditComposite(Composite parent, Currency _currency)
	{
		super(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		this.currency = _currency;

		getGridLayout().numColumns = 2;
		numberText = new Text(this, SWT.BORDER);
		setValue(0);
		new Label(this, SWT.NONE).setText(currency.getCurrencySymbol());

		numberText.addModifyListener(new ModifyListener(){
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e)
			{
				String s = numberText.getText();
				try {
					value = NumberFormatter.parseCurrency(s, currency, false);
					parseException = null;
				} catch (ParseException x) {
					parseException = x;

					if (errorDialogEnabled)
						MessageDialog.openError(getShell(), "Invalid text!", "The text you entered is not a valid number: " + x.getLocalizedMessage());
				}

				if (modifyListeners == null)
					return;

				Event event = new Event();
				event.widget = CurrencyEditComposite.this;
				event.display = e.display;
				event.time = e.time;
				event.data = e.data;
				ModifyEvent me = new ModifyEvent(event);
				for (Iterator it = modifyListeners.iterator(); it.hasNext(); ) {
					ModifyListener l = (ModifyListener) it.next();
					l.modifyText(me);
				}
			}
		});
	}

	private boolean errorDialogEnabled = true;

	/**
	 * @return Returns the errorDialogEnabled.
	 */
	public boolean isErrorDialogEnabled()
	{
		return errorDialogEnabled;
	}
	/**
	 * If <tt>errorDialogEnabled</tt> is <tt>true</tt> and the user enters
	 * invalid data, a {@link MessageDialog} will be opened. This is the
	 * default behaviour, but might be unwanted - e.g. in a wizard.
	 * <p>
	 * If you want to handle erroneous input externally, set this <tt>false</tt>
	 * and register a {@link ModifyListener}. You can then find out, whether
	 * there was an parser error by calling {@link #getParseException()}.
	 *
	 * @param errorDialogEnabled The errorDialogEnabled to set.
	 */
	public void setErrorDialogEnabled(boolean errorDialogEnabled)
	{
		this.errorDialogEnabled = errorDialogEnabled;
	}

	private ParseException parseException = null;

	/**
	 * @return Returns the last <tt>ParseException</tt> or <tt>null</tt> if the
	 *		user-input is OK.
	 */
	public ParseException getParseException()
	{
		return parseException;
	}

	private LinkedList modifyListeners = null;

	public void addModifyListener(ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			modifyListeners = new LinkedList();

		modifyListeners.add(modifyListener);
	}

	public boolean removeModifyListener(ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			return false;

		return modifyListeners.remove(modifyListener);
	}

	public void setValue(long currencyValue)
	{
		this.value = currencyValue;
		numberText.setText(
				NumberFormatter.formatCurrency(currencyValue, currency, false));
	}

	public long getValue()
	{
		return value;
	}
	
	/**
	 * @return Returns the currency.
	 */
	public Currency getCurrency()
	{
		return currency;
	}
}
