/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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
package org.nightlabs.base.form;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 *
 */
public class XScrolledForm 
extends ScrolledForm 
{

	/**
	 * @param parent
	 */
	public XScrolledForm(Composite parent) {
		this(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}

	protected XForm content;
	
	/**
	 * @param parent
	 * @param style
	 */
	public XScrolledForm(Composite parent, int style) 
	{
		super(parent, style);
		super.setMenu(parent.getMenu());
		content = new XForm(this, SWT.NULL);
		super.setContent(content);
		content.setMenu(getMenu());
	}

  /**
   * Passes the menu to the body.
   * @param menu
   */
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		if (content!=null) 
			content.setMenu(menu);
	}
	
	/**
	 * Returns the title text that will be rendered at the top of the form.
	 * 
	 * @return the title text
	 */
	public String getText() {
		return content.getText();
	}
	
	/**
	 * Returns the title image that will be rendered to the left of the title.
	 * 
	 * @return the title image
	 */
	public Image getImage() {
		return content.getImage();
	}
	/**
	 * Sets the foreground color of the form. This color will also be used for
	 * the body.
	 */
	public void setForeground(Color fg) {
		super.setForeground(fg);
		content.setForeground(fg);
	}
	/**
	 * Sets the background color of the form. This color will also be used for
	 * the body.
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		content.setBackground(bg);
	}

	/**
	 * Sets the text to be rendered at the top of the form above the body as a
	 * title.
	 * 
	 * @param text
	 *            the title text
	 */
	public void setText(String text) {
		content.setText(text);
		reflow(true);
	}
	
	/**
	 * Sets the image to be rendered to the left of the title.
	 * 
	 * @param image
	 *            the title image or <code>null</code> for no image.
	 */
	public void setImage(Image image) {
		content.setImage(image);
		reflow(true);
	}
	/**
	 * Returns the optional background image of this form. The image is
	 * rendered starting at the position 0,0 and is painted behind the title.
	 * 
	 * @return Returns the background image.
	 */
	public Image getBackgroundImage() {
		return content.getBackgroundImage();
	}
	/**
	 * Sets the optional background image to be rendered behind the title
	 * starting at the position 0,0.
	 * 
	 * @param backgroundImage
	 *            The backgroundImage to set.
	 */
	public void setBackgroundImage(Image backgroundImage) {
		content.setBackgroundImage(backgroundImage);
	}
	/**
	 * Returns the tool bar manager that is used to manage tool items in the
	 * form's title area.
	 * 
	 * @return form tool bar manager
	 */
	public IToolBarManager getToolBarManager() {
		return content.getToolBarManager();
	}
	/**
	 * Updates the local tool bar manager if used. Does nothing if local tool
	 * bar manager has not been created yet.
	 */
	public void updateToolBar() {
		content.updateToolBar();
	}
	/**
	 * Recomputes the body layout and form scroll bars. The method should be
	 * used when changes somewhere in the form body invalidate the current
	 * layout and/or scroll bars.
	 * 
	 * @param flushCache
	 *            if <samp>true </samp>, drop any cached layout information and
	 *            compute new one.
	 */
	public void reflow(boolean flushCache) {
		super.reflow(flushCache);
	}
	/**
	 * Returns the container that occupies the body of the form (the form area
	 * below the title). Use this container as a parent for the controls that
	 * should be in the form. No layout manager has been set on the form body.
	 * 
	 * @return Returns the body of the form.
	 */
	public Composite getBody() {
		return content.getBody();
	}
	/**
	 * Returns the instance of the form owned by the scrolled form.
	 * 
	 * @return the form instance
	 */
	public Form getForm() {
		return content;
	}	
}
