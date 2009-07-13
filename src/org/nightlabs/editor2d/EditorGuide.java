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
import java.util.Map;
import java.util.Set;

public interface EditorGuide
{
  /**
   * Property used to notify listeners when the parts attached to a guide are changed
   */
  public static final String PROPERTY_CHILDREN = "subparts changed"; //$NON-NLS-1$
  /**
   * Property used to notify listeners when the guide is re-positioned
   */
  public static final String PROPERTY_POSITION = "position changed"; //$NON-NLS-1$
  
  public static final boolean HORIZONTAL_DEFAULT = false;
  public static final int POSITION_DEFAULT = 0;
  public static final Map<DrawComponent, Integer> MAP_DEFAULT = null;
  
  public Set<DrawComponent> getParts();
  
  boolean isHorizontal();
  void setHorizontal(boolean value);

  int getPosition();
  void setPosition(int value);

  Map<DrawComponent, Integer> getMap();
  void setMap(Map<DrawComponent, Integer> value);

  void attachPart(DrawComponent drawComponent, int alignment);
  void detachPart(DrawComponent drawComponent);

  int getAlignment(DrawComponent drawComponent);

  void addPropertyChangeListener(PropertyChangeListener listener);
  void removePropertyChangeListener(PropertyChangeListener listener);

} // EditorGuide
