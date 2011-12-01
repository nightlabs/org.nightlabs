/**
 * 
 */
package org.nightlabs.singleton;

/**
 * @author abieber
 *
 */
public class SimpleServiceContext implements IServiceContext {

	/**
	 * 
	 */
	public SimpleServiceContext() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.singleton.IServiceContext#associateThread(java.lang.Thread)
	 */
	@Override
	public void associateThread(Thread thread) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.singleton.IServiceContext#associateThread()
	 */
	@Override
	public void associateThread() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.singleton.IServiceContext#disposeThread(java.lang.Thread)
	 */
	@Override
	public void disposeThread(Thread thread) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.singleton.IServiceContext#disposeThread()
	 */
	@Override
	public void disposeThread() {
	}

}
