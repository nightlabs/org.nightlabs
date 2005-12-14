/*
 * Created on Jan 6, 2005
 */
package com.nightlabs.rcp.language;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.nightlabs.language.LanguageCf;
import com.nightlabs.rcp.layout.WeightedTableLayout;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageChooserList
	extends AbstractLanguageChooser
{
	public static final Logger LOGGER = Logger.getLogger(LanguageChooserList.class);
	
	private LanguageTableContentProvider contentProvider;
	private LanguageTableLabelProvider labelProvider;
	private TableViewer viewer;
	
	public LanguageChooserList(Composite parent)
	{
		this(parent, true);
	}
	public LanguageChooserList(Composite parent, boolean grabExcessHorizontalSpace)
	{
		super(parent, SWT.NONE, true);
		((GridData)getLayoutData()).grabExcessHorizontalSpace = grabExcessHorizontalSpace;

		contentProvider = new LanguageTableContentProvider();
		labelProvider = new LanguageTableLabelProvider();
		viewer = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				LOGGER.debug("new language: "+getLanguage().getLanguageID());
				fireLanguageChangeEvent();
			}
		});

		Table t = viewer.getTable();
//		t.setHeaderVisible(true);
		t.setLinesVisible(true);

		GridData tgd = new GridData(GridData.FILL_BOTH);
		tgd.horizontalSpan = 1;
		tgd.verticalSpan = 1;

		t.setLayoutData(tgd);
		t.setLayout(new WeightedTableLayout(new int[] {1}));

		// Add the columns to the table
		new TableColumn(t, SWT.LEFT).setText("language");
		
		// must be called AFTER all columns are added
		viewer.setInput(contentProvider);
		
		StructuredSelection selection = new StructuredSelection(
				LanguageManager.sharedInstance().getCurrentLanguage());
		viewer.setSelection(selection, true);
	}

	/**
	 * @see com.nightlabs.rcp.language.LanguageChooser#getLanguage()
	 */
	public LanguageCf getLanguage()
	{
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		return (LanguageCf) selection.getFirstElement();
	}

}
