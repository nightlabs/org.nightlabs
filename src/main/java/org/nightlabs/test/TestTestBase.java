package org.nightlabs.test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * A test base class for testing tests (!).
 * @author @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class TestTestBase {

	private Runner getRunner(Class<?> classToTest) throws InitializationError {
		RunWith runWith = classToTest.getAnnotation(RunWith.class);
		if (runWith == null) {
			return new BlockJUnit4ClassRunner(classToTest);
		} else {
			try {
				Constructor<? extends Runner> constructor = runWith.value().getConstructor(Class.class);
				return constructor.newInstance(classToTest);
			} catch(Exception e) {
				throw new InitializationError(e);
			}
		}
	}

	protected void runTest(Class<?> classToTest, String... expectedFailures) throws InitializationError {
		final List<Failure> failures = new ArrayList<Failure>();
		Runner runner = getRunner(classToTest);
		runner.run(new RunNotifier() {
			@Override
			public void fireTestAssumptionFailed(Failure failure) {
				failures.add(failure);
			}
			@Override
			public void fireTestFailure(Failure failure) {
				failures.add(failure);
			}
		});
		handleFailures(classToTest, expectedFailures, failures);
	}

	private void handleFailures(Class<?> classToTest, String[] expectedFailures, final List<Failure> failures) {
		if (failures.isEmpty()) {
			return;
		}
		boolean haveUnexpectedFailures = false;
		if (expectedFailures == null || expectedFailures.length == 0) {
			haveUnexpectedFailures = true;
		} else {
			outer:
				for (Failure failure : failures) {
					for (String expectedFailure : expectedFailures) {
						String expectedDescription = expectedFailure + "(" + classToTest.getName() + ")";
						if (failure.getDescription().toString().equals(expectedDescription)) {
							continue outer;
						}
					}
					haveUnexpectedFailures = true;
				}
		}
		if (haveUnexpectedFailures) {
			StringBuilder sb = new StringBuilder("Test failures:");
			for (Failure failure : failures) {
				sb.append("\n\t");
				sb.append(failure.toString());
			}
			Assert.fail(sb.toString());
		}
	}
}
