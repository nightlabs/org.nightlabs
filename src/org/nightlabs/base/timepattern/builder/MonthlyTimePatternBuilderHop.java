/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.wizard.WizardHop;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class MonthlyTimePatternBuilderHop 
extends WizardHop 
implements TimePatternSetBuilderWizardHop 
{

	/**
	 * 
	 */
	public MonthlyTimePatternBuilderHop() {
		super(new MonthlyTimePatternBuilderHopPage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.timepattern.builder.TimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return NLBasePlugin.getResourceString("timepattern.builderWizard.monthly.hopDescription");
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		MonthlyTimePatternBuilderHopPage page = (MonthlyTimePatternBuilderHopPage) getEntryPage();
		page.configurePattern(timePatternSet.createTimePattern());
	}

}
