/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.wizard.WizardHop;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SingleExecTimePatternBuilderHop extends WizardHop implements
		TimePatternSetBuilderWizardHop {

	/**
	 * 
	 */
	public SingleExecTimePatternBuilderHop() {
		super(new SingleExecTimePatternBuilderHopPage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.timepattern.builder.TimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return NLBasePlugin.getResourceString("timepattern.builderWizard.singleExec.hopDescription");
	}

}
