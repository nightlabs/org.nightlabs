package org.nightlabs.jdo.query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;


/**
 * This is a light-weight base class that you should subclass when you intend to have your
 * client define arbitrary parameters for a server-side jdo query.
 * <p>
 * For security reasons, it's urgently recommended not to allow a client to directly specify
 * JDOQL. Although, it's impossible to modify data using JDOQL, it's possible to retrieve data
 * that this user otherwise isn't allowed to see.
 * </p>
 * <p>
 * That's why it's best practice to implement the {@link #prepareQuery(Query)} method with secure(!) JDOQL
 * and only to allow the client to specify some parameters. Because the client cannot make the server
 * load classes, it is restricted then to the predefined JDOQL (in your implementation of this class).
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractJDOQuery
	extends AbstractSearchQuery
{
	private static final long serialVersionUID = 1L;
	private transient PersistenceManager persistenceManager = null;

	/**
	 * The logger instance used in this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractJDOQuery.class);

	/**
	 * Use this method in your implementation of {@link #prepareQuery(Query)} to obtain
	 * a persistence manager for creation of your query.
	 * <p>
	 * The {@link PersistenceManager} is stored in a <i>transient</i> field.
	 * </p>
	 *
	 * @return Returns the persistence manager which has previously been assigned by
	 *		{@link #setPersistenceManager(PersistenceManager)}.
	 */
	public PersistenceManager getPersistenceManager()
	{
		return persistenceManager;
	}

	/**
	 * When using an instance of a JDOQuery's subclass - for example in your EJB
	 * method, you must assign a <code>PersistenceManager</code> before calling
	 * {@link #getResult()}.
	 * <p>
	 * The {@link PersistenceManager} is stored in a <i>transient</i> field.
	 * </p>
	 *
	 * @param pm The PersistenceManager instance that shall be used for accessing the
	 *		datastore.
	 */
	public void setPersistenceManager(PersistenceManager pm)
	{
		this.persistenceManager = pm;
	}

	/**
	 * This method checks, whether a {@link PersistenceManager} has been assigned before.
	 *
	 * @throws IllegalStateException Thrown if {@link #setPersistenceManager(PersistenceManager)} has not been called yet.
	 */
	protected void assertPersistenceManager()
	throws IllegalStateException
	{
		if (persistenceManager == null)
			throw new IllegalStateException("No PersistenceManager assigned! The method setPersistenceManager(...) must be called before!");
	}

	private Collection<?> result = null;

	/**
	 * This method delegates - if necessary - to {@link #executeQuery()} and
	 * returns the result. A subsequent call to this method returns the previously returned
	 * instance and does not call {@link #executeQuery()} again.
	 * <p>
	 * You should not override this method. It's probably always better to override {@link #executeQuery()} if
	 * implementing {@link #prepareQuery(Query)} isn't sufficient.
	 * </p>
	 *
	 * @return Returns the result of the query. Never returns <code>null</code>.
	 */
	@Override
	public Collection<?> getResult()
	{
		if (result == null) {
			assertPersistenceManager();

			result = executeQuery();
			if (result == null)
				throw new IllegalStateException("The method executeQuery() of class "+this.getClass().getName()+" is incorrectly implemented and returned null!");
		}

		return result;
	}

	/**
	 * This method first calls {@link #prepareQuery(Query)} in order to obtain a
	 * {@link Query} that's ready to be executed. The candidates specified by
	 * {@link #setCandidates(Collection)} are passed to the <code>Query</code>.
	 * Then, it collects all fields
	 * of your subclass and puts their names together with their values into
	 * the parameter map which is passed to {@link Query#executeWithMap(Map)}.
	 * <p>
	 * If you need to post-process your query result (e.g. perform security related stuff like
	 * removing passwords), you should extend this method: First call the super implementation, then
	 * post-process its result before returning it.
	 * </p>
	 * <p>
	 * This method is called by {@link #getResult()}, if necessary (i.e. only once - the result is cached).
	 * </p>
	 *
	 * {@inheritDoc}
	 */
	protected Collection<?> executeQuery()
	{
		Map<String, Object> params = null;
		Query q = null;
		try {
			q = createQuery();
			if (q == null)
				throw new IllegalStateException("Your implementation of createQuery() in class " +
					this.getClass().getName() + " returned null!");

			prepareQuery(q);

			if (getCandidates() != null)
				q.setCandidates(getCandidates());

			q.setRange(getFromInclude(), getToExclude());

//			params = new HashMap<String, Object>();
			params = getParameters();
			Class<?> clazz = getClass();
			while (clazz != AbstractJDOQuery.class) {
				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					if ((field.getModifiers() & Modifier.STATIC) != 0)
						continue;

//					if ((field.getModifiers() & Modifier.TRANSIENT) != 0)
//					continue;

					if (!field.isAccessible())
						field.setAccessible(true);

					String fieldName = field.getName();
					if (params.containsKey(fieldName))
						throw new IllegalStateException("The class " + this.getClass() + " has a duplicate " +
								"field declaration (probably private) for the field " + fieldName + " in its " +
										"inheritance structure! Every field name must be unique in the whole " +
										"inheritance structure (including private fields)!");

					Object value;
					try {
						value = field.get(this);
					} catch (IllegalAccessException e) {
						// this exception should never happen when accessing _this_ only
						throw new RuntimeException(e);
					}
					params.put(fieldName, value);
				}
				clazz = clazz.getSuperclass();
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Query type:"+ this.getClass().getName());
				logger.debug("Query: " + q.toString());
				logger.debug("Parameter Map: " + params);
			}

			// force the JDO implementation to initialise the MetaData of our queried class
			//  -> in case the Tables are not present, they are now created.
			getPersistenceManager().getExtent(getCandidateClass());

			Object result = q.executeWithMap(params);
			result = postProcessQueryResult(result);
			if (result instanceof Collection)
			{
				final Collection<?> jdoResult = (Collection<?>) result;
				for (Object element : jdoResult)
				{
					// check if the elements in the returned collection fit the declared return type.
					getResultClass().cast(element);
				}

				// important to not wrap this, because wrapping in a new ArrayList or similar might be very
				// expensive and prevents JPOX to optimise candidates-handling
				return jdoResult;
			}

			return Collections.singletonList(getResultClass().cast(result));
		}
		catch (Throwable t)
		{
			logger.error("Executing JDOQuery defined by an instance of " + this.getClass().getName() +
					" failed!\nQuery: " + q + "\nParams: " + params, t);
			if (t instanceof RuntimeException)
				throw (RuntimeException)t;

			if (t instanceof Error)
				throw (Error)t;

			throw new RuntimeException(t);
		}
	}

	protected Object postProcessQueryResult(Object result) {
		// nothing to do...
		return result;
	}

	/**
	 * Standard implementation uses {@link PersistenceManager#newQuery(Class)} with the
	 * {@link #getCandidateClass()} as parameter.
	 *
	 * @return a new JDO Query that is instantiated with the {@link #getCandidateClass()} as its
	 * 	candidate class, hence the query's starting point.
	 *
	 */
	protected Query createQuery()
	{
		return getPersistenceManager().newQuery(getCandidateClass());
	}

	/**
	 * Implement this method by preparing the given JDO query with a filter that takes your subclass'
	 * fields as parameters into account. Each field is (using java reflection) put into a
	 * parameter map. Hence, you have access to all fields of your subclass via their simple
	 * names as implicit parameter (don't forget the colon ":" prefix).
	 * <p>
	 * Example: You declare a field in your subclass:<br/>
	 * <code>
	 * private Date minimumBirthday;
	 * </code>
	 * <br/>
	 * You can access this field in your query using the simple name <i>":minimumBirthday"</i>
	 * (implicit parameter).
	 * </p>
	 * <p>
	 * This method is called by {@link #getResult()}.
	 * </p>
	 * @param q the Query to prepare.
	 */
	protected abstract void prepareQuery(Query q);

	/**
	 * Default implementation assumes that the result class of a query is the same as its candidate
	 * class.
	 *
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> initResultClass()
	{
		return initCandidateClass();
	}

	private transient StringBuilder filter;

	/**
	 * Returns the (shared) StringBuilder for the filters, which can also
	 * be used by subclasses in the method {@link #prepareQuery(Query)}
	 * @return the StringBuilder for the filter
	 */
	protected StringBuilder getFilter() {
		if (filter == null)
			filter = new StringBuilder();

		return filter;
	}

	private transient StringBuilder vars;

	/**
	 * Returns the (shared) StringBuilder for the variables, which can also
	 * be used by subclasses in the method {@link #prepareQuery(Query)}
	 * @return the StringBuilder for the variables
	 */
	protected String getVars() {
		if (vars == null)
			vars = new StringBuilder();

		return vars.toString();
	}

	private transient StringBuilder imports;

	/**
	 * Returns the (shared) StringBuilder for the imports, which can also
	 * be used by subclasses in the method {@link #prepareQuery(Query)}
	 * @return the StringBuilder for the imports
	 */
	protected String getImports() {
		if (imports == null) {
			imports = new StringBuilder();

			if (importsSet != null) {
				for (String importClass : importsSet) {
					if (imports.length() > 0)
						imports.append("; ");

					imports.append("import "+importClass);
				}
			}
		}

		return imports.toString();
	}

	private transient Set<String> importsSet;

	/**
	 * Adds an class name to the imports of the query.
	 * This method can be used by subclasses in the {@link #prepareQuery(Query)} method
	 * @param importClass the class to import
	 */
	protected void addImport(Class<?> importClass) {
		addImport(importClass.getName());
	}

	/**
	 * Adds an class name to the imports of the query.
	 * This method can be used by subclasses in the {@link #prepareQuery(Query)} method
	 * @param importClass the full qualified name of the class to import
	 */
	protected void addImport(String importClass) {
		importClass = importClass.trim();

		if (importsSet == null)
			importsSet = new HashSet<String>();

		if (!importsSet.add(importClass))
			return;

		imports = null; // invalidate cache
//		if (imports != null) {
//			if (imports.length() > 0)
//				imports.append("; ");
//
//			imports.append("import "+importClass);
//		}
	}

	/**
	 * Adds variable with the specified type and name to the query.
	 * This method can be used by subclasses in the {@link #prepareQuery(Query)} method.
	 * <p>
	 * This method is a convencience delegating to {@link #addVariable(String, String)}.
	 * </p>
	 * @param clazz the class of the variable.
	 * @param variableName the name of the variable for the given class name.
	 */
	protected void addVariable(Class<?> clazz, String variableName) {
		addVariable(clazz.getName(), variableName);
	}

	/**
	 * Adds variable with the specified type and name to the query.
	 * This method can be used by subclasses in the {@link #prepareQuery(Query)} method
	 * @param className the full qualified name of the class
	 * @param variableName the name of the variable for the given class name
	 */
	protected void addVariable(String className, String variableName) {
		if (vars == null)
			vars = new StringBuilder();

		if (vars.length() > 0)
			vars.append("; ");

		vars.append(className+" "+variableName);
	}

	private transient Map<String, Object> parameters = null;

	protected Map<String, Object> getParameters() {
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		return parameters;
	}

	/**
	 * Adds a parameter to the parameters map which will used later in {@link #executeQuery()}.
	 * This method can be used in case that you need to define parameters which are not members of
	 * subclass of {@link AbstractJDOQuery}.
	 *
	 * @param fieldName the fieldName to add to the parameters
	 * @param value the value of the fieldName
	 */
	protected void addParam(String fieldName, Object value) {
		getParameters().put(fieldName, value);
	}
}
