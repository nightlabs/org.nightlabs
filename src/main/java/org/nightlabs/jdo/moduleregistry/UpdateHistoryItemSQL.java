package org.nightlabs.jdo.moduleregistry;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nightlabs.jdo.moduleregistry.id.UpdateHistoryItemID;

/**
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class UpdateHistoryItemSQL {
	private Connection connection;

	private String moduleID;
	private String updateHistoryItemID;
	private boolean endUpdateCalled = false;

	private UpdateHistoryItemID updateHistoryItemID_updateInProgress = null;

	/**
	 * @param moduleID the moduleID - same as in {@link ModuleMetaData} and {@link UpdateHistoryItem}.
	 * @param updateHistoryItemID the ID of the {@link UpdateHistoryItem} to be written into the database.
	 */
	public UpdateHistoryItemSQL(Connection connection, String moduleID, String updateHistoryItemID)
	throws SQLException
	{
		if (connection == null)
			throw new IllegalArgumentException("connection must not be null!");

		this.connection = connection;
		connection.setAutoCommit(true);
		this.moduleID = moduleID;
		this.updateHistoryItemID = updateHistoryItemID;
	}

	public String getModuleID() {
		return moduleID;
	}
	public String getUpdateHistoryItemID() {
		return updateHistoryItemID;
	}

	public boolean isUpdateDone()
	throws SQLException
	{
		boolean result = false;

		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT resume_id, end_timestamp " +
				"FROM nightlabsjdo_updatehistoryitem " +
				"WHERE module_id = ? AND update_history_item_id = ? " +
				"ORDER BY resume_id"
		);
		try {
			preparedStatement.setString(1, moduleID);
			preparedStatement.setString(2, updateHistoryItemID);
			ResultSet resultSet = preparedStatement.executeQuery();

			// If there is no entry at all, the update was not even started, thus the result stays false.
			// If there are entries, the update is only done, if there is one entry with an end-timestamp.
			while (resultSet.next()) {
				int resumeID = resultSet.getInt("resume_id");
				Date endTimestamp = resultSet.getDate("end_timestamp");
				if (endTimestamp != null) {
					result = true;

					// Sanity check: This should be the last entry!
					if (resultSet.next())
						throw new IllegalStateException(
								"The UpdateHistoryItem with moduleID='" + moduleID + "' updateHistoryItemID='" + updateHistoryItemID + "' resumeID='" + resumeID + "' " +
								"contains an endTimestamp, but is not the last one for the given moduleID and updateHistoryItemID!!!"
						);

					// Since a following call to resultSet.next() might cause an SQLException depending on the driver vendor
					// according to the javadoc, we have to break here and prevent calling resultSet.next() again!
					break;
				}
			}
		} finally {
			preparedStatement.close();
		}
		return result;
	}

	/**
	 * Begins an update. This will cause a commit on the underlying database {@link Connection} after an INSERT was
	 * done into the table 'NightLabsJDO_UpdateHistoryItem'. Additionally, the underlying connection is set
	 * to auto-commit = <code>false</code>.
	 *
	 * @return the complete ID of the {@link UpdateHistoryItem} written to the database with a resumeID automatically determined
	 * (following directly the last used highest value). The first resumeID used is 1.
	 * @throws SQLException in case sth. goes wrong accessing the database.
	 */
	public UpdateHistoryItemID beginUpdate()
	throws SQLException
	{
		UpdateHistoryItemID result = UpdateHistoryItemID.create(moduleID, updateHistoryItemID, 1);

		if (updateHistoryItemID_updateInProgress != null)
			throw new IllegalStateException("beginUpdate() was already called on this instance!!!");

		connection.setAutoCommit(false);
		boolean rollback = true;
		try {
			if (isUpdateDone())
				throw new IllegalStateException("Cannot begin update with moduleID='" + moduleID + "' updateHistoryItemID='" + updateHistoryItemID + "', because this update is already done!");


			// According to http://www.w3schools.com/sql/sql_func_max.asp the MAX() function with AS keyword is ANSI standard.
			PreparedStatement preparedStatement = connection.prepareStatement(
					"SELECT MAX(resume_id) AS max_resume_id " +
					"FROM nightlabsjdo_updatehistoryitem " +
					"WHERE module_id = ? AND update_history_item_id = ?"
			);
			try {
				preparedStatement.setString(1, result.moduleID);
				preparedStatement.setString(2, result.updateHistoryItemID);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) { // Maybe some drivers return no record at all, if there is no record at all.
					// According to javadoc, resultSet.getInt("max_resume_id") would be 0, if the SQL value is NULL, which is not fine.
					// We could, of course, directly SELECT MAX(resume_id) + 1 AS next_resume_id, however, it might not be as portable,
					// depending on how the database server behaves, if there is no entry.
					// Thus, we use this way, which might not be as effective, but should work with all DB servers/drivers.
					Object maxResumeIDObj = resultSet.getObject("max_resume_id");
					if (maxResumeIDObj != null) { // we leave our result's resumeID as is (= 0), if we have no value here.
						if (!(maxResumeIDObj instanceof Number))
							throw new IllegalStateException("resultSet.getObject(\"max_resume_id\") returned an object of a type not implementing Number: " + maxResumeIDObj.getClass() + ": " + maxResumeIDObj);

						result.resumeID = ((Number)maxResumeIDObj).intValue() + 1;
						if (result.resumeID > Integer.MAX_VALUE - 10)
							throw new IllegalStateException("Too many resumes!!! Range overflow!");
					}
				}

			} finally {
				preparedStatement.close();
			}

			preparedStatement = connection.prepareStatement(
					"INSERT INTO nightlabsjdo_updatehistoryitem (module_id, update_history_item_id, resume_id, begin_timestamp) " +
					"VALUES (?, ?, ?, ?)"
			);
			try {
				preparedStatement.setString(1, result.moduleID);
				preparedStatement.setString(2, result.updateHistoryItemID);
				preparedStatement.setInt(3, result.resumeID);
				preparedStatement.setDate(4, new Date(System.currentTimeMillis()));
				preparedStatement.executeUpdate();
			} finally {
				preparedStatement.close();
			}

			connection.commit();
			rollback = false;
		} finally {
			if (rollback)
				connection.rollback();
		}

		updateHistoryItemID_updateInProgress = result;
		return result;
	}

	public void endUpdate(boolean doCommit)
	throws SQLException
	{
		if (updateHistoryItemID_updateInProgress == null)
			throw new IllegalStateException("beginUpdate() was not called on this instance!!!");

		if (endUpdateCalled)
			throw new IllegalStateException("endUpdate() was already called on this instance!!!");

		if (connection.getAutoCommit())
			throw new IllegalStateException("The underlying connection has auto-commit set to true! Why? You should not manipulate the auto-commit property yourself!");

		boolean rollback = true;
		try {
			if (isUpdateDone())
				throw new IllegalStateException("Cannot end update with moduleID='" + moduleID + "' updateHistoryItemID='" + updateHistoryItemID + "', because this update is already done!");

			PreparedStatement preparedStatement = connection.prepareStatement(
					"UPDATE nightlabsjdo_updatehistoryitem SET end_timestamp = ? " +
					"WHERE module_id = ? AND update_history_item_id = ? AND resume_id = ? AND end_timestamp IS NULL"
			);
			try {
				preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
				preparedStatement.setString(2, updateHistoryItemID_updateInProgress.moduleID);
				preparedStatement.setString(3, updateHistoryItemID_updateInProgress.updateHistoryItemID);
				preparedStatement.setInt(4, updateHistoryItemID_updateInProgress.resumeID);
				int affectedRowCount = preparedStatement.executeUpdate();
				if (affectedRowCount != 1)
					throw new IllegalStateException("UPDATE did not affect exactly 1 row, but " + affectedRowCount + " rows!");
			} finally {
				preparedStatement.close();
			}
			
			if (doCommit) {
				connection.commit();
			} else {
				connection.rollback();
			}
			rollback = false;
		} finally {
			if (rollback)
				connection.rollback();
		}

		connection.setAutoCommit(true);
		endUpdateCalled = true;
	}
}
