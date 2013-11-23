package org.nightlabs.test.jdo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nightlabs.test.BeanTestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JDOTestRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class PersistenceCapableTestBase<T> extends BeanTestBase<T> {
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceCapableTestBase.class);

	@JDOTestResource
	private PersistenceManager persistenceManager;

	private final Class<T> entityClass;

	/**
	 * Named query result type.
	 */
	protected static enum NamedQueryResultType {
		/** List result. */
		LIST_RESULT,
		/** Single object result. */
		SINGLE_RESULT,
		/** Non-result query. */
		UPDATE
	}

	/**
	 * Create a new GenericPersistenceEntityTest instance.
	 * @param entityClass The entity class
	 */
	protected PersistenceCapableTestBase(final Class<T> entityClass) {
		super(entityClass);
		this.entityClass = entityClass;
	}

	/**
	 * Get the query parameters for the given named query.
	 * @param queryName the query name
	 * @return the query parameters
	 */
	protected Map<String, Object> getNamedQueryParameters(final String queryName) {
		return Collections.emptyMap();
	}

	/**
	 * Get the query result type for the given named query.
	 * @param queryName the query name
	 * @return the result type
	 */
	protected NamedQueryResultType getNamedQueryResultType(final String queryName) {
		return NamedQueryResultType.LIST_RESULT;
	}

	private Collection<Query> getNamedQueries() {
		final Collection<Query> result = new ArrayList<Query>();
		final Query singleQuery = entityClass.getAnnotation(Query.class);
		if (singleQuery != null) {
			result.add(singleQuery);
		}
		final Queries queries = entityClass.getAnnotation(Queries.class);
		if (queries != null) {
			final Query[] nestedQueries = queries.value();
			if (nestedQueries != null) {
				result.addAll(Arrays.asList(nestedQueries));
			}
		}
		return result;
	}

	@Override
	protected boolean isBeanFieldCandidate(Field field) {
		return super.isBeanFieldCandidate(field) && !field.getName().startsWith("jdo");
	}

	/**
	 * Is the given field a persistent field candidate?
	 * @param field The field
	 * @return <code>true</code> if the given field is a persistent field candidate
	 */
	protected boolean isPersistentFieldCandidate(final Field field) {
		Persistent persistent = field.getAnnotation(Persistent.class);
		return isBeanFieldCandidate(field) && (persistent == null || persistent.persistenceModifier() != PersistenceModifier.NONE);
	}

	private Collection<Field> getPrimaryKeyFields() {
		final Collection<Field> result = new ArrayList<Field>();
		final Collection<Field> declaredFields = getBeanFields();
		for (final Field field : declaredFields) {
			if (isPersistentFieldCandidate(field) && field.getAnnotation(PrimaryKey.class) != null) {
				result.add(field);
			}
		}
		return result;
	}

	private Collection<Field> getNonGeneratedPrimaryKeyFields() {
		final Collection<Field> idFields = getPrimaryKeyFields();
		for (final Iterator<Field> iterator = idFields.iterator(); iterator.hasNext();) {
			final Field field = iterator.next();
			Persistent persistent = field.getAnnotation(Persistent.class);
			if (persistent != null && persistent.valueStrategy() != IdGeneratorStrategy.UNSPECIFIED) {
				iterator.remove();
			}
		}
		return idFields;
	}

	private Collection<Field> getGeneratedIdFields() {
		final Collection<Field> idFields = getPrimaryKeyFields();
		for (final Iterator<Field> iterator = idFields.iterator(); iterator.hasNext();) {
			final Field field = iterator.next();
			Persistent persistent = field.getAnnotation(Persistent.class);
			if (persistent == null || persistent.valueStrategy() == IdGeneratorStrategy.UNSPECIFIED) {
				iterator.remove();
			}
		}
		return idFields;
	}

	private Collection<Field> getPersistentFields() {
		final Collection<Field> result = new ArrayList<Field>();
		final Collection<Field> declaredFields = getBeanFields();
		for (final Field field : declaredFields) {
			if (isPersistentFieldCandidate(field) && field.getAnnotation(PrimaryKey.class) == null) {
				result.add(field);
			}
		}
		return result;
	}

	private Collection<Field> getMandatoryPersistentFields() {
		Collection<Field> persistentFields = getPersistentFields();
		for (final Iterator<Field> iterator = persistentFields.iterator(); iterator.hasNext();) {
			final Field field = iterator.next();
			Persistent persistent = field.getAnnotation(Persistent.class);
			if (persistent == null || persistent.nullValue() != NullValue.EXCEPTION) {
				iterator.remove();
			}
		}
		return persistentFields;
	}

	private void fillNonGeneratedIdFields(final T entity) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		final Collection<Field> idFields = getNonGeneratedPrimaryKeyFields();
		for (final Field field : idFields) {
			fillField(entity, field.getName());
		}
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	/**
	 * Test for @PersistenceCapable annotation.
	 */
	@Test
	public void entityAnnotationTest() {
		final PersistenceCapable persictenceCapable = entityClass.getAnnotation(PersistenceCapable.class);
		Assert.assertNotNull("Class is not @PersistenceCapable annotated", persictenceCapable);
	}

	/**
	 * Test for default constructor.
	 */
	@Test
	public void defaultConstructorTest() {
		try {
			entityClass.getDeclaredConstructor(new Class<?>[0]);
		} catch(NoSuchMethodException e) {
			Assert.fail("Class does not define a default (no-arg) constructor");
		}
	}

	/**
	 * Override to allow missing primary key field in persistent class.
	 * @return <code>true</code> to allow missing primary key field. The default
	 * 		implementation always returns <code>false</code>
	 */
	protected boolean allowMissingPrimaryKey() {
		return false;
	}

	/**
	 * Test for fields with @PrimaryKey annotation.
	 */
	@Test
	public void primaryKeyTest() {
		// pk test should also depend on identity type.
		// Identity type = APPLICATION must have a pk - but this already fails in the enhancer
		if (!allowMissingPrimaryKey()) {
			Assert.assertFalse("Class does not define any @PrimaryKey annotated fields", getPrimaryKeyFields().isEmpty());
		}
	}

	/**
	 * Test persisting the entity.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void makePersistentTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		final T entity = createEmptyInstance();

		final Collection<Field> idFields = getNonGeneratedPrimaryKeyFields();
		final List<Object> values = new ArrayList<Object>();
		for (final Field field : idFields) {
			values.add(fillField(entity, field.getName()));
		}
		Collection<Field> mandatoryPersistentFields = getMandatoryPersistentFields();
		for (Field field : mandatoryPersistentFields) {
			// TODO check? values.add() ?
			fillField(entity, field.getName());
		}

		LOG.info("Persisting " + entity);
		persistenceManager.makePersistent(entity);

		javax.jdo.Query query = persistenceManager.newQuery(entityClass);
		final List<?> resultList = (List<?>) query.execute();
		Assert.assertEquals(1, resultList.size());
		final T persistentEntity = (T)resultList.get(0);
		final Iterator<Object> valueIterator = values.iterator();
		for (final Field field : idFields) {
			final Object fieldValue = getFieldValue(persistentEntity, field.getName());
			assertEquals("PrimaryKey field " + field.getName(), valueIterator.next(), fieldValue);
		}

		final Collection<Field> generatedIdFields = getGeneratedIdFields();
		for (final Field field : generatedIdFields) {
			final Object fieldValue = getFieldValue(persistentEntity, field.getName());
			Assert.assertNotNull("Generated id field " + field.getName() + " is null after persisting", fieldValue);
		}
	}

	/**
	 * Test persisting the entity with all fields.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void makePersistentWithFieldsTest() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
		final T entity = createEmptyInstance();
		fillNonGeneratedIdFields(entity);

		final Collection<Field> persistentFields = getPersistentFields();
		final List<Object> values = new ArrayList<Object>(persistentFields.size());
		for (final Field field : persistentFields) {
			values.add(fillField(entity, field.getName()));
		}

		LOG.info("Persisting " + entity);
		persistenceManager.makePersistent(entity);

		javax.jdo.Query query = persistenceManager.newQuery(entityClass);
		final List<?> resultList = (List<?>) query.execute();
		Assert.assertEquals(1, resultList.size());
		final T persistentEntity = (T)resultList.get(0);
		final Iterator<Object> valueIterator = values.iterator();
		for (final Field field : persistentFields) {
			final Object fieldValue = getFieldValue(persistentEntity, field.getName());
			assertEquals("Field " + field.getName(), valueIterator.next(), fieldValue);
		}
	}

	/**
	 * Test named queries.
	 */
	@Test
	public void namedQueriesTest() {
		final Collection<Query> namedQueries = getNamedQueries();
		for (final Query namedQuery : namedQueries) {
			final String queryName = namedQuery.name();
			LOG.info("Testing named query " + queryName);
			javax.jdo.Query query = persistenceManager.newNamedQuery(entityClass, queryName);
			final Map<String, Object> parameters = getNamedQueryParameters(queryName);
			// FIXME does not fail - even with missing params :-(
			query.executeWithMap(parameters);
		}
	}
}
