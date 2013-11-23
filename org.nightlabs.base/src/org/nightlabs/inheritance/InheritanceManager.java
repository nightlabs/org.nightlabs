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
package org.nightlabs.inheritance;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.util.reflect.ReflectUtil;

/**
 * Throw-away instances of this class are used to apply inheritance via a copy-strategy from one object (called "mother") to another
 * object (called "child").
 * <p>
 * See the wiki page <a href="https://www.jfire.org/modules/phpwiki/index.php/Framework%20DataInheritance">Framework DataInheritance</a>
 * for further information.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class InheritanceManager
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(InheritanceManager.class);

	/**
	 * Default constructor. Use this for creation of instances of this class (which you throw away after having used them).
	 */
	public InheritanceManager() { }

	/**
	 * @param subject The object on which to execute the method {@link InheritanceCallbacks#preInherit(Inheritable, Inheritable)},
	 *		if it implements the necessary interface. This is either the same instance as <code>mother</code>
	 *		or as <code>child</code>.
	 * @param mother The source of the inheritance copy.
	 * @param child The destination of the inheritance copy.
	 */
	protected void callPreInherit(Inheritable subject, Inheritable mother, Inheritable child)
	{
		if (!(subject instanceof InheritanceCallbacks))
			return;

		((InheritanceCallbacks)subject).preInherit(mother, child);
	}

	/**
	 * @param subject The object on which to execute the method {@link InheritanceCallbacks#postInherit(Inheritable, Inheritable)},
	 *		if it implements the necessary interface. This is either the same instance as <code>mother</code>
	 *		or as <code>child</code>.
	 * @param mother The source of the inheritance copy.
	 * @param child The destination of the inheritance copy.
	 */
	protected void callPostInherit(Inheritable subject, Inheritable mother, Inheritable child)
	{
		if (!(subject instanceof InheritanceCallbacks))
			return;

		((InheritanceCallbacks)subject).postInherit(mother, child);
	}

	/**
	 * This method first calls {@link InheritanceCallbacks#preInherit(Inheritable, Inheritable)} of both, mother and child,
	 * if they implement the interface {@link InheritanceCallbacks}. Then it copies all fields, that
	 * are not overridden or ignored, from the mother to the child using the {@link FieldInheriter}s
	 * specified by the mother. Finally, it calls {@link InheritanceCallbacks#postInherit(Inheritable, Inheritable)}, if
	 * applicable.
	 *
	 * @param mother The mother from which to copy the data.
	 * @param child The child which inherits the data and is therefore seen as copy destination.
	 */
	public void inheritAllFields(Inheritable mother, Inheritable child)
	{
		Class<?> motherClass = mother.getClass();
		ArrayList<Field> allMotherFields = new ArrayList<Field>();

		Class<?> childClass = child.getClass();
// From 2008-12-12 on, it is possible to inherit data between objects that have no java class inheritance relationship.
// Only field values are inherited when a field with the same name and types exists in both, mother and child object.
//		if (!motherClass.isAssignableFrom(childClass))
//			throw new IllegalArgumentException("Child class is not identical to or inheriting the mother class!");

		Map<Field, FieldMetaData> m_fieldName2FieldMetaData = new HashMap<Field, FieldMetaData>();
		Map<Field, FieldMetaData> c_fieldName2FieldMetaData = new HashMap<Field, FieldMetaData>();

		Class<?> fcl = motherClass;
		while (fcl != Object.class) {
			Field[] fields = fcl.getDeclaredFields();
			for (Field f : fields) {
				int modifiers = f.getModifiers();
				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
					continue;

				FieldMetaData motherFieldMetaData = mother.getFieldMetaData(f.getName());
				FieldMetaData childFieldMetaData = child.getFieldMetaData(f.getName());

				if (motherFieldMetaData == null && childFieldMetaData == null)
					continue;

				if ((motherFieldMetaData == null || childFieldMetaData == null) &&
						(motherFieldMetaData != null || childFieldMetaData != null))
				{
					if (logger.isDebugEnabled()) {
						if (motherFieldMetaData != null)
							logger.debug("mother returned FieldMetaData, but child did not! motherClass=\"" + motherClass.getName() + "\" mother=\"" + mother + "\" childClass=\"" + childClass.getName() + "\" child=\"" + child + "\" field=\"" + f + "\"");
						else
							logger.debug("child returned FieldMetaData, but mother did not! motherClass=\"" + motherClass.getName() + "\" mother=\"" + mother + "\" childClass=\"" + childClass.getName() + "\" child=\"" + child + "\" field=\"" + f + "\"");
					}
//					throw new NullPointerException("Child \""+child+"\" returned no FieldMetaData for field \""+f.getName()+"\"!");
					continue;
				}

				allMotherFields.add(f);

				m_fieldName2FieldMetaData.put(f, motherFieldMetaData);
				c_fieldName2FieldMetaData.put(f, childFieldMetaData);
			}

			fcl = fcl.getSuperclass();
		} // while (fcl != Object.class) {

		callPreInherit(mother, mother, child);
		callPreInherit(child, mother, child);

		// We iterate allFields instead of one of the maps, because this ArrayList ensures
		// that the order of the fields is always the same (makes heisenbugs less probable).
		for (Field motherField : allMotherFields) {
			FieldMetaData motherFieldMetaData = m_fieldName2FieldMetaData.get(motherField);
			FieldMetaData childFieldMetaData = c_fieldName2FieldMetaData.get(motherField);

			Field childField;
			try {
				childField = ReflectUtil.getDeclaredField(childClass, motherField.getName(), motherField.getType());
			} catch (NoSuchFieldException e) {
				// the field does not exist in the child => silently skip
				continue;
			}

			inheritField(mother, child, motherClass, childClass, motherField, childField, motherFieldMetaData, childFieldMetaData);
		} // for (int i = 0; i < field.length; ++i) {

		callPostInherit(mother, mother, child);
		callPostInherit(child, mother, child);
	}

	/**
	 * This is a helper method, which does the actual field copying.
	 * @param mother the mother instance - i.e. the object <b>from</b> which to copy data.
	 * @param child the child instance - i.e. the object <b>to</b> which to copy data.
	 * @param motherClass the class of the mother - the same as returned by the method {@link Object#getClass()} of the <code>mother</code> instance.
	 * @param childClass the class of the child - the same as returned by the method {@link Object#getClass()} of the <code>child</code> instance.
	 * @param motherField the {@link Field} as found when iterating all declared fields of the <code>motherClass</code> (and its super-classes).
	 * @param childField TODO
	 * @param motherFieldMetaData the {@link FieldMetaData} of the <code>mother</code> for the current field.
	 * @param childFieldMetaData the {@link FieldMetaData} of the <code>child</code> for the current field.
	 */
	protected void inheritField(
			Inheritable mother, Inheritable child,
			Class<?> motherClass, Class<?> childClass,
			Field motherField, Field childField,
			FieldMetaData motherFieldMetaData, FieldMetaData childFieldMetaData
	)
	{
		if (logger.isDebugEnabled())
			logger.debug("inheritField(...): mother=\""+mother+"\" child=\""+child+"\" motherField=\""+motherField.getName()+"\" childField=\""+childField.getName()+"\"");

		if ((motherFieldMetaData.getWritableByChildren() & FieldMetaData.WRITABLEBYCHILDREN_YES) > 0) {

			if ((childFieldMetaData.getWritableByChildren() & FieldMetaData.WRITABLEBYCHILDREN_YES) == 0 &&
					(childFieldMetaData.getWritableByChildren() & FieldMetaData.WRITABLEBYCHILDREN_INHERITED) > 0)
			{
				childFieldMetaData.setWritableByChildren(
						(byte)(FieldMetaData.WRITABLEBYCHILDREN_YES | FieldMetaData.WRITABLEBYCHILDREN_INHERITED));
			}

			if (!childFieldMetaData.isWritable())
				childFieldMetaData.setWritable(true);
		}
		else {
			if (childFieldMetaData.isWritable())
				childFieldMetaData.setWritable(false);

			if (childFieldMetaData.getWritableByChildren() == FieldMetaData.WRITABLEBYCHILDREN_YES)
				childFieldMetaData.setWritableByChildren((byte)(FieldMetaData.WRITABLEBYCHILDREN_NO | FieldMetaData.WRITABLEBYCHILDREN_INHERITED));

			if (!childFieldMetaData.isValueInherited())
				childFieldMetaData.setValueInherited(true); // ensure it will be overwritten
		}

		if (childFieldMetaData.isValueInherited())
		{
			FieldInheriter fieldInheriter = child.getFieldInheriter(childField.getName());
			if (fieldInheriter == null)
				throw new NullPointerException("fieldInheriter must not be null. Attempt to inherit " + childField.getName() + " from " + child.getClass().getName() + ". Child: " + child + " mother: " + mother);

			if (logger.isDebugEnabled()) {
				String fieldName = motherField.getName();
				logger.debug("Copy FieldValue for field "+fieldName+" from "+mother+" to "+child);
				FieldMetaData mFieldMetaData = mother.getFieldMetaData(fieldName);
				if (!motherFieldMetaData.equals(mFieldMetaData)) {
					logger.error("given motherFieldMetaData "+motherFieldMetaData+" for field "+fieldName+" is not the same as mother.getFieldMetaData("+fieldName+") which is "+mFieldMetaData);
					try {
						if (motherFieldMetaData != null) {
							logger.error("given motherFieldMetaData fields:");
							List<Field> mfs = ReflectUtil.collectAllFields(motherFieldMetaData.getClass(), true);
							for (Field mf : mfs)
								logger.error(mf.getName() +" = "+mf.get(motherFieldMetaData));
						}
						if (mFieldMetaData != null) {
							logger.error("mother.getFieldMetaData("+fieldName+") fields:");
							List<Field> mfs = ReflectUtil.collectAllFields(mFieldMetaData.getClass(), true);
							for (Field mf : mfs)
								logger.error(mf.getName() +" = "+mf.get(mFieldMetaData));
						}
					} catch (Exception e) {
						logger.error("Error during field.get()", e);
					}
				}
				FieldMetaData cFieldMetaData = child.getFieldMetaData(fieldName);
				if (!childFieldMetaData.equals(cFieldMetaData)) {
					logger.error("given childFieldMetaData "+childFieldMetaData+" for field "+fieldName+" is not the same as child.getFieldMetaData("+fieldName+") which is "+cFieldMetaData);
					try {
						if (childFieldMetaData != null) {
							logger.error("given childFieldMetaData fields:");
							List<Field> cfs = ReflectUtil.collectAllFields(childFieldMetaData.getClass(), true);
							for (Field cf : cfs)
								logger.error(cf.getName() +" = "+cf.get(childFieldMetaData));
						}
						if (cFieldMetaData != null) {
							logger.error("child.getFieldMetaData("+fieldName+") fields:");
							List<Field> cfs = ReflectUtil.collectAllFields(cFieldMetaData.getClass(), true);
							for (Field cf : cfs)
								logger.error(cf.getName() +" = "+cf.get(cFieldMetaData));
						}
					} catch (Exception e) {
						logger.error("Error during field.get()", e);
					}
				}
			}

			fieldInheriter.copyFieldValue(
					mother, child, motherClass, childClass, motherField, childField, motherFieldMetaData, childFieldMetaData
			);
		}
	}

}
