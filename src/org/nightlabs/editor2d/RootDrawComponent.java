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

import java.util.Locale;
import java.util.Set;

import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.i18n.unit.resolution.IResolution;
import org.nightlabs.i18n.unit.resolution.Resolution;


public interface RootDrawComponent
extends DrawComponentContainer, IResolution
{
	public static final boolean GRID_ENABLED_DEFAULT = false;
	public static final boolean RULERS_ENABLED_DEFAULT = false;
	public static final boolean SNAP_TO_GEOMETRY_DEFAULT = false;
	public static final double ZOOM_DEFAULT = 1.0;
	public static final long LAST_ID_DEFAULT = 0;

	public static final String PROP_RESOLUTION = "resolution";
	public static final String PROP_CURRENT_PAGE = "currentPage";
	public static final String PROP_CURRENT_LAYER = "currentLayer";
	public static final String TYPE_ADDED = "New type added!";
	public static final String PROP_LANGUAGE_ID = "languageID";

  /**
   * return the current selected layer of the current selected page
   * to which all new drawComponents will be added
   * @return the current selected Layer of the current Page
   * @see Layer
   */
  Layer getCurrentLayer();

  /**
   * sets the current layer of the current page
   * @param layer the Layer which should be set as current layer of the current page
   * @see Layer
   */
  void setCurrentLayer(Layer layer);

  /**
   * returns the current page of RootDrawComponent
   * @return the current page of the RootDrawComponent
   * @see PageDrawComponent
   */
  PageDrawComponent getCurrentPage();

  /**
   * sets the current page of RootDrawComponent
   * @param page the new current page to set
   * @see PageDrawComponent
   */
  void setCurrentPage(PageDrawComponent page);

  /**
   *
   * @return true if the grid in the editor is enabled
   */
  boolean isGridEnabled();

  /**
   *
   * @param value determines if the grid in the editor is enabled or not
   */
  void setGridEnabled(boolean value);

  /**
   *
   * @return if the rulers in the editor is enabled
   */
  boolean isRulersEnabled();

  /**
   *
   * @param value determines if the rulers in the editor are enabled or not
   */
  void setRulersEnabled(boolean value);

  /**
   *
   * @return if the SnapToGeometry in the editor is enabled
   */
  boolean isSnapToGeometry();

  /**
   *
   * @param value determines if the SnapToGeometry in the editor is enabled or not
   */
  void setSnapToGeometry(boolean value);

  /**
   *
   * @return the zoomFactor given as double (100% = 1.0d)
   */
  double getZoom();

  /**
   *
   * @param value the zoomFactor to set (100% = 1.0d)
   */
  void setZoom(double value);

  /**
   *
   * @return the left EditorRuler
   * @see EditorRuler
   */
  EditorRuler getLeftRuler();

  /**
   *
   * @param value the left EditorRuler to set
   */
  void setLeftRuler(EditorRuler value);

  /**
   *
   * @return the top EditorRuler
   * @see EditorRuler
   */
  EditorRuler getTopRuler();

  /**
   *
   * @param value the top EditorRuler to set
   * @see EditorRuler
   */
  void setTopRuler(EditorRuler value);

  /**
   *
   * @return the last ID which has been assigned to the last drawComponent
   * which was added/registered
   */
  long getLastID();

  /**
   * !WARNING
   * this Method should never be called,
   * except for deserialization purposes, otherwise this can lead to
   * an inconsistant Data-Model
   *
   * @param value sets the lastID
   */
  void setLastID(long value);

  /**
   * generates an continious (unique) ID for drawComponents which are added/registered
   *
   * @return the next unique ID
   */
  long nextID();

  /**
   *
   * @param id the ID of the DrawComponent
   * @return the drawComponent with the given ID
   */
  DrawComponent getDrawComponent(long id);

  /**
   * registers a DrawComponent in the RootDrawComponent
   * This Method assigns the new id, adds the DrawComponent to the
   * id2DrawComponent-Map as well as in the class2DrawComponents-Map
   *
   * This Method must be called from a DrawComponentContainer when
   * a child is added
   *
   * @param drawComponent the drawComponent to register
   */
  void registerDrawComponent(DrawComponent drawComponent);

  /**
   * unregisters a DrawComponent from the RootDrawComponent
   * It removes the DrawComponent from the id2DrawComponent-Map as well
   * as from the class2DrawComponents-Map.
   *
   * @param drawComponent the DrawComponent to unregister
   */
  void unregisterDrawComponent(DrawComponent drawComponent);

  /**
   * unregisters a DrawComponent from the RootDrawComponent
   * It removes the DrawComponent from the id2DrawComponent-Map as well
   * as from the class2DrawComponents-Map.
   *
   * @param id the id of the DrawComponent to unregister
   */
  void unregisterDrawComponent(long id);

//  /**
//   * This Method returns a List containing all DrawComponents for the given
//   * Class (Type)
//   *
//   * @param type the Class of the DrawComponents
//   * @return a List containing all DrawComponents of the given Class
//   */
////  List<DrawComponent> getDrawComponents(Class type);
//  <T extends DrawComponent> List<T> getDrawComponents(Class<T> type);
//
//  /**
//   * @return a Map with the following key and value
//   * key: Class (type) e.g. LineDrawComponent.class
//   * value: a List of DrawComponents of the given class
//   */
////  Map<Class, List<DrawComponent>> getClass2DrawComponents();
////  <T extends DrawComponent> Map<Class<T>, List<T>> getClass2DrawComponents();
//  Map<Class<? extends DrawComponent>, List<DrawComponent>> getClass2DrawComponents();

//  /**
//   * !WARNING
//   * This Method should only be called for deserializing purposes,
//   * otherwise this can lead to an inconsistent Data-Model
//   *
//   * @param value the class2DrawComponents-Map to set
//   * @see #getClass2DrawComponents()
//   */
////	void setClass2DrawComponents(Map<Class, List<DrawComponent> value);
//  <T extends DrawComponent> void setClass2DrawComponents(Map<Class<T>, List<T>> value);

//	/**
//	 * @return a Map with the following key and value
//	 * key: the ID (Long) of a DrawComponent
//	 * value: the DrawComponent with the given ID
//	 */
//  Map<Long, DrawComponent> getId2DrawComponent();

//  /**
//   * !WARNING
//   * This Method should only be called for deserializing purposes,
//   * otherwise this can lead to an inconsistent Data-Model
//   *
//   * @param value the id2DrawComponents-Map to set
//   * @see #getId2DrawComponent()
//   */
//	void setId2DrawComponent(Map<Long, DrawComponent> value);

  /**
   * Get all classes of all {@link DrawComponent} instances.
   * Implementations should cache the result and update or at least
   * invalidate the cache whenever objects are added or removed.
   */
  Set<Class<? extends DrawComponent>> getDrawComponentClasses();

  /**
   * return the resolution of the RootDrawComponent
   * @return the resolution of the RootDrawComponent
   */
  Resolution getResolution();

  /**
   * @param resolution the Resolution to set
   */
  void setResolution(Resolution resolution);

  /**
   * returns the unit for the current resolution
   * @return the unit for the current resolution
   */
  DotUnit getModelUnit();

  /**
   * return the languageID, which is used for the I18nText when adding new drawComponents
   * by default this is {@link Locale#getLanguage()} from the current default Locale
   *
   * @return the languageID, which is used for the I18nText when adding new drawComponents
   */
  String getLanguageID();

  /**
   * sets the languageID which is used for the I18nText when adding new drawComponents
   * @param languageID the languageID to set
   */
  void setLanguageID(String languageID);

  /**
   *
   * @return the (optional) nameProvider which is responsible for setting names
   */
  NameProvider getNameProvider();

  /**
   * sets the nameProvider which is responsible for setting the names
   * @param nameProvider the namePorvider to set
   */
  void setNameProvider(NameProvider nameProvider);
} // RootDrawComponent
