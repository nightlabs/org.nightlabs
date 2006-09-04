/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.wizard.DynamicPathWizard;
import org.nightlabs.base.wizard.WizardHopPage;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePatternSetBuilderEntryWizardPage extends WizardHopPage {

	private List<TimePatternSetBuilderWizardHop> builderHops = new ArrayList<TimePatternSetBuilderWizardHop>();
	private TimePatternSetBuilderWizardHop currentBuilderHop;
	
	public TimePatternSetBuilderEntryWizardPage() {		
		super(
				TimePatternSetBuilderEntryWizardPage.class.getName(), 
				NLBasePlugin.getResourceString("timepattern.builderWizard.entryPage.name")
			);
		setMessage(NLBasePlugin.getResourceString("timepattern.builderWizard.entryPage.message"));
	}
	
	/**
	 * @param pageName
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName, String title) {
		super(pageName, title);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public void addBuilderHop(TimePatternSetBuilderWizardHop builderHop) {
		builderHops.add(builderHop);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		for (TimePatternSetBuilderWizardHop builderHop : builderHops) {
			Button button = new Button(wrapper, SWT.RADIO);
			button.setData(builderHop);
			button.setText(builderHop.getHopDescription());
			button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			button.addSelectionListener(builderSelectListener);
		}
		return wrapper;
	}

	private SelectionListener builderSelectListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Button) {
				Object bo = ((Button)e.getSource()).getData();
				if (bo instanceof TimePatternSetBuilderWizardHop) {
					if (currentBuilderHop != null) {
						if (getWizard() instanceof DynamicPathWizard) {
							DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
							wiz.removeDynamicWizardPage(currentBuilderHop.getEntryPage());
						}
					}
					currentBuilderHop = (TimePatternSetBuilderWizardHop)bo;
					if (currentBuilderHop != null) {
						if (getWizard() instanceof DynamicPathWizard) {
							DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
							wiz.addDynamicWizardPage(currentBuilderHop.getEntryPage());
						}					
					}
					getContainer().updateButtons();
				}
			}
		}
		
	};
	
}
