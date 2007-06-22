/* *****************************************************************************
 * DelegatingClassLoader - NightLabs extendable classloader                    *
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

package org.nightlabs.classloader;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The JFire class loading mechanism consists out of multiple parts:<br/>
 * <ul>
 *   <li>DelegatingClassLoader (project DelegatingClassLoader): A system class loader that allows delegates to plug in at a later point.</li>
 *   <li>JFireCLDelegate (project JFireRemoteClassLoader): Client to load classes from the JFire j2ee server (caching them locally).</li>
 *   <li>Backend (projects JFireRCLBackend, JFireRCLBackendBean): The backend provides the classes to the client.</li>
 *   <li>EPClassLoaderDelegate (project org.nightlabs.classsharing): A local delegate that provides a global class namespace (necessary, if mother needs to load child classes).</li>
 * </ul>
 *
 * The DelegatingClassLoader must be declared to be the system class loader of
 * the application. This is done by the VM parameter 
 * <tt>-Djava.system.class.loader=org.nightlabs.ipanema.classloader.boot.DelegatingClassLoader</tt>.
 * <br/><br/>
 * For this to work, the project DelegatingClassLoader, which contains this class loader must
 * be defined to be part of the bootstrap class path. Note, that there are two bootstrap paths:
 * First, the real JVM bootstrap classpath: To define this one,
 * you use the system property <tt>sun.boot.class.path</tt>. Second, when working with OSGI (in Eclipse RCP),
 * you put sth. like "-Xbootclasspath/p:startup.jar;plugins\a.b.c.jar;plugins\d.e.f.jar" into the eclipse.ini.
 * <br/><br/>
 * A possible boot classpath param could look like this:
 * <tt>-Dsun.boot.class.path=/opt/java/j2sdk1.4.2_05/jre/lib/rt.jar:/opt/java/j2sdk1.4.2_05/jre/lib/i18n.jar:/opt/java/j2sdk1.4.2_05/jre/lib/sunrsasign.jar:/opt/java/j2sdk1.4.2_05/jre/lib/jsse.jar:/opt/java/j2sdk1.4.2_05/jre/lib/jce.jar:/opt/java/j2sdk1.4.2_05/jre/lib/charsets.jar:/opt/java/j2sdk1.4.2_05/jre/classes:/opt/java/ipanema/JFireBoot.jar</tt>
 * <br/><br/>
 * If the DelegatingClassLoader is not loadable by the bootstrap loader, a different system class loader will be
 * instantiated by the java runtime and this class will log a warning to the console!
 *
 * @author marco
 */
