/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import org.nightlabs.concurrent.DeadLockException;
import org.nightlabs.concurrent.RWLock;
import org.nightlabs.concurrent.RWLockable;


/**
 * The base class for all ConfigModule implementations. Inherit
 * this class to create a custom config module.
 * <p>
 * To make non-atomic write and read operations thread safe, you
 * should use the {@link RWLockable} ability by calling
 * {@link #acquireReadLock()}, {@link #acquireWriteLock()} and
 * {@link #releaseLock()}.
 * <p>
 * Create a custom config module in this way:
 * <p><blockquote><pre>
 * public class MyConfigModule extends ConfigModule
 * {
 *   // a custom config variable:
 *   private String myConfigValue;
 *
 *   // provide a public default constructor:
 *   public MyConfigModule() {
 *   }
 *
 *   // initialize custom config variables with
 *   // default values if they are not already
 *   // initialized:
 *   public void init() throws InitException {
 *     if(myConfigValue == null)
 *       myConfigValue = "My default config data";
 *   }
 *
 *   // provide getters and setters for your
 *   // custom config variables:
 *   public String getMyConfigValue() {
 *     return myConfigValue;
 *   }
 *   public void setMyConfigValue(String myConfigValue) {
 *     this.myConfigValue = myConfigValue;
 *     // tell the system that the config value has
 *     // changed. You might want to check the values
 *     // to find out if they really have changed:
 *     setChanged();
 *   }
 * }
 * </pre></blockquote>
 *
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 * @author Alexander Bieber
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Niklas Schiffler
 * @author Marco Schulze
 */
public abstract class ConfigModule
	implements Serializable, Initializable, RWLockable, Cloneable
{
	private static final long serialVersionUID = 1L;

	private transient Config config;

  private String identifier;
  private String searchClass;

  public ConfigModule()
  {
  }

  public void _setConfig(Config _config)
  {
  	this.config = _config;
  }

  public Config _getConfig()
  {
  	return config;
  }

  public void init()
  throws InitException
  {
  }

  /**
   * Callback method triggered by the {@link Config} before this instance of {@link ConfigModule} is serialized to a file.
   * This method is called after {@link #acquireReadLock()} in order to be thread-safe. If this method decides to modify
   * the object in order to save different data than the application sees, this method should call
   * {@link #acquireWriteLock()} - do not forget to call {@link #releaseLock()} in this case in {@link #afterSave()}! And note that {@link #acquireWriteLock()} will cause the read-lock to be released in order to prevent dead-locks!
   */
  protected void beforeSave()
  {
  }

  /**
   * Callback method triggered by the {@link Config} after this instance of {@link ConfigModule} has been serialized to a file.
   * This method is called before {@link #releaseLock()} in order to be thread-safe.
   * <p>
   * This method is called in a finally block in order to ensure it's is really triggered in case {@link #beforeSave()} has been
   * triggered before.
   * </p>
   * @param successful <code>true</code>, if everything was fine; <code>false</code> if an exception occured during saving.
   */
  protected void afterSave(boolean successful)
  {
  }

  protected transient RWLock rwLock = new RWLock(this.getClass().getName(), true);

  private void writeObject(java.io.ObjectOutputStream out)
  throws IOException
  {
  	out.defaultWriteObject();
  }
  private void readObject(java.io.ObjectInputStream in)
  throws IOException, ClassNotFoundException
  {
  	in.defaultReadObject();
  	rwLock = new RWLock(this.getClass().getName(), true); // transient fields are null after being deserialized - even if they have a direct field initialisation as rwLock does!
  }

  public void acquireReadLock() throws DeadLockException
  {
    rwLock.acquireReadLock();
  }

  public void acquireWriteLock() throws DeadLockException
  {
    rwLock.acquireWriteLock();
  }

  public void releaseLock()
  {
    rwLock.releaseLock();
  }


  private volatile boolean changed = false;

  /**
   * This method controls, whether this config module is written to disk.
   * <br/>
   * Note, that this works only, if the Config is in multi files mode. In single
   * file mode, the whole config is always written. But, because the single file
   * mode is deprecated anyway, this should not cause head-aches.
   */
  public boolean _isChanged()
  {
    return changed;
  }

  public void setChanged() {
  	setChanged(true);
  }

  public void setChanged(boolean changed) {
  	this.changed = changed;
  }

  /**
   * Accessor method for property {@link #identifier}.
   * @return {@link #identifier}.
   */
  public String getIdentifier()
  {
    return identifier;
  }

  /**
   * Accessor method for property {@link #identifier}.
   * @param identifier The new value.
   */
  public void setIdentifier(String identifier)
  {
  	if ("null".equals(identifier))
  		throw new IllegalArgumentException("null is a reserved word and should not be used as identifier!");
    this.identifier = identifier;
  }

  /**
   * Accessor method for property {@link #searchClass}.
   * <p>
   * // TODO Marco: Who has added this? It seems to me, the work isn't complete!
   *
   * @return {@link #searchClass}.
   */
  public String getSearchClass()
  {
    return searchClass;
  }

  /**
   * Accessor method for property {@link #searchClass}.
   * @param searchClass The new value.
   */
  public void setSearchClass(String searchClass)
  {
  	if ("null".equals(searchClass))
  		throw new IllegalArgumentException("null is a reserved word and should not be used as searchClassName!");
    this.searchClass = searchClass;
  }

	/**
	 * Warning: The member config is transient and will be null at the clone!
	 * <p>
	 * The implementation of <code>clone()</code> in <code>ConfigModule</code> uses {@link Util#cloneSerializable(Object)}
	 * to create the copy. Hence, it will be a deep (and pretty complete) copy.
	 * </p>
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConfigModule clone()
	{
//		ConfigModule clone;
//		this.acquireReadLock();
//		try {
//			clone = Util.cloneSerializable(this);
//		} finally {
//			this.releaseLock();
//		}
//		return clone;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			out.close();

			ObjectInputStream in = new ClassLoaderObjectInputStream(
					new ByteArrayInputStream(buf.toByteArray()), this.getClass().getClassLoader()
			);
			Object n = in.readObject();
			in.close();
			return (ConfigModule) n;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * An {@link ObjectInputStream} instance that uses the given
	 * {@link ClassLoader} to resolve classes that are to be deserialized.
	 * @author Marc Klinger - marc[at]nightlabs[dot]de
	 */
	private static class ClassLoaderObjectInputStream extends ObjectInputStream
	{
		private ClassLoader classLoader;
		public ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
			super(in);
			this.classLoader = classLoader;
		}
		/* (non-Javadoc)
		 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
		 */
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc)
				throws IOException, ClassNotFoundException
		{
			if(classLoader == null)
				return super.resolveClass(desc);
			String name = desc.getName();
			try {
			    return Class.forName(name, false, classLoader);
			} catch (ClassNotFoundException ex) {
				return super.resolveClass(desc);
			}
		}
	}

}
