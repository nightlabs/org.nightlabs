/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionUtil
{

	public SelectionUtil() {
		super();
	}
	
	/**
	 * 
	 * @param selection the ISelection to check
	 * @param clazz the Class which determines which entries are allowed
	 * @return a StructuredSelection which contains only Objects from the given
	 * selection of the given Class
	 */
	public static StructuredSelection checkSelection(ISelection selection, Class clazz) 
	{
		if (!selection.isEmpty() && selection instanceof StructuredSelection) 
		{
			List list = new ArrayList();
			boolean containsOther = false;
			StructuredSelection s = (StructuredSelection) selection;
			for (Iterator it = s.iterator(); it.hasNext(); ) 
			{
				Object o = it.next();
				if (clazz.isAssignableFrom(o.getClass())) {
					list.add(o);
				} else {
					containsOther = true;
				}
			}
			if (!containsOther)
				return s;
			else
				return new StructuredSelection(list);
		}
		return StructuredSelection.EMPTY;
	}

	/**
	 * 
	 * @param l the list to check
	 * @param clazz the Class which determines which entries are allowed
	 * @return a List which contains only Objects which are assignable from
	 * the given Class and were contained in the given list
	 */
	public static List checkList(List l, Class clazz) 
	{
		List list = new ArrayList();
		boolean containsOther = false;		
		for (Iterator it = list.iterator(); it.hasNext(); ) 
		{
			Object o = it.next();
			if (clazz.isAssignableFrom(o.getClass())) {
				list.add(o);
			} else {
				containsOther = true;
			}
		}
		if (!containsOther)
			return l;
		else
			return list;				
	}
	
}
