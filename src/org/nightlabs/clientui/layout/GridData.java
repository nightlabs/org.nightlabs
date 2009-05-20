package org.nightlabs.clientui.layout;

import java.io.Serializable;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.nightlabs.clientui.layout.id.GridDataID;

/**
 * Instances of {@link GridData} are used to configure the size, margin and alignment of a cell in a {@link GridLayout}.
 * It is highly inspired by SWT GirdData and reflects all its fields.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] -->
 *
 * @jdo.persistence-capable
 *		identity-type="application"
 *		objectid-class="org.nightlabs.clientui.layout.id.GridDataID"
 *		detachable="true"
 *		table="NightLabsClientUI_GridData"
 *
 * @jdo.inheritance strategy="new-table"
 * @jdo.inheritance-discriminator strategy="class-name"
 *
 * @jdo.create-objectid-class field-order="gridDataID"
 */
@PersistenceCapable(
	objectIdClass=GridDataID.class,
	identityType=IdentityType.APPLICATION,
	detachable="true",
	table="NightLabsClientUI_GridData")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class GridData implements Serializable
{
	private static final long serialVersionUID = 20090119L;

	/**
	 * Value for horizontalAlignment or verticalAlignment.
	 * Position the control at the top or left of the cell.
	 */
	public static final int BEGINNING = 1;

	/**
	 * Value for horizontalAlignment or verticalAlignment.
	 * Position the control in the vertical or horizontal center of the cell
	 */
	public static final int CENTER = 2;

	/**
	 * Value for horizontalAlignment or verticalAlignment.
	 * Position the control at the bottom or right of the cell
	 */
	public static final int END = 3;

	/**
	 * Value for horizontalAlignment or verticalAlignment.
	 * Resize the control to fill the cell horizontally or vertically.
	 */
	public static final int FILL = 4;

	public static final int DEFAULT = -1;

	/**
	 * @jdo.field primary-key="true"
	 */
	@PrimaryKey
	private long gridDataID;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int verticalAlignment;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int horizontalAlignment;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int widthHint;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int heightHint;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int horizontalIndent;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int verticalIndent;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int horizontalSpan;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int verticalSpan;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public boolean grabExcessHorizontalSpace;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public boolean grabExcessVerticalSpace;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int minimumWidth;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public int minimumHeight;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	public boolean exclude;

	/**
	 * @deprecated Only for JDO
	 */
	@Deprecated
	protected GridData() {

	}

	/**
	 * Constructs a new {@link GridData} with the given id and
	 * default values for all fields.
	 */
	public GridData(long gridDataID) {
		this.gridDataID = gridDataID;
		this.verticalAlignment = CENTER;
		this.horizontalAlignment = BEGINNING;
		this.widthHint = DEFAULT;
		this.heightHint = DEFAULT;
		this.horizontalIndent = 0;
		this.verticalIndent = 0;
		this.horizontalSpan = 1;
		this.verticalSpan = 1;
		this.grabExcessHorizontalSpace = false;
		this.grabExcessVerticalSpace = false;
		this.minimumWidth = 0;
		this.minimumHeight = 0;
		this.exclude = false;
	}

	/**
	 * verticalAlignment specifies how controls will be positioned
	 * vertically within a cell.
	 *
	 * The default value is {@link #CENTER}.
	 *
	 * Possible values are: <ul>
	 *    <li>{@link #BEGINNING}: Position the control at the top of the cell</li>
	 *    <li>{@link #CENTER}: Position the control in the vertical center of the cell</li>
	 *    <li>{@link #END}: Position the control at the bottom of the cell</li>
	 *    <li>{@link #FILL}: Resize the control to fill the cell vertically</li>
	 * </ul>
	 *
	 * @return the verticalAlignment
	 */
	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * verticalAlignment specifies how controls will be positioned
	 * vertically within a cell.
	 *
	 * The default value is {@link #CENTER}.
	 *
	 * Possible values are: <ul>
	 *    <li>{@link #BEGINNING}: Position the control at the top of the cell</li>
	 *    <li>{@link #CENTER}: Position the control in the vertical center of the cell</li>
	 *    <li>{@link #END}: Position the control at the bottom of the cell</li>
	 *    <li>{@link #FILL}: Resize the control to fill the cell vertically</li>
	 * </ul>
	 *
	 * @param verticalAlignment the verticalAlignment to set
	 */
	public void setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	/**
	 * horizontalAlignment specifies how controls will be positioned
	 * horizontally within a cell.
	 *
	 * The default value is {@link #BEGINNING}.
	 *
	 * Possible values are: <ul>
	 *    <li>{@link #BEGINNING}: Position the control at the left of the cell</li>
	 *    <li>{@link #CENTER}: Position the control in the horizontal center of the cell</li>
	 *    <li>{@link #END}: Position the control at the right of the cell</li>
	 *    <li>{@link #FILL}: Resize the control to fill the cell horizontally</li>
	 * </ul>
	 *
	 * @return the horizontalAlignment
	 */
	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * horizontalAlignment specifies how controls will be positioned
	 * horizontally within a cell.
	 *
	 * The default value is {@link #BEGINNING}.
	 *
	 * Possible values are: <ul>
	 *    <li>{@link #BEGINNING}: Position the control at the left of the cell</li>
	 *    <li>{@link #CENTER}: Position the control in the horizontal center of the cell</li>
	 *    <li>{@link #END}: Position the control at the right of the cell</li>
	 *    <li>{@link #FILL}: Resize the control to fill the cell horizontally</li>
	 * </ul>
	 *
	 * @param horizontalAlignment the horizontalAlignment to set
	 */
	public void setHorizontalAlignment(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 * widthHint specifies the preferred width in pixels.
	 *
	 * The default value is {@link #DEFAULT}.
	 *
	 * @return the widthHint
	 */
	public int getWidthHint() {
		return widthHint;
	}

	/**
	 * widthHint specifies the preferred width in pixels.
	 *
	 * The default value is {@link #DEFAULT}.
	 *
	 * @param widthHint the widthHint to set
	 */
	public void setWidthHint(int widthHint) {
		this.widthHint = widthHint;
	}

	/**
	 * heightHint specifies the preferred height in pixels.
	 *
	 * The default value is {@link #DEFAULT}.
	 *
	 * @return the heightHint
	 */
	public int getHeightHint() {
		return heightHint;
	}

	/**
	 * heightHint specifies the preferred height in pixels.
	 *
	 * The default value is {@link #DEFAULT}.
	 *
	 * @param heightHint the heightHint to set
	 */
	public void setHeightHint(int heightHint) {
		this.heightHint = heightHint;
	}

	/**
	 * horizontalIndent specifies the number of pixels of indentation
	 * that will be placed along the left side of the cell.
	 *
	 * The default value is 0.
	 *
	 * @return the horizontalIndent
	 */
	public int getHorizontalIndent() {
		return horizontalIndent;
	}

	/**
	 * horizontalIndent specifies the number of pixels of indentation
	 * that will be placed along the left side of the cell.
	 *
	 * The default value is 0.
	 *
	 * @param horizontalIndent the horizontalIndent to set
	 */
	public void setHorizontalIndent(int horizontalIndent) {
		this.horizontalIndent = horizontalIndent;
	}

	/**
	 * verticalIndent specifies the number of pixels of indentation
	 * that will be placed along the top side of the cell.
	 *
	 * The default value is 0.
	 *
	 * @return the verticalIndent
	 */
	public int getVerticalIndent() {
		return verticalIndent;
	}

	/**
	 * verticalIndent specifies the number of pixels of indentation
	 * that will be placed along the top side of the cell.
	 *
	 * The default value is 0.
	 *
	 * @param verticalIndent the verticalIndent to set
	 */
	public void setVerticalIndent(int verticalIndent) {
		this.verticalIndent = verticalIndent;
	}

	/**
	 * horizontalSpan specifies the number of column cells that the control
	 * will take up.
	 *
	 * The default value is 1.
	 *
	 * @return the horizontalSpan
	 */
	public int getHorizontalSpan() {
		return horizontalSpan;
	}

	/**
	 * horizontalSpan specifies the number of column cells that the control
	 * will take up.
	 *
	 * The default value is 1.
	 *
	 * @param horizontalSpan the horizontalSpan to set
	 */
	public void setHorizontalSpan(int horizontalSpan) {
		this.horizontalSpan = horizontalSpan;
	}

	/**
	 * verticalSpan specifies the number of row cells that the control
	 * will take up.
	 *
	 * The default value is 1.
	 *
	 * @return the verticalSpan
	 */
	public int getVerticalSpan() {
		return verticalSpan;
	}

	/**
	 * verticalSpan specifies the number of row cells that the control
	 * will take up.
	 *
	 * The default value is 1.
	 *
	 * @param verticalSpan the verticalSpan to set
	 */
	public void setVerticalSpan(int verticalSpan) {
		this.verticalSpan = verticalSpan;
	}

	/**
	 * grabExcessHorizontalSpace specifies whether the width of the cell
	 * changes depending on the size of the parent widget.
	 *
	 * The default value is false.
	 *
	 * @return the grabExcessHorizontalSpace
	 */
	public boolean isGrabExcessHorizontalSpace() {
		return grabExcessHorizontalSpace;
	}

	/**
	 * grabExcessHorizontalSpace specifies whether the width of the cell
	 * changes depending on the size of the parent widget.
	 *
	 * The default value is false.
	 *
	 * @param grabExcessHorizontalSpace the grabExcessHorizontalSpace to set
	 */
	public void setGrabExcessHorizontalSpace(boolean grabExcessHorizontalSpace) {
		this.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
	}

	/**
	 * grabExcessVerticalSpace specifies whether the height of the cell
	 * changes depending on the size of the parent widget.
	 *
	 * The default value is false.
	 *
	 * @return the grabExcessVerticalSpace
	 */
	public boolean isGrabExcessVerticalSpace() {
		return grabExcessVerticalSpace;
	}

	/**
	 * grabExcessVerticalSpace specifies whether the height of the cell
	 * changes depending on the size of the parent widget.
	 *
	 * The default value is false.
	 *
	 * @param grabExcessVerticalSpace the grabExcessVerticalSpace to set
	 */
	public void setGrabExcessVerticalSpace(boolean grabExcessVerticalSpace) {
		this.grabExcessVerticalSpace = grabExcessVerticalSpace;
	}

	/**
	 * minimumWidth specifies the minimum width in pixels.  This value
	 * applies only if grabExcessHorizontalSpace is true. A value of
	 * {@link #DEFAULT} means that the minimum width will the width
	 * the widget in the cell returns as preferred size.
	 *
	 * The default value is 0.
	 * @return the minimumWidth
	 */
	public int getMinimumWidth() {
		return minimumWidth;
	}

	/**
	 * minimumWidth specifies the minimum width in pixels.  This value
	 * applies only if grabExcessHorizontalSpace is true. A value of
	 * {@link #DEFAULT} means that the minimum width will the width
	 * the widget in the cell returns as preferred one.
	 *
	 * The default value is 0.
	 *
	 * @param minimumWidth the minimumWidth to set
	 */
	public void setMinimumWidth(int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	/**
	 * minimumHeight specifies the minimum height in pixels.  This value
	 * applies only if grabExcessVerticalSpace is true.  A value of
	 * {@link #DEFAULT} means that the minimum height will be the height
	 * the widget in the cell returns as preferred one.
	 *
	 * The default value is 0.
	 *
	 * @return the minimumHeight
	 */
	public int getMinimumHeight() {
		return minimumHeight;
	}

	/**
	 * minimumHeight specifies the minimum height in pixels.  This value
	 * applies only if grabExcessVerticalSpace is true.  A value of
	 * {@link #DEFAULT} means that the minimum height will be the height
	 * the widget in the cell returns as preferred one.
	 *
	 * The default value is 0.
	 *
	 * @param minimumHeight the minimumHeight to set
	 */
	public void setMinimumHeight(int minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	/**
	 * exclude informs the layout to ignore the widget of this cell.
	 * If this value is <code>true</code>, the size and position of
	 * the control will not be managed by the layout.
	 * If this	value is <code>false</code>, the size and
	 * position of the control will be computed and assigned.
	 *
	 * The default value is <code>false</code>.
	 * @return the exclude
	 */
	public boolean isExclude() {
		return exclude;
	}

	/**
	 * exclude informs the layout to ignore the widget of this cell.
	 * If this value is <code>true</code>, the size and position of
	 * the control will not be managed by the layout.
	 * If this	value is <code>false</code>, the size and
	 * position of the control will be computed and assigned.
	 *
	 * The default value is <code>false</code>.
	 *
	 * @param exclude the exclude to set
	 */
	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	/**
	 * @return The gridDataID of this.
	 */
	public long getGridDataID() {
		return gridDataID;
	}
}
