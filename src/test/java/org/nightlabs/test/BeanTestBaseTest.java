package org.nightlabs.test;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

/**
 * Tests for {@link BeanTestBase}.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class BeanTestBaseTest extends TestTestBase {

	public static class DisfunctGetterBeanSubtest extends BeanTestBase<DisfunctGetterBean> {
		public DisfunctGetterBeanSubtest() {
			super(DisfunctGetterBean.class);
		}
	}

	@Test
	public void disfunctGetterTest() throws InitializationError {
		runTest(DisfunctGetterBeanSubtest.class, "getterSetterTest");
	}

	public static class DisfunctSetterBeanSubtest extends BeanTestBase<DisfunctSetterBean> {
		public DisfunctSetterBeanSubtest() {
			super(DisfunctSetterBean.class);
		}
	}

	@Test
	public void disfunctSetterTest() throws InitializationError {
		runTest(DisfunctSetterBeanSubtest.class, "getterSetterTest");
	}

	public static class BeanSubtest extends BeanTestBase<Bean> {
		public BeanSubtest() {
			super(Bean.class);
		}

		@Override
		protected String[] getIgnorePropertyNames() {
			return new String[] { "fieldWithoutGetter" };
		}
	}

	@Test
	public void beanTest() throws InitializationError {
		runTest(BeanSubtest.class);
	}
}
