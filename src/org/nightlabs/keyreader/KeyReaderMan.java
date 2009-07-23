/* *****************************************************************************
 * KeyReader - Framework library for reading keys from arbitrary reader-devices*
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.keyreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.keyreader.config.KeyReaderCf;
import org.nightlabs.keyreader.config.KeyReaderConfigModule;
import org.nightlabs.util.IOUtil;

/**
 * This is your entry point class for using key readers. You use the shared
 * instance of this class to create or drop key readers. They are managed
 * by names, means for every name exists either none or exactly one instance of
 * a KeyReader.
 * <br/><br/>
 * Note, that there should only exist one shared instance of KeyReaderMan.
 * Use the static method sharedInstance() to get it. You need to create the
 * shared instance once when you initialize your application.
 *
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderMan
{
	private static final Logger logger = Logger.getLogger(KeyReaderMan.class);

	private static KeyReaderMan sharedInstance = null;

	public static KeyReaderMan createSharedInstance()
	throws ConfigException
	{
		return createSharedInstance(Config.sharedInstance());
	}

	/**
	 * Use this method to create and initialize the key reader manager. If it does
	 * already exist, a warning is logged. The shared instance will not be recreated!
	 *
	 * @param config The instance of config, the manager should use to read and store the settings
	 * of all key readers.
	 * @return Returns the shared instance of KeyReaderMan.
	 * @throws ConfigException
	 */
	public static synchronized KeyReaderMan createSharedInstance(Config config)
		throws ConfigException
	{
		if (sharedInstance == null)
			sharedInstance = new KeyReaderMan(config);
		else {
			if (sharedInstance.config != config)
				logger.warn("createSharedInstance(...) has already been called with another instance of Config! CanNOT recreate KeyReaderMan!", new Exception()); //$NON-NLS-1$
		}

		return sharedInstance;
	}

	/**
	 * Use this method to obtain the shared instance of KeyReaderMan
	 * @return Returns the shared instance of the key reader Manager.
	 */
	public static KeyReaderMan sharedInstance()
	{
		if (sharedInstance == null)
			throw new IllegalStateException("Shared instance of KeyReaderMan does not exist! Create one first by calling createSharedInstance(...)!"); //$NON-NLS-1$

		return sharedInstance;
	}

	protected Config config;
	protected KeyReaderConfigModule keyReaderCfMod;

	private List<KeyReaderImplementation> keyReaderImplementations = null;

	public List<KeyReaderImplementation> getKeyReaderImplementations()
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
		if (keyReaderImplementations == null) {
			synchronized(this) {
				if (keyReaderImplementations == null) {
					ArrayList<KeyReaderImplementation> res = new ArrayList<KeyReaderImplementation>();
					InputStream in = KeyReaderMan.class.getResourceAsStream("resource/KeyReaderImplementation.conf"); //$NON-NLS-1$
					if (in == null)
						throw new IllegalStateException("resource/KeyReaderImplementation.conf not found!"); //$NON-NLS-1$

					String implementationStr;
					try {
						implementationStr = IOUtil.readTextFile(in);
					} finally {
						in.close();
					}

					String[] implementationArray = implementationStr.split("\n"); //$NON-NLS-1$
					for (String className : implementationArray) {
						className = className.replace(" ", "").replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

						if ("".equals(className)) //$NON-NLS-1$
							continue;

						if (className.startsWith("#")) //$NON-NLS-1$
							continue;

						try {
							Class<?> clazz = Class.forName(className);
							KeyReader keyReader = (KeyReader) clazz.newInstance();
							res.add(new KeyReaderImplementation(keyReader));
						} catch (Throwable t) {
							logger.error("Loading/instantiating KeyReader class failed! className=" + className, t); //$NON-NLS-1$
						}
					}
					keyReaderImplementations = res;
				}
			} // synchronized(this) {
		}
		return keyReaderImplementations;
	}

	/**
	 * You should not call this constructor directly. Use createSharedInstance() and sharedInstance()!
	 *
	 * @param _config
	 * @throws ConfigException
	 */
	public KeyReaderMan(Config _config)
		throws ConfigException
	{
		this.config = _config;
		keyReaderCfMod = config
				.createConfigModule(KeyReaderConfigModule.class);

		Runtime.getRuntime().addShutdownHook(
				new Thread() {
					@Override
					public void run() {
						for (KeyReader keyReader : new ArrayList<KeyReader>(keyReaders.values())) {
							keyReader.close(true);
						}
					}
				}
		);
	}

	/**
	 * key: String keyReaderID<br/>
	 * value: KeyReader keyReader
	 */
	private HashMap<String, KeyReader> keyReaders = new HashMap<String, KeyReader>();

	/**
	 * Use this method to test whether a KeyReader exists for a certain name.
	 *
	 * @param keyReaderID The name of the interesting KeyReader.
	 * @return True if it exists, false if not.
	 */
	public synchronized boolean isKeyReaderExisting(String keyReaderID)
	{
		return keyReaders.containsKey(keyReaderID);
	}

	/**
	 * @param keyReaderID The name of the desired reader.
	 * @return Returns the key reader that is stored under the given name.
	 *
	 * @throws IllegalStateException If there is no key reader existing with this name.
	 *
	 * @see #isKeyReaderExisting(String keyReaderID)
	 */
	public synchronized KeyReader getKeyReader(String keyReaderID, boolean throwExceptionIfNotFound)
	throws IllegalStateException
	{
		KeyReader keyReader = keyReaders.get(keyReaderID);
		if (throwExceptionIfNotFound && keyReader == null)
			throw new IllegalStateException("There is no key reader existing with the name \""+keyReaderID+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$

		return keyReader;
	}

	protected synchronized void onKeyReaderClosed(KeyReader keyReader)
	{
		if (keyReader == null)
			throw new IllegalStateException("Param keyReader must not be null!"); //$NON-NLS-1$

		KeyReader removed = keyReaders.remove(keyReader.getKeyReaderID());
		if (removed == null)
			throw new IllegalStateException("The keyReader \"" + keyReader.getKeyReaderID() + "\" was not registered in this manager!"); //$NON-NLS-1$ //$NON-NLS-2$

		if (removed != keyReader) {
			keyReaders.put(removed.getKeyReaderID(), removed); // repair ;-)
			throw new IllegalStateException("The param keyReader named \"" + keyReader.getKeyReaderID() + "\" is not the same instance as the registered one!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <p>
	 * Use this method to create a key reader. Note, that the keyReaderID
	 * is unique. Which Class will be instanced, is defined in the configuration which
	 * is linked to the keyReaderID. If there is no configuration existing, an
	 * instance of DummyKeyReader will be created.
	 * </p>
	 * <p>
	 * If the {@link KeyReader} instance already exists for the given
	 * <code>keyReaderID</code>, it will silently be returned (no new instance
	 * will be created). Hence, in this case, this method behaves just like
	 * {@link #getKeyReader(String, boolean)}.
	 * </p>
	 *
	 * @param keyReaderID Name of new keyReader. The config is based on this name.
	 * @return The newly created KeyReader.
	 *
	 * @throws KeyReaderException if sth. goes wrong with the creation of
	 *	 the desired keyReader.
	 *
	 * @see KeyReader#close(boolean)
	 * @see #dropKeyReader(String, boolean)
	 */
	public synchronized KeyReader createKeyReader(String keyReaderID)
	throws KeyReaderException
	{
		KeyReader keyReader = keyReaders.get(keyReaderID);
		if (keyReader != null) {
			keyReader.open();
			return keyReader;
		}

		try {
			KeyReaderCf keyReaderCf = keyReaderCfMod
				._getKeyReaderCf(keyReaderID);

			if (keyReaderCf.getShareDeviceWith().equals("")) { //$NON-NLS-1$
				String keyReaderClassName = keyReaderCf.getKeyReaderClass();
				Class<?> keyReaderClass = null;
				try {
					keyReaderClass = Class.forName(keyReaderClassName);
				} catch (ClassNotFoundException x) {
					if (keyReaderImplementations == null)
						throw x;

					for (KeyReaderImplementation kri : keyReaderImplementations) {
						if (kri.getKeyReaderClassName().equals(keyReaderClassName)) {
							keyReaderClass = kri.getKeyReaderClass();
							break;
						}
					}

					if (keyReaderClass == null)
						throw x;
				}

				if (!KeyReader.class.isAssignableFrom(keyReaderClass))
					throw new ClassCastException("KeyReaderClass configured for key reader \""+keyReaderClass.getName()+"\" is not an inheritent of KeyReader!"); //$NON-NLS-1$ //$NON-NLS-2$

				keyReader = (KeyReader)keyReaderClass.newInstance();
				keyReader.init(this, keyReaderID, keyReaderCf);
			} // if (keyReaderCf.getShareDeviceWith().equals("")) {
			else {
				KeyReader other = createKeyReader(keyReaderCf.getShareDeviceWith());
				keyReader = new KeyReaderSharingDevice();
				keyReader.init(
					this, keyReaderID,
					keyReaderCf
				);
				((KeyReaderSharingDevice)keyReader).setRealKeyReader(other);
			} // if (keyReaderCf.getShareDeviceWith().equals("")) {

		} catch (Throwable t) {
			throw new KeyReaderException(t);
		}

		keyReaders.put(keyReaderID, keyReader);
		keyReader.open();
		return keyReader;
	}

//	/**
//	 * <p>
//	 * This method drops a key reader. It does the following steps:
//	 * <ul>
//	 * <li>It stops the key reader.</li>
//	 * <li>It closes the port.</li>
//	 * <li>It removes the registration of the key reader.</li>
//	 * </ul>
//	 * </p>
//	 * <p>
//	 * Note, that this is equivalent to calling {@link KeyReader#close(boolean)}
//	 * as this method simply delegates to {@link KeyReader#close(boolean)}.
//	 * </p>
//	 *
//	 * @param keyReaderID The name of the reader to drop.
//	 * @param wait If this is true, this method waits until the barcode reader
//	 *	 has stopped its listener thread. If you do not wait, the registration
//	 *	 will immediately be removed, but the closing of the port is always performed
//	 *	 after the reader thread has stopped.
//	 * @return <code>true</code>, if the given keyReaderID is known (i.e. a <code>KeyReader</code>
//	 *		instance exists) and <code>false</code>, if there is no <code>KeyReader</code>
//	 *		existing for the given id (in this case, the method doesn't do anything).
//	 *
//	 * @see #createKeyReader(String)
//	 */
//	public synchronized boolean dropKeyReader(String keyReaderID, boolean wait)
//	{
//		KeyReader keyReader = keyReaders.get(keyReaderID);
//		if (keyReader == null)
//			return false;
//
//		return keyReader.close(wait);
//	}

}
