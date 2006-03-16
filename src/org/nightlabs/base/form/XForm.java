/**
 * Created Mar 16, 2006, 7:49:40 PM by nick
 */
package org.nightlabs.base.form;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ILayoutExtension;
import org.eclipse.ui.forms.widgets.SizeCache;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *				 Daniel.Mazurek <at> NightLabs <dot> de
 */
public class XForm extends Form
{
	protected SizeCache bodyCache = new SizeCache();
	protected SizeCache headCache = new SizeCache();
	
	protected Composite body;
	protected Text selectionText;
	protected XFormHeading head;
	
	protected class XFormLayout extends Layout implements ILayoutExtension 
	{
		public int computeMinimumWidth(Composite composite, boolean flushCache) {
			return computeSize(composite, 5, SWT.DEFAULT, flushCache).x;
		}

		public int computeMaximumWidth(Composite composite, boolean flushCache) {
			return computeSize(composite, SWT.DEFAULT, SWT.DEFAULT, flushCache).x;
		}

		public Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			if (flushCache) {
				bodyCache.flush();
				headCache.flush();
			}
			bodyCache.setControl(body);
			headCache.setControl(head);

			int width = 0;
			int height = 0;

			Point hsize = headCache.computeSize(FormUtil.getWidthHint(wHint,
					head), SWT.DEFAULT);
			width = Math.max(hsize.x, width);
			height = hsize.y;

			Point bsize = bodyCache.computeSize(FormUtil.getWidthHint(wHint,
					body), SWT.DEFAULT);
			width = Math.max(bsize.x, width);
			height += bsize.y;
			return new Point(width, height);
		}

		protected void layout(Composite composite, boolean flushCache) {
			if (flushCache) {
				bodyCache.flush();
				headCache.flush();
			}
			bodyCache.setControl(body);
			headCache.setControl(head);
			Rectangle carea = composite.getClientArea();

			Point hsize = headCache.computeSize(carea.width, SWT.DEFAULT);
			headCache.setBounds(0, 0, carea.width, hsize.y);
			bodyCache
					.setBounds(0, hsize.y, carea.width, carea.height - hsize.y);
		}
	}
	
	
	public XForm(Composite parent, int style)
	{
		super(parent, style);
		// Problem because super.setLayout() does nothing and is final
		super.setLayout(new XFormLayout());
		head = new XFormHeading(this, SWT.NULL);
		head.setMenu(parent.getMenu());		
		body = new XLayoutComposite(this, SWT.NULL);
		body.setMenu(parent.getMenu());
	}
	
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		head.setMenu(menu);
		body.setMenu(menu);
	}
	
	// TODO: Problem with final method
//	/**
//	 * Fully delegates the size computation to the internal layout manager.
//	 */
//	public Point computeSize(int wHint, int hHint, boolean changed) 
//	{
//		return ((GridLayout)getLayout()).computeSize(this, wHint, hHint,
//				changed);
//	}
	
	// TODO: Problem with final method
