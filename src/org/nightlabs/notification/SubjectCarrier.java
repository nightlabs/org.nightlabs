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
package org.nightlabs.notification;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SubjectCarrier
// implements Cloneable
{
	private Object subject;
	private Set<Class<?>> subjectClasses = new HashSet<Class<?>>();
	private boolean inheritanceIgnored;
	private boolean interfacesIgnored;
	private boolean breakOnFirstFound = false;

	public SubjectCarrier(Object subject)
	{
		this(subject, false, false);
	}

	public SubjectCarrier(Object subject, boolean inheritanceIgnored)
	{
		this(subject, inheritanceIgnored, false);
	}

	public SubjectCarrier(Object subject, boolean inheritanceIgnored, boolean interfacesIgnored)
	{
		if (subject == null)
			throw new NullPointerException("Cannot use subject=null without specifying a class!");

		this.subject = subject;
		subjectClasses.add(subject.getClass());
		this.inheritanceIgnored = inheritanceIgnored;
		this.interfacesIgnored = interfacesIgnored;
	}

	public SubjectCarrier(Class<?> subjectClass)
	{
		this(subjectClass, false, false);
	}

	public SubjectCarrier(Class<?> subjectClass, boolean inheritanceIgnored) {
		this(subjectClass, inheritanceIgnored, false);
	}

	public SubjectCarrier(Class<?> subjectClass, boolean inheritanceIgnored, boolean interfacesIgnored)
	{
		if (subjectClass == null)
			throw new NullPointerException("Parameter subjectClass must not be null!");

		subject = null;
		subjectClasses.add(subjectClass);
		this.inheritanceIgnored = inheritanceIgnored;
		this.interfacesIgnored = interfacesIgnored;
	}

	/**
	 * @param subject May be <tt>null</tt> if <tt>subjectClass</tt> isn't.
	 * @param subjectClass If <tt>subject</tt> is <tt>null</tt>, this must be specified,
	 *		otherwise <tt>subjectClass</tt> may be <tt>null</tt>. If this <tt>Class</tt>
	 *		differs from the type of <tt>subject</tt>, it is added to the <tt>Set</tt> of
	 *		subject classes.
	 */
	public SubjectCarrier(Object subject, Class<?> subjectClass)
	{
		this.subject = subject;

		if (subject != null)
			subjectClasses.add(subject.getClass());
	}

	/**
	 * @return Returns the subject.
	 */
	public Object getSubject()
	{
		return subject;
	}
	/**
	 * @return Returns the subjectClasses.
	 */
	public Set<Class<?>> getSubjectClasses()
	{
		return subjectClasses;
	}

//	/**
//	 * This method does NOT clone the subject - hence the subject will be the same
//	 * for the new SubjectCarrier.
//	 *
//	 * @see java.lang.Object#clone()
//	 */
//	public Object clone()
//	{
//		SubjectCarrier n = new SubjectCarrier(subject);
//		n.subjectClasses.addAll(subjectClasses);
//		return n;
//	}

	/**
	 * @return Returns the inheritanceIgnored.
	 */
	public boolean isInheritanceIgnored()
	{
		return inheritanceIgnored;
	}
	/**
	 * @param inheritanceIgnored The inheritanceIgnored to set.
	 */
	public void setInheritanceIgnored(boolean inheritanceIgnored)
	{
		this.inheritanceIgnored = inheritanceIgnored;
	}

	/**
	 * @return Returns the interfacesIgnored.
	 */
	public boolean isInterfacesIgnored() {
		return interfacesIgnored;
	}

	/**
	 * @param interfacesIgnored The interfacesIgnored to set.
	 */
	public void setInterfacesIgnored(boolean interfacesIgnored) {
		this.interfacesIgnored = interfacesIgnored;
	}

	public boolean isBreakOnFirstFound() {
		return breakOnFirstFound;
	}

	public void setBreakOnFirstFound(boolean breakOnFirstFound) {
		this.breakOnFirstFound = breakOnFirstFound;
	}
}
