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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class XFormToolkit 
extends FormToolkit 
{
	public static final int MODE_COMPOSITE = 1; 
	public static final int MODE_FORM = 2;
	
	/**
	 * determines the current creation mode
	 * either MODE_COMPOSITE or MODE_FORM
	 */
	protected int currentMode = MODE_FORM;
	
	/**
	 * returns the current creation mode
	 * either MODE_COMPOSITE or MODE_FORM 
	 * @return the current creation Mode
	 */
	public int getCurrentMode() {
		return currentMode;
	}
	/**
	 * sets the current creation mode
	 * either MODE_COMPOSITE or MODE_FORM
	 * @param currentMode the creation mode to set
	 */
	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

	/**
	 * @param display
	 */
	public XFormToolkit(Display display) {
		super(display);
	}

	/**
	 * @param colors
	 */
	public XFormToolkit(FormColors colors) {
		super(colors);
	}

	protected Composite createInternalComposite(Composite parent, int style) 
	{
//		return new XComposite(parent, style);
		return new Composite(parent, style);
	}
	
	protected int defaultStyle = SWT.NONE;
	
	@Override
	public Button createButton(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createButton(parent, text, style);
			case(MODE_COMPOSITE):
				Button b = new Button(parent, style);
				b.setText(text);
				return b;
			default:
				return super.createButton(parent, text, style);
		}			
	}
	
	@Override
	public Composite createComposite(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createComposite(parent, style);
			case(MODE_COMPOSITE):
				Composite c = createInternalComposite(parent, style);
				return c;
			default:
				return super.createComposite(parent, style);
		}			
	}

	@Override
	public Composite createComposite(Composite parent) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createComposite(parent);
			case(MODE_COMPOSITE):
				return createInternalComposite(parent, defaultStyle);
			default:
				return super.createComposite(parent);
		}			
	}

	@Override
	public ExpandableComposite createExpandableComposite(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createExpandableComposite(parent, style);
			case(MODE_COMPOSITE):
				return new ExpandableComposite(parent, style);				
			default:
				return super.createExpandableComposite(parent, style);
		}			
	}

	@Override
	public Label createLabel(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createLabel(parent, text, style);
			case(MODE_COMPOSITE):
				Label l = new Label(parent, style);
				l.setText(text);
				return l;				
			default:
				return super.createLabel(parent, text, style);
		}			
	}

	@Override
	public Label createLabel(Composite parent, String text) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createLabel(parent, text);
			case(MODE_COMPOSITE):
				Label l = new Label(parent, defaultStyle);
				l.setText(text);
				return l;								
			default:
				return super.createLabel(parent, text);
		}			
	}

	@Override
	public Table createTable(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createTable(parent, style);
			case(MODE_COMPOSITE):
				return new Table(parent, style);
			default:
				return super.createTable(parent, style);
		}			
	}

	@Override
	public Text createText(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createText(parent, text, style);
			case(MODE_COMPOSITE):
				Text t = new Text(parent, style);
				t.setText(text);
				return t;
			default:
				return super.createText(parent, text, style);
		}			
	}

	@Override
	public Text createText(Composite parent, String text) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createText(parent, text);
			case(MODE_COMPOSITE):
				Text t = new Text(parent, defaultStyle);
				t.setText(text);
				return t;
			default:
				return super.createText(parent, text);
		}	
	}

	@Override
	public Tree createTree(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case(MODE_FORM):
				return super.createTree(parent, style);
			case(MODE_COMPOSITE):
				return new Tree(parent, style);
			default:
				return super.createTree(parent, style);
		}	
	}

	@Override
	public void adapt(Composite composite) {
		// TODO Auto-generated method stub
		super.adapt(composite);
	}

	@Override
	public void adapt(Control control, boolean trackFocus, boolean trackKeyboard) {
		// TODO Auto-generated method stub
		super.adapt(control, trackFocus, trackKeyboard);
	}

	@Override
	public Composite createCompositeSeparator(Composite parent) {
		// TODO Auto-generated method stub
		return super.createCompositeSeparator(parent);
	}

	@Override
	public Form createForm(Composite parent) 
	{		
		return super.createForm(parent);
	}

	@Override
	public FormText createFormText(Composite parent, boolean trackFocus) {
		// TODO Auto-generated method stub
		return super.createFormText(parent, trackFocus);
	}

	@Override
	public Hyperlink createHyperlink(Composite parent, String text, int style) {
		// TODO Auto-generated method stub
		return super.createHyperlink(parent, text, style);
	}

	@Override
	public ImageHyperlink createImageHyperlink(Composite parent, int style) {
		// TODO Auto-generated method stub
		return super.createImageHyperlink(parent, style);
	}

	@Override
	public ScrolledPageBook createPageBook(Composite parent, int style) {
		// TODO Auto-generated method stub
		return super.createPageBook(parent, style);
	}

	@Override
	public ScrolledForm createScrolledForm(Composite parent) {
		// TODO Auto-generated method stub
		return super.createScrolledForm(parent);
	}

	@Override
	public Section createSection(Composite parent, int sectionStyle) {
		// TODO Auto-generated method stub
		return super.createSection(parent, sectionStyle);
	}

	@Override
	public Label createSeparator(Composite parent, int style) {
		// TODO Auto-generated method stub
		return super.createSeparator(parent, style);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	@Override
	public int getBorderStyle() {
		// TODO Auto-generated method stub
		return super.getBorderStyle();
	}

	@Override
	public FormColors getColors() {
		// TODO Auto-generated method stub
		return super.getColors();
	}

	@Override
	public HyperlinkGroup getHyperlinkGroup() {
		// TODO Auto-generated method stub
		return super.getHyperlinkGroup();
	}

	@Override
	public int getOrientation() {
		// TODO Auto-generated method stub
		return super.getOrientation();
	}

	@Override
	public void paintBordersFor(Composite parent) {
		// TODO Auto-generated method stub
		super.paintBordersFor(parent);
	}

	@Override
	public void refreshHyperlinkColors() {
		// TODO Auto-generated method stub
		super.refreshHyperlinkColors();
	}

	@Override
	public void setBackground(Color bg) {
		// TODO Auto-generated method stub
		super.setBackground(bg);
	}

	@Override
	public void setBorderStyle(int style) {
		// TODO Auto-generated method stub
		super.setBorderStyle(style);
	}

	@Override
	public void setOrientation(int orientation) {
		// TODO Auto-generated method stub
		super.setOrientation(orientation);
	}

	
	//TODO: add Method createSpinner(Composite parent, int style)
}
