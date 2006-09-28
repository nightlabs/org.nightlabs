/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * 
 */
public class DailyTimePatternBuilderHopPage extends WizardHopPage {

	private PatternExecutionTimeComposite startTimeEdit;
	private XComposite typeWrapper;
	
	private Button typeDaily;
	
	private Button typeChooseDays;
	private XComposite chooseDaysWrapper;
	private Button chooseSun;
	private Button chooseMon;
	private Button chooseTue;
	private Button chooseWed;
	private Button chooseThu;
	private Button chooseFri;
	private Button chooseSat;

	private Button typeEachXDay;
	private XComposite eachXDayWrapper;
	private Spinner eachXDaySpinner;

	/**
	 * @param pageName
	 */
	public DailyTimePatternBuilderHopPage() {
		super(DailyTimePatternBuilderHopPage.class.getName(), NLBasePlugin.getResourceString("timepattern.builderWizard.daily.pageName"));
		setMessage(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.message"));
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public DailyTimePatternBuilderHopPage(String pageName, String title) {
		super(pageName, title);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public DailyTimePatternBuilderHopPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		startTimeEdit = new PatternExecutionTimeComposite(wrapper, SWT.NONE);

		(new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL))
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		typeWrapper = new XComposite(wrapper, SWT.NONE);
		// typeWrapper.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.name"));

		typeWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
		typeWrapper.setLayout(new GridLayout());
		typeDaily = new Button(typeWrapper, SWT.RADIO);
		typeDaily.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.daily"));
		typeDaily.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeDaily.addSelectionListener(typeSelectionListener);

		typeChooseDays = new Button(typeWrapper, SWT.RADIO);
		typeChooseDays.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays"));
		typeChooseDays.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeChooseDays.addSelectionListener(typeSelectionListener);
		chooseDaysWrapper = new XComposite(typeWrapper, SWT.NONE);
		chooseDaysWrapper.getGridData().horizontalIndent = 30;
		chooseDaysWrapper.setLayout(new RowLayout());
		chooseSun = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseSun.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.sun"));
		chooseMon = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseMon.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.mon"));
		chooseTue = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseTue.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.tue"));
		chooseWed = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseWed.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.wed"));
		chooseThu = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseThu.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.thu"));
		chooseFri = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseFri.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.fri"));
		chooseSat = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseSat.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.chooseDays.sat"));

		typeEachXDay = new Button(typeWrapper, SWT.RADIO);
		typeEachXDay.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.eachXDay"));
		typeEachXDay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeEachXDay.addSelectionListener(typeSelectionListener);
		eachXDayWrapper = new XComposite(typeWrapper, SWT.NONE);
		eachXDayWrapper.setLayout(new GridLayout(3, false));
		eachXDayWrapper.getGridData().horizontalIndent = 30;
		Label before = new Label(eachXDayWrapper, SWT.NONE);
		before.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.execEach"));
		eachXDaySpinner = new Spinner(eachXDayWrapper, SWT.BORDER);
		eachXDaySpinner.setMaximum(31);
		Label after = new Label(eachXDayWrapper, SWT.NONE);
		after.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.days"));

		typeDaily.setSelection(true);

		updateEnabled();
		return wrapper;
	}

	private SelectionListener typeSelectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			updateEnabled();
		}
	};

	protected void updateEnabled() {
		chooseDaysWrapper.setEnabled(typeChooseDays.getSelection());
		eachXDayWrapper.setEnabled(typeEachXDay.getSelection());
	}

	public void configureTimePatternSet(TimePattern pattern) 
	throws TimePatternFormatException 
	{
//		TimePattern pattern = timePatternSet.createTimePattern();
		pattern.setYear("*");
		pattern.setMonth("*");
		if (typeDaily.getSelection()) {
			pattern.setDay("*");
			pattern.setDayOfWeek("*");
		}
		else if (typeChooseDays.getSelection()) {
			String dayStr = "";
			if (chooseSun.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "0";
			}
			if (chooseMon.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "1";
			}
			if (chooseTue.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "2";
			}
			if (chooseWed.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "3";
			}
			if (chooseThu.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "4";
			}
			if (chooseFri.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "5";
			}
			if (chooseSat.getSelection()) {
				if (!"".equals(dayStr))
					dayStr = dayStr + ",";
				dayStr = dayStr + "6";
			}
			pattern.setDay("*");
			pattern.setDayOfWeek(dayStr);
		}
		else if (typeEachXDay.getSelection()) {
			pattern.setDay("*/"+eachXDaySpinner.getSelection());
			pattern.setDayOfWeek("*");
		}
		
		startTimeEdit.configurePattern(pattern);
	}
}
