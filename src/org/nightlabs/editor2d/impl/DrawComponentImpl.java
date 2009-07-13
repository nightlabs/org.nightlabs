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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.EditorModelUtil;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;
import org.nightlabs.util.collection.MapChangedEvent;
import org.nightlabs.util.collection.MapChangedListener;
import org.nightlabs.util.collection.NotifyingMap;
import org.nightlabs.util.collection.MapChangedEvent.MapChange;

/**
 * The Base Implementation of the Interface {@link DrawComponent}
 *
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class DrawComponentImpl
implements DrawComponent
{
	private static final Logger logger = Logger.getLogger(DrawComponentImpl.class);

	private static final long serialVersionUID = 1L;
	private long id = ID_DEFAULT;
	protected int x = X_DEFAULT;
	protected int y = Y_DEFAULT;
	protected int width = WIDTH_DEFAULT;
	protected int height = HEIGHT_DEFAULT;
	private double rotation = ROTATION_DEFAULT;
	private EditorGuide horizontalGuide = null;
	private EditorGuide verticalGuide = null;
	private int rotationX = ROTATION_X_DEFAULT;
	private int rotationY = ROTATION_Y_DEFAULT;
	protected transient Rectangle bounds = BOUNDS_DEFAULT;
	private AffineTransform affineTransform = new AffineTransform();
	private transient int tmpRotationX = TMP_ROTATION_X_DEFAULT;
	private transient int tmpRotationY = TMP_ROTATION_Y_DEFAULT;
	//	private I18nText name = new I18nTextBuffer();
	protected I18nText name = null;

	private transient PropertyChangeSupport listeners = null;
	private PropertyChangeSupport getPropertyChangeSupport()
	{
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);
		return listeners;
	}

	/**
	 * adds a {@link PropertyChangeListener} to the {@link DrawComponent}
	 * which is notifyed of all property changes
	 *
	 * @param l the PropertyChangeListener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener l){
		getPropertyChangeSupport().addPropertyChangeListener(l);
	}

	/**
	 * removes a PropertyChangeListener from the DrawComponent
	 * @param l the PropertyChangeListener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener l){
		getPropertyChangeSupport().removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object oldValue, Object newValue){
		getPropertyChangeSupport().firePropertyChange(prop, oldValue, newValue);
	}
	protected void firePropertyChange(String prop, int oldValue, int newValue){
		getPropertyChangeSupport().firePropertyChange(prop, oldValue, newValue);
	}
	protected void firePropertyChange(String prop, double oldValue, double newValue){
		getPropertyChangeSupport().firePropertyChange(prop, new Double(oldValue), new Double(newValue));
	}
	protected void firePropertyChange(String prop, boolean oldValue, boolean newValue){
		getPropertyChangeSupport().firePropertyChange(prop, oldValue, newValue);
	}

	public DrawComponentImpl() {
		super();
	}

	/**
	 * return the ID of the DrawComponent
	 * @see org.nightlabs.editor2d.DrawComponent#getId()
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets the ID and fires a PropertyChange with the PropertyName {@link DrawComponent#PROP_ID }
	 * @see org.nightlabs.editor2d.DrawComponent#setId(long)
	 */
	public void setId(long newId) {
		long oldId = id;
		id = newId;
		firePropertyChange(PROP_ID, new Long(oldId), new Long(newId));
	}

	// TODO: is this ok
	private String getLanguageID()
	{
		String languageID = null;
		if (getRoot() != null)
			languageID = getRoot().getLanguageID();
		else
			languageID = NLLocale.getDefault().getLanguage();

		return languageID;
	}

	/**
	 * returns the name based on the languageID from the Root ({@link #getRoot().getLanguageID()}
	 * @see org.nightlabs.editor2d.DrawComponent#getName()
	 */
	public String getName() {
		return getI18nText().getText(getLanguageID());
	}

	/**
	 * puts the given name into the I18nText based on languageID of the root {@link #getRoot().getLanguageID())
	 * and fires a PropertyChange with the PropertyName {@link DrawComponent#PROP_NAME}
	 * @see org.nightlabs.editor2d.DrawComponent#setName(String)
	 */
	public void setName(String newName) {
		String oldName = getName();
		primSetName(newName);
		firePropertyChange(PROP_NAME, oldName, newName);
	}

	/**
	 * sets the name in the {@link I18nText} with the current languageID
	 * of the {@link RootDrawComponent} without firing a propertyChangeEvent
	 *
	 * @param newName the new name to set
	 */
	protected void primSetName(String newName) {
		getI18nText().setText(getLanguageID(), newName);
	}

	/**
	 * returns the multiLanguage text of the DrawComponent
	 *
	 * @return the I18nText which contains the names for each languageID
	 * @see org.nightlabs.editor2d.DrawComponent#getI18nText()
	 */
	public I18nText getI18nText() {
		if (name == null)
			name = new I18nTextBuffer();
		return name;
	}

	/**
	 * sets the I18nText which contains the names for each languageID and fires a propertyChange
	 * with the PropertyName {@link DrawComponent#PROP_NAME}
	 *
	 * @param text the I18nText to set
	 * @see org.nightlabs.i18n.I18nText
	 * @see org.nightlabs.editor2d.DrawComponent#setI18nText(I18nText)
	 */
	public void setI18nText(I18nText text) {
		if (text == null)
			throw new IllegalArgumentException("Param text must not be null!");

		this.name = text;
		firePropertyChange(PROP_NAME, null, name);
	}

	/**
	 * this Method is only a wrapper which internally calls getBounds().x
	 *
	 * @return the X-Coordinate in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#getX()
	 */
	public int getX() {
		return getBounds().x;
	}

	/**
	 * sets the X-Coordinate of the DrawComponent and fires a propertyChange
	 * with the PropertyName {@link DrawComponent#PROP_X}
	 *
	 * @param newX the X-Coordinate to set in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#setX(int)
	 */
	public void setX(int newX)
	{
		int oldX = x;
		x = newX;
		primSetX(x);
		firePropertyChange(PROP_X, oldX, x);
	}

	/**
	 * sets the X-Coordinate of the DrawComponent without firing a propertyChange
	 *
	 * @param x the X-Coordinate to set in absolute Coordinates
	 */
	protected void primSetX(int x)
	{
		if (x == getBounds().x)
			return;
		primSetLocation(x, getY());
	}

	/**
	 * this Method is only a wrapper which internally calls getBounds().y
	 *
	 * @return the Y-Coordinate in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#getY()
	 */
	public int getY() {
		return getBounds().y;
	}

	/**
	 * sets the Y-Coordinate of the DrawComponent and fires a propertyChange
	 *
	 * @param newY the Y-Coordinate to set in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#setY(int)
	 */
	public void setY(int newY)
	{
		int oldY = y;
		y = newY;
		primSetY(y);
		firePropertyChange(PROP_Y, oldY, y);
	}

	/**
	 * sets the Y-Coordinate of the DrawComponent without firing a propertyChange
	 * @param y the Y-Coordinate to set in absolute Coordinates
	 */
	protected void primSetY(int y)
	{
		if (y == getBounds().y)
			return;
		primSetLocation(getX(), y);
	}

	/**
	 * this Method is only a wrapper which internally calls getBounds().width
	 * @return the Width in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#getWidth()
	 */
	public int getWidth() {
		return getBounds().width;
	}

	/**
	 * sets the Width of the DrawComponent and fires a propertyChange
	 * @param newWidth the Width to set in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#setWidth(int)
	 */
	public void setWidth(int newWidth)
	{
		int oldWidth = width;
		width = newWidth;
		primSetWidth(width);
		firePropertyChange(PROP_WIDTH, oldWidth, width);
	}

	/**
	 * sets the Width of the DrawComponent without firing a propertyChange
	 * @param width the Width to set in absolute Coordinates
	 */
	protected void primSetWidth(float width)
	{
		if (width == getBounds().width)
			return;
		setSize((int)width, getHeight());
	}

	/**
	 * this Method is only a wrapper which internally calls getBounds().height
	 * @return the Height in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#getHeight()
	 */
	public int getHeight() {
		return getBounds().height;
	}

	/**
	 * sets the Height of the DrawComponent and fires a propertyChange
	 * @param newHeight the Height to set in absolute Coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#setHeight(int)
	 */
	public void setHeight(int newHeight)
	{
		int oldHeight = height;
		height = newHeight;
		primSetHeight(height);
		firePropertyChange(PROP_HEIGHT, oldHeight, height);
	}

	/**
	 * sets the Height of the DrawComponent without firing a propertyChange
	 * @param height the Height to set in absolute Coordinates
	 */
	protected void primSetHeight(float height)
	{
		if (height == getBounds().height)
			return;
		setSize(getWidth(), (int)height);
	}

	/**
	 * @see org.nightlabs.editor2d.DrawComponent#getHorizontalGuide()
	 */
	public EditorGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	/**
	 * sets the Horizontal EditorGuide and fires a propertyChange with the PropertyName
	 * {@link DrawComponent#PROP_HORIZONTAL_GUIDE}
	 * @see org.nightlabs.editor2d.DrawComponent#setHorizontalGuide(EditorGuide)
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	public void setHorizontalGuide(EditorGuide newHorizontalGuide)
	{
		EditorGuide old = horizontalGuide;
		horizontalGuide = newHorizontalGuide;
		firePropertyChange(PROP_HORIZONTAL_GUIDE, old, horizontalGuide);
	}

	/**
	 * @see org.nightlabs.editor2d.DrawComponent#getVerticalGuide()
	 */
	public EditorGuide getVerticalGuide() {
		return verticalGuide;
	}

	/**
	 * sets the Vertical EditorGuide and fires a propertyChange with the PropertyName
	 * {@link DrawComponent#PROP_VERTICAL_GUIDE}
	 * @see org.nightlabs.editor2d.DrawComponent#setVerticalGuide(EditorGuide)
	 * @see org.nightlabs.editor2d.EditorGuide
	 */
	public void setVerticalGuide(EditorGuide newVerticalGuide)
	{
		EditorGuide old = verticalGuide;
		verticalGuide = newVerticalGuide;
		firePropertyChange(PROP_VERTICAL_GUIDE, old, verticalGuide);
	}

	/**
	 * @return the rotation of the DrawComponent in degrees
	 * @see org.nightlabs.editor2d.DrawComponent#getRotation()
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * determines the max/min rotation.
	 * Each rotation above/under this limit is minimzied by this value
	 */
	protected static transient double rotationLimit = 360.0d;

	/**
	 * returns the cached value of the bounds, which are cached until a new transformation took place
	 * if the cached value is null, a new Rectangle is returned is created from the simple members x,y,width,height
	 *
	 * @return the bounds of the DrawComponent in absolute coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#getBounds()
	 */
	public Rectangle getBounds()
	{
		if (bounds == null) {
			if (logger.isDebugEnabled()) {
				if (x == 0 && y == 0 && width == 0 && height == 0) {
					logger.debug("getBounds() returned EMPTY bounds for DrawComponent "+this);
				}
			}
			bounds = new Rectangle(x, y, width, height);
		}
		return bounds;
	}

	/**
	 * sets the bounds of the DrawComponent and fires a propertyChange
	 * @param newBounds the bounds to set in absolute coordinates
	 * @see org.nightlabs.editor2d.DrawComponent#setBounds(Rectangle)
	 */
	public void setBounds(Rectangle newBounds)
	{
		Rectangle oldBounds = bounds;
		primSetBounds(newBounds);

		bounds = newBounds;
		x = bounds.x;
		y = bounds.y;
		width = bounds.width;
		height = bounds.height;

		firePropertyChange(PROP_BOUNDS, oldBounds, bounds);
	}

	/**
	 * sets the bounds of the DrawComponent without firing a propertyChange
	 * @param newBounds the bounds to set in absolute coordinates
	 */
	protected void primSetBounds(Rectangle newBounds)
	{
		if (getBounds().equals(newBounds))
			return;

		if (newBounds.x == getX() && newBounds.y == getY()) {
			setSize(newBounds.width, newBounds.height);
			return;
		}

		if (newBounds.width == getWidth() && newBounds.height == getHeight()) {
			setLocation(newBounds.x, newBounds.y);
			return;
		}

		setSize(newBounds.width, newBounds.height);
		setLocation(newBounds.x, newBounds.y);
	}

	/**
	 * @return the AffineTransform which contains all transformation Information of
	 * the DrawComponent
	 *
	 * @see java.awt.geom.AffineTransform
	 * @see org.nightlabs.editor2d.DrawComponent#getAffineTransform()
	 */
	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	/**
	 * IMPORTANT:
	 * This method is only for deserilaztion and should never be called
	 *
	 *@see org.nightlabs.editor2d.DrawComponent#setAffineTransform(AffineTransform)
	 */
	public void setAffineTransform(AffineTransform newAffineTransform)
	{
		AffineTransform oldAffineTransform = affineTransform;
		affineTransform = newAffineTransform;
		firePropertyChange(PROP_AFFINE_TRANSFORM, oldAffineTransform, affineTransform);
	}

	/**
	 * sets the Location of the DrawComponent in absolute Coordinates
	 * and fires a propertyChangeEvent
	 *
	 * @param newX the X-Cooridnate of the DrawComponent in absolute Coordinates
	 * @param newY the Y-Cooridnate of the DrawComponent in absolute Coordinates
	 *
	 * @see org.nightlabs.editor2d.DrawComponent#setLocation(int, int)
	 */
	public void setLocation(int newX, int newY)
	{
		primSetLocation(newX, newY);
		firePropertyChange(PROP_BOUNDS, null, getBounds());
	}

	/**
	 * sets the Location of the DrawComponent in absolute Coordinates
	 *
	 * @param newX the X-Cooridnate of the DrawComponent in absolute Coordinates
	 * @param newY the Y-Cooridnate of the DrawComponent in absolute Coordinates
	 *
	 * @see org.nightlabs.editor2d.DrawComponent#setLocation(int, int)
	 */
	protected void primSetLocation(int newX, int newY)
	{
		atUtil.setToIdentity();
		atUtil.translate((float)newX - (float)getX(), (float)newY - (float)getY());

		transformRotationCenter(atUtil);
		transform(atUtil);
	}

	/**
	 * returns the location the of the drawComponent
	 * @return the location the of the drawComponent
	 */
	public Point getLocation()
	{
		return getBounds().getLocation();
	}

	/**
	 * sets the size of the DrawComponent and fires a PropertyChangeEvent
	 *
	 * @param newWidth the Width of the DrawComponent in absolute Coordinates
	 * @param newHeight the Height of the DrawComponent in absolute Coordinates
	 *
	 */
	public void setSize(int newWidth, int newHeight)
	{
		primSetSize(newWidth, newHeight);
		firePropertyChange(PROP_BOUNDS, null, getBounds());
	}

	/**
	 * sets the size of the DrawComponent
	 *
	 * @param newWidth the Width of the DrawComponent in absolute Coordinates
	 * @param newHeight the Height of the DrawComponent in absolute Coordinates
	 *
	 */
	protected void primSetSize(int newWidth, int newHeight)
	{
		atUtil.setToIdentity();
		float ratioY = (float)newHeight / (float)getHeight();
		ratioY = EditorModelUtil.checkFactor(ratioY);
		float ratioX = (float)newWidth / (float)getWidth();
		ratioX = EditorModelUtil.checkFactor(ratioX);
		float distanceX = getX() - (getX()*ratioX);
		float distanceY = getY() - (getY()*ratioY);
		atUtil.translate(distanceX, distanceY);
		atUtil.scale(ratioX, ratioY);

		transformRotationCenter(atUtil);
		transform(atUtil);
	}

	/**
	 * returns the size of the DrawComponent
	 *
	 * @return the size of the DrawComponent
	 */
	public Dimension getSize()
	{
		return getBounds().getSize();
	}

	//	/**
	//	 * transforms the RotationCenter (getRotationX(), getRotationY()) based on the
	//	 * given AffineTransform
	//	 *
	//	 * @param at the AffineTransform to transform the RotationCenter
	//	 */
	//	protected void transformRotationCenter(AffineTransform at)
	//	{
	//		if (rotationX != ROTATION_X_DEFAULT || rotationY != ROTATION_Y_DEFAULT)
	//		{
	//			Point rotationCenter = new Point(getRotationX(), getRotationY());
	//			Point newRotationCenter = new Point();
	//			at.transform(rotationCenter, newRotationCenter);
	//			rotationX = newRotationCenter.x;
	//			rotationY = newRotationCenter.y;
	//		}
	//	}

	/**
	 * this AffineTransform is used for recurrent transformation to avoid to create each
	 * time a new AffineTransform, but instead just reset this to Identity
	 */
	protected static transient final AffineTransform atUtil = new AffineTransform();

	/**
	 * return a String which prints all members with the current values
	 */
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(", name: ");
		result.append(name);
		result.append(", x: ");
		result.append(x);
		result.append(", y: ");
		result.append(y);
		result.append(", width: ");
		result.append(width);
		result.append(", height: ");
		result.append(height);
		result.append(", rotation: ");
		result.append(rotation);
		result.append(", rotationX: ");
		result.append(rotationX);
		result.append(", rotationY: ");
		result.append(rotationY);
		result.append(", bounds: ");
		result.append(bounds);
		result.append(", affineTransform: ");
		result.append(affineTransform);
		result.append(", tmpRotationX: ");
		result.append(tmpRotationX);
		result.append(", tmpRotationY: ");
		result.append(tmpRotationY);
		result.append(", renderMode: ");
		result.append(renderMode);
		result.append(')');
		return result.toString();
	}

	/**
	 * the DrawComponentContainer in which the DrawComponent is contained
	 */
	private DrawComponentContainer parent = null;

	/**
	 * returns the parent DrawComponentContainer of the DrawComponent in which it is contained
	 *
	 * @return the DrawComponentContainer in which the DrawComponent is contained
	 * @see org.nightlabs.editor2d.DrawComponentContainer
	 * @see org.nightlabs.editor2d.DrawComponent#getParent()
	 */
	public DrawComponentContainer getParent() {
		return parent;
	}

	/**
	 * sets the DrawComponentContainer in which the DrawComponent is contained
	 * and fires a propertyChange
	 *
	 * @param newParent the DrawComponentContainer to set
	 * @see org.nightlabs.editor2d.DrawComponentContainer
	 * @see org.nightlabs.editor2d.DrawComponent#setParent(DrawComponentContainer)
	 */
	public void setParent(DrawComponentContainer newParent)
	{
		DrawComponentContainer oldParent = parent;
		primSetParent(newParent);
		firePropertyChange(PROP_PARENT, oldParent, parent);
	}

	/**
	 * sets the DrawComponentContainer in which the DrawComponent is contained
	 * without firing a propertyChange
	 *
	 * @param newParent the DrawComponentContainer to set
	 * @see org.nightlabs.editor2d.DrawComponentContainer
	 */
	protected void primSetParent(DrawComponentContainer newParent) {
		parent = newParent;
	}

	/**
	 * the RenderMode of the DrawComponent which determines how the DrawComponent is drawn
	 */
	//	protected transient String renderMode = RENDER_MODE_DEFAULT;
	protected String renderMode = RENDER_MODE_DEFAULT;

	/**
	 * returns the renderMode of the DrawComponent
	 *
	 * @return the renderMode of the DrawComponent
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 * @see org.nightlabs.editor2d.DrawComponent#getRenderMode()
	 */
	public String getRenderMode() {
		return renderMode;
	}

	/**
	 * sets the renderMode of the DrawComponent and fires a propertyChange
	 *
	 * @param mode the renderMode to set
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 * @see org.nightlabs.editor2d.DrawComponent#setRenderMode(String)
	 */
	public void setRenderMode(String mode)
	{
		//		int oldRenderMode = renderMode;
		primSetRenderMode(mode);
		firePropertyChange(PROP_RENDER_MODE, null, renderMode);
	}

	/**
	 * sets the renderMode of the DrawComponent without firing a propertyChange
	 *
	 * @param mode the renderMode to set
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 */
	protected void primSetRenderMode(String mode)
	{
		renderMode = mode;
		if (getRenderModeManager() != null)
			//			renderer = getRenderModeManager().getRenderer(renderMode, getRenderModeClass());
			renderer = getRenderModeManager().getRenderer(renderMode, getRenderModeClass().getName());
	}

	private transient Renderer renderer;

	/**
	 * the Renderer which is responsible for how the the DrawComponent is drawn
	 * based on the renderMode and the renderModeClass, the RenderModeManager returns
	 * the corresponding Renderer
	 *
	 * @return the current Renderer for the DrawComponent
	 * @see org.nightlabs.editor2d.DrawComponent#getRenderer()
	 */
	public Renderer getRenderer()
	{
		if (renderer == null) {
			//			renderer = getRenderModeManager().getRenderer(getRenderMode(), getRenderModeClass());
			renderer = getRenderModeManager().getRenderer(getRenderMode(), getRenderModeClass().getName());
			if (renderer == null)
				renderer = getDefaultRenderer();
		}

		return renderer;
	}

	/**
	 * IMPORTANT: Inheritated classes should override this Method
	 *
	 * @return the Class for which Renderers should be registered at the
	 * RenderModeManager
	 *
	 */
	public abstract Class<? extends DrawComponent> getRenderModeClass();

	private transient Renderer defaultRenderer;

	/**
	 * returns the {@link Renderer} which is initalized by {@link DrawComponentImpl#initDefaultRenderer()}
	 *
	 * @return the Renderer which should be used for the default rendererMode
	 */
	public Renderer getDefaultRenderer() {
		if (defaultRenderer == null) {
			defaultRenderer = initDefaultRenderer();
		}
		return defaultRenderer;
	}

	/**
	 * returns the {@link Renderer} which should be used for the default rendererMode
	 * just in case that no Renderer has been registered in the {@link RenderModeManager}
	 * for the class {@link DrawComponentImpl#getRenderModeClass()}, which is e.g. sometimes
	 * the case after deserialization, as the RenderModeManager is transient
	 *
	 * @return the Renderer which should be used for the default rendererMode
	 */
	protected abstract Renderer initDefaultRenderer();

	protected transient RenderModeManager renderModeManager = null;

	/**
	 * sets the RenderModeManager for the DrawComponent
	 * by Default the RenderModeManager is assigned from the Root RootDrawComponent
	 *
	 * @param man the RenderModeManager to set
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 * @see org.nightlabs.editor2d.DrawComponent#setRenderModeManager(RenderModeManager)
	 */
	public void setRenderModeManager(RenderModeManager man) {
		renderModeManager = man;
	}

	/**
	 * returns the {@link RenderModeManager} of the DrawComponent, usually this
	 * is the RenderModeManager of the RootDrawComponent
	 *
	 * @return the RenderModeManager where all Renderers are registered
	 * @see org.nightlabs.editor2d.render.RenderModeManager
	 * @see org.nightlabs.editor2d.DrawComponent#getRenderModeManager()
	 */
	public RenderModeManager getRenderModeManager()
	{
		if (renderModeManager == null && getRoot() != null) {
			renderModeManager = getRoot().getRenderModeManager();
		}
		return renderModeManager;
	}

	/**
	 * returns the name of the DrawComponent-Type
	 * can be used for display purposes of e.g. filtering different DrawComponent-Types
	 *
	 * IMPORTANT: Inherited classes should override this Method
	 *
	 * @return the Description of the Type for the DrawComponent
	 */
	public abstract String getTypeName();

	/**
	 * returns the RootDrawComponent by going recursively through all parents until the root is reached
	 * if the parent is null, also null is returned
	 *
	 * @return the Root-Model-Object of the DrawComponent
	 * @see org.nightlabs.editor2d.RootDrawComponent
	 * @see org.nightlabs.editor2d.DrawComponent#getRoot()
	 */
	public RootDrawComponent getRoot()
	{
		if (getParent() != null)
			return getParent().getRoot();
		return null;
	}

	/**
	 * This Method is only a Wrapper for the transform(AffineTransform at, boolean fromParent)-Method
	 * with the boolean set to false
	 *
	 * IMPORTANT:
	 * This Method should never be overridden, but instead override {@link DrawComponent#transform(AffineTransform, boolean)}
	 *
	 * @see #transform(AffineTransform, boolean)
	 * @see org.nightlabs.editor2d.DrawComponent#transform(AffineTransform)
	 */
	public void transform(AffineTransform newAT)
	{
		transform(newAT, false);
		//		firePropertyChange(TRANSFORM_CHANGED, null, affineTransform);
		if (logger.isDebugEnabled()) {
			logger.debug("transform(AffineTransform at)");
		}
	}

	/**
	 * This Method can be implemented by subclasses to avoid the firing of a propertyChange
	 * if the transformation comes from the parent.
	 *
	 * IMPORTANT:
	 * Inherited classes should override this Method and transform the DrawComponent but also call
	 * super.transform(newAT, fromParent) first
	 *
	 * This is necessary because all Geometric Transformation
	 * (setX(), setY(), setWidth(), setHeight(), setLocation(), setSize(), setRotation())
	 * depend on this Method
	 *
	 * @param newAT the AffineTransform to transform
	 * @param fromParent determines if the Transformation comes from the parent DrawComponentContainer
	 * or if the DrawComponent is transformed directly, means it notifies the parent
	 * (getParent.notifyChildTransform(this)) about the transformation or not
	 *
	 * @see org.nightlabs.editor2d.DrawComponent#transform(AffineTransform, boolean)
	 */
	public void transform(AffineTransform newAT, boolean fromParent)
	{
		affineTransform.preConcatenate(newAT);
		bounds = null;
		if (logger.isDebugEnabled()) {
			logger.debug("transform(AffineTransform at, boolean fromParent)");
		}
	}

	/**
	 * clears the bounds without recalculating them
	 * and calls {@link DrawComponentContainer#notifyChildTransform(DrawComponent)}
	 * with this drawcomponent as parameter for the parent
	 */
	protected void clearBoundsAndNotifyParent() {
		clearBounds();
		if (getParent() != null)
			getParent().notifyChildTransform(this);
	}

	/**
	 * clears the cached bounds
	 *
	 * @see org.nightlabs.editor2d.DrawComponent#clearBounds()
	 */
	public void clearBounds()
	{
		clearBounds(false);
	}

	/**
	 * clears the cached bounds and calculates the new bounds
	 *
	 * @see org.nightlabs.editor2d.DrawComponent#clearBounds()
	 */
	public void clearBounds(boolean renew)
	{
		bounds = null;
		if (renew) {
			bounds = getBounds();
			firePropertyChange(PROP_BOUNDS, null, bounds);
		}
	}

	/**
	 * makes a deep copy of the {@link DrawComponent}
	 *
	 * IMPORTANT: always override this method instead of {@link DrawComponent#clone()}
	 *
	 * @param parent the {@link DrawComponentContainer} to which the clone should be added
	 */
	public Object clone(DrawComponentContainer parent)
	{
		try {
			DrawComponentImpl dc = (DrawComponentImpl) super.clone();
			dc.affineTransform = (AffineTransform)affineTransform.clone();
			dc.bounds = new Rectangle(getBounds());
			dc.id = DrawComponent.ID_DEFAULT;
			if (name != null) {
				dc.name = new I18nTextBuffer();
				name.copyTo(dc.name);
			}
			//			dc.languageId = new String(languageId);
			// TODO: clone Guides
			dc.horizontalGuide = null;
			dc.verticalGuide = null;

			dc.renderer = renderer;
			dc.renderMode = renderMode;
			//			dc.renderModeClass = renderModeClass;
			dc.renderModeManager = renderModeManager;
			dc.rotation = rotation;
			dc.rotationX = rotationX;
			dc.rotationY = rotationY;
			dc.tmpRotationX = tmpRotationX;
			dc.tmpRotationY = tmpRotationY;
			dc.height = height;
			dc.width = width;
			dc.x = x;
			dc.y = y;

			// clone PropertyChangeListeners
			if (listeners != null) {
				dc.listeners = new PropertyChangeSupport(dc);
				PropertyChangeListener[] propertyChangeListeners = listeners.getPropertyChangeListeners();
				for (int i = 0; i < propertyChangeListeners.length - 1; i++) {
					dc.listeners.addPropertyChangeListener(propertyChangeListeners[i]);
				}
			}

			if (properties != null) {
				Map<String, Object> cloneProperties = new HashMap<String, Object>();
				for (Iterator<Map.Entry<String, Object>> it = properties.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, Object> entry = it.next();
					Object value = entry.getValue();
					Object cloneValue;
					if (value instanceof Cloneable)
						cloneValue = Util.cloneCloneable((Cloneable)value);
					else
						cloneValue = value;
					cloneProperties.put(entry.getKey(), cloneValue);
				}
				dc.properties = cloneProperties;
			}

			// add To Parent to register in RootDrawComponent
			if (parent != null)
				parent.addDrawComponent(dc);

			return dc;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("How the hell can clone fail?! I implemented it!", e);
		}
	}

	/**
	 * makes a deep copy the DrawComponent and adds it to its parent
	 *
	 * IMPORTANT: always override {@link DrawComponent#clone(DrawComponentContainer)} instead of this method
	 */
	@Override
	public Object clone()
	{
		return clone(getParent());
	}

	private Map<String, Object> properties = null;

	/**
	 * return a Map of properties
	 *
	 * @see DrawComponent#getProperties()
	 */
	public Map<String, Object> getProperties()
	{
		if (properties == null) {
			NotifyingMap<String, Object> properties = new NotifyingMap<String, Object>();
			properties.addMapChangedListener(new MapChangedListener<String, Object>(){
				public void mapChanged(MapChangedEvent<String, Object> e) {
					if (e.getMapChange() == MapChange.PUT) {
						firePropertyChange(e.getKey(), null, e.getValue());
					}
					if (e.getMapChange() == MapChange.REMOVE) {
						firePropertyChange(e.getKey(), null, e.getValue());
					}
				}
			});
			this.properties = properties;
		}
		return properties;
	}

	private transient List<Double> constrainedRotationValues = CONSTRAINED_ROTATION_VALUES_DEFAULT;
	public List<Double> getConstrainedRotationValues() {
		return constrainedRotationValues;
	}
	public void setConstrainedRotationValues(List<Double> rotationValues) {
		this.constrainedRotationValues = rotationValues;
	}

	private transient RotationCenterPositions rotationCenterPosition = ROTATION_CENTER_POSITION_DEFAULT;
	public RotationCenterPositions getRotationCenterPosition() {
		return rotationCenterPosition;
	}
	public void setRotationCenterPosition(RotationCenterPositions rotationCenterPosition) {
		this.rotationCenterPosition = rotationCenterPosition;
	}

	private transient boolean changedRotationCenterAllowed = CHANGED_ROTATION_CENTER_ALLOWED_DEFAULT;
	public boolean isChangedRotationCenterAllowed() {
		return changedRotationCenterAllowed;
	}
	public void setChangedRotationCenterAllowed(boolean b) {
		this.changedRotationCenterAllowed = b;
	}

	public int getRotationX()
	{
		if (isChangedRotationCenterAllowed() && rotationX != ROTATION_X_DEFAULT) {
			return rotationX;
		}
		else
		{
			return getRotationCenterPositionX(getRotationCenterPosition());
		}
	}

	protected int getRotationCenterPositionX(RotationCenterPositions position)
	{
		switch(position)
		{
			case LEFT_TOP:
				return (int) getBounds().getMinX();
			case CENTER:
				return (int) getBounds().getCenterX();
			case LEFT_BOTTOM:
				return (int) getBounds().getMinX();
			case RIGHT_TOP:
				return (int) getBounds().getMaxX();
			case RIGHT_BOTTOM:
				return (int) getBounds().getMaxX();
			default:
				return (int) getBounds().getCenterX();
		}
	}

	protected int getRotationCenterPositionY(RotationCenterPositions position)
	{
		switch(position)
		{
			case LEFT_TOP:
				return (int) getBounds().getMinY();
			case CENTER:
				return (int) getBounds().getCenterY();
			case LEFT_BOTTOM:
				return (int) getBounds().getMaxY();
			case RIGHT_TOP:
				return (int) getBounds().getMinY();
			case RIGHT_BOTTOM:
				return (int) getBounds().getMaxY();
			default:
				return (int) getBounds().getCenterY();
		}
	}

	public void setRotationX(int newRotationX)
	{
		if (isChangedRotationCenterAllowed())
		{
			int oldRotationX = rotationX;
			primSetRotationX(newRotationX);
			firePropertyChange(PROP_ROTATION_X, oldRotationX, rotationX);
		}
	}

	protected void primSetRotationX(int newRotationX)
	{
		if (isChangedRotationCenterAllowed())
			rotationX = newRotationX;
	}

	public int getRotationY()
	{
		if (isChangedRotationCenterAllowed() && rotationY != ROTATION_Y_DEFAULT) {
			return rotationY;
		}
		else
		{
			return getRotationCenterPositionY(getRotationCenterPosition());
		}
	}

	public void setRotationY(int newRotationY)
	{
		if (isChangedRotationCenterAllowed())
		{
			int oldRotationY = rotationY;
			primSetRotationY(newRotationY);
			firePropertyChange(PROP_ROTATION_Y, oldRotationY, rotationY);
		}
	}

	protected void primSetRotationY(int newRotationY)
	{
		if (isChangedRotationCenterAllowed())
			rotationY = newRotationY;
	}

	public int getTmpRotationX() {
		return tmpRotationX;
	}

	public void setTmpRotationX(int newTmpRotationX)
	{
		if (isChangedRotationCenterAllowed()) {
			int oldTmpRotationX = tmpRotationX;
			tmpRotationX = newTmpRotationX;
			firePropertyChange(PROP_TMP_ROTATION_X, oldTmpRotationX, tmpRotationX);
		}
	}

	public int getTmpRotationY() {
		return tmpRotationY;
	}

	public void setTmpRotationY(int newTmpRotationY)
	{
		if (isChangedRotationCenterAllowed()) {
			int oldTmpRotationY = tmpRotationY;
			tmpRotationY = newTmpRotationY;
			firePropertyChange(PROP_TMP_ROTATION_Y, oldTmpRotationY, tmpRotationY);
		}
	}

	public void setRotation(double newRotation)
	{
		if (getConstrainedRotationValues() == null ||
				(getConstrainedRotationValues() != null &&
						getConstrainedRotationValues().contains(newRotation)) )
		{
			double oldRotation = rotation;
			primSetRotation(newRotation);
			firePropertyChange(PROP_ROTATION, oldRotation, rotation);
		}
	}

	/**
	 * sets the Rotation of the DrawComponent in degrees without firing a propertyChange
	 * @param newRotation the rotation to set in degrees
	 */
	protected void primSetRotation(double newRotation)
	{
		if (getConstrainedRotationValues() == null ||
				(getConstrainedRotationValues() != null &&
						getConstrainedRotationValues().contains(newRotation)) )
		{
			double oldRotation = rotation;
			double rotationX = getRotationX();
			double rotationY = getRotationY();
			rotation = EditorModelUtil.getConstrainedValue(newRotation, rotationLimit);

			if (oldRotation == newRotation)
				return;

			double degreesToRotateInRadians;
			double degreesToRotate = EditorModelUtil.calcDiffRotation(rotation, oldRotation);
			degreesToRotateInRadians = Math.toRadians(degreesToRotate);

			if (degreesToRotate != 0)
			{
				atUtil.setToIdentity();
				if (tmpRotationX != TMP_ROTATION_X_DEFAULT && tmpRotationY != TMP_ROTATION_Y_DEFAULT)
					atUtil.rotate(degreesToRotateInRadians, getTmpRotationX(), getTmpRotationY());
				else
					atUtil.rotate(degreesToRotateInRadians, rotationX, rotationY);
				transform(atUtil);
			}
		}
	}

	public void setRotationMember(double value) {
		this.rotation = value;
	}

	//	public double getRotationAbsolute()
	//	{
	//		double rotationTotal = rotation;
	//		DrawComponentContainer parent = getParent();
	//		if (parent != null)
	//			rotationTotal = rotationTotal + parent.getRotation();
	//		while (!(parent instanceof RootDrawComponent)) {
	//			parent = parent.getParent();
	//			rotationTotal = rotationTotal + parent.getRotation();
	//		}
	//		return rotationTotal;
	//	}
	//
	//	public void setRotationAbsolute(double rotationAbsolute)
	//	{
	//		double oldRotationAbsolute = getRotationAbsolute();
	//		double rotationToRotate = EditorModelUtil.calcDiffRotation(rotationAbsolute, oldRotationAbsolute);
	//		primSetRotation(rotationToRotate);
	//		firePropertyChange(PROP_ROTATION, oldRotationAbsolute, rotationAbsolute);
	//	}

	/**
	 * transforms the RotationCenter (getRotationX(), getRotationY()) based on the
	 * given AffineTransform
	 *
	 * @param at the AffineTransform to transform the RotationCenter
	 */
	protected void transformRotationCenter(AffineTransform at)
	{
		if (rotationX != ROTATION_X_DEFAULT && rotationY != ROTATION_Y_DEFAULT && isChangedRotationCenterAllowed())
		{
			Point rotationCenter = new Point(getRotationX(), getRotationY());
			Point newRotationCenter = new Point();
			at.transform(rotationCenter, newRotationCenter);
			rotationX = newRotationCenter.x;
			rotationY = newRotationCenter.y;
		}
	}

	/**
	 * initialize all transient fields which require not default initialization
	 * @see Serializable
	 */
	private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		this.tmpRotationX = TMP_ROTATION_X_DEFAULT;
		this.tmpRotationY = TMP_ROTATION_Y_DEFAULT;
	}

	private boolean visible = true;
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		primSetVisible(visible);
		firePropertyChange(PROP_VISIBLE, !visible, visible);
	}

	protected void primSetVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Override in order to do sth. when the datamodel is disposed.
	 * You MUST always call <b>super.dispose()</b>!
	 */
	public void dispose() {
		disposed = true;
	}

	// TODO FIXME XXX Needs to be marked transient
	private volatile boolean disposed;

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	private boolean template = false;

	public boolean isTemplate() {
		return template;
	}

	protected void primSetTemplate(boolean template) {
		this.template = template;
	}

	public void setTemplate(boolean template) {
		primSetTemplate(template);
		firePropertyChange(DrawComponent.PROP_TEMPLATE, !template, template);
	}

	private boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		primSetEditable(editable);
		firePropertyChange(DrawComponent.PROP_EDITABLE, !editable, editable);
	}

	protected void primSetEditable(boolean editable) {
		this.editable = editable;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.DrawComponent#resolutionChanged(org.nightlabs.i18n.unit.resolution.Resolution, org.nightlabs.i18n.unit.resolution.Resolution)
	 */
	@Override
	public void resolutionChanged(Resolution oldResolution, Resolution newResolution)
	{
		Point2D scale = getResolutionScale(oldResolution, newResolution);
		Rectangle newBounds = GeomUtil.scaleRect(getBounds(), scale.getX(), scale.getY(), false);
		primSetBounds(newBounds);

//		double oldX = getX();
//		double oldY = getY();
//		double newX = oldX * scale.getX();
//		double newY = oldY * scale.getY();
//		AffineTransform atTranslate = AffineTransform.getTranslateInstance(newX - oldX, newY - oldY);
//		AffineTransform atScale = AffineTransform.getScaleInstance(
//			scale.getX(), scale.getY());
//		atTranslate.concatenate(atScale);
//		transform(atTranslate);
	}

	protected Point2D getResolutionScale(Resolution oldResolution, Resolution newResolution)
	{
		if (oldResolution != null && newResolution != null)
		{
			if (!oldResolution.equals(newResolution))
			{
				double oldResX = oldResolution.getResolutionX(IResolutionUnit.dpiUnit);
				double oldResY = oldResolution.getResolutionY(IResolutionUnit.dpiUnit);
				double newResX = newResolution.getResolutionX(IResolutionUnit.dpiUnit);
				double newResY = newResolution.getResolutionY(IResolutionUnit.dpiUnit);
				double newFactorX = newResX / oldResX;
				double newFactorY = newResY / oldResY;
				return new Point2D.Double(newFactorX, newFactorY);
			}
		}
		return new Point2D.Double(1, 1);
	}
} //DrawComponentImpl
