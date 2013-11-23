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
import java.util.ArrayList;
import java.util.List;

import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;

public class EditorRulerImpl
implements EditorRuler
{
  private static final long serialVersionUID = 1L;

	private List<EditorGuide> guides = null;
  private boolean horizontal = HORIZONTAL_DEFAULT;
  private int unit = UNIT_DEFAULT;
  private boolean hidden = HIDDEN_DEFAULT;

  // TODO: need to reattach all listeners when loading model
  private transient PropertyChangeSupport listeners = null;
  protected PropertyChangeSupport getPropertyChangeSupport() {
  	if (listeners == null) {
  		listeners = new PropertyChangeSupport(this);
  	}
  	return listeners;
  }

  public EditorRulerImpl() {
		super();
	}

  public List<EditorGuide> getGuides() {
		if (guides == null) {
			guides = new ArrayList<EditorGuide>();
		}
		return guides;
	}

  public boolean isHorizontal() {
		return horizontal;
	}

  public void setHorizontal(boolean newHorizontal) {
		horizontal = newHorizontal;
	}

  public int getUnit() {
		return unit;
	}

  public void setUnit(int newUnit) {
  	if (unit != newUnit) {
  		int oldUnit = unit;
  		unit = newUnit;
  		getPropertyChangeSupport().firePropertyChange(PROPERTY_UNIT, oldUnit, newUnit);
  	}
  }

  public boolean isHidden() {
		return hidden;
	}

  public void setHidden(boolean newHidden) {
		hidden = newHidden;
	}

  public void addGuide(EditorGuide guide) {
  	if (!guides.contains(guide)) {
  		guide.setHorizontal(!isHorizontal());
  		guides.add(guide);
  		getPropertyChangeSupport().firePropertyChange(PROPERTY_CHILDREN, null, guide);
  	}
  }

  public void removeGuide(EditorGuide guide) {
  	if (guides.remove(guide)) {
  		getPropertyChangeSupport().firePropertyChange(PROPERTY_CHILDREN, null, guide);
  	}
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
		result.append(", unit: ");
		result.append(unit);
		result.append(", hidden: ");
		result.append(hidden);
		result.append(')');
		return result.toString();
	}

} //EditorRulerImpl