//	/**
//	 * Prevents from changing the custom control layout.
//	 */
//	public void setLayout(Layout layout) {
//	}	
	
	/**
	 * Returns the title text that will be rendered at the top of the form.
	 * 
	 * @return the title text
	 */
	public String getText() {
		return head.getText();
	}

	/**
	 * Returns the title image that will be rendered to the left of the title.
	 * 
	 * @return the title image or <code>null</code> if not set.
	 * @since 3.2
	 */
	public Image getImage() {
		return head.getImage();
	}

	/**
	 * Sets the foreground color of the form. This color will also be used for
	 * the body.
	 * 
	 * @param fg
	 *            the foreground color
	 */
	public void setForeground(Color fg) {
		super.setForeground(fg);
		head.setForeground(fg);
		body.setForeground(fg);
	}

	/**
	 * Sets the background color of the form. This color will also be used for
	 * the body.
	 * 
	 * @param bg
	 *            the background color
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		head.setBackground(bg);
		body.setBackground(bg);
	}

	/**
	 * Sets the font of the header text.
	 * 
	 * @param font
	 *            the new font
	 */
	public void setFont(Font font) {
		super.setFont(font);
		head.setFont(font);
	}

	/**
	 * Sets the text to be rendered at the top of the form above the body as a
	 * title.
	 * 
	 * @param text
	 *            the title text
	 */
	public void setText(String text) {
		head.setText(text);
		layout();
		redraw();
	}

	/**
	 * Sets the image to be rendered to the left of the title.
	 * 
	 * @param image
	 *            the title image or <code>null</code> to show no image.
	 * @since 3.2
	 */
	public void setImage(Image image) {
		head.setImage(image);
		layout();
		redraw();
	}

	/**
	 * Sets the background colors to be painted behind the title text in a
	 * gradient.
	 * 
	 * @param gradientColors
	 *            the array of colors that form the gradient
	 * @param percents
	 *            the partition of the overall space between the gradient colors
	 * @param vertical
	 *            of <code>true</code>, the gradient will be rendered
	 *            vertically, if <code>false</code> the orientation will be
	 *            horizontal.
	 */

	public void setTextBackground(Color[] gradientColors, int[] percents,
			boolean vertical) {
		head.setTextBackground(gradientColors, percents, vertical);
	}

	/**
	 * Returns the optional background image of the form head.
	 * 
	 * @return the background image or <code>null</code> if not specified.
	 */
	public Image getBackgroundImage() {
		return head.getBackgroundImage();
	}

	/**
	 * Sets the optional background image to be rendered behind the title
	 * starting at the position 0,0. If the image is smaller than the container
	 * in any dimension, it will be tiled.
	 * 
	 * @since 3.2
	 * 
	 * @param backgroundImage
	 *            the head background image.
	 * 
	 */
	public void setBackgroundImage(Image backgroundImage) {
		head.setBackgroundImage(backgroundImage);
	}

	/**
	 * Returns the tool bar manager that is used to manage tool items in the
	 * form's title area.
	 * 
	 * @return form tool bar manager
	 */
	public IToolBarManager getToolBarManager() {
		return head.getToolBarManager();
	}

	/**
	 * Updates the local tool bar manager if used. Does nothing if local tool
	 * bar manager has not been created yet.
	 */
	public void updateToolBar() {
		head.updateToolBar();
	}

	/**
	 * Returns the container that occupies the head of the form (the form area
	 * above the body). Use this container as a parent for the head client.
	 * 
	 * @return the head of the form.
	 * @since 3.2
	 */
	public Composite getHead() {
		return head;
	}

	/**
	 * Returns the optional head client if set.
	 * 
	 * @return the head client or <code>null</code> if not set.
	 * @see #setHeadClient(Control)
	 * @since 3.2
	 */
	public Control getHeadClient() {
		return head.getHeadClient();
	}

	/**
	 * Sets the optional head client. Head client is placed after the form
	 * title. This option causes the tool bar to be placed in the second raw of
	 * the header (below the head client).
	 * <p>
	 * The head client must be a child of the composite returned by
	 * <code>getHead()</code> method.
	 * 
	 * @param headClient
	 *            the optional child of the head
	 * @since 3.2
	 */
	public void setHeadClient(Control headClient) {
		head.setHeadClient(headClient);
		layout();
	}

	/**
	 * Returns the container that occupies the body of the form (the form area
	 * below the title). Use this container as a parent for the controls that
	 * should be in the form. No layout manager has been set on the form body.
	 * 
	 * @return Returns the body of the form.
	 */
	public Composite getBody() {
		return body;
	}

	/**
	 * Tests if the background image is tiled to cover the entire area of the
	 * form heading.
	 * 
	 * @return <code>true</code> if heading background image is tiled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isBackgroundImageTiled() {
		return true;
	}

	/**
	 * Sets whether the header background image is repeated to cover the entire
	 * heading area or not.
	 * 
	 * @param backgroundImageTiled
	 *            set <code>true</code> to tile the image, or
	 *            <code>false</code> to paint the background image only once
	 */
	public void setBackgroundImageTiled(boolean backgroundImageTiled) {
		head.setBackgroundImageTiled(backgroundImageTiled);
	}

	/**
	 * Returns the background image alignment.
	 * 
	 * @deprecated due to the underlying widget limitations, background image is
	 *             always tiled and alignment cannot be controlled.
	 * @return SWT.LEFT
	 */
	public int getBackgroundImageAlignment() {
		return head.getBackgroundImageAlignment();
	}

	protected void setSelectionText(Text text) {
		if (selectionText != null && selectionText != text) {
			selectionText.clearSelection();
		}
		this.selectionText = text;
	}

	/**
	 * Tests if the form head separator is visible.
	 * 
	 * @return <code>true</code> if the head/body separator is visible,
	 *         <code>false</code> otherwise
	 * @since 3.2
	 */
	public boolean isSeparatorVisible() {
		return head.isSeparatorVisible();
	}

	/**
	 * If set, adds a separator between the head and body. If gradient text
	 * background is used, the separator will use gradient colors.
	 * 
	 * @param addSeparator
	 *            <code>true</code> to make the separator visible,
	 *            <code>false</code> otherwise.
	 * @since 3.2
	 */
	public void setSeparatorVisible(boolean addSeparator) {
		head.setSeparatorVisible(addSeparator);
	}

	/**
	 * Returns the color used to render the optional head separator. If gradient
	 * text background is used additional colors from the gradient will be used
	 * to render the separator.
	 * 
	 * @return separator color or <code>null</code> if not set.
	 * @since 3.2
	 */

	public Color getSeparatorColor() {
		return head.getSeparatorColor();
	}

	/**
	 * Sets the color to be used to render the optional head separator.
	 * 
	 * @param separatorColor
	 *            the color to render the head separator or <code>null</code>
	 *            to use the default color.
	 * @since 3.2
	 */
	public void setSeparatorColor(Color separatorColor) {
		head.setSeparatorColor(separatorColor);
	}

	/**
	 * Sets the message for this form. Message text is rendered in the form head
	 * when shown.
	 * 
	 * @param message
	 *            the message, or <code>null</code> to clear the message
	 * @see #setMessage(String, int)
	 * @since 3.2
	 */
	public void setMessage(String message) {
		head.setMessage(message);
	}

	/**
	 * Sets the message for this form with an indication of what type of message
	 * it is.
	 * <p>
	 * The valid message types are one of <code>NONE</code>,
	 * <code>INFORMATION</code>,<code>WARNING</code>, or
	 * <code>ERROR</code>.
	 * </p>
	 * <p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 * @param newType
	 *            the message type
	 * @since 3.2
	 */

	public void setMessage(String newMessage, int newType) {
		head.setMessage(newMessage, newType);
	}

	/**
	 * Tests if the form is in the 'busy' state. Busy form displays 'busy'
	 * animation in the area of the title image.
	 * 
	 * @return <code>true</code> if busy, <code>false</code> otherwise.
	 * @since 3.2
	 */

	public boolean isBusy() {
		return head.isBusy();
	}

	/**
	 * Sets the form's busy state. Busy form will display 'busy' animation in
	 * the area of the title image.
	 * 
	 * @param busy
	 *            the form's busy state
	 * @since 3.2
	 */

	public void setBusy(boolean busy) {
		head.setBusy(busy);
	}
	
}
