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
package org.nightlabs.datastructure;

import java.lang.ref.Reference;
import java.util.ArrayList;

/**
 * @version $Revision$ - $Date$
 */
public class ReferenceList<T> extends ArrayList<Reference<T>>
{
  /**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

  public ReferenceList()
  {
  }

  @Override
	public void add(int index, Reference<T> reference)
  {
    if (reference == null)
      throw new NullPointerException("Param reference must not be null!");

    super.add(index, reference);
  }

  @Override
	public boolean add(Reference<T> reference)
  {
    if (reference == null)
      throw new NullPointerException("Param reference must not be null!");

    return super.add(reference);
  }

  protected static <T> boolean referenceEquals(Reference<T> ref, Object other)
  {
    if (ref == null)
      throw new NullPointerException("Param ref must not be null!");

    if (other instanceof Reference) {
      Reference<?> otherRef = (Reference<?>)other;

      if (ref.get() == null)
        return otherRef.get() == null;

      return ref.get().equals(otherRef.get());
    } // if (other instanceof Reference) {

    if (ref.get() == null)
      return other == null;

    return ref.get().equals(other);
  }

  @Override
	public int indexOf(Object elem)
  {
    return indexOf(elem, 0);
  }

  public int indexOf(Object elem, int index)
  {
    if (elem == null)
      throw new NullPointerException("Param elem must not be null!");

    for (int i = index; i < this.size(); ++i) {
      if (referenceEquals(this.get(i), elem))
        return i;
    } // for (int i = index; i < elementData.length; ++i) {

    return -1;
  }


  @Override
	public int lastIndexOf(Object elem) {
    return lastIndexOf(elem, this.size()-1);
  }

  public int lastIndexOf(Object elem, int index) {
    if (elem == null)
      throw new NullPointerException("Param elem must not be null!");

    for (int i = index; i >= 0; --i) {
      if (referenceEquals(this.get(i), elem))
        return i;
    } // for (int i = index; i < elementData.length; ++i) {

    return -1;
  }

  @Override
	public boolean remove(Object o) {
    return super.remove(o);
  }

//  public boolean contains(Object obj)
//  {
//    super.contains(obj);
//    for (int i = size()-1; i >=0; --i) {
//      Object o = get(i);
//      if (o == null && obj == null)
//        remove(i);
//      else {
//        if (o.equals(obj))
//          return true;
//      } // if (o != null) {
//    } // for (int i = size()-1; i >=0; --i) {
//
//    return false;
//  }
//
//  public boolean remove(Object obj)
//  {
//    return false;
//  }

}
