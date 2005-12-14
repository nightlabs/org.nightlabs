/*
 * Created on May 31, 2005
 *
 */
package org.nightlabs.base.entitylist;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public class EntityListContentProvider implements IStructuredContentProvider 
{

  public Object[] getElements(Object inputElement) 
	{
  	if(inputElement instanceof Collection)
  	{
  		ArrayList arr = new ArrayList((Collection)inputElement);
  		return arr.toArray();
  	}
  	return null;
	}

	public void dispose() 
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{
	}

}
