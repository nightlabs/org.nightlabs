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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.unit.resolution.Resolution;

/**
 * This is the base interface for all drawable objects
 * The complete NightLabs Editor2D Framework is based on this interface
 *
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public interface DrawComponent
extends Cloneable, IVisible //,Serializable
{
	public static final String TRANSFORM_CHANGED = "transform changed";

	// Default Values
	public static final int ROTATION_X_DEFAULT = Integer.MAX_VALUE;
	public static final int ROTATION_Y_DEFAULT = Integer.MAX_VALUE;
	public static final long ID_DEFAULT = 0L;
	public static final int X_DEFAULT = 0;
	public static final int Y_DEFAULT = 0;
	public static final int WIDTH_DEFAULT = 0;
	public static final int HEIGHT_DEFAULT = 0;
	public static final Rectangle BOUNDS_DEFAULT = new Rectangle(X_DEFAULT, Y_DEFAULT, WIDTH_DEFAULT, HEIGHT_DEFAULT);
	public static final double ROTATION_DEFAULT = 0.0;
	public static final int TMP_ROTATION_X_DEFAULT = Integer.MAX_VALUE;
	public static final int TMP_ROTATION_Y_DEFAULT = Integer.MAX_VALUE;
	public static final String RENDER_MODE_DEFAULT = RenderModeManager.DEFAULT_MODE;
	public static final RotationCenterPositions ROTATION_CENTER_POSITION_DEFAULT = RotationCenterPositions.CENTER;
	public static final List<Double> CONSTRAINED_ROTATION_VALUES_DEFAULT = null;
	public static final boolean CHANGED_ROTATION_CENTER_ALLOWED_DEFAULT = true;

	// Property Names
	public static final String PROP_AFFINE_TRANSFORM = "affineTransform";
	public static final String PROP_BOUNDS = "bounds";
	public static final String PROP_HEIGHT = "height";
	public static final String PROP_WIDTH = "width";
	public static final String PROP_NAME = "name";
	public static final String PROP_ID = "id";
	public static final String PROP_PARENT = "parent";
	public static final String PROP_RENDERER = "renderer";
	public static final String PROP_RENDER_MODE = "renderMode";
	public static final String PROP_ROTATION = "rotation";
	public static final String PROP_ROTATION_X = "rotationX";
	public static final String PROP_ROTATION_Y = "rotationY";
	public static final String PROP_X = "x";
	public static final String PROP_Y = "y";
	public static final String PROP_HORIZONTAL_GUIDE = "horizontalGuide";
	public static final String PROP_VERTICAL_GUIDE = "verticalGuide";
	public static final String PROP_TMP_ROTATION_X = "tmpRotationX";
	public static final String PROP_TMP_ROTATION_Y = "tmpRotationY";
	public static final String PROP_TEMPLATE = "template";
	public static final String PROP_EDITABLE = "editable";

	/**
	 *
	 * This enum contains all valid values for the position of the rotation center
	 * @see DrawComponent#setRotationCenterPosition(org.nightlabs.editor2d.DrawComponent.RotationCenterPositions)
	 */
	public enum RotationCenterPositions
	{
		LEFT_TOP, CENTER, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM;
	}

	/**
	 * adds an PropertyChangeListener to the DrawComponent
	 * is needed to get notified for property changes of the model
	 * @param pcl the PropertyChangeListener to add
	 */
	void addPropertyChangeListener(PropertyChangeListener pcl);

	/**
	 * removes the given PropertyChangeListener from the DrawComponent
	 * @param pcl the PropertyChangeListener to remove
	 */
	void removePropertyChangeListener(PropertyChangeListener pcl);

	/**
	 *
	 * @return the ID of the DrawComponent
	 */
	long getId();

	/**
	 *
	 * @param value sets the ID of the DrawComponent
	 */
	void setId(long value);

	/**
	 *
	 * @return the name of the DrawComponent, for the current languageID
	 * @see #getLanguageId()
	 */
	String getName();

	/**
	 * sets the name of the DrawComponent
	 * (normally this is done by adding the name for the current languageID to the i18nText)
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * the I18nText which contains localized Strings for different languageIDs
	 * @return the I18nText for the DrawComponent
	 * @see org.nightlabs.i18n.I18nText
	 */
	I18nText getI18nText();

	/**
	 *
	 * @param text the I18nText to set for the DrawComponent
	 * @see org.nightlabs.i18n.I18nText
	 */
	void setI18nText(I18nText text);

	/**
	 *
	 * @return the X-Coordinate for the DrawComponent in absolute Coordinates
	 */
	int getX();

	/**
	 *
	 * @param value the X-Coordinate for the DrawComponent to set in absolute Coordinates
	 */
	void setX(int value);

	/**
	 *
	 * @return the Y-Coordinate for the DrawComponent in absolute Coordinates
	 */
	int getY();

	/**
	 *
	 * @param value the Y-Coordinate for the DrawComponent to set in absolute Coordinates
	 */
	void setY(int value);

	/**
	 *
	 * @return the Width for the DrawComponent in absolute Coordinates
	 */
	int getWidth();

	/**
	 *
	 * @param value the Width for the DrawComponent to set in absolute Coordinates
	 */
	void setWidth(int value);

	/**
	 *
	 * @return the Height for the DrawComponent in absolute Coordinates
	 */
	int getHeight();

	/**
	 *
	 * @param value the Height for the DrawComponent to set in absolute Coordinates
	 */
	void setHeight(int value);

	/**
	 *
	 * @return the rotation of the DrawComponent in degrees (e.g. 45.00)
	 */
	double getRotation();

	/**
	 *
	 * @param value the rotation of DrawComponent to set in degrees
	 */
	void setRotation(double value);

	/**
	 * This Method is only for DrawComponentContainer rotation to
	 * perform the rotation generic with the transform-Method but to be
	 * able to set the rotation method without performing the rotation itself
	 *
	 * @param value the rotation value to set
	 */
	void setRotationMember(double value);

	/**
	 * if no HorizontalGuide is set this Method should return null
	 * @return the Horizontal EditorGuide of the DrawComponent
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	EditorGuide getHorizontalGuide();

	/**
	 *
	 * @param value the Horizontal EditorGuide of the DrawComponent to set
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	void setHorizontalGuide(EditorGuide value);

	/**
	 * if no VerticalGuide is set this Method should return null
	 * @return the Vertical EditorGuide of the DrawComponent
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	EditorGuide getVerticalGuide();

	/**
	 *
	 * @param value the Vertical EditorGuide of the DrawComponent to set
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	void setVerticalGuide(EditorGuide value);

	/**
	 *
	 * @return the X-Coordinate of the RotationCenter in absolute Coordinates
	 */
	int getRotationX();

	/**
	 *
	 * @param value the X-Coordinate of the RotationCenter to set in absolute Coordinates
	 */
	void setRotationX(int value);

	/**
	 *
	 * @return the Y-Coordinate of the RotationCenter in absolute Coordinates
	 */
	int getRotationY();

	/**
	 *
	 * @param value the Y-Coordinate of the RotationCenter to set in absolute Coordinates
	 */
	void setRotationY(int value);

	/**
	 *
	 * @return the Bounds of the DrawComponent in absolute Coordinates
	 */
	Rectangle getBounds();

	/**
	 * This Method should also update the values for (X, Y, Width, Height)
	 * @param bounds the Bounds to set for the DrawComponent in absolute Coordinates
	 */
	void setBounds(Rectangle bounds);

	/**
	 *
	 * @return the current AffineTransform
	 */
	AffineTransform getAffineTransform();

	/**
	 * this Method does not transform the DrawComponent, it only sets the current AffineTransform
	 * @param at the AffineTransform to set,
	 */
	void setAffineTransform(AffineTransform at);

	/**
	 * the Temporary Rotation-Center is used when a Group of DrawComponents is rotated, so
	 * that for this time not the normal Rotation-Center (getRotationX, getRotationY) is used but
	 * the temporary Rotation-Center (getTmpRotationX, getTmpRotationY)
	 * This value should not be serialzied.
	 *
	 * @param newTmpRotationX the X-Coordinate of the temporary Rotation-Center
	 * in absolute Coordinates
	 */
	void setTmpRotationX(int newTmpRotationX);

	/**
	 *
	 * @return the temporary X-Coordinate of the Rotation-Center when rotating a group of DrawComponents
	 * in absolute Coordinates
	 */
	int getTmpRotationX();

	/**
	 * the Temporary Rotation-Center is used when a Group of DrawComponents is rotated, so
	 * that for this time not the normal Rotation-Center (getRotationX, getRotationY) is used but
	 * the temporary Rotation-Center (getTmpRotationX, getTmpRotationY)
	 * This value should not be serialzied.
	 *
	 * @param newTmpRotationY the Y-Coordinate of the temporary Rotation-Center
	 * in absolute Coordinates
	 */
	void setTmpRotationY(int newTmpRotationY);

	/**
	 *
	 * @return the temporary Y-Coordinate of the Rotation-Center when rotating a group of DrawComponents
	 * in absolute Coordinates
	 */
	int getTmpRotationY();

	/**
	 * Based on the renderMode the DrawComponent is drawn
	 * @param mode the renderMode to set
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	void setRenderMode(String mode);

	/**
	 *
	 * @return the renderMode of the DrawComponent
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	String getRenderMode();

	/**
	 * the Renderer determines how the DrawComponent is drawn
	 *
	 * @return the Renderer of the DrawComponent
	 * @see org.nightlabs.editor2d.render.Renderer
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	Renderer getRenderer();

	/**
	 * sets the RenderModeManager for the DrawComponent, which
	 * contains all Renderer for the renderModes and the corresponding Class
	 *
	 * @param man the RenderModeManager to set
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	void setRenderModeManager(RenderModeManager man);

	/**
	 *
	 * @return the RenderModeManager for the DrawComponent
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	RenderModeManager getRenderModeManager();

	/**
	 *
	 * @return a String which describes the type of the DrawComponent
	 * e.g. LineDrawComponent
	 */
	String getTypeName();

	/**
	 *
	 * @return the Model-Root-Object for the DrawComponent
	 * @see org.nightlabs.editor2d.RootDrawComponent
	 */
	RootDrawComponent getRoot();

	/**
	 * sets the Location of the DrawComponent in absolute Coordinates
	 * @param x the X-Coordinate in absolute Coordinates
	 * @param y the Y-Coordinate in absolute Coordinates
	 */
	void setLocation(int x, int y);

	/**
	 * returns the location the of the drawComponent
	 * @return the location the of the drawComponent
	 */
	Point getLocation();

	/**
	 * sets the size of the DrawComponent
	 *
	 * @param newWidth the new Width to set
	 * @param newHeight the new Height to set
	 */
	void setSize(int newWidth, int newHeight);

	/**
	 * returns the size of the DrawComponent
	 *
	 * @return the size of the DrawComponent
	 */
	Dimension getSize();

	/**
	 *
	 * @return the DrawComponentContainer in which the DrawComponent is contained
	 * @see org.nightlabs.editor2d.DrawComponentContainer
	 */
	DrawComponentContainer getParent();

	/**
	 *
	 * @param value sets the DrawComponentContainer in which the DrawComponent is contained
	 * @see org.nightlabs.editor2d.DrawComponentContainer
	 */
	void setParent(DrawComponentContainer value);

	/**
	 * Transformes the DrawComponent and its bounds based on the given AffineTransform
	 *
	 * all geometric transformations {@link DrawComponent#setWidth(int)}, {@link DrawComponent#setHeight(int)},
	 * {@link DrawComponent#setX(int)}, {@link DrawComponent#setY(int)}, {@link DrawComponent#setLocation(int, int)},
	 * {@link DrawComponent#setRotation(double)}
	 * should be based on this Method or respectivly call this Method	by transform the
	 * AffineTransform with the given values and apply it
	 *
	 * IMPORTANT:
	 * Inheriters should never override this Method, but instead override {@link DrawComponent#transform(AffineTransform, boolean)}
	 * which should be called by this method
	 *
	 * @param at the AffineTransform which determines the transformation
	 * @see java.awt.geom.AffineTransform
	 */
	void transform(AffineTransform at);

	/**
	 * This Method can be implemented to avoid the firing of a propertyChange
	 * if the transformation comes from the parent.
	 *
	 * IMPORTANT:
	 * Inheritated classes should override this Method and transform the DrawComponent but also call
	 * super.transform(newAT, fromParent) first
	 *
	 * This is necessary because all Geometric Transformation
	 * (setX(), setY(), setWidth(), setHeight(), setLocation(), setSize(), setRotation())
	 * depend on this Method
	 *
	 * @param at the AffineTransform to transform
	 * @param fromParent determines if the Transformation comes from the parent DrawComponentContainer
	 * or if the DrawComponent is transformed directly, means it notificies the parent
	 * (getParent.notifyChildTransform(this)) about the transformation or not
	 */
	void transform(AffineTransform at, boolean fromParent);

	/**
	 * resets the cached bounds
	 */
	void clearBounds();

	/**
	 * resets the cached bounds and calculates the new bounds
	 */
	void clearBounds(boolean renew);

	/**
	 * clones the DrawComponent, makes a deep copy
	 *
	 * IMPORTANT:
	 * this method should never be overriden, but instead always implement {@link DrawComponent#clone(DrawComponentContainer)}
	 * which is called by the Implementation of clone() with {@link DrawComponent#getParent()}
	 *
	 * @return a clone of the DrawComponent of the same class
	 * @see Object#clone()
	 */
	Object clone();

	/**
	 * makes a deep copy of the DrawComponent
	 * IMPORTNAT!
	 * inheriters must override this Method, and call super.clone(parent) first
	 *
	 * @param parent the {@link DrawComponentContainer} to add the clone
	 * @return a deep copy of a DrawComponent which is added to the given parent
	 */
	Object clone(DrawComponentContainer parent);

	/**
	 * This field can be used to store some special properties which are hard to implement
	 * by inheritance, or for external system properties
	 *
	 * key: unique String
	 * value: Object
	 *
	 * IMPORTANT!
	 * This field should never be used under normal circumstances, the prefered way
	 * to extend the data-Model is to inherit ShapeDrawComponentImpl for vector-graphics
	 * or ImageDrawComponentImpl for bitmaps
	 *
	 * @return a Map of properties
	 */
	Map<String, Object> getProperties();

	/**
	 * returns the rotationCenterPosition of the DrawComponent,
	 * which determines where the rotation center is located
	 * and which is returned by {@link DrawComponent#getRotationX()} and
	 * {@link DrawComponent#getRotationY()}
	 * the default is {@link RotationCenterPositions#CENTER}
	 *
	 * @return the rotationCenterPosition
	 */
	RotationCenterPositions getRotationCenterPosition();

	/**
	 * sets the the position of the rotation center
	 * which determines where the rotation center is located
	 * and which is returned by {@link DrawComponent#getRotationX()} and
	 * {@link DrawComponent#getRotationY()}
	 *
	 * @param rotationCenterPosition the position of the rotation center
	 * @see RotationCenterPositions
	 */
	void setRotationCenterPosition(RotationCenterPositions rotationCenterPosition);

	/**
	 * return the constrained rotation values which are only allowed for rotation
	 * if only some rotation values are allowed
	 *
	 * by default all rotation values are allowed and this method returns
	 * {@link DrawComponent#CONSTRAINED_ROTATION_VALUES_DEFAULT} which is null
	 *
	 * @return the constrained rotation values which are only allowed for rotation
	 */
	List<Double> getConstrainedRotationValues();

	/**
	 * sets the constrained rotation values which are only allowed for rotation
	 * @param rotationValues the constrained rotation values to set
	 */
	void setConstrainedRotationValues(List<Double> rotationValues);

	/**
	 * returns true if the change of the rotation center is allowed and false if not
	 * the default is {@link DrawComponent#CHANGED_ROTATION_CENTER_ALLOWED_DEFAULT} which is true
	 *
	 * @return true if a change of the rotation center is allowed and false if not
	 */
	boolean isChangedRotationCenterAllowed();

	/**
	 * determines if the change of the rotation center is allowed or not
	 * the default is {@link DrawComponent#CHANGED_ROTATION_CENTER_ALLOWED_DEFAULT} which is true
	 *
	 * @param b determines if the change of the rotation center is allowed or not
	 */
	void setChangedRotationCenterAllowed(boolean b);

	/**
	 * can be called in case a drawComponent allocated any kind of ressources
	 * which should be freed/disposed
	 *
	 * TODO @Daniel: Why does a data model object have a dispose method??? Is it actually used or can we remove this? And is it really called reliably? Please let's talk about this. Marco.
	 * @Daniel: I just fixed a huge memory leak which was caused by this method (of the RootDrawComponent) not being called at all. Please ensure, that you execute this method whenever working with a DrawComponent!
	 * Besides that, we should think about whether we can implement a better way, because I don't think a dispose() method belongs into a data model!
	 * Marco.
	 */
	void dispose();

	/**
	 * Returns true if the drawComponent is disposed means {@link #dispose()} has been called before or false if not
	 * @return if the drawComponent is disposed
	 */
	boolean isDisposed();

	/**
	 * determines if this drawComponent is a template, which means that it the root has been saved as
	 * as a template and so all contained elements are then get the template state assigned
	 * so that they can not be modified any more, when opening a file from a template
	 *
	 * @return true if the drawComponent is part of a template or not
	 */
	boolean isTemplate();

	/**
	 * sets the template state for the drawComponent
	 * @param template determines if this drawComponent is a template
	 */
	void setTemplate(boolean template);

	/**
	 * determines if the drawcomponent can be edited or not
	 */
	boolean isEditable();

	/**
	 * sets the editable state
	 * @param editable the editabel state to set
	 */
	void setEditable(boolean editable);

	/**
	 *
	 * @return the class for which the a render mode is registered for.
	 */
	Class<? extends DrawComponent> getRenderModeClass();

	/**
	 * Is called when the the resolution of the {@link RootDrawComponent} has changed by
	 * calling {@link RootDrawComponent#setResolution(Resolution)}
	 *
	 * Implementations must then adopt their internal data (bounds, x, y, with, height, ....)
	 *
	 * @param oldResolution the old {@link Resolution}
	 * @param newResolution the new {@link Resolution}
	 */
	void resolutionChanged(Resolution oldResolution, Resolution newResolution);
} // DrawComponent
