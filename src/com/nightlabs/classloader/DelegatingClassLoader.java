/* ************************************************************************** *
 * Copyright (C) 2004 NightLabs GmbH, Marco Schulze                           *
 * All rights reserved.                                                       *
 * http://www.NightLabs.de                                                    *
 *                                                                            *
 * This program and the accompanying materials are free software; you can re- *
 * distribute it and/or modify it under the terms of the GNU Lesser General   *
 * Public License as published by the Free Software Foundation; either ver 2  *
 * of the License, or any later version.                                      *
 *                                                                            *
 * This module is distributed in the hope that it will be useful, but WITHOUT *
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FIT- *
 * NESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more *
 * details.                                                                   *
 *                                                                            *
 * You should have received a copy of the GNU General Public License along    *
 * with this module; if not, write to the Free Software Foundation, Inc.:     *
 *    59 Temple Place, Suite 330                                              *
 *    Boston MA 02111-1307                                                    *
 *    USA                                                                     *
 *                                                                            *
 * Or get it online:                                                          *
 *    http://www.opensource.org/licenses/lgpl-license.php                     *
 *                                                                            *
 * In case, you want to use this module or parts of it in a commercial way    * 
 * that is not allowed by the LGPL, pleas contact us and we will provide a    *
 * commercial licence.                                                        *
 * ************************************************************************** */

/*
 * Created on 02.10.2004
 */
package com.nightlabs.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The Ipanema class loading mechanism consists out of multiple parts:<br/>
 * <ul>
 *   <li>DelegatingClassLoader (project IpanemaBoot): A system class loader that allows delegates to plug in at a later point.</li>
 *   <li>IpanemaCLDelegate (project IpanemaClassLoader): Client to load classes from the Ipanema j2ee server (caching them locally).</li>
 *   <li>Backend (projects IpanemaCLBackend, IpanemaCLBackendBean): The backend provides the classes to the client.</li>
 * </ul>
 *
 * The DelegatingClassLoader must be declared to be the system class loader of
 * the application. This is done by the VM parameter 
 * <tt>-Djava.system.class.loader=com.nightlabs.ipanema.classloader.boot.DelegatingClassLoader</tt>.
 * <br/><br/>
 * For this to work, the project IpanemaBoot, which contains this class loader must
 * be defined to be part of the bootstrap class path. To define the bootstrap class
 * path, you use the system property <tt>sun.boot.class.path</tt>.
 * <br/><br/>
 * A possible boot classpath param could look like this:
 * <tt>-Dsun.boot.class.path=/opt/java/j2sdk1.4.2_05/jre/lib/rt.jar:/opt/java/j2sdk1.4.2_05/jre/lib/i18n.jar:/opt/java/j2sdk1.4.2_05/jre/lib/sunrsasign.jar:/opt/java/j2sdk1.4.2_05/jre/lib/jsse.jar:/opt/java/j2sdk1.4.2_05/jre/lib/jce.jar:/opt/java/j2sdk1.4.2_05/jre/lib/charsets.jar:/opt/java/j2sdk1.4.2_05/jre/classes:/opt/java/ipanema/IpanemaBoot.jar</tt>
 * <br/><br/>
 * If the DelegatingClassLoader is not loadable by the bootstrap loader, a different system class loader will be
 * instantiated by the java runtime and this class will log a warning to the console!
 *
 * @author marco
 */
