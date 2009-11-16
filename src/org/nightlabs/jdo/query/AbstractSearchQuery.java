package org.nightlabs.jdo.query;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * The abstract base class of all {@link SearchQuery}s.
 *
 * <p><b>Important</b>: All subclasses have to have a static inner class called
 * '{@value #FIELD_CLASS_NAME}' that contains public static final string members, which have the
 * name of the corresponding field as value!</p>
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public abstract class AbstractSearchQuery
implements Serializable, SearchQuery
{
	private static final long serialVersionUID = 2008111800L;

	private transient Class<?> candidateClass;
	private String candidateClassName;
	private transient Class<?> resultClass;
	private String resultClassName;
	private transient Collection<?> candidates;
	protected long fromInclude = 0;
	protected long toExclude = Long.MAX_VALUE;

	// Property IDs used for the PropertyChangeListeners
	public static final String ENABLED_SUFFIX = ".enabled";
	public static final String PROPERTY_WHOLE_QUERY  = "wholeQuery";

	/**
	 * Static final class containing all field names.
	 */
	public static final class FieldName
	{
		public static final String fromInclude = "fromInclude";
		public static final String toExclude   = "toExclude";
	}

	/**
	 * Returns the identifier of the enabled field of the given <code>fieldName</code>.
	 *
	 * @param fieldName the fieldName of the field for whose enabled field to return the identifier.
	 * @return the identifier of the enabled field of the given <code>fieldName</code>.
	 */
	public static final String getEnabledFieldName(String fieldName)
	{
		if (fieldName == null)
			return null;

		if (! fieldName.endsWith(ENABLED_SUFFIX))
			return fieldName += ENABLED_SUFFIX;

		return fieldName;
	}

	/**
	 * The logger instance used in this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractSearchQuery.class);

	public AbstractSearchQuery()
	{
		candidateClass = initCandidateClass();
		resultClass = initResultClass();
		assert candidateClass != null;
		assert resultClass != null;
		candidateClassName = candidateClass.getName();
		resultClassName = resultClass.getName();
		pcs = new PropertyChangeSupport(this);
	}

	protected abstract Class<?> initResultClass();

	/**
	 * This method has to return the runtime class of the declared result type and may do additional
	 * initialisation.
	 */
	protected abstract Class<?> initCandidateClass();

	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.query.SearchQuery#executeQuery()
	 */
	public abstract Collection<?> getResult();

	/**
	 * Performs setting the default values to all fields in the query.
	 */
	public void clearQuery() {
		//-----------------Set All Fields Enabled First----------------Maybe we should find other ways to do this
		//------------------------------------------------------------Because the listeners won't be triggered if it has the same values!!!!
		for (String fieldName : getFieldName2FieldMap().keySet())
		{
			Field field = getFieldName2FieldMap().get(fieldName);
//			TODO: What was this supposed to mean? And please read the javadoc of the methods you use!
//			      The getEneabledFieldName(fieldName) was unnecessary for setFieldEnabled(...)
//			fieldsEnabledMap.put(getEnabledFieldName(fieldName), Boolean.TRUE);
			setFieldEnabled(fieldName, false);
			if (!field.getType().isPrimitive())
			{
				final Object oldValue = getFieldValue(fieldName);
				setFieldValue(fieldName, null);
				notifyListeners(fieldName, oldValue, null);
			}
			else
			{
				logger.error("Cannot reset a query field value, since it is of primitive type! Query class: "+
						getClass().getName() + ", fieldName='"+fieldName+"'.");
			}
		}
	}

	/**
	 * Returns the candidates from which this query will filter the possible result.
	 * @return the candidates from which this query will filter the possible result.
	 */
	@Override
	public Collection<?> getCandidates()
	{
		return candidates;
	}

	/**
	 * Sets the candidates from which to filter the possible result.
	 * @param candidates the candidates from which to filter the possible result.
	 */
	@Override
	public void setCandidates(Collection<?> candidates)
	{
		this.candidates = candidates;
	}

	/**
	 * Returns the class of the result type.
	 * @return the class of the result type.
	 */
	protected Class<?> getCandidateClass()
	{
		if (candidateClass == null)
		{
			candidateClass = instantiateClass(candidateClassName);
		}
		return candidateClass;
	}

	/**
	 * Returns the class of the result type.
	 * @return the class of the result type.
	 */
	public Class<?> getResultClass()
	{
		if (resultClass == null)
		{
			resultClass = instantiateClass(resultClassName);
		}
		return resultClass;
	}

	/**
	 * Helper that instantiates the Query relevant classes.
	 *
	 * @param <C> the type of class expected as return value.
	 * @param className the fully qualified class name.
	 * @return the runtime class object of the class corresponding to the given <code>className</code>.
	 */
	@SuppressWarnings("unchecked")
	private <C> Class<C> instantiateClass(String className)
	{
		try
		{
			return (Class<C>) Class.forName(className);
		} catch (ClassNotFoundException e)
		{
			// TODO: What if the current classloader does not know the candidateClass?
			throw new RuntimeException("The classloader assigned to this thread does not know " +
					"the given class!", e);
		}
	}

	/**
	 * returns the range beginning including the given value.
	 * @return the 0-based inclusive start index.
	 */
	protected long getFromInclude()
	{
		return fromInclude;
	}

	/**
	 * sets the the range beginning including the given value.
	 * @param fromInclude 0-based inclusive start index.
	 */
	protected void setFromInclude(long fromInclude)
	{
		final long oldInclude = this.fromInclude;
		this.fromInclude = fromInclude;
		notifyListeners(FieldName.fromInclude, oldInclude, fromInclude);
	}

	/**
	 * returns the range end excluding the given value.
	 * @return the 0-based exclusive end index, or {@link Long#MAX_VALUE} for no limit.
	 */
	protected long getToExclude()
	{
		return toExclude;
	}

	/**
	 * Sets the range end excluding the given value.
	 * @param toExclude 0-based exclusive end index, or {@link Long#MAX_VALUE} for no limit.
	 */
	protected void setToExclude(long toExclude)
	{
		final long oldToExclude = this.toExclude;
		this.toExclude = toExclude;
		notifyListeners(FieldName.toExclude, oldToExclude, toExclude);
	}

	/**
	 * Support object managing all property change listeners.
	 */
	private transient PropertyChangeSupport pcs;

	/**
	 * Internal method that shall be used to notify all pcs that I have changed.
	 */
	protected void notifyListeners(String propertyName, Object oldValue, Object newValue)
	{
		checkCreatePCS();
		final QueryEvent event = new QueryEvent(this, propertyName, oldValue, newValue);
		pcs.firePropertyChange(event);
	}

	/**
	 * Adds the given listener to my list of listeners, if not already in the list.
	 * @param listener the listener to add.
	 */
	@Override
	public void addQueryChangeListener(PropertyChangeListener listener)
	{
		checkCreatePCS();
		if (! Arrays.asList(pcs.getPropertyChangeListeners()).contains(listener))
		{
			pcs.addPropertyChangeListener(listener);
		}
	}

	/**
	 * Removes the given listener from my list of listeners.
	 * @param listener the listener to remove.
	 */
	@Override
	public void removeQueryChangeListener(PropertyChangeListener listener)
	{
		checkCreatePCS();
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Tests whether <code>pcs == null</code> and if instantiates a new PropertyChangeSupport.
	 */
	private void checkCreatePCS()
	{
		if (pcs == null)
			pcs = new PropertyChangeSupport(this);
	}

	/**
	 * key: String groupID <br/>
	 * value: backup of the fields' enable states that belong the a group with a unique ID.<br/>
	 *
	 * <p>This map is used in the UI to persist disabled properties of a query in order to be able to
	 * reenable them again once the user decides to do so.
	 * E.g. if the user selected a query configuration, then disables a facet of that configuration
	 * and stores it afterwards. Then he can now enable the disabled facet after loading the
	 * configuration and the properties that were enabled before disabling this facet are enabled
	 * again.</p>
	 */
	private Map<String, Map<String, Boolean>> groupID2fieldsEnabledBackup =
		new HashMap<String, Map<String,Boolean>>();

	/**
	 * Returns a backup of the fields' enable states that belong the a group with a unique ID. <br />
	 * <p>Note: The backup is removed after the retrieval. See
	 * {@link #storeBackupOfGroupFields(String, Map)}</p>
	 *
	 * @param groupID the ID of the UI group for which the active states of all contained elements
	 * 	shall be retrieved.
	 * @return a backup of the fields' enable states that belong the a group with a unique ID.
	 */
	public Map<String, Boolean> getBackupOfGroupFields(String groupID)
	{
		final Map<String, Boolean> result = groupID2fieldsEnabledBackup.remove(groupID);
		if (result == null)
			return Collections.emptyMap();

		return result;
	}

	/**
	 * Stores a backup of the fields' enable states that belong the a group with a unique ID. <br />
	 * <p>Note: A backup should only be stored after having been retrieved!</p>
	 *
	 * @param groupID The unique id of the UI group consisting of the fields whose active state shall
	 * 	be saved
	 * @param backup The mapping of the fields, in the group, to their active state.
	 */
	protected void storeBackupOfGroupFields(String groupID, Map<String, Boolean> backup)
	{
		assert groupID != null : "UI-groupIDs must NOT be null!";
		assert backup != null && !backup.isEmpty() : "If you want to backup some UI-enable states " +
		"for grouped elements, then don't give an empty backup!";

		final Map<String, Boolean> result = groupID2fieldsEnabledBackup.get(groupID);
		if (result != null)
		{
			logger.trace("A group's enable backup is about to be set, but there is still an existing one" +
					" with that identifier! This might be a groupID collision or the client is not obeying " +
					"the contract, that the backup should first be retrieved before a new one is set!",
					new Exception());
		}

		groupID2fieldsEnabledBackup.put(groupID, backup);
	}

	/**
	 * Stores a snapshot of the current enable state of the fields with the given
	 * <code>fieldNames</code>.
	 * <p><b>Note</b>: The <code>fieldNames</code> are passed to {@link #getEnabledFieldName(String)}
	 * 	in order to retrieve the fieldName for the enabled flag of the corresponding fieldName. So
	 * 	they must NOT be already parsed via this method!
	 * </p>
	 *
	 * @param groupID The ID of the group under which the backup will be stored.
	 * @param fieldNames The names of the fields whose enabled state shall be stored.
	 */
	public void storeBackupOfGroupFieldsEnabledState(String groupID, Set<String> fieldNames)
	{
		assert groupID != null && groupID.trim().length() != 0 : "UI-groupIDs must NOT be null or empty!";
		if (fieldNames == null || fieldNames.size() == 0)
			return;

		Map<String, Boolean> backup = new HashMap<String, Boolean>(fieldNames.size());
		for (String fieldName : fieldNames)
		{
			String fieldEnabledName = getEnabledFieldName(fieldName);
			backup.put(fieldEnabledName, isFieldEnabled(fieldEnabledName));
		}

		storeBackupOfGroupFields(groupID, backup);
	}

	/**
	 * Restores a previously stored backup of active fields of a group with given <code>groupID</code>.
	 *
	 * @param groupID The identifier of the group whose backup of active fields shall be restored.
	 */
	public void restoreBackupOfGroup(String groupID)
	{
		if (groupID == null || groupID.trim().length() == 0)
			return;

		Map<String, Boolean> backup = getBackupOfGroupFields(groupID);
		setFieldsEnabled(backup);
	}

	/**
	 * key: String - FieldName <br/>
	 * value: Boolean - the current enable state of the corresponding field. <br/>
	 *
	 * <p>The current enable states of the fields are stored herein.</p>
	 * <p><b>Important</b>: If the Query is initialised the enable fields are all <code>null</code>.
	 * To maintain compatibility with the older UI, we interpret a non-set enable field as
	 * <code>true</code>! </p>
	 */
	private Map<String, Boolean> fieldsEnabledMap = new HashMap<String, Boolean>();

	/**
	 * Returns the current active state of the field with the given <code>fieldName</code>.
	 * <p><b>Important</b>: If the Query is initialised the enable fields are all <code>null</code>.
	 * To maintain compatibility with the older UI, we interpret a non-set enable field as
	 * <code>true</code>! </p>
	 * <p><b>Note</b>: You can either pass the the field name for the corresponding enable field, or
	 * you can pass the field name of the enable field (original field name +
	 * {@link #getEnabledFieldName(String)}).</p>
	 *
	 * @param fieldName The name of the field for which the current enable state shall be returned.
	 * @return the current active state of the field with the given <code>fieldName</code>.
	 */
	public boolean isFieldEnabled(String fieldName)
	{
		fieldName = getEnabledFieldName(fieldName);

		final Boolean result = fieldsEnabledMap.get(fieldName);
		return result == null || result.booleanValue();
	}

	/**
	 * Sets all enable flags of all query fields to <code>false</code>. This might be necessary
	 * since, any entry that is not found in the {@link #fieldsEnabledMap} is assumed to be true.
	 * Any newly created Query will therefore seem to have all fields activated.
	 */
	public void setAllFieldsDisabled()
	{
		Map<String, Field> fieldName2FieldMap = getFieldName2FieldMap();
		for (String fieldName : fieldName2FieldMap.keySet())
		{
			fieldsEnabledMap.put(getEnabledFieldName(fieldName), Boolean.FALSE);
		}
	}

	/**
	 * Sets the field with the given <code>fieldName</code> to the given <code>enabled</code> state.
	 * <p><b>Note</b>: It is assumed that the given fieldName is not already appended with the
	 * EnableField suffix, hence no call to {@link #getEnabledFieldName(String)} has been made.</p>
	 *
	 * @param fieldName The name of the field for which the enabled state shall change.
	 * @param enabled the new state the corresponding field shall be set to.
	 */
	public void setFieldEnabled(String fieldName, boolean enabled)
	{
		if (fieldName == null)
			return;

		String fieldEnableName = getEnabledFieldName(fieldName);
		final Boolean oldValue = fieldsEnabledMap.get(fieldEnableName);
		fieldsEnabledMap.put(fieldEnableName, enabled);
		notifyListeners(fieldEnableName, oldValue, Boolean.valueOf(enabled));
	}

	/**
	 * Sets the active state of the fields given as keys to the values given as values of the
	 * <code>field2EnableStateMap</code>.
	 * <p><b>Note</b>: It is assumed that the given fieldNames are not already appended with the
	 * EnableField suffix, hence no call to {@link #getEnabledFieldName(String)} has been made.</p>
	 *
	 * @param field2EnableStateMap contains all the identifier of the fields as keys and their enabled
	 * 	state as values.
	 */
	public void setFieldsEnabled(Map<String, Boolean> field2EnableStateMap)
	{
		if (field2EnableStateMap == null || field2EnableStateMap.isEmpty())
			return;

		for (Entry<String, Boolean> entry : field2EnableStateMap.entrySet())
		{
			setFieldEnabled(entry.getKey(), entry.getValue());
		}
	}

	private static final String FIELD_CLASS_NAME = "FieldName";

	/**
	 * A Map from all declared field names (the ones in all FielName inner classes) to the
	 * corresponding Field instances.
	 * <p>The map will lazily be created at most once.</p>
	 */
	private transient volatile Map<String, Field> fieldName2Field;

	/**
	 * Helper method to get all fields that may be set for the query.
	 *
	 * @return A mapping of fieldName to actual field object for all fields that are declared in the
	 * 	static FieldName classes.
	 */
	protected Map<String, Field> getFieldName2FieldMap()
	{
		if (fieldName2Field == null)
		{
			Class<?> currentClass = getClass();
			Set<String> fieldNames = new HashSet<String>();
			fieldName2Field = new HashMap<String, Field>();

			while (currentClass != AbstractSearchQuery.class.getSuperclass())
			{
				for (Class<?> innerClass : currentClass.getDeclaredClasses())
				{
					// apply filtering according to invariant:Class has to have name == FieldName & must be static
					if (FIELD_CLASS_NAME.equals(innerClass.getSimpleName()) &&
							(innerClass.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
					{
						fieldNames.addAll(getStaticStringMemberContents(innerClass));
					}
				}

				for (Field field : currentClass.getDeclaredFields())
				{
					if ( fieldNames.contains(field.getName()) )
						fieldName2Field.put(field.getName(), field);
				}

				currentClass = currentClass.getSuperclass();
			}
		}

		return fieldName2Field;
	}

	@Override
	public Map<String, Boolean> getFieldsEnabled() {
		Map<String, Boolean> result = new HashMap<String, Boolean>();

		for (String fieldName : getFieldName2FieldMap().keySet()) {
			result.put(fieldName, isFieldEnabled(fieldName));
		}

		return result;
	}

	/**
	 * Returns all changed fields affected by the given <code>propertyName</code>.
	 * <p>Note: If <code>propertyName == {@link #PROPERTY_WHOLE_QUERY}</code>, then a
	 * 	FieldChangeCarrier for all fields is created.</p>
	 *
	 * @param propertyName the name of the property for which a List of {@link FieldChangeCarrier}s
	 * 	containing the changes shall be returned.
	 */
	@Override
	public List<FieldChangeCarrier> getChangedFields(final String propertyName)
	{
		assert propertyName != null;
		final List<FieldChangeCarrier> changedFields = new LinkedList<FieldChangeCarrier>();
		final boolean allFields = PROPERTY_WHOLE_QUERY.equals(propertyName);
		if (allFields)
		{
			final Set<String> fieldNames = getFieldName2FieldMap().keySet();
			changedFields.addAll( getFieldValueCarriers(fieldNames) );
			changedFields.addAll( getFieldEnableStates(fieldNames) );
		}
		else
		{
			if (propertyName.endsWith(ENABLED_SUFFIX))
			{
				changedFields.add(new FieldChangeCarrier(propertyName, isFieldEnabled(propertyName)));
			}
			else
			{
				FieldChangeCarrier fieldValueCarrier = getFieldValueCarrier(propertyName);
				if (fieldValueCarrier != null)
					changedFields.add(getFieldValueCarrier(propertyName));
			}
		}

		return changedFields;
	}

	/**
	 * Sets the <code>value</code> of the field specified by the given <code>fieldName</code>.
	 *
	 * @param fieldName The name of the field whose value shall be returned.
	 * @param value The new value to set.
	 */
	public void setFieldValue(String fieldName, Object value)
	{
		assert fieldName != null && fieldName.length() > 0;
		final Map<String, Field> fieldName2FieldMap = getFieldName2FieldMap();
		Field target = fieldName2FieldMap.get(fieldName);
		if (target == null)
			return;

		if (!target.isAccessible())
			target.setAccessible(true);

		try
		{
			target.set(this, value);
		}
		catch (IllegalArgumentException e)
		{
			throw e;
		}
		catch (IllegalAccessException e)
		{
			logger.error("Cannot set my own field! Fieldname:" + fieldName, e);
		}
	}

	/**
	 * Returns the value of the field specified by the given <code>fieldName</code>.
	 *
	 * @param fieldName The name of the field whose value shall be returned.
	 * @return The value of the field specified by the given <code>fieldName</code> or <code>null</code>
	 * 	if no field with the given name can be found.
	 */
	public Object getFieldValue(String fieldName)
	{
		FieldChangeCarrier fieldValueCarrier = getFieldValueCarrier(fieldName);
		return fieldValueCarrier == null ? null : fieldValueCarrier.getNewValue();
	}

	/**
	 * Same as {@link #getFieldValueCarriers(Set)} only for a single field.
	 *
	 * @param fieldName The name of the field for which to return the value packaged in a
	 * 	FieldChangeCarrier
	 * @return A {@link FieldChangeCarrier} containing the value of the given field.
	 */
	private FieldChangeCarrier getFieldValueCarrier(String fieldName)
	{
		List<FieldChangeCarrier> fieldValues =
			getFieldValues(Collections.singleton(fieldName), getFieldName2FieldMap());

		if (fieldValues.isEmpty())
			return null;

		return fieldValues.iterator().next();
	}

	/**
	 * Returns a list of {@link FieldChangeCarrier}s containing all values corresponding to
	 * the fields given as <code>fieldNames</code>.
	 * <p>Note: Field names of enable fields (like the ones produced by
	 * 	{@link #getEnabledFieldName(String)} are ignored. </p>
	 *
	 * @param fieldNames The names of the fields for which to return the values via reflection.
	 * @return a list of {@link FieldChangeCarrier}s containing all values corresponding to
	 * 	the fields given as <code>fieldNames</code>.
	 */
	private List<FieldChangeCarrier> getFieldValueCarriers(Set<String> fieldNames)
	{
		return getFieldValues(fieldNames, getFieldName2FieldMap());
	}

	/**
	 * Returns a list of {@link FieldChangeCarrier}s containing all values corresponding to
	 * the fields given as <code>fieldNames</code>.
	 * <p>Note: Field names of enable fields (like the ones produced by
	 * 	{@link #getEnabledFieldName(String)} are ignored. </p>
	 *
	 * @param fieldNames The names of the fields for which to return the values via reflection.
	 * @param fieldName2Field A mapping to all valid query properties by their names.
	 * @return a list of {@link FieldChangeCarrier}s containing all values corresponding to
	 * 	the fields given as <code>fieldNames</code>.
	 */
	private List<FieldChangeCarrier> getFieldValues(
			Set<String> fieldNames,
			Map<String, Field> fieldName2Field
	)
	{
		if (fieldName2Field == null)
			fieldName2Field = getFieldName2FieldMap();

		if (fieldName2Field.isEmpty())
			return Collections.emptyList();

		List<FieldChangeCarrier> carriers = new LinkedList<FieldChangeCarrier>();
		for (String fieldName : fieldNames)
		{
			if (fieldName.endsWith(ENABLED_SUFFIX))
				continue;

			if (fieldName2Field.get(fieldName) == null)
			{
				logger.warn("You are trying to retrieve:"+fieldName+" from "+getClass().getName()+", but " +
						"there doesn't exist such a field!", new Exception());

				continue;
			}

			Field field = fieldName2Field.get(fieldName);

			if (! field.isAccessible())
				field.setAccessible(true);

			Object fieldValue = null;
			try
			{
				fieldValue = field.get(this);
			}
			catch (IllegalAccessException e)
			{
				// This should not happen, since field belongs to me.
				logger.error(e);
				throw new RuntimeException(e);
			}

			carriers.add( new FieldChangeCarrier(fieldName, fieldValue) );
		}
		return carriers;
	}

	/**
	 * Returns a list of {@link FieldChangeCarrier}s containing all enabled states corresponding to
	 * the fields given as <code>fieldNames</code>.
	 *
	 * @param fieldNames The names of the fields for which to return the enable states.
	 * @return a list of {@link FieldChangeCarrier}s containing all enabled states corresponding to
	 * 	the fields given as <code>fieldNames</code>.
	 */
	private List<FieldChangeCarrier> getFieldEnableStates(Set<String> fieldNames)
	{
		List<FieldChangeCarrier> carriers = new LinkedList<FieldChangeCarrier>();
		for (String fieldName : fieldNames)
		{
			carriers.add( new FieldChangeCarrier(
					getEnabledFieldName(fieldName), isFieldEnabled(fieldName))
			);
		}
		return carriers;
	}

	/**
	 * Returns a set of values of all declared members of the given class object, which are public,
	 * static and final strings.
	 *
	 * @param clazz the object from which to return all public, static and final strings.
	 * @return a set of values of all declared members of the given class object, which are public,
	 * 	static and final strings.
	 */
	private Set<String> getStaticStringMemberContents(Class<?> clazz)
	{
		assert clazz != null;
		if ((clazz.getModifiers() & Modifier.STATIC) != Modifier.STATIC &&
				(clazz.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC)
		{
			logger.debug("A class object has been given as parameter is either not static or not public: "+
					clazz.getName(), new Exception());
		}

		Set<String> stringMemberContents = new HashSet<String>();

		for (Field field : clazz.getDeclaredFields())
		{
			if (field.getType() != String.class)
				continue;

			if ((field.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC &&
					(field.getModifiers() & Modifier.STATIC) != Modifier.STATIC &&
					(field.getModifiers() & Modifier.FINAL) != Modifier.FINAL)
			{
				logger.warn("There are members declared that are not public, static and final! " +
						"This shouldn't be.\n Field: "+ clazz.getName() +"."+field.getName());
			}

			try
			{ // null because the field is static and the type does not need to be specified.
				stringMemberContents.add((String) field.get(null));
			}
			catch (Exception e) {
				logger.warn("This should not happen!", e);
			}
		}

		return stringMemberContents;
	}

	/**
	 * Returns all enable flags of all fields that are listed as public, static, final string members
	 * of the given class object.
	 *
	 * @param fieldNameClass the class containing all fieldNames for which to return the enable state.
	 * @return all enable flags of all fields that are listed as public, static, final string members
	 * 	of the given class object.
	 */
	protected List<FieldChangeCarrier> getAllEnableFlags(Class<?> fieldNameClass)
	{
		return getFieldEnableStates(getStaticStringMemberContents(fieldNameClass));
	}

	/**
	 * Carrier that contains the changed field property name and its new value;
	 *
	 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
	 */
	public static class FieldChangeCarrier
	{
		private String propertyName;
		private Object newValue;

		/**
		 * @param propertyName
		 * @param newValue
		 */
		public FieldChangeCarrier(String propertyName, Object newValue)
		{
			assert propertyName != null;
			this.propertyName = propertyName;
			this.newValue = newValue;
		}

		/**
		 * @return the propertyName
		 */
		public String getPropertyName()
		{
			return propertyName;
		}

		/**
		 * @return the newValue
		 */
		public Object getNewValue()
		{
			return newValue;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("changed field: ").append(propertyName).append(" -> ").append(newValue);
			return sb.toString();
		}
	}
}
