package org.nightlabs.keyreader;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.config.Config;

public class KeyReaderRegistry
extends AbstractEPProcessor
{
	private static KeyReaderRegistry sharedInstance = null;
	public synchronized static KeyReaderRegistry sharedInstance()
	{
		if (sharedInstance == null) {
			try {
				KeyReaderMan.createSharedInstance(Config.sharedInstance());
				sharedInstance = new KeyReaderRegistry();
				sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sharedInstance;
	}

	public String getExtensionPointID()
	{
		return "org.nightlabs.keyreader.keyReader";
	}

	public void processElement(IExtension extension, IConfigurationElement element)
			throws EPProcessorException
	{
		try {
			KeyReader keyReader = (KeyReader) element.createExecutableExtension("class");
			KeyReaderMan.sharedInstance().getKeyReaderImplementations().add(new KeyReaderImplementation(keyReader));
		} catch (Throwable e) {
			throw new EPProcessorException("Loading extension failed!", extension, e);
		}
	}

	public List<KeyReaderImplementation> getKeyReaderImplementations()
	{
		try {
			return KeyReaderMan.sharedInstance().getKeyReaderImplementations();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
