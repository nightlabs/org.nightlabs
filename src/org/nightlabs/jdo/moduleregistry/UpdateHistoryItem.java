package org.nightlabs.jdo.moduleregistry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jdo.moduleregistry.id.UpdateHistoryItemID;
import org.nightlabs.util.Util;

@PersistenceCapable(
		objectIdClass=UpdateHistoryItemID.class,
		identityType=IdentityType.APPLICATION,
		detachable="true",
		table="NightLabsJDO_UpdateHistoryItem"
)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Queries(
		@Query(
				name="getUpdateHistoryItemsForModuleIDAndUpdateHistoryItemID",
				value="SELECT WHERE this.moduleID == :moduleID && this.updateHistoryItemID == :updateHistoryItemID ORDER BY resumeID ASCENDING"
		)
)
public class UpdateHistoryItem
implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static List<UpdateHistoryItem> getUpdateHistoryItems(PersistenceManager pm, String moduleID, String updateHistoryItemID)
	{
		if (pm == null)
			throw new IllegalArgumentException("pm must not be null!");

		if (moduleID == null)
			throw new IllegalArgumentException("moduleID must not be null!");

		if (updateHistoryItemID == null)
			throw new IllegalArgumentException("updateHistoryItemID must not be null!");

		javax.jdo.Query q = pm.newNamedQuery(UpdateHistoryItem.class, "getUpdateHistoryItemsForModuleIDAndUpdateHistoryItemID");
		try {
			@SuppressWarnings("unchecked")
			List<UpdateHistoryItem> result = (List<UpdateHistoryItem>) q.execute(moduleID, updateHistoryItemID);
			result = new ArrayList<UpdateHistoryItem>(result);
			return result;
		} finally {
			q.closeAll();
		}
	}

	public static UpdateNeededHandle updateNeeded(PersistenceManager pm, String moduleID, String updateHistoryItemID)
	{
		if (isUpdateDone(pm, moduleID, updateHistoryItemID))
			return null;

		return new UpdateNeededHandle(pm, moduleID, updateHistoryItemID);
	}

	private static boolean isUpdateDone(PersistenceManager pm, String moduleID, String updateHistoryItemID)
	{
		List<UpdateHistoryItem> updateHistoryItems = getUpdateHistoryItems(pm, moduleID, updateHistoryItemID);
		boolean result = false;
		for (Iterator<UpdateHistoryItem> it = updateHistoryItems.iterator(); it.hasNext(); ) {
			UpdateHistoryItem updateHistoryItem = it.next();
			if (updateHistoryItem.getEndTimestamp() != null) {
				result = true;

				// Sanity check: This should be the last entry!
				if (it.hasNext())
					throw new IllegalStateException(
							"The UpdateHistoryItem with moduleID='" + updateHistoryItem.getModuleID() + "' updateHistoryItemID='" + updateHistoryItem.getUpdateHistoryItemID() + "' resumeID='" + updateHistoryItem.getResumeID() + "' " +
							"contains an endTimestamp, but is not the last one for the given moduleID and updateHistoryItemID!!!"
					);

				break;
			}
		}
		return result;
	}

	public static UpdateHistoryItem updateDone(UpdateNeededHandle handle)
	{
		if (handle == null)
			throw new IllegalArgumentException("handle must not be null!");

		PersistenceManager pm = handle.getPersistenceManager();
		String moduleID = handle.getModuleID();
		String updateHistoryItemID = handle.getUpdateHistoryItemID();

		int resumeID = 1;
		List<UpdateHistoryItem> updateHistoryItems = getUpdateHistoryItems(pm, moduleID, updateHistoryItemID);
		if (!updateHistoryItems.isEmpty())
			resumeID = updateHistoryItems.get(updateHistoryItems.size() - 1).getResumeID() + 1;

		UpdateHistoryItem updateHistoryItem = new UpdateHistoryItem(moduleID, updateHistoryItemID, resumeID);
		updateHistoryItem.setEndTimestamp(updateHistoryItem.getBeginTimestamp());
		updateHistoryItem = pm.makePersistent(updateHistoryItem);
		return updateHistoryItem;
	}

	@PrimaryKey
	@Column(name="module_id", length=100)
	private String moduleID;

	@PrimaryKey
	@Column(name="update_history_item_id", length=255)
	private String updateHistoryItemID;

	@PrimaryKey
	@Column(name="resume_id")
	private int resumeID;

	@Persistent(nullValue=NullValue.EXCEPTION)
	@Column(name="begin_timestamp")
	private Date beginTimestamp;

	@Persistent()
	@Column(name="end_timestamp")
	private Date endTimestamp;

	/**
	 * @deprecated Only for JDO!
	 */
	@Deprecated
	protected UpdateHistoryItem() { }

	public UpdateHistoryItem(String moduleID, String updateHistoryItemID, int resumeID) {
		ObjectIDUtil.assertValidIDString(moduleID, "moduleID");
		ObjectIDUtil.assertValidIDString(updateHistoryItemID, "updateHistoryItemID");
		if (resumeID < 1)
			throw new IllegalArgumentException("resumeID < 1");

		this.moduleID = moduleID;
		this.updateHistoryItemID = updateHistoryItemID;
		this.resumeID = resumeID;
		this.beginTimestamp = new Date();
	}

	public String getModuleID() {
		return moduleID;
	}

	public String getUpdateHistoryItemID() {
		return updateHistoryItemID;
	}

	public int getResumeID() {
		return resumeID;
	}

	public Date getBeginTimestamp() {
		return beginTimestamp;
	}

	public Date getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(Date endTimestamp) {
		if (endTimestamp == null)
			throw new IllegalArgumentException("endTimestamp must not be null!");

		if (this.endTimestamp != null)
			throw new IllegalStateException("endTimestamp has already been set and cannot be overwritten!");

		this.endTimestamp = endTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleID == null) ? 0 : moduleID.hashCode());
		result = prime * result + ((updateHistoryItemID == null) ? 0 : updateHistoryItemID.hashCode());
		result = prime * result + resumeID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		UpdateHistoryItem other = (UpdateHistoryItem) obj;
		return (
				Util.equals(this.resumeID, other.resumeID) &&
				Util.equals(this.updateHistoryItemID, other.updateHistoryItemID) &&
				Util.equals(this.moduleID, other.moduleID)
		);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + moduleID + ',' + updateHistoryItemID + ',' + resumeID + ']';
	}
}
