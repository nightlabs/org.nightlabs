package org.nightlabs.test;

public class DisfunctGetterBean {
	private String testData;

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public String getTestData() {
		return testData + "some other data";
	}
}
