/*
 * Created on 05.11.2004
 *
 */
package org.nightlabs.base.entitylist;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import org.nightlabs.base.composite.XComposite;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class DefaultEntityListView 
	extends AbstractEntityListView  
	implements SelectionListener, EntityManagementViewStateChangedListener
{
  private TableViewer viewer;
  private Combo filterCombo;
  
  private EntityListContentProvider contentProvider;
  private ArrayList selections;
  private class EntitySelectionListener implements ISelectionChangedListener
	{
  	private DefaultEntityListView mother;
		private ISelectionChangedListener listChangeListener;
		
  	public EntitySelectionListener(DefaultEntityListView source)
  	{
  		mother = source;
			this.listChangeListener =  mother.createListChangeListener();
  	}
  	public void selectionChanged(SelectionChangedEvent event) 
  	{
      ISelection selection = event.getSelection();
      if(!selection.isEmpty())
      	mother.entityListSelected(mother.getSelectedListID());
			
			if (listChangeListener != null)
				listChangeListener.selectionChanged(event);
  	}
  }
  private class EntityManagementViewListener implements IPartListener2 
	{
  	private DefaultEntityListView elv;
  	public EntityManagementViewListener(DefaultEntityListView elv)
  	{
  		this.elv = elv;
  	}
  	
  	public void partActivated(IWorkbenchPartReference partRef) 
  	{
  		Object o = partRef.getPart(false);
  		if(o instanceof EntityManagementView)
  			elv.viewActivated(partRef.getId());
  	}

  	public void partBroughtToTop(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : brought to top");
  	}

  	public void partClosed(IWorkbenchPartReference partRef) 
  	{
  	}

  	public void partDeactivated(IWorkbenchPartReference partRef) 
  	{
  	}

  	public void partOpened(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : opened");
  	}

  	public void partHidden(IWorkbenchPartReference partRef) 
  	{
  	}

  	public void partVisible(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : visible");
  	}

  	public void partInputChanged(IWorkbenchPartReference partRef) 
  	{
  	}

  }
  private EntitySelectionListener eSelectionListener;
  
  public DefaultEntityListView()
  {
  	super();
  	selections = new ArrayList();
  }

  public void createEntityListControl(Composite parent) {
//    GridLayout gridLayout = new GridLayout();
//    gridLayout.numColumns = 2;
//    
//    TabFolder composite = new TabFolder(parent, SWT.NULL);
//    composite.setLayout(gridLayout);

  	XComposite wrapper = new XComposite(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
    filterCombo = new Combo(wrapper, SWT.READ_ONLY);
    filterCombo.addSelectionListener(this);
    
    viewer = new TableViewer(wrapper, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    eSelectionListener = new EntitySelectionListener(this);
    viewer.addSelectionChangedListener(eSelectionListener);
    getSite().setSelectionProvider(viewer);

    contentProvider = new EntityListContentProvider();
    viewer.setContentProvider(contentProvider);
    
    
    GridData tgd = new GridData(GridData.FILL_BOTH);
//    tgd.horizontalSpan = 2;
//    tgd.verticalSpan = 1;
    
    Table t = viewer.getTable(); 
    t.setHeaderVisible(true);
    t.setLinesVisible(true);
    t.setLayoutData(tgd);
	}

  public void setFocus()
  {
  	if (filterCombo != null && !filterCombo.isDisposed())
  		filterCombo.setFocus();
  }
	
	private static Collection EMPTY_COLLECTION = new ArrayList();
	
  public void refresh()
  {
  	if (filterCombo.getSelectionIndex() < 0) {
  		if (filterCombo.getItemCount() > 0)
  			filterCombo.select(0);
  		else
  			return;
  	}
  	String key = (String)selections.get(filterCombo.getSelectionIndex());
  	EntityList el = (EntityList)getEntityLists().get(key);
  	if(el != null)
  	{
			viewer.setInput(EMPTY_COLLECTION);
  		viewer.setLabelProvider(el.getLabelProvider());
			TableColumn[] columns = viewer.getTable().getColumns();
			for (int i = 0; i < columns.length; i++) {
				columns[i].dispose();
			}
			el.addTableColumns(viewer.getTable());
			viewer.getTable().layout();
  		viewer.setInput(el.getEntities());
  		viewer.refresh();
  	}
  }

  public String getSelectedListID()
  {
  	if (filterCombo.getSelectionIndex() < 0) {
  		if (filterCombo.getItemCount() > 0)
  			filterCombo.select(0);
  		else
  			return null;
  	}
  	return (String)selections.get(filterCombo.getSelectionIndex());
  }
	
	
  

	protected void entityListAdded(EntityList el) {
		filterCombo.add(el.getLabel());
		selections.add(el.getID());
		
	}



	public void setSelectedEntityList(String entityListID) {
		if (!canDisplayView())
			return;
		if ((filterCombo.getSelectionIndex() < 0) && (getEntityListCount() > 0))
			filterCombo.select(0);
		if(filterCombo.getSelectionIndex() != selections.indexOf(entityListID))
			filterCombo.select(selections.indexOf(entityListID));
		refresh();
	}



	// combo selection - begin
  public void widgetSelected(SelectionEvent arg0)
  {
    Combo c = (Combo)arg0.getSource();
		String entityListID = (String)selections.get(c.getSelectionIndex()); 
		setSelectedEntityList(entityListID);
		showManagerViews(entityListID);
  }

  public void widgetDefaultSelected(SelectionEvent arg0)
  {
  }
  // combo selection - end

  public void dispose()
  {
  	super.dispose();
  	viewer.removeSelectionChangedListener(eSelectionListener);
  }
}
