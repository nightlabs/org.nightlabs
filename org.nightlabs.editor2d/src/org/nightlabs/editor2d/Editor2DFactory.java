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

import java.awt.Font;
import java.util.Set;

import org.nightlabs.i18n.unit.resolution.Resolution;


public interface Editor2DFactory
{
	/**
	 * Returns a new {@link RootDrawComponent}.
	 * @return a new new RootDrawComponent.
	 */
  RootDrawComponent createRootDrawComponent(boolean validate);
  
  /**
   * validates the given {@link RootDrawComponent}, and creates
   * all necessary things to be valid (like a page and a currentLayer if not present)
   * @param root the {@link RootDrawComponent} to validate
   */
  void validateRoot(RootDrawComponent root);
  
  /**
   * creates a new {@link PageDrawComponent}.
   * @return a new PageDrawComponent
   */
  PageDrawComponent createPageDrawComponent();

  /**
   * creates a new {@link PageDrawComponent} with the given parent
   * @return a new PageDrawComponent with a currentLayer
   */
  PageDrawComponent createPageDrawComponent(RootDrawComponent parent);
    
	/**
	 * creates a new {@link Layer}.
	 * @return a new Layer
	 */
  Layer createLayer();
  
	/**
	 * Returns a new {@link RectangleDrawComponent}.
	 * @return a new RectangleDrawComponent.
	 */
  RectangleDrawComponent createRectangleDrawComponent();

	/**
	 * Returns a new {@link EllipseDrawComponent}.
	 * @return a new EllipseDrawComponent.
	 */
  EllipseDrawComponent createEllipseDrawComponent();

	/**
	 * Returns a new {@link TextDrawComponent}.
	 * @return a new TextDrawComponent.
	 */
  TextDrawComponent createTextDrawComponent();

  /**
   * creates a TextDrawComponent with the given properties
   * 
   * @param text the text of the TextDrawComponent to display
   * @param fontName the name of the font
   * @param fontSize the size of the font
   * @param fontStyle the style of the font
   * @param x the x-Position
   * @param y the y-Position
   * @param parent the parent to add the TextDrawComponent to
   * @return a TextDrawComponent with the given properties
   */
  TextDrawComponent createTextDrawComponent(String text, String fontName,
  		int fontSize, int fontStyle, int x, int y, DrawComponentContainer parent);
  
  /**
   * creates a TextDrawComponent with the given properties
   * 
   * @param text the text of the TextDrawComponent to display
   * @param font the font
   * @param x the x-Position
   * @param y the y-Position
   * @param parent the parent to add the TextDrawComponent to
   * @return a TextDrawComponent with the given properties
   */
  TextDrawComponent createTextDrawComponent(String text, Font font, int x, int y,
  		DrawComponentContainer parent);
  
	/**
	 * Returns a new {@link LineDrawComponent}
	 * @return a new LineDrawComponent.
	 */
  LineDrawComponent createLineDrawComponent();

	/**
	 * Returns a new {@link EditorGuide}
	 * @return a new EditorGuide.
	 */
  EditorGuide createEditorGuide();

	/**
	 * Returns a new {@link EditorRuler}.
	 * @return a new EditorRuler.
	 */
  EditorRuler createEditorRuler();

	/**
	 * Returns a new {@link ImageDrawComponent}.
	 * @return a new ImageDrawComponent.
	 */
  ImageDrawComponent createImageDrawComponent();

  /**
   * Returns a new {@link Resolution}
   * @return a new Resolution.
   */
  Resolution createResolution();
  
  /**
   * Returns a new {@link Resolution}
   * @return a new Resolution.
   */
  GroupDrawComponent createGroupDrawComponent();
  
  /**
   * returns a Set of all Classes which can be created by the factory
   * @return a Set of all Classes which can be created by the factory
   */
  Set<Class<?>> getSupportedClass();
  
  /**
   * returns a Set of all DrawComponent (interface) classes which can be created by the factory
   * @return a Set of all DrawComponent (interface) classes which can be created by the factory
   */
  Set<Class<? extends DrawComponent>> getSupportedDrawComponentClasses();
  
  /**
   * 
   * @param clazz the class of the object to create
   * @return the corresponding object of the given class
   */
  Object createObject(Class<?> clazz);
    
  /**
   * 
   * @param clazz the DrawComponent-class to create the corresponding DrawComponent for
   * @return the corresponding DrawComponent for the given class
   */
  DrawComponent createDrawComponent(Class<?> clazz);
} //Editor2DFactory
