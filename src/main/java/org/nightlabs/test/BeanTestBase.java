package org.nightlabs.test;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic bean test.
 * @param <T> The bean type
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class BeanTestBase<T> {

	private static final Logger LOG = LoggerFactory.getLogger(BeanTestBase.class);

	private static final float DELTA = 0.0000000000001f;
	private static final int CREATED_ARRAY_LENGTH = 5;
	private static final String CLASS_AND_FIELD_SEPERATOR = "#";

	private final Class<T> beanClass;
	private long nextValue = 1;

	/**
	 * Create a new BeanTestBase instance.
	 * @param beanClass The bean class
	 */
	public BeanTestBase(final Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Create an empty instance of the bean class. This requires a default constructor.
	 * @return The empty instance
	 */
	protected T createEmptyInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Constructor<T> constructor = beanClass.getDeclaredConstructor(new Class<?>[0]);
		constructor.setAccessible(true);
		return constructor.newInstance(new Object[0]);
	}

	private static void addAllFields(final Class<?> clazz, final List<Field> allFields) {
		allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			addAllFields(superclass, allFields);
		}
	}

	/**
	 * Get all fields for the bean class. This also includes fields
	 * from parent classes whether or not they are visible.
	 * @return All fields
	 */
	private Field[] getAllFields() {
		final List<Field> allFields = new ArrayList<Field>();
		addAllFields(beanClass, allFields);
		return allFields.toArray(new Field[allFields.size()]);
	}

	private static void addAllSetters(final Class<?> clazz, final List<Method> allSetters) {
		final Method[] methods = clazz.getDeclaredMethods();
		for (final Method method : methods) {
			if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
				allSetters.add(method);
			}
		}
		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			addAllSetters(superclass, allSetters);
		}
	}

	private Method[] getAllSetters() {
		final List<Method> allSetters = new ArrayList<Method>();
		addAllSetters(beanClass, allSetters);
		return allSetters.toArray(new Method[allSetters.size()]);
	}

	/**
	 * Get all bean property names.
	 * @see #isBeanFieldCandidate(Field)
	 * @return The bean property names
	 */
	protected Collection<String> getBeanPropertyNames() {
		final Collection<String> beanPropertyNames = new HashSet<String>();
		final Field[] allFields = getAllFields();
		for (final Field field : allFields) {
			if (isBeanFieldCandidate(field)) {
				beanPropertyNames.add(field.getName());
			}
		}
		final Method[] allSetters = getAllSetters();
		for (final Method setter : allSetters) {
			String propertyName = setter.getName().substring("set".length());
			if (propertyName.length() == 1) {
				propertyName = propertyName.toLowerCase();
			} else {
				propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
			}
			final Method getter = getGetter(propertyName);
			if (getter != null && !isIgnoreProperty(propertyName)) {
				beanPropertyNames.add(propertyName);
			}
		}
		return beanPropertyNames;
	}

	protected Collection<Field> getBeanFields() {
		final Collection<String> beanPropertyNames = getBeanPropertyNames();
		final Collection<Field> beanFields = new ArrayList<Field>(beanPropertyNames.size());
		for (final String propertyName : beanPropertyNames) {
			Field field;
			try {
				field = beanClass.getDeclaredField(propertyName);
				field.setAccessible(true);
				beanFields.add(field);
			} catch (final NoSuchFieldException e) {
				// ignore
			}
		}
		return beanFields;
	}

	private long getNextValue() {
		final long value = nextValue;
		nextValue++;
		return value;
	}

	/**
	 * Create a test value for the given type.
	 * @param type The type
	 * @return The test value
	 */
	protected Object createValue(final Type type, String propertyName) {
		Class<?> clazz = null;
		ParameterizedType parameterizedType = null;
		if (type instanceof Class<?>) {
			clazz = (Class<?>)type;
		} else if (type instanceof ParameterizedType) {
			parameterizedType = (ParameterizedType)type;
			final Type rawType = parameterizedType.getRawType();
			if (rawType instanceof Class<?>) {
				clazz = (Class<?>)rawType;
			}
		}
		if (clazz != null && clazz.isArray()) {
			final Object array = Array.newInstance(clazz.getComponentType(), CREATED_ARRAY_LENGTH);
			for (int i = 0; i < CREATED_ARRAY_LENGTH; i++) {
				Array.set(array, i, createValue(clazz.getComponentType(), propertyName + "#arrayitem"));
			}
			return array;
		} else if (parameterizedType != null && parameterizedType.getRawType() == Map.class) {
			final Map<Object, Object> result = new HashMap<Object, Object>();
			final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length != 2) {
				throw new IllegalStateException("Have map with actualTypeArguments.length != 2");
			}
			for (int i = 0; i < CREATED_ARRAY_LENGTH; i++) {
				result.put(createValue(actualTypeArguments[0], propertyName + "#mapkey"), createValue(actualTypeArguments[1], propertyName + "#mapvalue"));
			}
			return result;
		} else if (parameterizedType != null && parameterizedType.getRawType() == List.class) {
			final List<Object> result = new ArrayList<Object>();
			addValuesToCollection(result, parameterizedType, propertyName + "#listitem");
			return result;
		} else if (parameterizedType != null && parameterizedType.getRawType() == Set.class) {
			final Set<Object> result = new HashSet<Object>();
			addValuesToCollection(result, parameterizedType, propertyName + "#setitem");
			return result;
		} else if (parameterizedType != null && parameterizedType.getRawType() == Collection.class) {
			final Collection<Object> result = new ArrayList<Object>();
			addValuesToCollection(result, parameterizedType, propertyName + "#collectionitem");
			return result;
		} else if (type == Long.TYPE || type == Long.class) {
			return getNextValue();
		} else if (type == Integer.TYPE || type == Integer.class) {
			return (int)getNextValue();
		} else if (type == Character.TYPE || type == Character.class) {
			return (char)getNextValue();
		} else if (type == Short.TYPE || type == Short.class) {
			return Short.parseShort(String.valueOf(getNextValue())); // evil construct becaused PMD does not want to see "short"
		} else if (type == Byte.TYPE || type == Byte.class) {
			return (byte)getNextValue();
		} else if (type == Float.TYPE || type == Float.class) {
			return (float)getNextValue();
		} else if (type == Double.TYPE || type == Double.class) {
			return (double)getNextValue();
		} else if (type == Boolean.TYPE || type == Boolean.class) {
			return (getNextValue() % 2) == 0;
		} else if (type == String.class) {
			return Long.toHexString(getNextValue());
		} else if (type == Date.class) {
			return new Date(System.currentTimeMillis() - getNextValue() * 100000);
		} else if (type == Object.class) {
			return new Object();
		}
		throw new UnsupportedOperationException("Test " + getClass() + " must override createValue(Class<?> type) and return a value for type " + type + " for property " + propertyName);
	}

	private void addValuesToCollection(final Collection<Object> result, final ParameterizedType parameterizedType, String propertyName) {
		final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		if (actualTypeArguments.length != 1) {
			throw new IllegalStateException("Have collection with actualTypeArguments.length != 1");
		}
		for (int i = 0; i < CREATED_ARRAY_LENGTH; i++) {
			result.add(createValue(actualTypeArguments[0], propertyName));
		}
	}

	private Field findField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(propertyName);
		} catch(NoSuchFieldException e) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass.equals(Object.class)) {
				throw e;
			}
			return findField(superclass, propertyName);
		}
	}

	/**
	 * Set a value for the given field using a setter if possible.
	 */
	protected Object fillField(final T entity, final String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		final Object value = createValue(getPropertyGenericType(propertyName), propertyName);
		final Method setter = getSetter(getPropertyType(propertyName), propertyName);
		if (setter != null) {
			setter.invoke(entity, value);
		} else {
			Field field = findField(beanClass, propertyName);
			field.setAccessible(true);
			field.set(entity, value);
		}
		return value;
	}

	private Method getSetter(final Class<?> propertyType, final String propertyName) {
		String upperName;
		if (propertyName.length() == 1) {
			upperName = propertyName.toUpperCase();
		} else {
			upperName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		}
		final String setterName = "set" + upperName;
		try {
			return beanClass.getMethod(setterName, new Class<?>[] { propertyType });
		} catch (final NoSuchMethodException e) {
			LOG.warn("No setter found for persistent field: " + beanClass.getName() + CLASS_AND_FIELD_SEPERATOR + propertyName);
			return null;
		}
	}

	private Method getGetter(final String propertyName) {
		String upperName;
		if (propertyName.length() == 1) {
			upperName = propertyName.toUpperCase();
		} else {
			upperName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		}
		String getterName = "get" + upperName;
		try {
			return beanClass.getMethod(getterName, new Class<?>[0]);
		} catch (final NoSuchMethodException e) {
			getterName = "is" + upperName;
			try {
				return beanClass.getMethod(getterName, new Class<?>[0]);
			} catch (final NoSuchMethodException e2) {
				LOG.warn("No getter found for property: " + beanClass.getName() + CLASS_AND_FIELD_SEPERATOR + propertyName);
				return null;
			}
		}
	}

	/**
	 * Get the value of the given field using a getter if possible.
	 */
	protected Object getFieldValue(final T entity, final String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Object fieldValue;
		final Method getter = getGetter(propertyName);
		if (getter != null) {
			getter.setAccessible(true);
			fieldValue = getter.invoke(entity, new Object[0]);
		} else {
			Field field = findField(beanClass, propertyName);
			field.setAccessible(true);
			fieldValue = field.get(entity);
		}
		return fieldValue;
	}

	protected String[] getIgnorePropertyNames() {
		return new String[0];
	}

	private boolean isIgnoreProperty(final String propertyName) {
		final String[] ignorePropertyNames = getIgnorePropertyNames();
		if (ignorePropertyNames == null) {
			return false;
		}
		for (final String ignoreName : ignorePropertyNames) {
			if (ignoreName != null && ignoreName.equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is the given field a bean field candidate?
	 * @param field The field
	 * @return <code>true</code> if the given field is a bean field candidate
	 */
	protected boolean isBeanFieldCandidate(final Field field) {
		return ((field.getModifiers() & Modifier.STATIC) == 0 && (field.getModifiers() & Modifier.TRANSIENT) == 0) && !isIgnoreProperty(field.getName());
	}

	/**
	 * Get constructor parameters for non-default constructors to test.
	 */
	protected String[][] getConstructorParameterFieldNames() {
		return new String[0][];
	}

	/**
	 * Assert equals with support for Double/double and Float/float.
	 */
	protected static void assertEquals(final String message, final Object expected, final Object actual) {
		if (expected != null && actual != null) {
			if ((expected.getClass() == Double.TYPE || expected.getClass() == Double.class) && (actual.getClass() == Double.TYPE || actual.getClass() == Double.class)) {
				Assert.assertEquals(((Double)expected).doubleValue(), ((Double)actual).doubleValue(), DELTA);
				return;
			} else if ((expected.getClass() == Float.TYPE || expected.getClass() == Float.class) && (actual.getClass() == Float.TYPE || actual.getClass() == Float.class)) {
				Assert.assertEquals(((Float)expected).floatValue(), ((Float)actual).floatValue(), DELTA);
				return;
			}
		}
		Assert.assertEquals(message, expected, actual);
	}

	private Type getPropertyGenericType(final String propertyName) {
		try {
			final Field field = beanClass.getDeclaredField(propertyName);
			return field.getGenericType();
		} catch (final NoSuchFieldException e) {
			final Method getter = getGetter(propertyName);
			if (getter == null) {
				throw new IllegalStateException("Property " + propertyName + " not found as field or getter");
			}
			return getter.getGenericReturnType();
		}
	}

	private Class<?> getPropertyType(final String propertyName) {
		try {
			final Field field = beanClass.getDeclaredField(propertyName);
			return field.getType();
		} catch (final NoSuchFieldException e) {
			final Method getter = getGetter(propertyName);
			if (getter == null) {
				throw new IllegalStateException("Property " + propertyName + " not found as field org getter");
			}
			return getter.getReturnType();
		}
	}

	/**
	 * Constructor test.
	 */
	@Test
	public void constructorTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		final String[][] allConstructorParameters = getConstructorParameterFieldNames();
		if (allConstructorParameters == null || allConstructorParameters.length == 0) {
			return;
		}
		for (final String[] propertyNames : allConstructorParameters) {
			if (propertyNames == null || propertyNames.length == 0) {
				continue;
			}
			final Class<?>[] types = new Class<?>[propertyNames.length];
			final Object[] values = new Object[propertyNames.length];
			int i = 0;
			for (final String propertyName : propertyNames) {
				types[i] = getPropertyType(propertyName);
				values[i] = createValue(getPropertyGenericType(propertyName), propertyName);
				i++;
			}
			final Constructor<T> constructor = beanClass.getConstructor(types);
			LOG.info("Testing constructor " + constructor);
			final T entity = constructor.newInstance(values);

			i = 0;
			for (final String propertyName : propertyNames) {
				final Object fieldValue = getFieldValue(entity, propertyName);
				assertEquals("Property value " + propertyName, values[i], fieldValue);
				i++;
			}
		}
	}

	/**
	 * Getter / Setter test.
	 */
	@Test
	public void getterSetterTest() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
		final T bean = createEmptyInstance();
		final Collection<String> beanPropertyNames = getBeanPropertyNames();
		final Object[] values = new Object[beanPropertyNames.size()];

		int i = 0;
		for (final String propertyName : beanPropertyNames) {
			values[i] = fillField(bean, propertyName);
			i++;
		}

		i = 0;
		for (final String propertyName : beanPropertyNames) {
			LOG.info("Testing property " + propertyName);
			assertEquals("Field value " + propertyName, values[i], getFieldValue(bean, propertyName));
			i++;
		}
	}
}
