package org.nightlabs.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

public class ExceptionTestBaseTest extends TestTestBase {

	@Ignore("Manually invoked below.")
	public static class ExceptionSubtest extends ExceptionTestBase<MyException> {
		public ExceptionSubtest() {
			super(MyException.class);
		}
	}

	@Test
	public void testException() throws InitializationError {
		runTest(ExceptionSubtest.class);
	}

	@Ignore("Manually invoked below.")
	public static class DisfunctException2Subtest extends ExceptionTestBase<DisfunctException2> {
		public DisfunctException2Subtest() {
			super(DisfunctException2.class);
		}
	}

	@Test
	public void testDisfunctException2() throws InitializationError {
		runTest(DisfunctException2Subtest.class, "testConstructor2");
	}

	@Ignore("Manually invoked below.")
	public static class DisfunctException3Subtest extends ExceptionTestBase<DisfunctException3> {
		public DisfunctException3Subtest() {
			super(DisfunctException3.class);
		}
	}

	@Test
	public void testDisfunctException3() throws InitializationError {
		runTest(DisfunctException3Subtest.class, "testConstructor3");
	}

	@Ignore("Manually invoked below.")
	public static class DisfunctException4Subtest extends ExceptionTestBase<DisfunctException4> {
		public DisfunctException4Subtest() {
			super(DisfunctException4.class);
		}
	}

	@Test
	public void testDisfunctException4() throws InitializationError {
		runTest(DisfunctException4Subtest.class, "testConstructor4");
	}
}
