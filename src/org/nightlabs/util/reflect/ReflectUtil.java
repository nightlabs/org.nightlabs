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
package org.nightlabs.util.reflect;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * @author Daniel Mazurek
 * @author Marc Klinger - marc at nightlabs dot de (API documentation fixes)
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DT] de -->
 */
public class ReflectUtil
{

	public ReflectUtil()
	{
		super();
	}

	/**
	 * Does the same as {@link org.nightlabs.util.reflect.ReflectUtil#clone(Object original, Class stopClass)}
	 * but has Object.class as stopClass
	 *
	 * @param original The instance to clone
	 * @return a cloned instance of the Param original
	 * @see #clone(Object original, Class stopClass)
	 */
	public static Object clone(Object original) {
		return clone(original, Object.class);
	}

	public static Object clone(Object original, String[] ignoredMembers) {
		return clone(original, Object.class, ignoredMembers);
	}

	public static Object clone(Object original, Class<? extends CloneDelegate>[] cloneDelegates) {
		return clone(original, Object.class, null, cloneDelegates);
	}

	public static Object clone(Object original, String[] ignoredMembers, Class<? extends CloneDelegate>[] cloneDelegtes) {
		return clone(original, Object.class, ignoredMembers, cloneDelegtes);
	}

	public static Map<Class<? extends Object>, Class<? extends CloneDelegate>> initCloneDelegates(Class<? extends CloneDelegate>[] cloneDelegates)
	throws CloneException
	{
		Map<Class<? extends Object>, Class<? extends CloneDelegate>> class2CloneDelegate = new HashMap<Class<? extends Object>, Class<? extends CloneDelegate>>(cloneDelegates.length);
		for (Class<? extends CloneDelegate> cdClass : cloneDelegates) {
			if (CloneDelegate.class.isAssignableFrom(cdClass)) {
				try {
					Method getCloneClass = cdClass.getMethod("getCloneClass", (Class<?>) null);
					Class<?> cloneClass = (Class<?>) getCloneClass.invoke((Object) null, (Object) null);
					class2CloneDelegate.put(cloneClass, cdClass);
				}
				catch (SecurityException e) {
					throw new CloneException(e);
				}
				catch (NoSuchMethodException e) {
					throw new CloneException(e);
				}
				catch (IllegalArgumentException e) {
					throw new CloneException(e);
				}
				catch (IllegalAccessException e) {
					throw new CloneException(e);
				}
				catch (InvocationTargetException e) {
					throw new CloneException(e);
				}
			}
		}
		return class2CloneDelegate;
	}

