package com.nightlabs.jdo.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.nightlabs.j2ee.InitialContextProvider;
import com.nightlabs.jdo.JdoPlugin;
import com.nightlabs.rcp.composite.XComposite;

/**
 * A Composite for manipulating a list of SearchFieldItems ({@link com.nightlabs.jdo.search.SearchFilterItemList})
 * and choosing their conjunction. <br/>
 * This Composite will accept a {@link com.nightlabs.jdo.search.SearchResultFetcher} to
 * actually perform a search.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class AbstractItemBasedSearchFilterProviderComposite extends Composite implements SelectionListener{
	
	private Composite controlsComposite;
	private Button radioMatchAll;
	private Button radioMatchAny;
	private Composite buttonsComposite;
	private SearchFilterItemList itemList;
	private Button buttonMore;
	private Button buttonSearch;
	
	private SearchFilterItemListMutator listMutator;
	private SearchResultFetcher resultFetcher;
	private InitialContextProvider login;
	private SearchFilterProvider searchFilterProvider;
	
	
	/**
	 * Creates a new ItemBasedSearchFilterProviderComposite,
	 * wich has a list of SearchFilterItems and provides 
	 * selection of the conjunction of these items.
	 * This Composites delegates to its {@link SearchFilterItemList}
	 * for a request of a list of SearchFilterItems.
	 * 
	 * @param parent The widgets parent.
	 * @param style	The widgets style.
	 * @param searchFilterProvider The SearchFilterProvider that is the owner of this Composite.
	 * @param listMutator A mutator for the SearchFilterItemList.
	 * @param resultFetcher A fetcher to actually perform the search. 
	 * @param login parameter to the fetchers searchTriggered method.
	 */
	public AbstractItemBasedSearchFilterProviderComposite(
		Composite parent, 
		int style,
		SearchFilterProvider searchFilterProvider,
		SearchFilterItemListMutator listMutator,
		SearchResultFetcher resultFetcher,
		InitialContextProvider login
	) {
		super(parent, style);
		this.listMutator = listMutator;
		this.resultFetcher = resultFetcher;
		this.login = login;
		this.searchFilterProvider = searchFilterProvider;
		
		this.setLayout(new GridLayout());
		
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// item list
		itemList = createSearchFilterItemList(this, SWT.NONE);
		GridData itemListCompositeLData = new GridData();
		itemListCompositeLData.horizontalAlignment = GridData.FILL;
		itemListCompositeLData.verticalAlignment = GridData.FILL;
		itemListCompositeLData.grabExcessVerticalSpace = true;
		itemListCompositeLData.grabExcessHorizontalSpace = true;
		itemList.setLayoutData(itemListCompositeLData);
		
		
		controlsComposite = new XComposite(this, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		((GridLayout)controlsComposite.getLayout()).numColumns = 3;
		((GridLayout)controlsComposite.getLayout()).horizontalSpacing = 10;
		((GridLayout)controlsComposite.getLayout()).makeColumnsEqualWidth = false;
		
		GridData controlsCompositeLData = new GridData();
		controlsCompositeLData.horizontalAlignment = GridData.FILL;
		controlsCompositeLData.grabExcessHorizontalSpace = true;
		controlsComposite.setLayoutData(controlsCompositeLData);

		
		
		radioMatchAll = new Button(controlsComposite, SWT.RADIO | SWT.LEFT);
		radioMatchAll.setText(JdoPlugin.getResourceString("search.conjunction"+SearchFilter.CONJUNCTION_AND));
		GridData radioMatchAllLData = new GridData();
		radioMatchAllLData.grabExcessHorizontalSpace = false;
		radioMatchAllLData.horizontalAlignment = GridData.FILL;
		radioMatchAll.setLayoutData(radioMatchAllLData);

		radioMatchAny = new Button(controlsComposite, SWT.RADIO | SWT.LEFT);
		radioMatchAny.setText(JdoPlugin.getResourceString("search.conjunction"+SearchFilter.CONJUNCTION_OR));
		GridData radioMatchAnyLData = new GridData();
		radioMatchAnyLData.grabExcessHorizontalSpace = false;
		radioMatchAnyLData.horizontalAlignment = GridData.FILL;
		radioMatchAny.setLayoutData(radioMatchAnyLData);

		buttonsComposite = new Composite(controlsComposite, SWT.NONE);
		GridLayout buttonsCompositeLayout = new GridLayout();
		buttonsCompositeLayout.numColumns = 2;
		GridData buttonsCompositeLData = new GridData();
		buttonsCompositeLData.grabExcessHorizontalSpace = true;
		buttonsCompositeLData.horizontalAlignment = GridData.FILL;
		buttonsComposite.setLayoutData(buttonsCompositeLData);
		buttonsCompositeLayout.makeColumnsEqualWidth = true;
		buttonsComposite.setLayout(buttonsCompositeLayout);

		buttonMore = new Button(buttonsComposite, SWT.PUSH | SWT.CENTER);
		buttonMore.setText(JdoPlugin.getResourceString("search.ItemBasedFilterProviderComposite.buttons.add"));
		GridData buttonMoreLData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonMoreLData.grabExcessHorizontalSpace = true;
		buttonMore.setLayoutData(buttonMoreLData);
		buttonMore.addSelectionListener(this);

		buttonSearch = new Button(buttonsComposite, SWT.PUSH | SWT.CENTER);
		buttonSearch.setText(JdoPlugin.getResourceString("search.ItemBasedFilterProviderComposite.buttons.search"));
		GridData buttonSearchLData = new GridData(GridData.HORIZONTAL_ALIGN_END);
//		buttonSearchLData.widthHint = 58;
//		buttonSearchLData.heightHint = 32;
		buttonSearch.setLayoutData(buttonSearchLData);
		buttonSearch.addSelectionListener(this);	

		this.layout();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent evt) {
		if (evt.getSource().equals(buttonMore)) {
			if (listMutator != null)
				listMutator.addItemEditor(itemList);
		}
		if (evt.getSource().equals(buttonSearch)) {
			if (listMutator != null)
				resultFetcher.searchTriggered(searchFilterProvider,login);
		}
	}
	
	public void clearItemList() {
		itemList.clear();
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public abstract SearchFilterItemList createSearchFilterItemList(Composite parent, int style);
	
	public SearchFilterItemList getItemList() {
		return itemList;
	}
	
	public int getConjuction() {
		if (radioMatchAll.getSelection())
			return SearchFilter.CONJUNCTION_AND;
		else if (radioMatchAny.getSelection())
			return SearchFilter.CONJUNCTION_OR;
		else
			return SearchFilter.CONJUNCTION_DEFAULT;
	}
}
