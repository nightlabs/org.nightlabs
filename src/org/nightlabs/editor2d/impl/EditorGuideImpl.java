/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;


public class EditorGuideImpl
implements EditorGuide
{
  /**
   * @return	the set of all the parts attached to this guide; a set is used because a part
   * 			can only be attached to a guide along one edge.
   */
  public Set<DrawComponent> getParts() {
  	return getMap().keySet();
  }
  
  private boolean horizontal = HORIZONTAL_DEFAULT;
  private int position = POSITION_DEFAULT;
  private Map<DrawComponent, Integer> map = MAP_DEFAULT;

//  private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);
  // TODO: need to reattach all listeners when loading model
  private transient PropertyChangeSupport listeners = null;
  public PropertyChangeSupport getPropertyChangeSupport() {
  	if (listeners == null) {
  		listeners = new PropertyChangeSupport(this);
  	}
  	return listeners;
  }

  public EditorGuideImpl() {
		super();
	}

  public boolean isHorizontal() {
		return horizontal;
	}

  public void setHorizontal(boolean newHorizontal) {
		horizontal = newHorizontal;
	}

  public int getPosition() {
		return position;
	}

  public void setPosition(int newPosition)
  {
  	if (position != newPosition) {
  		int oldValue = position;
  		position = newPosition;
  		getPropertyChangeSupport().firePropertyChange(
  				PROPERTY_POSITION, oldValue, position);
  	}
  }
  
  public Map<DrawComponent, Integer> getMap() {
  	if (map == null) {
  		map = new HashMap<DrawComponent, Integer>();
  	}
  	return map;
  }
  
  public void setMap(Map<DrawComponent, Integer> newMap) {
		map = newMap;
	}

  public void attachPart(DrawComponent drawComponent, int alignment)
  {
  	if (getMap().containsKey(drawComponent) && getAlignment(drawComponent) == alignment)
  		return;
  	
  	getMap().put(drawComponent, alignment);
  	EditorGuide parent = isHorizontal() ? drawComponent.getHorizontalGuide() : drawComponent.getVerticalGuide();
  	if (parent != null && parent != this) {
  		parent.detachPart(drawComponent);
  	}
  	if (isHorizontal()) {
  	  drawComponent.setHorizontalGuide(this);
  	} else {
  	  drawComponent.setVerticalGuide(this);
  	}
  	getPropertyChangeSupport().firePropertyChange(PROPERTY_CHILDREN, null, drawComponent);
  }

  public void detachPart(DrawComponent drawComponent)
  {
  	if (getMap().containsKey(drawComponent)) {
  		getMap().remove(drawComponent);
  		if (isHorizontal()) {
  		  drawComponent.setHorizontalGuide(null);
  		} else {
  		  drawComponent.setVerticalGuide(null);
  		}
  		getPropertyChangeSupport().firePropertyChange(PROPERTY_CHILDREN, null, drawComponent);
  	}
  }

  public int getAlignment(DrawComponent drawComponent)
  {
  	if (getMap().get(drawComponent) != null)
  		return getMap().get(drawComponent);
  	return -2;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
  	getPropertyChangeSupport().addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
  	getPropertyChangeSupport().removePropertyChangeListener(listener);
  }

  @Override
	public String toString()
  {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (horizontal: ");
		result.append(horizontal);
		result.append(", position: ");
		result.append(position);
		result.append(", map: ");
		result.append(map);
		result.append(')');
		return result.toString();
	}

} //EditorGuideImpl