	public static Object clone(Object original,
			Class<? extends Object> stopClass,
			String[] ignoredMembers,
			Class<? extends CloneDelegate>[] cloneDelegates)
	{
		try
		{
			Class<? extends Object> orgClass = original.getClass();
			List<Field> fields = collectAllFields(orgClass, stopClass, true, ignoredMembers);
			boolean cloneDelegate = false;
			Map<Class<? extends Object>, Class<? extends CloneDelegate>> class2CloneDelegate = new HashMap<Class<? extends Object>, Class<? extends CloneDelegate>>(0);
			if (cloneDelegates != null) {
				class2CloneDelegate = new HashMap<Class<? extends Object>, Class<? extends CloneDelegate>>(initCloneDelegates(cloneDelegates));
				cloneDelegate = true;
			}

			try
			{
				// Iterate through all collected fields (except static and final)
				// and clone the values for the created instance
				Object dcInstance = orgClass.newInstance();
				for (Iterator<Field> it = fields.iterator(); it.hasNext(); )
				{
					Field field = it.next();
					Class<?> fieldType = field.getType();

					// if a CloneDelegate is registered
					if (cloneDelegate && class2CloneDelegate.containsKey(fieldType))
					{
						Class<? extends CloneDelegate> cdClass = class2CloneDelegate.get(fieldType);
						Method clone = cdClass.getMethod("clone", new Class[] {fieldType});
						Object clonedField = clone.invoke(cdClass.newInstance(), new Object[] {field.get(dcInstance)});
						field.set(dcInstance, clonedField);
					}
					// check if primitive
					else if (fieldType.isPrimitive())
					{
						field.set(dcInstance, field.get(original));
					}
					// check if Array
					else if (fieldType.isArray())
					{
						Object array = field.get(original);
						if (array == null)
							continue;

						int length = Array.getLength(array);
						Class<?> arrayType = array.getClass();
						Object clonedArray = Array.newInstance(arrayType.getComponentType(), length);
						for (int i=0; i<length; ++i) {
							Object value = Array.get(array, i);
							Array.set(clonedArray, i, value);
						}
						field.set(dcInstance, clonedArray);
					}
					// check if Cloneable
					else if (Cloneable.class.isAssignableFrom(fieldType))
					{
						Object org = field.get(original);
						field.set(dcInstance,
								org.getClass().getMethod(CLONE, (Class<?>) null).invoke(org, (Object) null)
						);
					}
					else
						throw new IllegalStateException("Not all members are primitive or Cloneable! Class=\""+original.getClass().getName()+"\" Member=\""+field.getType().getName()+" "+field.getName()+"\"");
				}
				return dcInstance;
			}
			catch (NoSuchMethodException e) {
				throw new CloneException(e);
			}
			catch (IllegalAccessException e) {
				throw new CloneException(e);
			}
			catch (InstantiationException e) {
				throw new CloneException(e);
			}
			catch (InvocationTargetException e) {
				throw new CloneException(e);
			}
		}
		catch (CloneException x) {
			throw new RuntimeException(x);
		}
	}

	private static String CLONE = "clone";
	public static Object clone(Object original,
			Class<? extends Object> stopClass,
			String[] ignoredMembers)
	{
		return clone(original, stopClass, ignoredMembers, null);
		//  	try
		//  	{
			//	  	Class orgClass = original.getClass();
		//	  	List fields = collectAllFields(orgClass, stopClass, true, ignoredMembers);
		//
		//	  	// Iterate through all collected fields (except static and final)
		//	  	// and clone the values for the created instance
		//    	Object dcInstance = orgClass.newInstance();
		//	  	for (Iterator it = fields.iterator(); it.hasNext(); )
		//	  	{
		//	  		Field field = (Field) it.next();
		//	  		// check if primitive
		//	  		if (field.getType().isPrimitive())
		//	  		{
		//	  			field.set(dcInstance, field.get(original));
		//	  		}
		//	  		// check if Array
		//	  		else if (field.getType().isArray())
		//	  		{
		//	  			Object array = field.get(original);
		//	  			int length = Array.getLength(array);
		//	  			Class arrayType = array.getClass();
		//	  			Object clonedArray = Array.newInstance(arrayType.getComponentType(), length);
		//	  			for (int i=0; i<length; ++i) {
		//	  				Object value = Array.get(array, i);
		//	  				Array.set(clonedArray, i, value);
		//	  			}
		//	  			field.set(dcInstance, clonedArray);
		//	  		}
		//	  		// check if Cloneable
		//	  		else if (Cloneable.class.isAssignableFrom(field.getType()))
		//	  		{
		//	  			Object org = field.get(original);
		//	  			field.set(dcInstance,
		//	  				org.getClass().getMethod(CLONE, null).invoke(org, null)
		//	  			);
		//	  		}
		//	  		else
		//	  			throw new IllegalStateException("Not all members are primitive or Cloneable! Class=\""+original.getClass().getName()+"\" Member=\""+field.getType().getName()+" "+field.getName()+"\"");
		//	  	}
		//	  	return dcInstance;
		//  	}
		//  	catch (Exception x) {
		//  		throw new RuntimeException(x);
		//  	}
	}


