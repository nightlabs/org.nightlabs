/**
 * 
 */
package org.nightlabs.classloader.url.testclasses4;

/**
 * @author abieber
 *
 */
public class A {
	/** References testclasses1.A so it can only be loaded when this is in classpath as well */
	public static org.nightlabs.classloader.url.testclasses1.A reference = new org.nightlabs.classloader.url.testclasses1.A();
}
