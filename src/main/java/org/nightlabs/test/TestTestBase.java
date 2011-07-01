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

	/**
	 * Run the test for the given class with optional expected failure descriptions.
	 * @param classToTest The class to test
	 * @param expectedFailures Expected test failures, i.e. test names.
	 */
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

	/**
	 * Handle failures.
	 * Check for failures that are unexpected and missing expected failures.
	 */
	private void handleFailures(Class<?> classToTest, String[] expectedFailures, final List<Failure> failures) {
		List<String> missingExpectedFailures = getMissingExpectedFailures(classToTest, expectedFailures, failures);
		List<Failure> unexpectedFailures = getUnexpectedFailures(classToTest, expectedFailures, failures);

		if (missingExpectedFailures.isEmpty() && unexpectedFailures.isEmpty()) {
			return;
		}

		String errorMessage = buildErrorMessage(classToTest, missingExpectedFailures, unexpectedFailures);
		Assert.fail(errorMessage);
	}

	private String buildErrorMessage(Class<?> classToTest, List<String> missingExpectedFailures, List<Failure> unexpectedFailures) {
		StringBuilder errors = new StringBuilder();
		if (!unexpectedFailures.isEmpty()) {
			errors.append("Test failures:");
			for (Failure failure : unexpectedFailures) {
				errors.append("\n\t");
				errors.append(failure.toString());
			}
		}
		if (!missingExpectedFailures.isEmpty()) {
			errors.append("Missing expected test failures:");
			for (String failureDesc : missingExpectedFailures) {
				errors.append("\n\t");
				errors.append(failureDesc);
				errors.append("(");
				errors.append(classToTest.getName());
				errors.append(")");
			}
		}
		String errorMessage = errors.toString();
		return errorMessage;
	}

	private List<Failure> getUnexpectedFailures(Class<?> classToTest, String[] expectedFailures, final List<Failure> failures) {
		List<Failure> unexpectedFailures = new ArrayList<Failure>();
		for (Failure failure : failures) {
			if (!isExpectedFailure(expectedFailures, failure, classToTest)) {
				unexpectedFailures.add(failure);
			}
		}
		return unexpectedFailures;
	}

	private List<String> getMissingExpectedFailures(Class<?> classToTest, String[] expectedFailures, final List<Failure> failures) {
		List<String> missingExpectedFailures = new ArrayList<String>();
		for (String expectedFailure : expectedFailures) {
			if(!containsFailure(failures, expectedFailure, classToTest)) {
				missingExpectedFailures.add(expectedFailure);
			}
		}
		return missingExpectedFailures;
	}

	/**
	 * Does the given list of failures contain a failure
	 * that matches the given failure description?
	 */
	private boolean containsFailure(List<Failure> failures, String failureDesc, Class<?> classToTest) {
		for (Failure failure : failures) {
			if(matchesFailure(failure, failureDesc, classToTest)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Does the given failure descriptions match the given failure?
	 */
	private boolean matchesFailure(Failure failure, String failureDesc, Class<?> classToTest) {
		String expectedDescription = failureDesc + "(" + classToTest.getName() + ")";
		return failure.getDescription().toString().equals(expectedDescription);
	}

	/**
	 * Does the given array of expected failure descriptions contain a description
	 * that matches the given failure?
	 */
	private boolean isExpectedFailure(String[] expectedFailures, Failure failure, Class<?> classToTest) {
		for (String failureDesc : expectedFailures) {
			if (matchesFailure(failure, failureDesc, classToTest)) {
				return true;
			}
		}
		return false;
	}
}
