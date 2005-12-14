/*
 * Created on Jun 1, 2005
 *
 */
package org.nightlabs.base.entitylist;

import java.util.Collection;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Table;

/**
 * EntityLists are used to display lists of certain entities. These are 
 * managed by {@link org.nightlabs.base.entitylist.EntityManagementView}s
 * and EntityManagementComposites.
 * Lists are registered by the extension-point "org.nightlabs.base.entitylist"
 * where you have to specifiy an implementation of this interface and
 * a view that should manage this list.
 * 
 * @see org.nightlabs.base.entitylist.EntityManagementView
 * @see org.nightlabs.base.entitylist.EntityManagementOrdinaryComposite
 * @see org.nightlabs.base.entitylist.EntityManagementTightComposite
 * 
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface EntityList 
{
	/**
	 * Get the unique list id
	 * @return the list id
	 */
	public String getID();
	
	/**
	 * get a label for the selection
	 * @return the label for this list
	 */
	public String getLabel();

	/**
   * This should return a collection of objects that can be managed by 
   * the EntityManager registered with this list (e.g. a user list)
   * @return a Collection of entity objects
   */
  public Collection getEntities();

  /**
   * This should return a lable provider for the entities in this list
   * @return the label provider
   */
  public ITableLabelProvider getLabelProvider();
	
	/**
	 * Should add as many columns to the table the
	 * ITableLabelProvider expects. Additionally a
	 * TableLayout should be added to the table
	 * when more than one column is added.
	 *  
	 * @param table The cleared Table
	 */
	public void addTableColumns(Table table);
  
}
