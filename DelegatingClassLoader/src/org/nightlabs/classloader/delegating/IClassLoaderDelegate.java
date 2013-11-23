package org.nightlabs.classloader.delegating;

import java.security.ProtectionDomain;

public interface IClassLoaderDelegate {
    public Class<?> delegateDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectionDomain);
}
