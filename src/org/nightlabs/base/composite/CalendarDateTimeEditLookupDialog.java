package org.nightlabs.base.composite;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.l10n.DateFormatProvider;

import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class CalendarDateTimeEditLookupDialog
		extends Dialog
{
	
	private SWTCalendar cal;
	private Label day;

	public CalendarDateTimeEditLookupDialog(Shell parentShell, DateTimeEdit dateTimeEdit)
	{
		super(parentShell);
		this.cal = new SWTCalendar(parentShell, SWT.NONE | SWTCalendar.RED_SUNDAY);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite page = (Composite) super.createDialogArea(parent);
		RowLayout rLayout_cal = new RowLayout();
	    rLayout_cal.type = SWT.VERTICAL;
	    page.setLayout(rLayout_cal);
	    
	    Label localeLabel = new Label(page, SWT.SINGLE);
	    localeLabel.setText("Locale:");
	    // combobox for the choosable locales
	    final Combo localeCombo = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
	   
	    // search for available locales in java.util.Calendar and get the count
	    Locale[] temp = Calendar.getAvailableLocales();
	    int count = 0;
	    for (int i = 0; i < temp.length; i++) {
	    	if (temp[i].getCountry().length() > 0) {
	    		count++;
	    	}
	    }

	    // fill in all locales in the combobox
	    final Locale[] locales = new Locale[count];
	    count = 0;
	    for (int i = 0; i < temp.length; i++) {
	    	if (temp[i].getCountry().length() > 0) {
	    		locales[count] = temp[i];
	    		localeCombo.add(locales[count].getDisplayName());
	    		count++;
	    	}
	    }

	    // set the default locale in the combobox
	    for (int i = 0; i < locales.length; i++) {
	    	if (locales[i].equals(Locale.getDefault())) {
	    		localeCombo.select(i);
	    		break;
	    	}
	    }
    
	    // Leerzeile
        final Label leer = new Label(page, SWT.NONE);
	    
	    // initialize the time
	    DateFormat df_temp = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
	    day.setText(df_temp.format(cal.getCalendar().getTime()));
    
	    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
	    day.setText(df.format(cal.getCalendar().getTime()));
	    cal.addSWTCalendarListener(new SWTCalendarListener() {
	    	public void dateChanged(SWTCalendarEvent calendarEvent) {
	    		Locale _locale = locales[localeCombo.getSelectionIndex()];
	    		DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG, _locale);
	    		day.setText(df2.format(calendarEvent.getCalendar().getTime()));
	    	}
	    });

	    localeCombo.addSelectionListener(new SelectionListener() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Locale _locale = locales[localeCombo.getSelectionIndex()];
	    		DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG, _locale);
	    		day.setText(df2.format(cal.getCalendar().getTime()));
	    		cal.setLocale(_locale);
	    	}

	    	public void widgetDefaultSelected(SelectionEvent event) {

	    	}
	    });
	    
	    return page;
	
	}
		
	@Override
	protected void initializeBounds()
	{
		super.initializeBounds();
		getShell().pack();
		Point mouse = Display.getDefault().getCursorLocation();
		Rectangle r = getShell().getBounds(); 
		r.x = mouse.x;
		r.y = mouse.y;
		getShell().setBounds(r);
	}
	
}