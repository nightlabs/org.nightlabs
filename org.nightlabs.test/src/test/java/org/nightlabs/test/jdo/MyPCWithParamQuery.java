package org.nightlabs.test.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Query;

@PersistenceCapable
@Query(name="queryWithParam", value="SELECT FROM org.nightlabs.test.jdo.MyPCWithParamQuery WHERE testString == :myStringParam")
public class MyPCWithParamQuery {
	@PrimaryKey
	private long id;
	private String testString;
	private boolean testBoolean;

	@Persistent(persistenceModifier = PersistenceModifier.NONE)
	private Object nonPersistent;

	public long getId() {
		return id;
	}

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