	/**
	 * a generic clone-Method for all Classes based on java.lang.reflect.
	 * <p>
	 * The basic Mechanism works as following:
	 * This methods iterates through all superClasses and collects,
	 * all declared Fields (private, protected, public)
	 * until it reaches the stopClass. Then it creates a new Instance
	 * of the original class (a standard Constructor is needed) and
	 * clones the values of all fields (except FINAL and STATIC) to
	 * the new instance. If one field is not a primitive Type (int, boolean, ...)
	 * it has to implement the Cloneable Interface, otherwise a
	 * IllegalStateException is thrown.
	 *
	 * @throws IllegalStateException if a field is not primitive and
	 * 				 does not implement Cloneable
	 * @throws RuntimeException if something else went wrong
	 * 				 (e.g. InstantationException, when no Standard Constructor exists, ...)
	 * @param original The Object to be cloned
	 * @param stopClass The Class where the cloning should stop
	 * @return An cloned Instance of the original class (original.getClass())
	 * 				 which fields have the same values as the original instance  *
	 * @see java.lang.Class
	 * @see java.lang.Cloneable
	 */
	public static Object clone(Object original, Class<? extends Object> stopClass)
	{
		return clone(original, stopClass, null);
		//  	try {
		//	  	Class orgClass = original.getClass();
		//	  	List fields = collectAllFields(orgClass, stopClass, true, null);
		//
		//	  	// Iterate through all collected fields (except static and final)
		//	  	// and clone the values for the created instance
		//    	Object dcInstance = orgClass.newInstance();
		//	  	for (Iterator it = fields.iterator(); it.hasNext(); ) {
		//	  		Field field = (Field) it.next();
		//	  		if (field.getType().isPrimitive()) {
		//	  			field.set(dcInstance, field.get(original));
		//	  		}
		//	  		else if (field.getType().isArray())
		//	  		{
		//	  			Object array = field.get(original);
		//	  			int length = Array.getLength(array);
		//	  			Class arrayType = array.getClass();
		//	  			Object clonedArray = Array.newInstance(arrayType.getComponentType(), length);
		//	  			for (int i=0; i<length; ++i) {
		//	  				Object value = Array.get(array, i);
		//	  				Array.set(clonedArray, i, value);
		//	  			}
		//	  			field.set(dcInstance, clonedArray);
		//	  		}
		//	  		else if (Cloneable.class.isAssignableFrom(field.getType()))
		//	  		{
		//	  			Object org = field.get(original);
		//	  			field.set(dcInstance,
		//	  				org.getClass().getMethod(CLONE, null).invoke(org, null)
		//	  			);
		//	  		}
		//	  		else
		//	  			throw new IllegalStateException("Not all members are primitive or Cloneable! Class=\""+original.getClass().getName()+"\" Member=\""+field.getType().getName()+" "+field.getName()+"\"");
		//	  	}
		//	  	return dcInstance;
		//  	} catch (Exception x) {
		//  		throw new RuntimeException(x);
		//  	}
	}

	/**
	 * collects recursivly all Fields for a certain class (originalClass)
	 * until a certain superClass is reached (stopClass)
	 *
	 * @param originalClass the Class to inspect
	 * @param stopClass the superClass where to stop
	 * @param ignoreStaticAndFinalFields determines if static and final fields should be ignored
	 * @return a List of all collected fields
	 * @see java.lang.reflect.Field
	 */
	public static List<Field> collectAllFields(Class<?> originalClass,
			Class<?> stopClass,
			boolean ignoreStaticAndFinalFields,
			String[] ignoredMembers)
			{
		List<Field> fields = new ArrayList<Field>();
		Class<?> superClass = originalClass;

		// iterate through all superclasses and
		// collect all declared fields (public, private, protected)
		// until the stopClass is reached
		while (superClass != stopClass)
		{
			Field[] f = superClass.getDeclaredFields();
			if (ignoreStaticAndFinalFields) {
				for (Field element : f) {
					if (ignoreField(element))
						continue;
					else if (ignoreMembers(element, ignoredMembers))
						continue;
					else {
						Field field = element;
						// makes also private Fields accessible
						field.setAccessible(true);
						fields.add(field);
					}
				}
			}
			else {
				for (Field field : f) {
					if (ignoreMembers(field, ignoredMembers))
						continue;
					// makes also private Fields accessible
					field.setAccessible(true);
					fields.add(field);
				}
			}
			superClass = superClass.getSuperclass();
		}
		return fields;
			}

