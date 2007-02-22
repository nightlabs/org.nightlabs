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
	    localeLabel.setText("Your Locale:");
	    
	    Label locale = new Label(page, SWT.SINGLE);
	    locale.setText(Locale.getDefault().getDisplayName());
    
	    // Leerzeile
        final Label leer = new Label(page, SWT.NONE);
	    
	    // initialize the time
	    DateFormat df_temp = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
	    day.setText(df_temp.format(cal.getCalendar().getTime()));
    
	    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
	    day.setText(df.format(cal.getCalendar().getTime()));
	    cal.addSWTCalendarListener(new SWTCalendarListener() {
	    	public void dateChanged(SWTCalendarEvent calendarEvent) {
	    		Locale _locale = Locale.getDefault();
	    		DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG, _locale);
	    		day.setText(df2.format(calendarEvent.getCalendar().getTime()));
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