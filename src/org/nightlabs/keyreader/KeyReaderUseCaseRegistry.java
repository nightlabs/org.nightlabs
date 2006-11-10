package org.nightlabs.keyreader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.config.Config;

public class KeyReaderUseCaseRegistry
		extends AbstractEPProcessor
{
	private static KeyReaderUseCaseRegistry sharedInstance = null;
	public synchronized static KeyReaderUseCaseRegistry sharedInstance()
	{
		if (sharedInstance == null) {
			try {
				KeyReaderMan.createSharedInstance(Config.sharedInstance());
				sharedInstance = new KeyReaderUseCaseRegistry();
				sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sharedInstance;
	}

	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.keyreader.keyReaderUseCase";
	}

	private Map<String, KeyReaderUseCase> keyReaderUseCases = new HashMap<String, KeyReaderUseCase>();

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
			throws EPProcessorException
	{
		String keyReaderID = element.getAttribute("keyReaderID");
		if (keyReaderID == null || "".equals(keyReaderID))
			throw new EPProcessorException("The attribute 'keyReaderID' is missing or empty!", extension);

		if (keyReaderUseCases.containsKey(keyReaderID))
			throw new EPProcessorException("Duplicate definition of keyReaderID: " + keyReaderID, extension);

		String name = element.getAttribute("name");
		if (name == null) name = "";
		String description = element.getAttribute("description");
		if (description == null) description = "";

		keyReaderUseCases.put(keyReaderID, new KeyReaderUseCase(keyReaderID, name, description));
	}

	public KeyReaderUseCase getKeyReaderUseCase(String keyReaderID, boolean throwExceptionIfNotFound)
	{
		KeyReaderUseCase res = keyReaderUseCases.get(keyReaderID);
		if (throwExceptionIfNotFound && res == null)
			throw new IllegalArgumentException("No KeyReaderUseCase registered for keyReaderID=" + keyReaderID);
		return res;
	}

	public Collection<KeyReaderUseCase> getKeyReaderUseCases()
	{
		return keyReaderUseCases.values();
	}
}
