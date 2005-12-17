/*
 * Created 	on Oct 28, 2004
 * 					by Alexander Bieber
 *
 */
package org.nightlabs.base.extensionpoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * @author Alexander Bieber
 */
public interface IEPProcessor {
	String getExtensionPointID();
	
	void processElement(IExtension extension, IConfigurationElement element) throws EPProcessorException;
  
  void process() throws EPProcessorException;
}
