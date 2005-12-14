/*
 * Created on Oct 3, 2005
 */
package org.nightlabs.rcp.action.registry;

import java.util.ArrayList;
import java.util.List;

public class MenuDescriptor extends ItemDescriptor
{
	private String id;
	private String label;
	private String path;

	/**
	 * A List with instances of either SeparatorDescriptor or GroupMarkerDescriptor
	 */
	private List subItems = new ArrayList();

	public void addSubItem(ItemDescriptor item)
	{
		subItems.add(item);
	}

	/**
	 * @return Returns instances of {@link ItemDescriptor}.
	 */
	public List getSubItems()
	{
		return subItems;
	}

	public MenuDescriptor(String id, String label, String path)
	{
		this.id = id;
		this.label = label;
		this.path = path;
	}
	public String getId()
	{
		return id;
	}
	public String getLabel()
	{
		return label;
	}
	public String getPath()
	{
		return path;
	}
}
