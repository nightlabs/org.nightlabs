/*
 * Created on Oct 3, 2005
 */
package org.nightlabs.base.action.registry;

public class GroupMarkerDescriptor extends ItemDescriptor
{
	private String name;
	/**
	 * The path is only used, if this is declared on toplevel (not within a menu).
	 */
	private String path;

	public GroupMarkerDescriptor(String name)
	{
		this.name = name;
	}
	public GroupMarkerDescriptor(String name, String path)
	{
		this.name = name;
		this.path = path;
	}
	public String getName()
	{
		return name;
	}
	public String getPath()
	{
		return path;
	}
}
