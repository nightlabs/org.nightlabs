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
public class SingleExecTimePatternBuilderHop 
extends WizardHop 
implements ITimePatternSetBuilderWizardHop 
{

	/**
	 * 
	 */
	public SingleExecTimePatternBuilderHop() {
		super(new SingleExecTimePatternBuilderHopPage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.timepattern.builder.ITimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return NLBasePlugin.getResourceString("timepattern.builderWizard.singleExec.hopDescription");
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		SingleExecTimePatternBuilderHopPage page = (SingleExecTimePatternBuilderHopPage) getEntryPage();
		page.configureTimePattern(timePatternSet.createTimePattern());
	}

}
