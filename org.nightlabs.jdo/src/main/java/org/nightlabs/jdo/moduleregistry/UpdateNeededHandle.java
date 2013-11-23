package org.nightlabs.jdo.moduleregistry;

import javax.jdo.PersistenceManager;

public class UpdateNeededHandle
{
	private PersistenceManager pm;
	private String moduleID;
	private String updateHistoryItemID;

	protected UpdateNeededHandle(PersistenceManager pm, String moduleID, String updateHistoryItemID) {
		this.pm = pm;
		this.moduleID = moduleID;
		this.updateHistoryItemID = updateHistoryItemID;
	}

	protected PersistenceManager getPersistenceManager()
	{
		return pm;
	}

	protected String getModuleID() {
		return moduleID;
	}

	protected String getUpdateHistoryItemID() {
		return updateHistoryItemID;
	}
}
