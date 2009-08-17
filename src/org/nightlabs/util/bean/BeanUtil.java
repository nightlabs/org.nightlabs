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
package org.nightlabs.util.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * @author unascribed (Original author)
 * @author Marc Klinger - marc[at]nightlabs[dot]de (Usage of generics)
 */
public class BeanUtil
{
	public static final int USE_ALL_BEANINFO = Introspector.USE_ALL_BEANINFO;
	public static final int IGNORE_ALL_BEANINFO = Introspector.IGNORE_ALL_BEANINFO;
	public static final int IGNORE_IMMEDIATE_BEANINFO  = Introspector.IGNORE_IMMEDIATE_BEANINFO;

	public BeanUtil(int mode)
	{
		if (mode != USE_ALL_BEANINFO &&
				mode != IGNORE_ALL_BEANINFO &&
				mode != IGNORE_IMMEDIATE_BEANINFO)
			throw new IllegalArgumentException("Param mode is neither USE_ALL_BEANINFO nor IGNORE_ALL_BEANINFO nor IGNORE_IMMEDIATE_BEANINFO!");

		this.currentMode = mode;
	}

	/**
	 * This int determines the Mode by which the Introspector should
	 * search for BeanInfo-Files
	 *
	 * @see #USE_ALL_BEANINFO
	 * @see #IGNORE_ALL_BEANINFO
	 * @see #IGNORE_IMMEDIATE_BEANINFO
	 *
	 * @see java.beans.Introspector
	 */
	protected int currentMode = USE_ALL_BEANINFO;
	public int getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(int mode) {
		currentMode = mode;
	}

