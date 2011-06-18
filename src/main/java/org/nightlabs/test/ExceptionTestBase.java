package org.nightlabs.test;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Generic Exception test.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @param <T> The exception class
 */
public class ExceptionTestBase<T extends Throwable> {
	private static final String THE_MESSAGE = "The message";
	private final Class<T> exceptionClass;

	/**
	 * Create a new ExceptionTestBase instance.
	 * @param exceptionClass The exception class
	 */
	public ExceptionTestBase(final Class<T> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/**
	 * Tests constuctor.
	 */
	@Test
	public void testConstructor1() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final T exce = exceptionClass.getConstructor(new Class[0]).newInstance(new Object[0]);
		Assert.assertNull(exce.getMessage());
		Assert.assertNull(exce.getCause());
	}

	/**
	 * Tests constuctor.
	 */
	@Test
	public void testConstructor2() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final T exce = exceptionClass.getConstructor(String.class).newInstance(THE_MESSAGE);
		Assert.assertEquals(THE_MESSAGE, exce.getMessage());
		Assert.assertNull(exce.getCause());
	}

	/**
	 * Tests constuctor.
	 */
	@Test
	public void testConstructor3() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final Throwable cause = new IllegalStateException();
		final T exce = exceptionClass.getConstructor(Throwable.class).newInstance(cause);
		Assert.assertEquals(cause.toString(), exce.getMessage());
		Assert.assertNotNull(exce.getCause());
		Assert.assertEquals(cause, exce.getCause());
	}

	/**
	 * Tests constuctor.
	 */
	@Test
	public void testConstructor4() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final Throwable cause = new IllegalStateException();
		final T exce = exceptionClass.getConstructor(String.class, Throwable.class).newInstance(THE_MESSAGE, cause);
		Assert.assertEquals(THE_MESSAGE, exce.getMessage());
		Assert.assertNotNull(exce.getCause());
		Assert.assertEquals(cause, exce.getCause());
	}
}
