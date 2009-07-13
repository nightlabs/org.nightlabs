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

package org.nightlabs.editor2d;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;

public interface EditorRuler extends Serializable
{
  public static final String PROPERTY_CHILDREN = "children changed"; //$NON-NLS-1$
  public static final String PROPERTY_UNIT = "units changed"; //$NON-NLS-1$

  public static final boolean HORIZONTAL_DEFAULT = false;
  public static final int UNIT_DEFAULT = 0;
  public static final boolean HIDDEN_DEFAULT = false;

  List<EditorGuide> getGuides();

  boolean isHorizontal();
  void setHorizontal(boolean value);

  int getUnit();
  void setUnit(int value);

  boolean isHidden();
  void setHidden(boolean value);

  void addGuide(EditorGuide guide);
  void removeGuide(EditorGuide guide);

  void addPropertyChangeListener(PropertyChangeListener listener);
  void removePropertyChangeListener(PropertyChangeListener listener);

} // EditorRuler
