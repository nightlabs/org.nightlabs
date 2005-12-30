/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.request;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.SelectionRequest;

public class EditorRotateRequest 
//extends GroupRequest
extends SelectionRequest
implements EditorRequestConstants,
           EditorLocationRequest,
           EditorGroupRequest
{  
  public EditorRotateRequest() {
    super();
  }
  
  protected Point rotationCenter;  
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected boolean constrainedRotation = false;
  public boolean isConstrainedRotation() {
    return constrainedRotation;
  }
  public void setConstrainedRotation(boolean constrainedRotation) {
    this.constrainedRotation = constrainedRotation;
  }
  
  protected double rotation = 0;
  public double getRotation() {
    return rotation;
  }
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }
  
  protected List parts;
  public List getEditParts() {
    return parts;
  }
  public void setEditParts(List list) {
    parts = list;
  }  
  
  protected int direction;  
  public int getDirection() {
    return direction;
  }
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
}
