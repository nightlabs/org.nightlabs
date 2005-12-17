/*
 * Created on Aug 4, 2005
 */
package org.nightlabs.base.composite;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.l10n.DateFormatProvider;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.DateParseException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DateTimeEditComposite extends XComposite
{
	private Text text;
	private Button lookupButton;

	private Date date;
	private long flags;

	protected static boolean autoCalendarButton(long flags)
	{
		return (DateFormatProvider.DATE & flags) != 0;
	}

	/**
	 * This constructor calls {@link #DateTimeEditComposite(Composite, long, Date)}
	 * with <tt>new Date()</tt>.
	 */
	public DateTimeEditComposite(Composite parent, long flags)
	{
		this(parent, flags, new Date());
	}

	/**
	 * This constructor calls {@link #DateTimeEditComposite(Composite, long, Date, String)}
	 * with <tt>new Date()</tt>.
	 */
	public DateTimeEditComposite(Composite parent, long flags, String caption)
	{
		this(parent, flags, new Date(), caption);
	}

	/**
	 * This constructor calls {@link #DateTimeEditComposite(Composite, long, Date, String)}
	 * with <tt>caption = null</tt>.
	 */
	public DateTimeEditComposite(Composite parent, long flags, Date date)
	{
		this(parent, flags, date, null);
	}

	/**
	 * @param parent The SWT parent.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter}.
	 * @param date The current date to display. Must not be <tt>null</tt>.
	 * @param caption Either <tt>null</tt> or a text that should be displayed above the date-input.
	 */
	public DateTimeEditComposite(Composite parent, long flags, Date date, String caption)
	{
		super(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		this.flags = flags;
		this.getGridData().grabExcessHorizontalSpace = false;
		this.getGridData().grabExcessVerticalSpace = false;

		getGridLayout().numColumns = 2;

		if (caption != null) {
			Label l = new Label(this, SWT.WRAP);
			l.setText(caption);
			GridData gd = new GridData();
			gd.horizontalSpan = getGridLayout().numColumns;
			l.setLayoutData(gd);
		}

		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(textModifyListener);
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				setTimestamp(DateTimeEditComposite.this.date.getTime());
			}
		});

		this.setDate(date); // text needs to exist

		if ((DateFormatProvider.DATE & flags) != 0) {
			lookupButton = new Button(this, SWT.NONE);
			lookupButton.setText("...");
		}
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

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose()
	{
		text.removeModifyListener(textModifyListener);

		super.dispose();
	}

	private ModifyListener textModifyListener = new ModifyListener() {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e)
		{
			try {
				date = DateFormatter.parseDate(text.getText());
				dateParseException = null;
			} catch (DateParseException x) {
				dateParseException = x;
			}

			if (modifyListeners == null)
				return;

			Event event = new Event();
			event.widget = DateTimeEditComposite.this;
			event.display = e.display;
			event.time = e.time;
			event.data = e.data;
			ModifyEvent me = new ModifyEvent(event);
			for (Iterator it = modifyListeners.iterator(); it.hasNext(); ) {
				ModifyListener l = (ModifyListener) it.next();
				l.modifyText(me);
			}
		}
	};

	private DateParseException dateParseException;

	/**
	 * @return Returns either the last {@link DateParseException} that occured or
	 *		<tt>null</tt>, if all is fine.
	 */
	public DateParseException getDateParseException()
	{
		return dateParseException;
	}

	/**
	 * @param timestamp The timestamp to set.
	 */
	public void setTimestamp(long timestamp)
	{
		date.setTime(timestamp);
		text.setText(DateFormatter.formatDate(date, flags));
		this.dateParseException = null;
	}
	/**
	 * @return Returns the timestamp.
	 */
	public long getTimestamp()
	{
		return date.getTime();
	}

	/**
	 * @return Returns the text. Note, that this is the raw text of what the user
	 *		entered and it is not parsable if {@link #getDateParseException()} returns
	 *		not <tt>null</tt>.
	 */
	public String getText()
	{
		return text.getText();
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date)
	{
		if (date == null)
			throw new NullPointerException("date must not be null!");

		this.date = date;
		text.setText(DateFormatter.formatDate(date, flags));
		this.dateParseException = null;
	}
	/**
	 * @return Returns the date.
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @return Returns the flags.
	 */
	public long getFlags()
	{
		return flags;
	}
}
