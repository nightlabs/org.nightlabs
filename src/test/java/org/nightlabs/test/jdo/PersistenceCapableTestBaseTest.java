package org.nightlabs.test.jdo;

import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.nightlabs.test.TestTestBase;

public class PersistenceCapableTestBaseTest extends TestTestBase {
	public static class MyPCSubtest extends PersistenceCapableTestBase<MyPC> {
		public MyPCSubtest() {
			super(MyPC.class);
		}
	}

	@Test
	public void testMyPC() throws InitializationError {
		runTest(MyPCSubtest.class);
	}

	public static class MyPCNoPKSubtest extends PersistenceCapableTestBase<MyPCNoPK> {
		public MyPCNoPKSubtest() {
			super(MyPCNoPK.class);
		}
	}

	@Test
	public void testMyPCNoPK() throws InitializationError {
		runTest(MyPCNoPKSubtest.class, "primaryKeyTest");
	}

	public static class MyPCNoPKOkSubtest extends PersistenceCapableTestBase<MyPCNoPK> {
		public MyPCNoPKOkSubtest() {
			super(MyPCNoPK.class);
		}

		@Override
		protected boolean allowMissingPrimaryKey() {
			return true;
		}
	}

	@Test
	public void testMyPCNoPKOk() throws InitializationError {
		runTest(MyPCNoPKOkSubtest.class);
	}
}
