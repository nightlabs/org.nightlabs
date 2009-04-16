package org.nightlabs.unifiedejbjndi.jboss;

import javax.management.ObjectName;

class EjbDescriptor {
	public EjbDescriptor(ObjectName sessionContainerName, Class<?> ejbClass, ObjectName ejb3ModuleName) {
		if (sessionContainerName == null)
			throw new IllegalArgumentException("sessionContainerName must not be null!");

		if (ejbClass == null)
			throw new IllegalArgumentException("ejbClass must not be null!");

		if (ejb3ModuleName == null)
			throw new IllegalArgumentException("ejb3ModuleName must not be null!");

		this.sessionContainerName = sessionContainerName;
		this.ejbClass = ejbClass;
		this.ejb3ModuleName = ejb3ModuleName;
	}

	private ObjectName sessionContainerName;
	private Class<?> ejbClass;
	private ObjectName ejb3ModuleName;

	public ObjectName getSessionContainerName() {
		return sessionContainerName;
	}

	public Class<?> getEjbClass() {
		return ejbClass;
	}

	public ObjectName getEjb3ModuleName() {
		return ejb3ModuleName;
	}

	@Override
	public int hashCode() {
		return 31 + sessionContainerName.hashCode(); // sessionContainerName must never be null (see constructor)
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;

		EjbDescriptor other = (EjbDescriptor) obj;
		return this.sessionContainerName.equals(other.sessionContainerName); // sessionContainerName must never be null (see constructor)
	}

	@Override
	public String toString() {
		return this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + sessionContainerName + ',' + ejbClass + ',' + ejb3ModuleName + ']';
	}
}
