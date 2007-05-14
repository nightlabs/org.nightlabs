/**
 * 
 */
package org.nightlabs.base.notification;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.notification.Interceptor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NotificationManagerInterceptorEPProcessor extends AbstractEPProcessor {

	private org.nightlabs.notification.NotificationManager manager;
	
	/**
	 * 
	 */
	public NotificationManagerInterceptorEPProcessor(org.nightlabs.notification.NotificationManager manager) {
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.base.notificationinterceptor";
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(
		IExtension extension,
		IConfigurationElement element
	) throws EPProcessorException 
	{
		try {
			Interceptor interceptor = (Interceptor) element.createExecutableExtension("class");
			element.getAttribute("name");

			manager.addInterceptor(interceptor);
		} catch (Throwable e) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", e);
		}
	}

}
