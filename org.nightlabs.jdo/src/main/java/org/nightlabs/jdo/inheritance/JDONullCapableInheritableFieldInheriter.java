/**
 * 
 */
package org.nightlabs.jdo.inheritance;

import org.nightlabs.inheritance.Inheritable;
import org.nightlabs.inheritance.InheritanceManager;
import org.nightlabs.inheritance.NullCapableInheritableFieldInheriter;

/**
 * @author abieber
 *
 */
public class JDONullCapableInheritableFieldInheriter<IType extends Inheritable, FType extends Inheritable> extends
		NullCapableInheritableFieldInheriter<IType, FType> {

	/**
	 * @param childFieldCreator
	 */
	public JDONullCapableInheritableFieldInheriter(ChildFieldCreator<IType, FType> childFieldCreator) {
		super(childFieldCreator);
	}
	
	@Override
	protected InheritanceManager createInheritanceManager() {
		return new JDOInheritanceManager();
	}
}
