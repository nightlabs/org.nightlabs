package org.nightlabs.clone;

/**
 * TODO: write (Marius)
 * 
 * @author Marius Heinzmann <!-- Marius[at]NightLabs[dot]de -->
 */
public interface CloneContext
{
	boolean containsObject(Object object);
	
	<T> T getClone(T originalObject);
	<T> T getClone(T originalObject, boolean throwExceptionIfNotFound);
	
	// TODO: are these two methods really necessary?? (Marius)
	<T> T getOriginal(T clonedObject);
	<T> T getOriginal(T clonedObject, boolean throwExceptionIfNotFound);
	
	boolean canClone(Object object);

	<T> T createClone(T originalObject)
		throws UnClonableException;
	
	<T> T createClone(T originalObject, boolean deepCopy)
		throws UnClonableException;
}
