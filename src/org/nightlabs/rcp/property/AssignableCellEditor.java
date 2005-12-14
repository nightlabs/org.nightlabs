/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.util.IAssignable;

public class AssignableCellEditor 
extends AbstractComboBoxCellEditor
{
	/**
	 * The IAssignable to edit
	 * @see IAssignable
	 */
	protected Collection assignables = null;
	
	public AssignableCellEditor(Collection assignables, Composite parent) 
	{
		this(assignables, parent, SWT.READ_ONLY);		
	}	
	
	public AssignableCellEditor(Collection assignables, Composite parent, int style) 
	{
		super(parent, style); 
		this.assignables = assignables;
		populateComboBoxItems();
	}
	
	protected Map index2Assignable = null;
	protected String items[] = null;
	protected String[] getItems() {
		return items;
	}
	
//  protected void populateComboBoxItems() 
//  {
//  	index2Assignable = new HashMap();
//  	items = new String[assignables.size()];
//    if (getComboBox() != null && assignables != null) 
//    {
//    	getComboBox().removeAll();      
//      int index = 0;
//      for (Iterator it = assignables.iterator(); it.hasNext(); ) 
//      {
//  			Object o = it.next();
//  			if (o instanceof IAssignable) {
//  				IAssignable assignable = (IAssignable) o;
//  				getComboBox().add(assignable.getName(), index);
//  				items[index] = assignable.getName(); 
//  				index2Assignable.put(new Integer(index), assignable);
//  				index++;
//  			}
//  		}
//      setValueValid(true);
//      selection = 0;
//    }
//  }
  protected void populateComboBoxItems() 
  {
  	index2Assignable = new HashMap();
  	Map id2Assignable = AssignablePropertyDescriptor.createID2Assignables(assignables);
  	
  	items = new String[assignables.size()];
    if (getComboBox() != null && assignables != null) 
    {
    	getComboBox().removeAll();      
      int index = 0;
      for (Iterator it = id2Assignable.keySet().iterator(); it.hasNext(); ) 
      {
  			Object key = it.next();
  			Object o = id2Assignable.get(key);
  			if (o instanceof IAssignable) {
  				IAssignable assignable = (IAssignable) o;
  				getComboBox().add(assignable.getName(), index);
  				items[index] = assignable.getName(); 
  				index2Assignable.put(new Integer(index), assignable);
  				index++;
  			}
  		}
      setValueValid(true);
      selection = 0;
    }
  }	
	
  protected int getID(int selectionIndex) 
  {
  	IAssignable assignable = (IAssignable) index2Assignable.get(new Integer(selectionIndex));
  	if (assignable != null)
  		return assignable.getId();
  	else
  		return 0;
  }
  
  /**
   * return the ID of the selected Assignable
   * @see IAssignable
   */
	protected Object getReturnValue() 
	{
		return new Integer(getID(getComboBox().getSelectionIndex()));
	}

}