public class DelegatingClassLoader
	extends ClassLoader
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

	/**
	 * Inner class ResourceSearchResult holds already found
	 * resources and remembers if the search was for the complete
	 * list or for one URL only.
	 * This is used internally for checking wheather a resource was
	 * already searched for.
	 * @author Alexander Bieber
	 */
	protected class ResourceSearchResult {
		
		public ResourceSearchResult(boolean fullSearch, List resources) {
			this.wasFullSearch = fullSearch;
			this.searchFoundResources = resources;
		}
		
		protected boolean wasFullSearch = false;
		protected List searchFoundResources;
		
		public List getFoundResources() {
			return searchFoundResources;
		}
		public void setFoundResources(List foundResources) {
			this.searchFoundResources = foundResources;
		}
		public boolean wasFullSearch() {
			return wasFullSearch;
		}
		
		public void setWasFullSearch(boolean wasFullSearch) {
			this.wasFullSearch = wasFullSearch;
		}
	}
	
	/**
	 * This is used internally for checking weather a class was already
	 * searched for. 
	 * @author Alexander Bieber
	 */
	protected class ClassSearchResult {
		
		public ClassSearchResult(Class _class) {
			this.foundClass = _class;
		}
		
		protected Class foundClass;
		
		public Class getFoundClass() {
			return foundClass;
		}
		public void setFoundClass(Class foundClass) {
			this.foundClass = foundClass;
		}
	}
	

	public static class PathEntry
	{
		private File path;

		public PathEntry(File _path)
		{
			this.path = _path;
		}

		private boolean initialized = false;		
		private boolean isdirectory;
		private boolean isjar;

		/**
		 * contains an instance of JarFile (or null, if memory short)
		 */
		private Reference jarFile = null;

		/**
		 * @return Returns the path.
		 */
		public File getPath()
		{
			return path;
		}

		private void init()
		{
			if (initialized)
				return;

			isdirectory = path.isDirectory();
			if (isdirectory)
				isjar = false;
			else {
				try {
					JarFile jf = new JarFile(path);
					isjar = true;
				} catch (IOException e) {
					isjar = false;
				}
			}

			initialized = true;
		}
		
		/**
		 * @return Returns the directory.
		 */
		public boolean isDirectory()
		{
			init();
			return isdirectory;
		}
		/**
		 * @return Returns the jar.
		 */
		public boolean isJar()
		{
			init();
			return isjar;
		}
		/**
		 * @return Returns the jarFile.
		 */
		public JarFile getJarFile()
		{
			JarFile jf = null;
			if (jarFile != null)
				jf = (JarFile)jarFile.get();

			if (jf == null) {
				init();
				if (!isJar())
					throw new IllegalStateException("PathEntry \""+path.getAbsolutePath()+"\" does not represent a jar, but you try to access it like one!");
				try {
					jf = new JarFile(path);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				jarFile = new WeakReference(jf); // Because the jarfiles otherwise stay open - which might cause problems because of the limited number of filehandles -, I use WeakReferences
			}
			return jf;
		}
	}

	private List libpath = new LinkedList();
	private List classpath = new LinkedList();
	
	public void addPathEntry(PathEntry pathEntry) {
		classpath.add(pathEntry);
	}

	private static class JarFilter implements FilenameFilter{
		/**
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name)
		{
			try {
				JarFile jf = new JarFile(new File(dir, name));
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}

	private List delegates = new LinkedList();

	/**
	 * The parent classloader is normally ignored to force this class loader being the begin
	 * of the chain directly after the bootstrap loader.
	 */
	private ClassLoader parent;

	public static boolean debugEnabled = false;

	private static void log_debug(String method, String message)
	{
		if (!debugEnabled)
			return;

		log_debug(DelegatingClassLoader.class, method, message);
	}
	private static void log_info(String method, String message)
	{
		log_info(DelegatingClassLoader.class, method, message);
	}
	private static void log_warn(String method, String message)
	{
		log_warn(DelegatingClassLoader.class, method, message);
	}
	private static void log_warn(String method, String message, Throwable x)
	{
		log_warn(DelegatingClassLoader.class, method, message, x);
	}
	private static void log_error(String method, String message)
	{
		log_error(DelegatingClassLoader.class, method, message);
	}
	private static void log_error(String method, String message, Throwable x)
	{
		log_error(DelegatingClassLoader.class, method, message, x);
	}

	public static void log_debug(Class clazz, String method, String message)
	{
		if (!debugEnabled)
			return;
		System.out.println(System.currentTimeMillis() + " DEBUG [" + Thread.currentThread().getName() + "] " + message);
	}
	public static void log_info(Class clazz, String method, String message)
	{
		System.out.println(System.currentTimeMillis() + " INFO [" + Thread.currentThread().getName() + "] " + message);
	}
	public static void log_warn(Class clazz, String method, String message)
	{
		log_warn(clazz, method, message, null);
	}
	public static void log_warn(Class clazz, String method, String message, Throwable x)
	{
		System.err.println(System.currentTimeMillis() + " WARNING [" + Thread.currentThread().getName() + "] " + message);
		if (x != null)
			x.printStackTrace();
	}
	public static void log_error(Class clazz, String method, String message)
	{
		log_error(clazz, method, message, null);
	}
	public static void log_error(Class clazz, String method, String message, Throwable x)
	{
		System.err.println(System.currentTimeMillis() + " ERROR [" + Thread.currentThread().getName() + "] " + message);
		if (x != null)
			x.printStackTrace();
	}

	private static String pathSeparator = System.getProperty("path.separator");
	private static void addPathEntry(Collection pathCollection, File pathItem, boolean recursiveDirs, FilenameFilter dirListFilter)
	{
		if (recursiveDirs) {
			if (!pathItem.isDirectory())
				pathCollection.add(new PathEntry(pathItem));
			else {
				String[] files = pathItem.list(dirListFilter);
				for (int i = 0; i < files.length; ++i) {
					addPathEntry(pathCollection, new File(pathItem, files[i]), recursiveDirs, dirListFilter);
				}
			}
		}
		else
			pathCollection.add(new PathEntry(pathItem));
	}
	private static void parsePathProperty(Collection pathCollection, String property, boolean recursiveDirs, FilenameFilter dirListFilter)
	{
		if (property != null)
		{
			if (property.indexOf(pathSeparator) != -1) {
				String[] pathItems = property.split(pathSeparator);
				for (int i = 0; i < pathItems.length; ++i) {
					addPathEntry(pathCollection, new File(pathItems[i]), recursiveDirs, dirListFilter);
				}
			}
			else {
				addPathEntry(pathCollection, new File(property), recursiveDirs, dirListFilter);
			}
		}
	}

	public DelegatingClassLoader(ClassLoader _parent)
	{
		// super(null); // we ignore the parent to force this classloader to be the direct child of the bootstrap loader.
		super(_parent);
		this.parent = _parent;

		// Add the system paths...
		parsePathProperty(libpath,   System.getProperty("java.ext.dirs"), false, null);
		parsePathProperty(libpath,   System.getProperty("java.library.path"), false, null);

		parsePathProperty(classpath, System.getProperty("java.ext.dirs"), true, new JarFilter());
		parsePathProperty(classpath, System.getProperty("java.class.path"), false, null);

		log_info("init", "DelegatingClassLoader instantiated.");
	}

	public void clearCache() {
		synchronized(foundResources){foundResources.clear();}
		synchronized(foundClasses){foundClasses.clear();}
	}

	public synchronized void addDelegate(ClassDataLoaderDelegate delegate) {
		addDelegate((Object)delegate);
	}
	
	public synchronized void addDelegate(ClassLoaderDelegate delegate) {
		addDelegate((Object)delegate);
	}

	protected synchronized void addDelegate(Object delegate)
	{
		// TODO Add a security check to allow only delegate classes that have been declared in
		// a properties file.
		// Or maybe another (better) security system?!
		
		log_info("addDelegate", "DelegatingClassLoader.addDelegate("+delegate+") registering delegate.");
		if (delegate == null)
			throw new NullPointerException("delegate must not be null!");
		if ((!(delegate instanceof ClassDataLoaderDelegate)) && (!(delegate instanceof ClassLoaderDelegate)))
			throw new IllegalArgumentException("delegate must be an instance of "+ClassDataLoaderDelegate.class.getName()+" or "+ClassLoaderDelegate.class.getName());
		delegates.add(delegate);
		clearCache();
	}

	public synchronized void removeDelegate(Object delegate)
	{
		log_info("removeDelegate", "DelegatingClassLoader.removeDelegate("+delegate+") unregistering delegate.");
		if (delegate == null)
			throw new NullPointerException("delegate must not be null!");
		delegates.remove(delegate);
		clearCache();
	}

	private static final int readBlockSize = 10240;
	/**
	 * This method reads always till the end of the stream (until <tt>in.read()</tt> returns -1)! The
	 * param <tt>length</tt> is only used for optimization of the buffer size.
	 * <br/><br/>
	 * This method closes the input stream after having read.
	 *
	 * @param name Name of the class.
	 * @param in InputStream to load from.
	 * @param probableLength Expected length of the data. This may be -1. It is only used for buffer optimization.
	 * @return Returns the loaded class (the result of defineClass(...)).
	 */
	private Class defineClassFromInputStream(String name, InputStream in, int probableLength, ProtectionDomain protectionDomain)
	{
		try {
			if (probableLength < 1)
				probableLength = readBlockSize * 5;
			byte[] classData = new byte[probableLength];
			byte[] rbuf = new byte[readBlockSize];
			int bytesRead = 0;		
			try {
				int br;
				do {
					br = in.read(rbuf);
					if (br > 0) {
						if (classData.length < bytesRead + br) {
							byte[] tmp = new byte[(int)(bytesRead * 1.3) + readBlockSize * 3];
							System.arraycopy(classData, 0, tmp, 0, bytesRead);
							classData = tmp;
						}
						System.arraycopy(rbuf, 0, classData, bytesRead, br);
						bytesRead += br;
					}
				} while (br >= 0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return defineClass(name, classData, 0, bytesRead, protectionDomain);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log_error("defineClassFromInputStream", "Closing input stream failed after loading class \""+name+"\"!", e);
			}
		}
	}


	/**
	 * Map caching already queried classes
	 * key: String className
	 * value: ClassSearchResult foundClass
	 */
	private Map foundClasses = new HashMap();

	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String name) throws ClassNotFoundException
	{
		if (debugEnabled)
			log_debug("findClass", "Entered findClass for name \"" + name + "\".");

		synchronized(foundClasses) {
			ClassSearchResult csr = (ClassSearchResult)foundClasses.get(name);
			if (csr != null) {
				log_debug("findClass", "Already searched the class \""+name+"\" before. Returning "+csr.getFoundClass());
				if (csr.getFoundClass() != null)
					return csr.getFoundClass();
				else
					throw new ClassNotFoundException(name);
			}
		}

		String threadClassKey = Thread.currentThread().toString() + '/' + name;
		synchronized(ignoredThreadsClassesOrResources) {
			if (ignoredThreadsClassesOrResources.contains(threadClassKey)) {
				if (debugEnabled)
					log_debug("findClass", "Entered findClass recursively for name \"" + name + "\"! Will throw a ClassNotFoundException to prevent endless recursion!");

				throw new ClassNotFoundException("Recursive call of DelegatingClassLoader!");
			}

			ignoredThreadsClassesOrResources.add(threadClassKey);
		}
		try {
	
//			if (name.startsWith("com.nightlabs.")) log_debug("DelegatingClassLoader.findClass(\""+name+"\")");
			Class foundClass = null;
	
			// first check the local repository (defined by path properties)
			String fileName = name.replace('.', '/').concat(".class");
			for (Iterator it = classpath.iterator(); it.hasNext(); ) {
				PathEntry pe = (PathEntry)it.next();
				if (foundClass != null)
					break;
	
				if (pe.isDirectory()) {
					File f = new File(pe.getPath(), fileName);
					if (f.exists()) {
						InputStream in;
						try {
							in = new FileInputStream(f);
						} catch (FileNotFoundException e) {
							throw new RuntimeException("File.exists(\""+f.getAbsolutePath()+"\") returned true, but new FileInputStream() threw FileNotFoundException!", e);
						}
						CodeSource codeSource;
						try {
							codeSource = new CodeSource(pe.getPath().toURL(), (Certificate[])null);
						} catch (MalformedURLException e) {
							throw new ClassNotFoundException("Creating codeSource failed!", e);
						}
						foundClass = defineClassFromInputStream(name, in, (int)f.length(), new ProtectionDomain(codeSource, null));
					}
				} // if (pe.isDirectory()) {
				else if (pe.isJar()) {
					JarFile jf = pe.getJarFile();
					JarEntry je = jf.getJarEntry(fileName);
					if (je != null) {
						InputStream in;
						try {
							in = jf.getInputStream(je);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						CodeSource codeSource;
						try {
							codeSource = new CodeSource(pe.getPath().toURL(), (Certificate[])null);
						} catch (MalformedURLException e) {
							throw new ClassNotFoundException("Creating codeSource failed!", e);
						}
						foundClass = defineClassFromInputStream(name, in, (int)je.getSize(), new ProtectionDomain(codeSource, null));
					}
				}
				else {
					log_warn("findClass", "Path entry \""+pe.getPath()+"\" defined in 'java.ext.dirs' or 'java.class.path' is neither a directory nor a readable jar file!");
				}
			}

			// now check whether one of the delegates can deliver the class
			for (Iterator it = delegates.iterator(); it.hasNext(); ) {
				Object delegateInstance = it.next();
				if (foundClass != null)
					break;
				
				if (delegateInstance instanceof ClassDataLoaderDelegate) {
					ClassDataLoaderDelegate delegate = (ClassDataLoaderDelegate) delegateInstance;
					
					ClassDataLoaderDelegate.ClassData classData = delegate.getClassData(name);
					if (classData != null) {
						if (classData.getClassDataAsByteArray() != null)
							foundClass = defineClass(name, classData.getClassDataAsByteArray(), classData.getOffset(), classData.getLength(), classData.getProtectionDomain());
						else
							foundClass = defineClassFromInputStream(name, classData.getClassDataAsInputStream(), -1, classData.getProtectionDomain());
					} // if (classData != null) {
				} else if (delegateInstance instanceof ClassLoaderDelegate) {
					foundClass = ((ClassLoaderDelegate)delegateInstance).loadClass(name);
				}
			}

			synchronized(foundClasses) {
				foundClasses.put(name,new ClassSearchResult(foundClass));
			}

			if (foundClass != null) {
		    log_debug("findClass", "DelegatingClassLoader.findClass(\""+name+"\"): responsible ClassLoader: " + foundClass.getClassLoader());
			  return foundClass;
			}
			log_debug("findClass", "DelegatingClassLoader.findClass(\""+name+"\"): Did not find class!");

		} finally {
			synchronized (ignoredThreadsClassesOrResources) {
				ignoredThreadsClassesOrResources.remove(threadClassKey);
			}
		}

		throw new ClassNotFoundException(name);
	}

	/**
	 * Map caching already queried resources
	 * key: String resourceName
	 * value: ResourceSearchResult found resources
	 */
	private Map foundResources = new HashMap();
	
	protected List findResources(String name, boolean returnAfterFoundFirst) throws IOException
	{
		synchronized(foundResources) {
			ResourceSearchResult rsr = (ResourceSearchResult)foundResources.get(name);
			if (rsr != null) {
				// only first element queried dont care for last search
				if (returnAfterFoundFirst) {
					return rsr.getFoundResources();
				}
				// all elements queried 
				else {
					if (rsr.wasFullSearch())
						return rsr.getFoundResources();
					// not all elements in list, search again
					else
						foundResources.remove(name);
				}
			}
		} // synchronized(foundResources) {
		
		String threadClassKey = Thread.currentThread().toString() + "/" + name; 
		synchronized (ignoredThreadsClassesOrResources) {
			if (ignoredThreadsClassesOrResources.contains(threadClassKey))
				return null;
			ignoredThreadsClassesOrResources.add(threadClassKey);
		}
		try {
		
			List resources = new LinkedList();
	
			String relativeFileName;
			if (name.startsWith("/"))
				relativeFileName = name.substring(1);
			else
				relativeFileName = name;
	
			String absoluteFileName = "/" + relativeFileName;
	
			// first check the local repository (defined by path properties)
			for (Iterator it = classpath.iterator(); it.hasNext(); ) {
				PathEntry pe = (PathEntry)it.next();
	
				if (pe.isDirectory()) {
					File f = new File(pe.getPath(), relativeFileName);
					if (f.exists()) {
						resources.add(f.toURL());
						if (returnAfterFoundFirst)
							return resources;
					}
				} // if (pe.isDirectory()) {
				else if (pe.isJar()) {
					JarFile jf = pe.getJarFile();
					JarEntry je = jf.getJarEntry(relativeFileName);
					if (je != null) {
						resources.add(new URL("jar", "", -1, "file:" + pe.getPath() + "!" + absoluteFileName, new sun.net.www.protocol.jar.Handler()));
						if (returnAfterFoundFirst)
							return resources;
					}
				}
				else {
					log_warn("findResources", "Path entry \""+pe.getPath()+"\" defined in 'java.ext.dirs' or 'java.class.path' is neither a directory nor a readable jar file!");
				}
			}
	
			// now check whether one of the delegates can deliver the class
			for (Iterator it = delegates.iterator(); it.hasNext(); ) {
				ResourceFinder delegate = (ResourceFinder) it.next();
				List delegateRes = delegate.getResources(name, returnAfterFoundFirst);
				if (delegateRes != null) {
					resources.addAll(delegateRes);
					if (returnAfterFoundFirst && !delegateRes.isEmpty())
						return resources;
				} //	if (delegateRes != null) {
			}
	
			synchronized(foundResources) {
				foundResources.put(name,new ResourceSearchResult(!returnAfterFoundFirst,resources));
			}
			
			return resources;			
			
		} finally {
			synchronized (ignoredThreadsClassesOrResources) {
				ignoredThreadsClassesOrResources.remove(threadClassKey);
			}
		}
	}

	protected Set ignoredThreadsClassesOrResources = new HashSet();
	
	public static class ResourceEnumeration implements Enumeration
	{
		private Iterator iterator;
		public ResourceEnumeration(Iterator it)
		{
			this.iterator = it;
		}
		/**
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		public boolean hasMoreElements()
		{
			return iterator.hasNext();
		}

		/**
		 * @see java.util.Enumeration#nextElement()
		 */
		public Object nextElement()
		{
			return iterator.next();
		}
	}

	/**
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	protected URL findResource(String name)
	{
		log_debug("findResource", "DelegatingClassLoader.findResource(\""+name+"\")");

		List resources;
		try {
			resources = findResources(name, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (resources == null || resources.isEmpty())
			return null;
		else
			return (URL)resources.get(0);
	}
	/**
	 * @see java.lang.ClassLoader#findResources(java.lang.String)
	 */
	protected Enumeration findResources(String name) throws IOException
	{
		log_debug("findResources", "DelegatingClassLoader.findResources(\""+name+"\")");

		List resources = findResources(name, false);
		return new ResourceEnumeration(resources.iterator());
	}
	/**
	 * @see java.lang.ClassLoader#findLibrary(java.lang.String)
	 */
	protected String findLibrary(String libname)
	{
		// TODO I guess, it is necessary to implement this! Probably, the bootstrap loader ignores the system properties?!
		log_error("findLibrary", "DelegatingClassLoader.findLibrary(\""+libname+"\") is unable to find the library. The method is not yet implemented!");
		return null;
	}
}
