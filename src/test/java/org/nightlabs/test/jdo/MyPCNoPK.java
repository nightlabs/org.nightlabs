package org.nightlabs.test.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class MyPCNoPK {
	private String testString;
	private boolean testBoolean;

	@Persistent(persistenceModifier = PersistenceModifier.NONE)
	private Object nonPersistent;

	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	public boolean isTestBoolean() {
		return testBoolean;
	}
	public void setTestBoolean(boolean testBoolean) {
		this.testBoolean = testBoolean;
	}
	public Object getNonPersistent() {
		return nonPersistent;
	}
	public void setNonPersistent(Object nonPersistent) {
		this.nonPersistent = nonPersistent;
	}
}
