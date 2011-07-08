package org.nightlabs.concurrent;

import org.nightlabs.test.ExceptionTestBase;

public class DeadLockExceptionTest extends ExceptionTestBase<DeadLockException> {
	public DeadLockExceptionTest() {
		super(DeadLockException.class);
	}
}
