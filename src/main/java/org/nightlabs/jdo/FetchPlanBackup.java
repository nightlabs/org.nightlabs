/**
 * 
 */
package org.nightlabs.jdo;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

public class FetchPlanBackup
{
	@SuppressWarnings("unchecked")
	public FetchPlanBackup(FetchPlan fetchPlan)
	{
		this.maxFetchDepth = fetchPlan.getMaxFetchDepth();
		this.groups = new HashSet<String>(fetchPlan.getGroups());
		this.detachmentOptions = fetchPlan.getDetachmentOptions();
		this.fetchSize = fetchPlan.getFetchSize();
//		this.detachmentRootClasses = fetchPlan.getDetachmentRootClasses();
//		this.detachmentRoots = fetchPlan.getDetachmentRoots();
	}

	private int detachmentOptions;
	private Set<String> groups;
	private int fetchSize;
	private int maxFetchDepth;

//	Caused by: org.jpox.exceptions.JPOXUserException: Detachment roots cannot be changed once set until commit is reached.
//	at org.jpox.FetchPlan.setDetachmentRoots(FetchPlan.java:253)
//	at org.jpox.jdo.JDOFetchPlan.setDetachmentRoots(JDOFetchPlan.java:238)
//	at org.nightlabs.jdo.NLJDOHelper.restoreFetchPlan(NLJDOHelper.java:84)
//	@SuppressWarnings("unchecked")
//	private Class[] detachmentRootClasses;
//	@SuppressWarnings("unchecked")
//	private Collection detachmentRoots;

	public int getDetachmentOptions()
	{
		return detachmentOptions;
	}
//	@SuppressWarnings("unchecked")
//	public Class[] getDetachmentRootClasses()
//	{
//		return detachmentRootClasses;
//	}
//	@SuppressWarnings("unchecked")
//	public Collection getDetachmentRoots()
//	{
//		return detachmentRoots;
//	}
	public Set<String> getGroups()
	{
		return groups;
	}
	public int getFetchSize()
	{
		return fetchSize;
	}
	public int getMaxFetchDepth()
	{
		return maxFetchDepth;
	}
}