package org.nightlabs.clone;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * TODO: write (Marius)
 * 
 * @author Marius Heinzmann <!-- Marius[at]NightLabs[dot]de -->
 */
public class DefaultCloneContext
	implements CloneContext
{
	private final Map<CloneableWithContext, CloneableWithContext> orig2Clone = new IdentityHashMap<CloneableWithContext, CloneableWithContext>();
	private final Map<CloneableWithContext, CloneableWithContext> clone2Orig = new IdentityHashMap<CloneableWithContext, CloneableWithContext>();

	@Override
	public boolean containsObject(Object object)
	{
		return orig2Clone.containsKey(object) || clone2Orig.containsKey(object);
	}

	@Override
	public boolean canClone(Object object)
	{
		if (object == null)
			return true;
		
		return CloneableWithContext.class.isAssignableFrom(object.getClass());
	}
	
	@Override
	public <T> T createClone(T originalObject)
		throws UnClonableException
	{
		return createClone(originalObject, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createClone(T originalObject, boolean deepCopy)
		throws UnClonableException
	{
		if (originalObject == null)
			return null;
		
		if (! CloneableWithContext.class.isAssignableFrom(originalObject.getClass()))
			throw new UnClonableException(
					"Given type cannot be cloned by the DefaultCloneContext! It doesn't implement CloneableWithContext! " +
					"\n\t givenClass = " + originalObject.getClass().getName()
			);
			
		// Note that even if the object is in the set of known objects, it may be that there is no clone set in the mapping.
		// This is the case for circular references.
		if (containsObject(originalObject))
			return getClone(originalObject);
		
		CloneableWithContext orgObject = (CloneableWithContext) originalObject;

		// add it first so that circular references are detected. 
		orig2Clone.put(orgObject, null);
		CloneableWithContext clone = orgObject.clone(this, deepCopy);
		orig2Clone.put(orgObject, clone);
		clone2Orig.put(clone, orgObject);
		
		// at that moment of time all reachable objects are cloned (depending on deepCopy)
		// => now we can link all cloned objects
		if (deepCopy)
		{
			orgObject.updateReferencesOfClone(clone, this);
		}
		
		return (T) originalObject.getClass().cast(clone);
	}

	@Override
	public <T> T getClone(T originalObject)
	{
		return getClone(originalObject, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getClone(T originalObject, boolean throwExceptionIfNotFound)
	{
		if (originalObject == null)
			return null;
		
		CloneableWithContext untypedClone = orig2Clone.get(originalObject);
		
		if (untypedClone == null && throwExceptionIfNotFound)
			throw new IllegalStateException("No cloned object for instance '"+originalObject+"' found!");
		
		return (T) originalObject.getClass().cast(untypedClone);
	}

	@Override
	public <T> T getOriginal(T clonedObject)
	{
		return getOriginal(clonedObject, false);
	}

	@Override
	public <T> T getOriginal(T clonedObject, boolean throwExceptionIfNotFound)
	{
		@SuppressWarnings("unchecked")
		T original = (T) clonedObject.getClass().cast(clone2Orig.get(clonedObject));
		
		if (original == null && throwExceptionIfNotFound)
			throw new IllegalStateException("No original object for cloned instance '"+clonedObject+"' found!");
		
		return original;
	}
}
