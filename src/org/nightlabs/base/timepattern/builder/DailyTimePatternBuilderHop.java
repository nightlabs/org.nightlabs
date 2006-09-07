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
public class DailyTimePatternBuilderHop 
extends WizardHop 
implements ITimePatternSetBuilderWizardHop 
{

	/**
	 * @param entryPage
	 */
	public DailyTimePatternBuilderHop() {
		super(new DailyTimePatternBuilderHopPage());
	}

	public String getHopDescription() {
		return NLBasePlugin.getResourceString("timepattern.builderWizard.daily.hopDescription");
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet) 
	throws TimePatternFormatException 
	{
		DailyTimePatternBuilderHopPage page = (DailyTimePatternBuilderHopPage) getEntryPage();
		page.configureTimePatternSet(timePatternSet.createTimePattern());		
	}

}
