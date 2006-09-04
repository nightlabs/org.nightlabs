/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.DateTimeEdit;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SingleExecTimePatternBuilderHopPage extends WizardHopPage {

	private PatternExecutionTimeComposite startTimeComposite;
	private DateTimeEdit dayEdit;
	
	/**
	 * @param pageName
	 */
	public SingleExecTimePatternBuilderHopPage() {
		super(SingleExecTimePatternBuilderHopPage.class.getName(), NLBasePlugin.getResourceString("timepattern.builderWizard.singleExec.pageName"));
		setMessage(NLBasePlugin.getResourceString("timepattern.builderWizard.singleExec.message"));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		startTimeComposite = new PatternExecutionTimeComposite(wrapper, SWT.NONE);
		startTimeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		(new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayEdit = new DateTimeEdit(wrapper, DateFormatter.FLAGS_DATE_LONG_WEEKDAY, NLBasePlugin.getResourceString("timepattern.builderWizard.singleExec.execDate"));
		return wrapper;
	}

}
