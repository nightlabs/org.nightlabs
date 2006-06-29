package org.nightlabs.classloader;

import java.security.ProtectionDomain;

public interface IClassLoaderDelegate {
    public Class<?> delegateDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectionDomain);
}
