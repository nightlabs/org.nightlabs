/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.wizard.IWizardHopPage;
import org.nightlabs.base.wizard.WizardHop;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DailyTimePatternBuilderHop extends WizardHop implements TimePatternSetBuilderWizardHop {

	/**
	 * @param entryPage
	 */
	public DailyTimePatternBuilderHop() {
		super(new DailyTimePatternBuilderHopPage());
	}

	public String getHopDescription() {
		return NLBasePlugin.getResourceString("timepattern.builderWizard.daily.hopDescription");
	}

}