public class DelegatingClassLoader
	extends ClassLoader
	implements IClassLoaderDelegate, IClassLoadingDelegator
{
	static {
		if (DelegatingClassLoader.class.getClassLoader() != null) {
			log_info("static init", "DelegatingClassLoader seems not to be loaded by the bootstrap loader! Make sure, it is part of the bootstrap classpath. Check system property 'sun.boot.class.path'!");
			log_info("static init", "sun.boot.class.path=" + System.getProperty("sun.boot.class.path"));
			log_info("static init", "Class loader is " + DelegatingClassLoader.class.getClassLoader());
		}
		else
			log_info("static init", "DelegatingClassLoader.class loaded by bootstrap loader.");
	}

	private static void log_debug(String method, String message)
	{
		if (!LogUtil.debugEnabled)
			return;

		LogUtil.log_debug(DelegatingClassLoader.class, method, message);
	}
	private static void log_info(String method, String message)
	{
		LogUtil.log_info(DelegatingClassLoader.class, method, message);
	}
	private static void log_error(String method, String message)
	{
		LogUtil.log_error(DelegatingClassLoader.class, method, message);
	}

	private ClassLoadingDelegator classLoadingDelegator;

	public DelegatingClassLoader(ClassLoader _parent)
	{
		// super(null); // we ignore the parent to force this classloader to be the direct child of the bootstrap loader.
		super(_parent);
		classLoadingDelegator = new ClassLoadingDelegator(this);
		classLoadingDelegator.parseLibPaths(new String[] {System.getProperty("java.ext.dirs"), System.getProperty("java.library.path")});

		classLoadingDelegator.parseClassPaths(new String[] {System.getProperty("java.ext.dirs")}, new ClassLoadingDelegator.JarFilter());
		classLoadingDelegator.parseClassPaths(new String[] {System.getProperty("java.class.path")}, null);
		log_info("init", "DelegatingClassLoader instantiated.");
	}

	//This method is invoked by the virtual machine to load a class.
	// TODO there's a lot of magic in this classloading stuff, thus I don't know whether this
	// method would ever be called. Even if this method is never called, it probably doesn't hurt, either ;-)
  @SuppressWarnings("unused")
	private Class loadClassInternal(String name)
  throws ClassNotFoundException
  {
  	log_info("loadClassInternal(String name)", "Amazing! The classloader magic calls my loadClassInternal method!");
  	return loadClass(name);
  }

  private Map<Thread, Integer> loadClassThreadMap = new HashMap<Thread, Integer>();

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException
	{
		int loadClassThreadMapSize;
		synchronized(loadClassThreadMap) {
			Integer lccI = loadClassThreadMap.get(Thread.currentThread());
			if (lccI != null)
				loadClassThreadMap.put(Thread.currentThread(), new Integer(lccI.intValue() + 1));
			else
				loadClassThreadMap.put(Thread.currentThread(), new Integer(1));

			loadClassThreadMapSize = loadClassThreadMap.size();
		}
		try {

			if (loadClassThreadMapSize > 1) { // if there is another thread doing loadClass
				try {
					this.wait(500);
					System.out.println("#################################################################################");
					System.out.println("### " + Thread.currentThread().getName());
					System.out.println("#################################################################################");
				} catch (InterruptedException e) {
					// ignore
				} catch (IllegalMonitorStateException e) {
					// ignore
				}
			}

			Class c;
			try {
				c = super.loadClass(name, resolve);
			} catch (ClassNotFoundException x) {
	//			log_info("loadClass(String name, boolean resolve)", "Before _findClass("+name+")");
				
				c = findDelegateClass(name);
				
	//			log_info("loadClass(String name, boolean resolve)", "After _findClass("+name+"), before sleep");
	//			try {
	//				Thread.sleep(10000);
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//			log_info("loadClass(String name, boolean resolve)", "After sleep, before resolveClass("+name+")");			
				
				resolveClass(c);
			}
			return c;

		} finally {
			synchronized(loadClassThreadMap) {
				Integer lccI = loadClassThreadMap.get(Thread.currentThread());
				if (lccI.intValue() > 1)
					loadClassThreadMap.put(Thread.currentThread(), new Integer(lccI.intValue() - 1));
				else
					loadClassThreadMap.remove(Thread.currentThread());
			}
		}
	}

	@Override
	protected URL findResource(String name)
	{
		log_debug("findResource", "DelegatingClassLoader.findResource(\""+name+"\")");

		List resources;
		try {
			resources = findDelegateResources(name, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (resources == null || resources.isEmpty())
			return null;
		else
			return (URL)resources.get(0);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException
	{
		log_debug("findResources", "DelegatingClassLoader.findResources(\""+name+"\")");

		List<URL> resources = findDelegateResources(name, false);
		if (resources != null)
			return new ClassLoadingDelegator.ResourceEnumeration<URL>(resources.iterator());
		return null;
	}

	@Override
	protected String findLibrary(String libname)
	{
		// TODO I guess, it is necessary to implement this! Probably, the bootstrap loader ignores the system properties?!
		log_error("findLibrary", "DelegatingClassLoader.findLibrary(\""+libname+"\") is unable to find the library. The method is not yet implemented!");
		return null;
	}
	
	
	public Class<?> delegateDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectionDomain) {
		return defineClass(name, b, off, len, protectionDomain);
	}
	
	public Class findDelegateClass(String name) throws ClassNotFoundException {
		return classLoadingDelegator.findDelegateClass(name);
	}
	public List<URL> findDelegateResources(String name, boolean returnAfterFoundFirst) throws IOException {
		return classLoadingDelegator.findDelegateResources(name, returnAfterFoundFirst);
	}
	
	public void addDelegate(ClassDataLoaderDelegate delegate) {
		classLoadingDelegator.addDelegate(delegate);
	}
	public void addDelegate(ClassLoaderDelegate delegate) {
		classLoadingDelegator.addDelegate(delegate);
	}
	public void removeDelegate(Object delegate) {
		classLoadingDelegator.addDelegate(delegate);
	}
}
