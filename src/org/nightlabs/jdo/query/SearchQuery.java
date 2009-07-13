package org.nightlabs.jdo.query;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;

/**
 * Defines all methods necessary to search for the given result type.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public interface SearchQuery
{
	/**
	 * The actual search is done in this method and the result is returned.
	 *
	 * @return Returns the result of the query. Never returns <code>null</code>!
	 */
	Collection<?> getResult();

	/**
	 * Sets the candidates from which to filter the possible result.
	 * @param candidates the candidates from which to filter the possible result.
	 */
	void setCandidates(Collection<?> candidates);

	/**
	 * Returns the candidates from which this query will filter the possible result.
	 * @return the candidates from which this query will filter the possible result.
	 */
	Collection<?> getCandidates();

	/**
	 * Returns a list of {@link FieldChangeCarrier}s that are combined by the given
	 * <code>propertyName</code>.
	 *
	 * @param propertyName the name of the property for which to return all changed fields.
	 * @return a list of {@link FieldChangeCarrier}s that are combined by the given
	 * <code>propertyName</code>.
	 */
	List<FieldChangeCarrier> getChangedFields(String propertyName);

	/**
	 * Adds the given listener to my list of listeners, if not already in the list.
	 * @param listener the listener to add.
	 */
	void addQueryChangeListener(PropertyChangeListener listener);

	/**
	 * Removes the given listener from my list of listeners.
	 * @param listener the listener to remove.
	 */
	void removeQueryChangeListener(PropertyChangeListener listener);

	/**
	 * Returns a backup of the fields' enable states that belong the a group with a unique ID. <br />
	 * <p>Note: The backup is removed after the retrieval. See
	 * {@link AbstractSearchQuery#storeBackupOfGroupFields(String, Map)}</p>
	 *
	 * @param groupID the ID of the UI group for which the active states of all contained elements
	 * 	shall be retrieved.
	 * @return a backup of the fields' enable states that belong the a group with a unique ID.
	 */
	Map<String, Boolean> getBackupOfGroupFields(String groupID);

	/**
	 * Stores a snapshot of the current enable state of the fields with the given
	 * <code>fieldNames</code>.
	 * <p><b>Note</b>: The <code>fieldNames</code> are passed to {@link AbstractSearchQuery#getEnabledFieldName(String)}
	 * 	in order to retrieve the fieldName for the enabled flag of the corresponding fieldName. So
	 * 	they must NOT be already parsed via this method!
	 * </p>
	 *
	 * @param groupID The ID of the group under which the backup will be stored.
	 * @param fieldNames The names of the fields whose enabled state shall be stored.
	 */
	void storeBackupOfGroupFieldsEnabledState(String groupID, Set<String> fieldNames);

	/**
	 * Restores a previously stored backup of active fields of a group with given <code>groupID</code>.
	 *
	 * @param groupID The identifier of the group whose backup of active fields shall be restored.
	 */
	void restoreBackupOfGroup(String groupID);

	/**
	 * Returns the current active state of the field with the given <code>fieldName</code>.
	 * <p><b>Important</b>: If the Query is initialised the enable fields are all <code>null</code>.
	 * To maintain compatibility with the older UI, we interpret a non-set enable field as
	 * <code>true</code>! </p>
	 * <p><b>Note</b>: You can either pass the the field name for the corresponding enable field, or
	 * you can pass the field name of the enable field (original field name +
	 * {@link AbstractSearchQuery#getEnabledFieldName(String)}).</p>
	 *
	 * @param fieldName The name of the field for which the current enable state shall be returned.
	 * @return the current active state of the field with the given <code>fieldName</code>.
	 */
	boolean isFieldEnabled(String fieldName);

	/**
	 * Sets all enable flags of all query fields to <code>false</code>. This might be necessary
	 * since, any entry that is not found in the {@link AbstractSearchQuery#fieldsEnabledMap} is
	 * assumed to be true. Any newly created Query will therefore seem to have all fields activated.
	 */
	void setAllFieldsDisabled();

	/**
	 * Sets the field with the given <code>fieldName</code> to the given <code>enabled</code> state.
	 * <p><b>Note</b>: It is assumed that the given fieldName is not already appended with the
	 * EnableField suffix, hence no call to {@link AbstractSearchQuery#getEnabledFieldName(String)}
	 * has been made.</p>
	 *
	 * @param fieldName The name of the field for which the enabled state shall change.
	 * @param enabled the new state the corresponding field shall be set to.
	 */
	public void setFieldEnabled(String fieldName, boolean enabled);

	/**
	 * Sets the active state of the fields given as keys to the values given as values of the
	 * <code>field2EnableStateMap</code>.
	 * <p><b>Note</b>: It is assumed that the given fieldNames are not already appended with the
	 * EnableField suffix, hence no call to {@link AbstractSearchQuery#getEnabledFieldName(String)}
	 * has been made.</p>
	 *
	 * @param field2EnableStateMap contains all the identifier of the fields as keys and their enabled
	 * 	state as values.
	 */
	void setFieldsEnabled(Map<String, Boolean> field2EnableStateMap);

	/**
	 * Sets the <code>value</code> of the field specified by the given <code>fieldName</code>.
	 *
	 * @param fieldName The name of the field whose value shall be returned.
	 * @param value The new value to set.
	 */
	void setFieldValue(String fieldName, Object value);

	/**
	 * Returns the value of the field specified by the given <code>fieldName</code>.
	 *
	 * @param fieldName The name of the field whose value shall be returned.
	 * @return The value of the field specified by the given <code>fieldName</code> or <code>null</code>
	 * 	if no field with the given name can be found.
	 */
	Object getFieldValue(String fieldName);

	Map<String, Boolean> getFieldsEnabled();
}
