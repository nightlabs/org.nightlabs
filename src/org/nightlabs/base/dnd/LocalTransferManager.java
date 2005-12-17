/*
 * Created 	on Oct 5, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.dnd;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalTransferManager {

	/**
	 * key String: objectkey
	 * value SoftReferene: A SoftReferene to the object held here
	 */
	private Map transferObjects = new HashMap();
	
	public LocalTransferManager() {
		super();
	}
	
	
	public String addObject(Object object) {
		synchronized (transferObjects) {
			String key = Integer.toHexString(object.hashCode())+"_"+Long.toHexString(System.currentTimeMillis());
			while (transferObjects.containsKey(key))
				key = Integer.toHexString(object.hashCode())+"_"+Long.toHexString(System.currentTimeMillis());
			
			WeakReference softReference = new WeakReference(object);
			
			transferObjects.put(key, softReference);
			return key;
		}
	}
	
	public Object popObject(String key) {
		synchronized (transferObjects) {
			Object result = getObject(key);
			transferObjects.remove(key);
			return result;
		}
	}

	public Object getObject(String key) {
		synchronized (transferObjects) {
			
			WeakReference softReference = (WeakReference) transferObjects.get(key);
			if (softReference == null)
				throw new IllegalArgumentException("Could not find a Object for the given key "+key);

			if (softReference.get() == null)
				throw new IllegalStateException("No reference to the Object with key "+key+" exists any more the SoftReference was cleared");

			return softReference.get();
		}
	}
	
	
	private static LocalTransferManager sharedInstance;
	
	public static LocalTransferManager sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new LocalTransferManager();
		return sharedInstance;
	}
	
}
