package org.nightlabs.singleton;

public interface IServiceContext {
	void associateThread(Thread thread);
	void associateThread();
	
	void disposeThread(Thread thread);
	void disposeThread();
}
