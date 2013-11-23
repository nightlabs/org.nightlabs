package org.nightlabs.test.jdo;

import java.util.Collections;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.nightlabs.test.TestTestBase;

public class PersistenceCapableTestBaseTest extends TestTestBase {
	@Ignore("Manually invoked below.")
	public static class MyPCSubtest extends PersistenceCapableTestBase<MyPC> {
		public MyPCSubtest() {
			super(MyPC.class);
		}
	}

	@Test
	public void testMyPC() throws InitializationError {
		runTest(MyPCSubtest.class);
	}

	@Ignore("Manually invoked below.")
	public static class MyPCNoPKSubtest extends PersistenceCapableTestBase<MyPCNoPK> {
		public MyPCNoPKSubtest() {
			super(MyPCNoPK.class);
		}
	}

	@Test
	public void testMyPCNoPK() throws InitializationError {
		runTest(MyPCNoPKSubtest.class, "primaryKeyTest");
	}

	@Ignore("Manually invoked below.")
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

	@Ignore("Manually invoked below.")
	public static class MyPCWithBrokenQuerySubtest extends PersistenceCapableTestBase<MyPCWithBrokenQuery> {
		public MyPCWithBrokenQuerySubtest() {
			super(MyPCWithBrokenQuery.class);
		}
	}

	@Test
	public void testMyPCWithBrokenQuery() throws InitializationError {
		runTest(MyPCWithBrokenQuerySubtest.class, "namedQueriesTest");
	}

	@Ignore("Manually invoked below.")
	public static class MyPCWithBrokenQueriesSubtest extends PersistenceCapableTestBase<MyPCWithBrokenQueries> {
		public MyPCWithBrokenQueriesSubtest() {
			super(MyPCWithBrokenQueries.class);
		}
	}

	@Test
	public void testMyPCWithBrokenQueries() throws InitializationError {
		runTest(MyPCWithBrokenQueriesSubtest.class, "namedQueriesTest");
	}

	@Ignore("Manually invoked below.")
	public static class MyPCWithParamQueryMissingParamSubtest extends PersistenceCapableTestBase<MyPCWithParamQuery> {
		public MyPCWithParamQueryMissingParamSubtest() {
			super(MyPCWithParamQuery.class);
		}
	}

	@Test
	public void testMyPCWithParamQueryMissingParam() throws InitializationError {
		// should fail as the test does not provide a parameter
		runTest(MyPCWithParamQueryMissingParamSubtest.class, "namedQueriesTest");
	}

	@Ignore("Manually invoked below.")
	public static class MyPCWithParamQuerySubtest extends PersistenceCapableTestBase<MyPCWithParamQuery> {
		public MyPCWithParamQuerySubtest() {
			super(MyPCWithParamQuery.class);
		}
		
		@Override
		protected Map<String, Object> getNamedQueryParameters(String queryName) {
			if ("queryWithParam".equals(queryName)) {
				return Collections.singletonMap("myStringParam", (Object)"BLABLABLA");
			}
			return super.getNamedQueryParameters(queryName);
		}
	}
	
	@Test
	public void testMyPCWithParamQuery() throws InitializationError {
		runTest(MyPCWithParamQuerySubtest.class);
	}
}
