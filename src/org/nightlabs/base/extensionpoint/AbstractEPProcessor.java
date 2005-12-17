/*
 * Created 	on Oct 28, 2004
 * 					by Alexander Bieber
 *
 */
package org.nightlabs.base.extensionpoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Used for parsing extensions to a certian point.
 * Calling {@link #process()} will cause {@link #processElement(IExtension, IConfigurationElement)}
 * to be called for every extension defined to the point returned by {@link #getExtensionPointID()}.
 * The IConfigurationElement passed to processElement is the root element of the extension.
 * Usually one will use element.createExecutableExtension() to get a instance of
 * the extensions object.<br/>
 * A common usage is subclassing this to a registry which registeres its
 * entries in processElement lazily by doing 
 * <pre>
 *	if (!isProcessed())
 *		process();
 * </pre>
 * everytime when asked for one element fist.
 * 
 * @author Alexander Bieber
 */
public abstract class AbstractEPProcessor 
implements IEPProcessor
{

	public abstract String getExtensionPointID();

	public abstract void processElement(IExtension extension, IConfigurationElement element) throws EPProcessorException;
  
	private boolean processed = false;
	public boolean isProcessed() {
		return processed;
	}
	
	private boolean processing = false;
	
	protected boolean isProcessing() {
		return processing;
	}

  public synchronized void process() throws EPProcessorException{
  	processing = true;
  	try {
	    try{
	    	IExtensionRegistry registry = Platform.getExtensionRegistry();
	    	if (registry != null) 
	    	{
		    	IExtensionPoint extensionPoint = registry.getExtensionPoint(getExtensionPointID());
		    	if (extensionPoint == null) {
		    		throw new EPProcessorException(
		    				"Unable to resolve extension-point: " + getExtensionPointID());
		    	}	        
		
		      IExtension[] extensions = extensionPoint.getExtensions();
		      // For each extension ...
		      for (int i = 0; i < extensions.length; i++) {           
		          IExtension extension = extensions[i];
		          IConfigurationElement[] elements = 
		            extension.getConfigurationElements();
		          // For each member of the extension ...
		          for (int j = 0; j < elements.length; j++) {
		          	IConfigurationElement element = elements[j];
		          	processElement(extension, element);               
		          }
		      }
		      processed = true;	    		
	    	}
	    }catch(Throwable e){
	    	if (e instanceof EPProcessorException)
	    		throw (EPProcessorException)e;
	    	else
	    		throw new EPProcessorException(e);
	    }
  	} finally {
  		processing = false;
  	}
  }

	/**
	 * Assures that this processor 
	 * has processed its extensions
	 */
  public void checkProcessing() 
  {
  	if (!isProcessed()) {
  		try {
  			process();
  		} catch (EPProcessorException e) {
  			throw new RuntimeException(e);
  		}  	  		
  	}
  }
  
  /**
   * 
   * @param s the String to check
   * @return true if the String is neither null nor an empty String otherwise 
   * returns false
   */
	protected boolean checkString(String s) 
	{
		if (s == null || "".equals(s))
			return false;
		
		return true;
	}  
}
