/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import java.util.ArrayList;
import java.util.List;

import org.nightlabs.base.wizard.DynamicPathWizard;
import org.nightlabs.base.wizard.DynamicPathWizardDialog;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePatternSetBuilderWizard extends DynamicPathWizard {

	private TimePatternSetBuilderEntryWizardPage entryPage;
	
	/**
	 * 
	 */
	public TimePatternSetBuilderWizard() {
		entryPage = new TimePatternSetBuilderEntryWizardPage();
		addPage(entryPage);
	}

	public void addBuilderHop(TimePatternSetBuilderWizardHop builderHop) {
		entryPage.addBuilderHop(builderHop);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}
	
	public static int open() {
		TimePatternSetBuilderWizard wiz = new TimePatternSetBuilderWizard();
		wiz.addBuilderHop(new DailyTimePatternBuilderHop());
		wiz.addBuilderHop(new SingleExecTimePatternBuilderHop());
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		return dlg.open();
	}

}
