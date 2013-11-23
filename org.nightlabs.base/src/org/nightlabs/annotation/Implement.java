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
package org.nightlabs.annotation;

/**
 * <p>
 * The @Override annotation should be used whenever a method that is already implemented
 * in a superclass is overridden. Unfortunately, there is no annotation for the case
 * that a super method does not have an implementation (i.e. is either an abstract class method
 * or defined by an interface). The Eclipse internal compiler does accept the @Override annotation
 * in some cases, but this code cannot be compiled by Sun's compiler.
 * </p>
 * <p>
 * Anyway, overriding and implementing are totally different and in case e.g. a method that was
 * abstract and got an implementation in the super class at a later point in time should not be
 * able to be overridden with the @Implement annotation (or at least a warning should be generated)
 * by the compiler.
 * </p>
 * <p>
 * In order to mark the methods already now and - once Sun's and Eclipse's compiler work the same
 * way - to be able to automatically replace the annotations by the official ones, we
 * implemented this annotation.
 * </p>
 *
 * @version $Revision$ - $Date$
 * @author Marco Schulze - marco at nightlabs dot de
 * @deprecated Since Java 1.6, the behaviour of the {@link Override} annotation has been fixed by Sun - hence there's no essential need for this anymore. You should replace 'Implement' by 'Override' everywhere.
 */
public @interface Implement {

}