	/**
	 * Does the same as {@link #collectAllFields(Class, Class, boolean, String[])}
	 * but has java.lang.Object.class as stopClass and doesn't ignore any fields
	 *
	 * @param originalClass The Class for which all Fields should be collected
	 * @param ignoreStaticAndFinalFields determines if static and final fields should be ignored
	 * @return a List of all declaredFields for the originalClass
	 * @see #collectAllFields(Class, Class, boolean, String[])
	 */
	public static List<Field> collectAllFields(Class<?> originalClass, boolean ignoreStaticAndFinalFields) {
		return collectAllFields(originalClass, Object.class, ignoreStaticAndFinalFields, null);
	}

	/**
	 * ignores all members/fields that are static or final.
	 * This method is used to check that.
	 *
	 * @param field Ignore this field?
	 * @return true if the field should be ignored, false if not.
	 */
	private static boolean ignoreField(Field field)
	{
		int modifiers = field.getModifiers();

		if ((modifiers & Modifier.STATIC) != 0)
			return true;

		if ((modifiers & Modifier.FINAL) != 0)
			return true;

		return false;
	}

	private static boolean ignoreMembers(Field field, String[] names)
	{
		if (names == null)
			return false;

		for (String name : names) {
			if (field.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * a generic equals-Method for all Classes based on java.lang.reflect
	 *
	 * @param original The original Instance which should be compared
	 * @param target The target Instance which will be compared with the original
	 * @return true if the values for all Fields of both instances are equivalent,
	 * 				 otherwise returns false
	 * @see java.lang.reflect.Field
	 * @see #collectAllFields(Class originalClass, boolean ignoreFields)
	 */
	public static boolean equals(Object original, Object target)
	{
		if (original == target)
			return true;

		Class<?> originalClass = original.getClass();
		Class<?> targetClass = target.getClass();

		if (!originalClass.isAssignableFrom(targetClass))
			return false;

		List<Field> originalFields = collectAllFields(originalClass, true);
		List<Field> targetFields = collectAllFields(targetClass, true);

		if (originalFields.size() != targetFields.size())
			return false;

		for (int i=0; i<originalFields.size(); ++i)
		{
			Field originalField = originalFields.get(i);
			Field targetField = targetFields.get(i);
			try {
				if (!originalField.get(original).equals(targetField.get(target)))
					return false;
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * a generic toString()-Method which print outs all Fieldnames and
	 * the corresponding values for an arbitary instance
	 *
	 * @param o the target instance
	 * @param withSuperClasses determines if the fields
	 * for all superclasses should be displayed as well
	 * @return a String which contains the Classname and all Fieldnames
	 * with the corresponding values
	 */
	public static String toString(Object o, boolean withSuperClasses)
	{
		Class<?> oClass = o.getClass();
		StringBuffer sb = new StringBuffer();
		// add Class Name
		sb.append(oClass.getName());
		sb.append("\n");
		if (withSuperClasses)
		{
			List<Field> fields = collectAllFields(oClass, false);
			for (Field field : fields) {
				try {
					sb.append(field.getName());
					sb.append(" = ");
					sb.append(String.valueOf(field.get(o)));
					sb.append("\n");
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			Field[] fields = oClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					sb.append(field.getName());
					sb.append(" = ");
					sb.append(String.valueOf(field.get(o)));
					sb.append("\n");
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static interface IObjectFoundHandler {
		public void objectFound(String path, Object object);
	}

	private static class ObjectFoundResult {
		private boolean found = false;
	}

	/**
	 * Returns whether an object of the given class could be found in the graph of the given object.
	 * @param checkObject The object to scan
	 * @param clazz The class to filter
	 * @param filter Filter for the scanning (Whether the found classes should or should not be assignable to the given clazz parameter)
	 * @return Whether an object of the given class could be found in the graph of the given object.
	 */
	public static boolean findContainedObjectsByClass(Object checkObject, Class<?> clazz, boolean filter) {
		final ObjectFoundResult result = new ObjectFoundResult();
		findContainedObjectsByClass(checkObject, clazz, filter, true, new IObjectFoundHandler() {
			public void objectFound(String path, Object object) {
				result.found = true;
			}
		});
		return result.found;
	}

	/**
	 * Finds objects of the given class in the object graph of the given object.
	 *
	 * @param checkObject The object to scan
	 * @param clazz The class to filter
	 * @param filter Filter for the scanning (Whether the found classes should or should not be assignable to the given clazz parameter)
	 * @param returnOnFirstInBranch Whether to return after the first match in a branch
	 * @param objectFoundHandler The handler to invoke when an object matches
	 */
	public static void findContainedObjectsByClass(
			Object checkObject,
			Class<?> clazz,
			boolean filter, boolean returnOnFirstInBranch,
			IObjectFoundHandler objectFoundHandler
	)
	{
		findContainedObjectsByClass(new HashSet<Object>(), "", checkObject, clazz, filter, returnOnFirstInBranch, objectFoundHandler);
	}

	private static void findContainedObjectsByClass(
			Set<Object> checked, String path,
			Object checkObject, Class<?> clazz,
			boolean filter, boolean returnOnFirstInBranch,
			IObjectFoundHandler objectFoundHandler
	)
	{
		if (checkObject == null)
			return;
		if (clazz.isAssignableFrom(checkObject.getClass()) == filter) {
			objectFoundHandler.objectFound(path, checkObject);
			if (returnOnFirstInBranch)
				return;
		}

		Object object = checkObject;
		Class<?> classRun = object.getClass();
		while (!classRun.equals(Object.class)) {

			Field[] fields = classRun.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					Object o = field.get(object);
					if (o == null)
						continue;
					if (!checked.contains(o)) {
						checked.add(o);
						if (o instanceof Collection) {
							Collection<?> col = (Collection<?>)o;
							for (Object o2 : col) {
								if (o2 == null)
									continue;
								findContainedObjectsByClass(checked, path+"/"+field.getName()+"$value$"+o2.getClass().getName(), o2, clazz, filter, returnOnFirstInBranch, objectFoundHandler);
							}
						} else if (o instanceof Map) {
							Map<?,?> map = (Map<?,?>)o;
							for (Object o2 : map.keySet()) {
								if (o2 == null)
									continue;
								findContainedObjectsByClass(checked, path+"/"+field.getName()+"$key$"+o2.getClass().getName(), o2, clazz, filter, returnOnFirstInBranch, objectFoundHandler);
							}
							for (Object o2 : map.values()) {
								if (o2 == null)
									continue;
								findContainedObjectsByClass(checked, path+"/"+field.getName()+"$value$"+o2.getClass().getName(), o2, clazz, filter, returnOnFirstInBranch, objectFoundHandler);
							}
						}
						else {
							findContainedObjectsByClass(checked, path+"/"+field.getName(), o, clazz, filter, returnOnFirstInBranch, objectFoundHandler);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			classRun = classRun.getSuperclass();
		}
	}

	/**
	 * Lists all (and of course loads all) the classes in the given package.
	 * The given classloader will be used to list the resources in the package,
	 * but to load the class {@link Class#forName(String)} is used without specifying the classloader.
	 *
	 * @param packageName The package name to search classes for.
	 * @param cld The classloader to use.
	 * @param recurse Whether this method should recurse in sub-packages.
	 * @return A list of classes that exists within the given package.
	 * @throws ClassNotFoundException If something went wrong.
	 */
	public static Collection<Class<?>> listClassesInPackage(String packageName, ClassLoader cld, boolean recurse)
	throws ClassNotFoundException
	{
		List<Class<?>> result = new LinkedList<Class<?>>();
		_listClassesOfPackage(packageName, cld, result, recurse);
		return result;
	}

	/**
	 * Lists all (and of course loads all) the classes in the given package.
	 * The {@link Thread#getContextClassLoader()} classloader will be
	 * used to list the resources in the package, but to load the class
	 * {@link Class#forName(String)} is used without specifying the classloader.
	 *
	 * @param packageName The package name to search classes for.
	 * @param recurse Whether this method should recurse in sub-packages.
	 * @return A list of classes that exists within the given package.
	 * @throws ClassNotFoundException If something went wrong.
	 */
	public static Collection<Class<?>> listClassesInPackage(String packageName, boolean recurse)
	throws ClassNotFoundException
	{
		List<Class<?>> result = new LinkedList<Class<?>>();
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		if (cld == null) {
			throw new ClassNotFoundException("Can't get class loader.");
		}
		_listClassesOfPackage(packageName, cld, result, recurse);
		return result;
	}

	/**
	 * Lists all (and of course loads all) the classes in the given package.
	 * The {@link Thread#getContextClassLoader()} classloader will be
	 * used to list the resources in the package, but to load the class
	 * {@link Class#forName(String)} is used without specifying the classloader.
	 * <p>
	 * Some of the code here was taken from:
	 * http://forum.java.sun.com/thread.jspa?threadID=341935&start=15
	 * </p>
	 * @param packageName The package name to search classes for.
	 * @param resultClasses The colleciton where the (recursive) runs of this method stores the found classes.
	 * @param recurse Whether this method should recurse in sub-packages.
	 * @throws ClassNotFoundException If something went wrong.
	 */
	protected static void _listClassesOfPackage(String packageName, ClassLoader cld, Collection<Class<?>> resultClasses, boolean recurse) throws ClassNotFoundException {
		// list all resource-entries for the given package
		List<File> pathEntries = new LinkedList<File>();
		List<JarFile> jarEntries = new LinkedList<JarFile>();
		String path = packageName.replace('.', '/') + '/';
		try {
			// Ask for all resources for the path
			Enumeration<URL> resources = cld.getResources(path);
			while (resources.hasMoreElements()) {
				URL resourceURL = resources.nextElement();
				if (resourceURL.getProtocol() == null || resourceURL.getProtocol().equalsIgnoreCase("file")) {
					pathEntries.add(new File(URLDecoder.decode(resourceURL.getPath(), "UTF-8")));
				} else if (resourceURL.getProtocol().equalsIgnoreCase("jar")) {
					String jarPath = resourceURL.getPath();
					jarPath = jarPath.substring(0, jarPath.indexOf("!"));
					if (jarPath.startsWith("file:"))
						jarPath = jarPath.substring(5);
					jarEntries.add(new JarFile(jarPath));
				}
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Unsupported encoding)");
		} catch (IOException ioex) {
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + packageName, ioex);
		}

		// separate into classes and directories
		List<String> recursePackages = new LinkedList<String>();
		// For every directory identified capture all the .class files
		for (File directory : pathEntries) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				File[] files = directory.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						recursePackages.add(packageName + "." + file.getName());
					} else {
						// we are only interested in .class files
						if (file.getName().endsWith(".class")) {
							// removes the .class extension
							resultClasses.add(cld.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
						}
					}
				}
			} else {
				throw new ClassNotFoundException(packageName + " (" + directory.getPath() + ") does not appear to be a valid package");
			}
		}
		for (JarFile jarFile : jarEntries) {
			for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); )
			{
				JarEntry entry = e.nextElement();
				if (!(entry.getName().length() >= path.length() && entry.getName().substring(0, path.length()).equals(path))) {
					continue;
				}
				if (entry.isDirectory()) {
					String suffix = entry.getName().substring(path.length());
					if ("".equals(suffix)) {
						continue;
					}
					if (suffix.endsWith("/")) {
						suffix = suffix.substring(0, suffix.length() -1);
					}
					if (suffix.indexOf('/') < 0) {
						recursePackages.add(packageName + "." + suffix);
					}
				}
				if(
						entry.getName().endsWith(".class") &&
						entry.getName().indexOf("/", path.length()) < 0
				) {
					String cn = entry.getName().substring(0, entry.getName().length() - ".class".length());
					cn = cn.replaceAll("/", ".");
					resultClasses.add(cld.loadClass(cn));
				}
			}
		}
		// recurse into sub-packages
		if (recurse) {
			for (String pack : recursePackages) {
				_listClassesOfPackage(pack, cld, resultClasses, recurse);
			}
		}
	}

	/**
	 * Gets the field specified by its name. In contrast to {@link Class#getDeclaredField(String)}, this
	 * method steps up the class hierarchy and returns the first {@link Field} it finds. If there is no
	 * field in the complete hierarchy, the first {@link NoSuchFieldException} (caught from {@link Class#getDeclaredField(String)})
	 * is re-thrown.
	 *
	 * @param clazz the class where to start searching for the field. Must <b>not</b> be <code>null</code>.
	 * @param fieldName the name of the field. Must <b>not</b> be <code>null</code>.
	 * @param fieldType the type of the field. Can be <code>null</code>. In this case, the first found will be returned - no matter what type it has.
	 * @return the first {@link Field} in the class hierarchy matching the specified field name.
	 * @throws SecurityException if {@link Class#getDeclaredField(String)} throws a <code>SecurityException</code>.
	 * @throws NoSuchFieldException if there is no field with the specified name in the complete class hierarchy.
	 */
	public static Field getDeclaredField(Class<?> clazz, String fieldName, Class<?> fieldType)
	throws SecurityException, NoSuchFieldException
	{
		if (clazz == null)
			throw new IllegalArgumentException("clazz must not be null!");

		if (fieldName == null)
			throw new IllegalArgumentException("fieldName must not be null!");

		NoSuchFieldException firstNoSuchFieldException = null;

		Class<?> c = clazz;
		while (c != null) {
			try {
				Field f = c.getDeclaredField(fieldName);
				if (fieldType != null && f.getType() != fieldType)
					throw new NoSuchFieldException("The field \"" + fieldName + "\" exists in class " + c.getName() + " but the type does not match! Expected type is " + fieldType.getName() + " but found " + f.getType().getName() + "!");

				return f;
			} catch (NoSuchFieldException e) {
				if (firstNoSuchFieldException == null)
					firstNoSuchFieldException = e;
			}
			c = c.getSuperclass();
		}

		throw firstNoSuchFieldException;
	}

	/**
	 * Returns the list of the type-arguments the given class used to configure a parameterized superclass with.
	 * <p>
	 * This method will walk up the inheritance of the given class until it finds
	 * the first parameterized superclass or reaches Object.
	 * </p>
	 * <p>
	 * This method works only for real (not in-line) subclasses of parameterized
	 * classes and will return the types the subclass used to parameterize its
	 * superclass.
	 * </p>
	 * <p>
	 * If no parameterized superclass of the given class can be found in its inheritance,
	 * <code>null</code> will be returned.
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * Given
	 * <pre>
	 *   class TestClass extends HashMap&lt;String, Object&gt; {
	 *   }
	 * </pre>
	 * The following code
	 * <pre>
	 *   getSuperClassTypeArguments(TestClass.class);
	 * </pre>
	 * will return <code>[class java.lang.String, class java.lang.Object]</code>.
	 * </p>
	 *
	 * @param clazz The class to find the superclass-parameterization for.
	 * @return
	 * 		The list of types used to parameterize the first parameterized super-class of the given class,
	 * 		or <code>null</code> if no parameterized superclass can be found for the given class.
	 */
	public static List<Class<?>> getSuperClassTypeArguments(Class<?> clazz) {
		Class<?> checkClazz = clazz;
		while (checkClazz != Object.class && checkClazz != null) {
			Type genSuperclass = checkClazz.getGenericSuperclass();
			if (genSuperclass instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genSuperclass;
				Type[] typeArguments = parameterizedType.getActualTypeArguments();
				List<Class<?>> result = new ArrayList<Class<?>>(typeArguments.length);
				for (int j = 0; j < typeArguments.length; j++) {
					if (typeArguments[j] instanceof Class) {
						result.add((Class<?>) typeArguments[j]);
					}
				}
				return result;
			}
			checkClazz = checkClazz.getSuperclass();
		}
		return null;
	}


	/**
	 *
	 * Collects the class-hierarchy (not including interfaces) for the given start-class until the
	 * given base-class is reached.
	 *
	 * @param startClass The class to start collecting the hierarchy for.
	 * @param baseClass The class to stop collecting.
	 * @param includeBaseClass Whether to include the base-class in the result.
	 * @return A list with the class-hierarchy of the given start-class.
	 */
	public static List<Class<?>> collectClassHierarchy(Class<?> startClass, Class<?> baseClass, boolean includeBaseClass) {
		Class<?> stopClass = baseClass;
		if (stopClass == null)
			stopClass = Object.class;

		List<Class<?>> result = new ArrayList<Class<?>>();
		Class<?> checkClass = startClass;
		while (checkClass != null && checkClass != stopClass) {
			result.add(checkClass);
			checkClass = checkClass.getSuperclass();
		}

		if (stopClass != Object.class && checkClass == Object.class)
			throw new IllegalStateException("The given start class " + startClass + " was not a subclass of " + baseClass.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		if (includeBaseClass) {
			result.add(stopClass);
		}
		Collections.reverse(result);
		return result;
	}

	/**
	 * Collects the type-hierarchy (including interfaces) for the given class. This method ensures
	 * that the type-hierarchy including interfaces is always in the same order.
	 *
	 * @param clazz The class to get the type hierarchy for.
	 * @return A list containing the type hierarchy of the given class (containing the given class
	 *         and all its direct and indirect super-classes/interfaces)
	 */
	public static List<Class<?>> collectTypeHierarchy(Class<?> clazz) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		computeClassOrder(clazz, classes);
		return classes;
	}

	/**
	 * Internally used by {@link #collectClassHierarchy(Class, Class, boolean)}.
	 */
	private static void computeClassOrder(Class<?> clazz, List<Class<?>> classes) {
		Class<?> _clazz = clazz;
		Set<Class<?>> seen = new HashSet<Class<?>>();
		while (_clazz != null) {
			classes.add(_clazz);
			computeInterfaceOrder(_clazz.getInterfaces(), classes, seen);
			_clazz = _clazz.isInterface() ? Object.class : _clazz.getSuperclass();
		}
	}

	/**
	 * Internally used by {@link #collectClassHierarchy(Class, Class, boolean)}.
	 */
	private static void computeInterfaceOrder(Class<?>[] interfaces, Collection<Class<?>> classes, Set<Class<?>> seen) {
		List<Class<?>> newInterfaces = new ArrayList<Class<?>>(interfaces.length);
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> iface = interfaces[i];
			if (seen.add(iface)) {
				// note we cannot recurse here without changing the resulting interface order
				classes.add(iface);
				newInterfaces.add(iface);
			}
		}
		for (Class<?> newInterface : newInterfaces) {
			computeInterfaceOrder((newInterface).getInterfaces(), classes, seen);
		}
	}

}