	/**
	 * Collects all Methods (java.lang.reflect.Method) for the
	 * given beanClass (java.lang.Class)
	 *
	 * @param beanClass The beanClass for which all Methods are collected
	 * @return a List of Method[] which contains the the get/Read-Method
	 * as Method[0] and the set/Write-Method as Method[1]
	 */
	public static List<Method[]> getBeanMethods(Class<? extends Object> beanClass, int flag)
	throws IntrospectionException
	{
		// FIXME: this makes no sense. The variable is initialized twice (Marc):
		Vector<Method[]> propertyMethods = new Vector<Method[]>();
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, flag);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		propertyMethods = new Vector<Method[]>(pds.length);
		for (PropertyDescriptor pd : pds) {
			Method[] methods = new Method[2];
			methods[0] = pd.getReadMethod();
			methods[1] = pd.getWriteMethod();
			propertyMethods.add(methods);
		}
		return propertyMethods;
	}

	/**
	 * Collects all Methods (java.lang.reflect.Method) for the
	 * given beanClass (java.lang.Class)
	 *
	 * @param beanClass The beanClass for which all Methods are collected
	 * @return a List of Method[] which contains the the get/Read-Method
	 * as Method[0] and the set/Write-Method as Method[1]
	 */
	public static List<Method[]> getBeanMethods(Class<? extends Object> beanClass, Class<? extends Object> stopClass)
	throws IntrospectionException
	{
		// FIXME: this makes no sense. The variable is initialized twice (Marc):
		Vector<Method[]> propertyMethods = new Vector<Method[]>();
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, stopClass);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		propertyMethods = new Vector<Method[]>(pds.length);
		for (PropertyDescriptor pd : pds) {
			Method[] methods = new Method[2];
			methods[0] = pd.getReadMethod();
			methods[1] = pd.getWriteMethod();
			propertyMethods.add(methods);
		}
		return propertyMethods;
	}


	/**
	 * @param beanClass the Class of the bean
	 * @param flag IGNORE_ALL_BEANINFO,
	 *             IGNORE_IMMEDIATE_BEANINFO,
	 * 					 	 USE_ALL_BEANINFO
	 * @return an PropertyDescriptor[] of all PropertyDescrptors for this certain beanClass and the corresponding flag
	 * @throws IntrospectionException
	 * @see java.lang.reflect.PropertyDescriptor
	 * @see java.lang.reflect.Introspector
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<? extends Object> beanClass, int flag)
	throws IntrospectionException
	{
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, flag);
		return beanInfo.getPropertyDescriptors();
	}

	/**
	 * @param beanClass the Class of the bean
	 * @param flag IGNORE_ALL_BEANINFO,
	 *             IGNORE_IMMEDIATE_BEANINFO,
	 * 						 USE_ALL_BEANINFO
	 * @return a List of all PropertyDescriptors for the specific beanClass
	 * @throws IntrospectionException
	 * @see java.lang.reflect.Introspector
	 */
	public static List<PropertyDescriptor> getPropertyDescriptorsAsVector(Class<? extends Object> beanClass, int flag)
	throws IntrospectionException
	{
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, flag);
		PropertyDescriptor[] pdsArray = beanInfo.getPropertyDescriptors();
		Vector<PropertyDescriptor> pdsVector = new Vector<PropertyDescriptor>(pdsArray.length);
		for (PropertyDescriptor element : pdsArray)
			pdsVector.add(element);
		return pdsVector;
	}

	public Map<String, Map<Object, PropertyDescriptor>> getProperty2Bean2Pd(List<? extends Object> beans)
	throws IntrospectionException
	{
		List<PropertyDescriptor> oldProperties = null;
		List<String> oldPropertyNames = new Vector<String>();
		Map<String, Map<Object, PropertyDescriptor>> property2Bean2Pd = new HashMap<String, Map<Object, PropertyDescriptor>>();

		for (Iterator<? extends Object> it = beans.iterator(); it.hasNext(); )
		{
			Object bean = it.next();
			List<PropertyDescriptor> pds = getPropertyDescriptorsAsVector(bean.getClass(), getCurrentMode());
			// if this is the first row copy all PropertyDescriptors(PDs) of the
			// first bean in the oldProperties Vector and put the propertyNames
			// in a seperate Vector because of quicker comparison (contains)
			if (oldProperties == null)
			{
				oldProperties = pds;
				for (Object element : oldProperties) {
					PropertyDescriptor pd = (PropertyDescriptor) element;
					String propertyName = pd.getName();
					oldPropertyNames.add(propertyName);
					HashMap<Object, PropertyDescriptor> bean2pd = new HashMap<Object, PropertyDescriptor>();
					bean2pd.put(bean, pd);
					property2Bean2Pd.put(propertyName, bean2pd);
				} // for (Iterator it2 = oldProperties.iterator(); it2.hasNext(); )
			} // if (oldProperties == null)

			// otherwise compare the new PDs with the oldProperties
			// and remove not equal properties
			else // if (oldProperties == null)
			{
				for (Object element : pds) {
					PropertyDescriptor newPD = (PropertyDescriptor) element;
					String newProperyName = newPD.getName();
					// if this bean has the same property add it to
					// ClassesVector in the corresponding HashMap
					if (oldPropertyNames.contains(newProperyName))
					{
						Map<Object, PropertyDescriptor> bean2pd = property2Bean2Pd.get(newProperyName);
						bean2pd.put(bean, newPD);
					}
					// otherwise remove it from the HashMap and the Vector
					else {
						property2Bean2Pd.remove(newProperyName);
						oldPropertyNames.remove(newProperyName);
					}
				}
			} // else // if (oldProperties.isEmpty())
		} // for (Iterator it = beans.iterator(); it.hasNext(); )
		return property2Bean2Pd;
	}

	/**
	 * Checks if all beans in the Vector are a Instance of the same Class
	 * @param beans a List of beans
 	 * @return the Class if all beans are a Instance from the same Class
 	 * 				 null if not all beans are a Instance from the same Class
	 */
	public static Class<? extends Object> getAllBeansSingleClass(List<? extends Object> beans)
	{
		// first check if all beans are a Instance from the same class
		Class<?> firstClass = null;
		boolean sameClass = true;
		for (Iterator<? extends Object> it = beans.iterator(); it.hasNext(); )
		{
			Object bean = it.next();
			Class<? extends Object> beanClass = bean.getClass();
			if (firstClass == null) {
				firstClass = beanClass;
			}
			if(!firstClass.equals(beanClass)) {
				sameClass = false;
				break;
			}
		}
		return sameClass ? firstClass : null;
	}

	public List<PropertyDescriptor> getSamePropertyDescriptors(List<? extends Object> beans)
	throws IntrospectionException
	{
		List<PropertyDescriptor> oldPropertyDescriptors = null;
		List<Class<? extends Object>> classes = new ArrayList<Class<? extends Object>>();

		for (Iterator<? extends Object> it = beans.iterator(); it.hasNext(); )
		{
			Object bean = it.next();
			List<PropertyDescriptor> pds = getPropertyDescriptorsAsVector(bean.getClass(), currentMode);

			// if this is the first row copy all PropertyDescriptors(PDs) of the
			// first bean in the oldPropertyDescriptors Vector
			// additionally save the beanClass and the corresponding PropertyDescriptors,
			// to avoid catching the PDs for another bean of the same class
			if (oldPropertyDescriptors == null)
			{
				oldPropertyDescriptors = pds;
				classes.add(bean.getClass());
			} // if (oldProperties == null)

			// otherwise compare the new PDs with the oldProperties
			// and remove not equal properties
			else // if (oldPropertyDescriptors == null)
			{
				if (classes.contains(bean.getClass()))
				{
					continue;
				}
				else // if (class2Beans.keySet().contains(bean.getClass()))
				{
					classes.add(bean.getClass());
					Vector<Integer> indexes = new Vector<Integer>();
					for (int i=oldPropertyDescriptors.size()-1; i>=0; i--)
					{
						PropertyDescriptor oldPD = oldPropertyDescriptors.get(i);
						if (pds.contains(oldPD)) {
							continue;
						}
						else {
							indexes.add(new Integer(i));
						}
					} // for (Iterator it2 = oldPropertyDescriptors.iterator(); it2.hasNext(); )
					if (!indexes.isEmpty())
					{
						for (Object element : indexes) {
							int index = ((Integer) element).intValue();
							oldPropertyDescriptors.remove(index);
						}
					}

				} // else // if (class2Beans.keySet().contains(bean.getClass()))
			} // else // if (oldPropertyDescriptors == null)

		} // for (Iterator it = beans.iterator(); it.hasNext(); )
		return oldPropertyDescriptors;
	} // public HashMap getSamePropertyDescriptors(Vector beans)

	/**
	 * @return the property name for a given set method
	 * @param methodName method name. If the name is not the one of a set or get method
	 *        name, the result is not guaranteed.
	 */
	public static String getPropertyName(String methodName, boolean spaces)
	{
		String propertyName = "";
		StringBuffer sb = new StringBuffer(methodName);
		if (sb.substring(0,3).equals("get") ||
				sb.substring(0,3).equals("set"))
		{
			propertyName = methodName.substring(3);
		}
		else if (sb.substring(0,2).equals("is")) {
			propertyName = methodName.substring(2);
		}

		if (spaces)
		{
			// Now, parse name and look for upper case letter
			// A space is inserted in front of each upper case letter
			// to get a more user friendly name
			char buff[] = propertyName.toCharArray();
			int n = buff != null ? buff.length : 0;
			char data[] = new char[n * 2];
			int j = 1;
			data[0] = buff[0];
			for (int i = 1; i < n; i++) {
				char c = buff[i];
				if (Character.isUpperCase(c))
					data[j++] = ' ';
				data[j++] = c;
			}

			return new String(data, 0, j);
		}
		return propertyName;
	}

	public Object getValue(Object bean, String propertyName)
	{
		try {
			BeanInfo bi = Introspector.getBeanInfo(bean.getClass(), currentMode);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getName().equals(propertyName))
					return pd.getValue(propertyName);
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setValue(Object bean, String propertyName)
	{
		try {
			BeanInfo bi = Introspector.getBeanInfo(bean.getClass(), currentMode);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getName().equals(propertyName))
					pd.setValue(propertyName, bean);
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param _propertyName The name of the property
	 * @param _beans The List of target beans
	 * @return A Map with a bean as key and the corresponding
	 * 				 PropertyDescriptor as value
	 * @throws IntrospectionException
	 */
	public Map<Object, PropertyDescriptor> getPropertyDescriptors(String _propertyName, List<? extends Object> _beans)
	throws IntrospectionException
	{
		Map<Object, PropertyDescriptor> bean2Pd = new HashMap<Object, PropertyDescriptor>();
		for (Iterator<? extends Object> itBean = _beans.iterator(); itBean.hasNext(); )
		{
			Object bean = itBean.next();
			PropertyDescriptor[] pds = getPropertyDescriptors(bean.getClass(), currentMode);
			for (PropertyDescriptor element : pds) {
				PropertyDescriptor pd = element;
				if (pd.getName().equals(_propertyName))
				{
					bean2Pd.put(bean, pd);
				} // if (pd.getName().equals(_propertyName))
			} // for (int i=0; i < pds.length; i++)
		} // for (Iterator itBean = _beans.iterator(); itBean.hasNext(); )
		return bean2Pd;
	}
}
