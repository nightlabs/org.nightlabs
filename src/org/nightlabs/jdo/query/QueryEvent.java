package org.nightlabs.jdo.query;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;

/**
 * The PropertyChangeEvent used for propagating Query changes.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class QueryEvent
	extends PropertyChangeEvent
{
	/**
	 * The serial version id.
	 */
	private static final long serialVersionUID = 1L;

	public QueryEvent(AbstractSearchQuery changedQuery, String propertyName, Object oldValue, Object newValue)
	{
		super(changedQuery, propertyName, oldValue, newValue);
	}

	/**
	 * @return the changedQuery
	 */
	public AbstractSearchQuery getChangedQuery()
	{
		// It is not allowed to set the source to null, but the QueryProvider might want to do this
		// when a new bunch of Queries are loaded and an existing one is removed without being replaced.
		// Then the source should be the new Query == null.
		// Since this is not possible the Provider will set the source to the old Query and we have to
		// handle this special case.
		if (AbstractSearchQuery.PROPERTY_WHOLE_QUERY.equals(getPropertyName()) && getNewValue() == null)
			return null;

		return (AbstractSearchQuery) getSource();
	}

	private List<FieldChangeCarrier> changedFields;

	/**
	 * @return a List of {@link FieldChangeCarrier} that describe the changes done the the
	 * 	corresponding query ({@link #getChangedQuery()}).
	 */
	public List<FieldChangeCarrier> getChangedFields()
	{
		if (getChangedQuery() == null)
			return null;

		if (changedFields == null)
		{
			changedFields = getChangedQuery().getChangedFields(getPropertyName());
		}

		return changedFields;
	}
}
