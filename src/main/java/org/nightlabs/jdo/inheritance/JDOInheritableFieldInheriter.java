package org.nightlabs.jdo.inheritance;

import org.nightlabs.inheritance.InheritableFieldInheriter;
import org.nightlabs.inheritance.InheritanceManager;

public class JDOInheritableFieldInheriter extends InheritableFieldInheriter {

	@Override
	protected InheritanceManager createInheritanceManager() {
		return new JDOInheritanceManager();
	}

}
