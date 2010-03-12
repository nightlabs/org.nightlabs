package org.nightlabs.jdo.moduleregistry;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.nightlabs.jdo.moduleregistry.id.UpdateHistoryItemID;

@PersistenceCapable(
		objectIdClass=UpdateHistoryItemID.class,
		identityType=IdentityType.APPLICATION,
		detachable="true",
		table="NightLabsJDO_UpdateHistoryItem"
)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class UpdateHistoryItem
implements Serializable
{
	private static final long serialVersionUID = 1L;

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
}
