/**
 * 
 */
package org.nightlabs.classloader;

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
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class ClassLoadingDelegator implements IClassLoadingDelegator {

	/**
	 * Inner class ResourceSearchResult holds already found
	 * resources and remembers if the search was for the complete
	 * list or for one URL only.
	 * This is used internally for checking wheather a resource was
	 * already searched for.
	 * @author Alexander Bieber
	 */
	protected class ResourceSearchResult {

		public ResourceSearchResult(boolean fullSearch, List<URL> resources) {
			this.wasFullSearch = fullSearch;
			this.searchFoundResources = resources;
		}

		protected boolean wasFullSearch = false;
		protected List<URL> searchFoundResources;

		public List<URL> getFoundResources() {
			return searchFoundResources;
		}
		public void setFoundResources(List<URL> foundResources) {
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
					new JarFile(path);
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
				jarFile = new WeakReference<JarFile>(jf); // Because the jarfiles otherwise stay open - which might cause problems because of the limited number of filehandles -, I use WeakReferences
			}
			return jf;
		}
	}
	
	public static class ResourceEnumeration<T> implements Enumeration<T>
	{
		private Iterator<T> iterator;
		public ResourceEnumeration(Iterator<T> it)
		{
			this.iterator = it;
		}

		public boolean hasMoreElements()
		{
			return iterator.hasNext();
		}

		public T nextElement()
		{
			return iterator.next();
		}
	}
	
	
	private IClassLoaderDelegate classLoader;
	
	/**
	 * Map caching already queried resources
	 * key: String resourceName
	 * value: ResourceSearchResult found resources
	 */
	private Map<String, ResourceSearchResult> foundResources = new HashMap<String, ResourceSearchResult>();
	
	
	/**
	 * Map caching already queried classes
	 * key: String className
	 * value: ClassSearchResult foundClass
	 */
	private Map<String, ClassSearchResult> foundClasses = new HashMap<String, ClassSearchResult>();
	
	/**
	 * Instances either of {@link ClassLoaderDelegate} or {@link ClassDataLoaderDelegate}.
	 */
	private List<Object> delegates = new LinkedList<Object>();
	
	private List<PathEntry> libpath = new LinkedList<PathEntry>();
	
	private List<PathEntry> classpath = new LinkedList<PathEntry>();
	
	protected Set<String> ignoredThreadsClassesOrResources = new HashSet<String>();
	
	public ClassLoadingDelegator(IClassLoaderDelegate classLoader) {
		if (classLoader == null)
			throw new IllegalArgumentException("Argument classLoader must not be null!);");
		this.classLoader = classLoader;
	}
	
	public synchronized void addDelegate(ClassDataLoaderDelegate delegate) {
		addDelegate((Object)delegate);
	}
	
	public synchronized void addDelegate(ClassLoaderDelegate delegate) {
		addDelegate((Object)delegate);
	}

	/*
	 * 
	 */
	protected synchronized void addDelegate(Object delegate)
	{
		// TODO Add a security check to allow only delegate classes that have been declared in
		// a properties file.
		// Or maybe another (better) security system?!
		
		LogUtil.log_info(this.getClass(), "addDelegate", "DelegatingClassLoader.addDelegate("+delegate+") registering delegate.");
		if (delegate == null)
			throw new NullPointerException("delegate must not be null!");
		if ((!(delegate instanceof ClassDataLoaderDelegate)) && (!(delegate instanceof ClassLoaderDelegate)))
			throw new IllegalArgumentException("delegate must be an instance of "+ClassDataLoaderDelegate.class.getName()+" or "+ClassLoaderDelegate.class.getName());
		delegates.add(delegate);
		clearCache();
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.classloader.IClassLoadingDelegator#removeDelegate(java.lang.Object)
	 */
	public synchronized void removeDelegate(Object delegate)
	{
		LogUtil.log_info(this.getClass(), "removeDelegate", "DelegatingClassLoader.removeDelegate("+delegate+") unregistering delegate.");
		if (delegate == null)
			throw new NullPointerException("delegate must not be null!");
		delegates.remove(delegate);
		clearCache();
	}

	public void addPathEntry(PathEntry pathEntry) {
		classpath.add(pathEntry);
	}

	public static class JarFilter implements FilenameFilter{
		/**
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name)
		{
			try {
				new JarFile(new File(dir, name));
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}
	
	private static String pathSeparator = System.getProperty("path.separator");
	private static void addPathEntry(Collection<PathEntry> pathCollection, File pathItem, boolean recursiveDirs, FilenameFilter dirListFilter)
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
	private static void parsePathProperty(Collection<PathEntry> pathCollection, String property, boolean recursiveDirs, FilenameFilter dirListFilter)
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
	
	public void parseLibPaths(String[] paths) {
		for (int i = 0; i < paths.length; i++) {
			parsePathProperty(libpath, paths[i], false, null);
		}
	}
	
	public void parseClassPaths(String[] paths, FilenameFilter filter) {
		for (int i = 0; i < paths.length; i++) {
			parsePathProperty(libpath, paths[i], false, filter);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.classloader.IDelegatingClassLoader#findDelegateClass(java.lang.String)
	 */
	public Class findDelegateClass(String name) throws ClassNotFoundException {
		LogUtil.log_debug(this.getClass(), "findClass", "Entered findClass for name \"" + name + "\".");

		synchronized(foundClasses) {
			ClassSearchResult csr = (ClassSearchResult)foundClasses.get(name);
			if (csr != null) {
				LogUtil.log_debug(this.getClass(), "findClass", "Already searched the class \""+name+"\" before. Returning "+csr.getFoundClass());
				if (csr.getFoundClass() != null)
					return csr.getFoundClass();
				else
					throw new ClassNotFoundException(name);
			}
		}

		String threadClassKey = Thread.currentThread().toString() + '/' + name;
		synchronized(ignoredThreadsClassesOrResources) {
			if (ignoredThreadsClassesOrResources.contains(threadClassKey)) {
				LogUtil.log_debug(this.getClass(), "findClass", "Entered findClass recursively for name \"" + name + "\"! Will throw a ClassNotFoundException to prevent endless recursion!");

				throw new ClassNotFoundException("Recursive call of DelegatingClassLoader!");
			}

			ignoredThreadsClassesOrResources.add(threadClassKey);
		}
		try {
	
//			if (name.startsWith("org.nightlabs.")) log_debug("DelegatingClassLoader.findClass(\""+name+"\")");
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
					LogUtil.log_warn(this.getClass(), "findClass", "Path entry \""+pe.getPath()+"\" defined in 'java.ext.dirs' or 'java.class.path' is neither a directory nor a readable jar file!");
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
							foundClass = classLoader.delegateDefineClass(name, classData.getClassDataAsByteArray(), classData.getOffset(), classData.getLength(), classData.getProtectionDomain());
						else
							foundClass = defineClassFromInputStream(name, classData.getClassDataAsInputStream(), -1, classData.getProtectionDomain());
					} // if (classData != null) {
				} else if (delegateInstance instanceof ClassLoaderDelegate) {
					foundClass = ((ClassLoaderDelegate)delegateInstance).loadClass(name);
				}
			}

			synchronized(foundClasses) {
				foundClasses.put(name, new ClassSearchResult(foundClass));
			}

			if (foundClass != null) {
				LogUtil.log_debug(this.getClass(), "findClass", "DelegatingClassLoader.findClass(\""+name+"\"): responsible ClassLoader: " + foundClass.getClassLoader());
			  return foundClass;
			}
			LogUtil.log_debug(this.getClass(), "findClass", "DelegatingClassLoader.findClass(\""+name+"\"): Did not find class!");

		} finally {
			synchronized (ignoredThreadsClassesOrResources) {
				ignoredThreadsClassesOrResources.remove(threadClassKey);
			}
		}

		throw new ClassNotFoundException(name);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.classloader.IDelegatingClassLoader#findDelegateResources(java.lang.String, boolean)
	 */
	public List<URL> findDelegateResources(String name,
			boolean returnAfterFoundFirst) throws IOException {
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
		
			List<URL> resources = new LinkedList<URL>();
	
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
					LogUtil.log_warn(this.getClass(), "findResources", "Path entry \""+pe.getPath()+"\" defined in 'java.ext.dirs' or 'java.class.path' is neither a directory nor a readable jar file!");
				}
			}
	
			// now check whether one of the delegates can deliver the class
			for (Iterator it = delegates.iterator(); it.hasNext(); ) {
				ResourceFinder delegate = (ResourceFinder) it.next();
				List<URL> delegateRes = delegate.getResources(name, returnAfterFoundFirst);
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
	
	public void clearCache() {
		synchronized(foundResources){foundResources.clear();}
		synchronized(foundClasses){foundClasses.clear();}
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
			return classLoader.delegateDefineClass(name, classData, 0, bytesRead, protectionDomain);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				LogUtil.log_error(this.getClass(), "defineClassFromInputStream", "Closing input stream failed after loading class \""+name+"\"!", e);
			}
		}
	}	

}
