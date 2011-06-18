package org.nightlabs.test;

public class DisfunctSetterBean {
	private String testData;

	public void setTestData(String testData) {
		this.testData = testData + "some other data";
	}

	public String getTestData() {
		return testData;
	}
}
