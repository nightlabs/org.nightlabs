/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.dnd;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This Implementation of org.eclipse.swt.dnd.Transfer, 
 * is only for locale Transfer of Objects between Java-Applications,
 * as it uses internal {@link LocalTransferManager} which only keeps a refernce of Java-Objects,
 * which are not serialized. 
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalObjectTransfer 
extends ByteArrayTransfer
{
	
	public static final String TYPE_NAME = "local-object-transfer";
	public static int TYPE_ID = registerType(TYPE_NAME);	

	public LocalObjectTransfer() {
		super();
	}

	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	protected void javaToNative(Object object, TransferData transferData) 
	{
		String objectKey = LocalTransferManager.sharedInstance().addObject(object);
		super.javaToNative(objectKey.getBytes(), transferData);
	}

	protected Object nativeToJava(TransferData transferData) 
	{
		byte[] superResult = (byte[])super.nativeToJava(transferData);
		String transferKey = new String(superResult);
		return LocalTransferManager.sharedInstance().popObject(transferKey);		
	}
	
	protected boolean validate(Object object) {
		return true;
	}
	
	private static LocalObjectTransfer sharedInstance;
	
	public static LocalObjectTransfer sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new LocalObjectTransfer();
		return sharedInstance;
	}
	
	public static Transfer[] TRANSFERS = new Transfer[] { sharedInstance() };
	
}
