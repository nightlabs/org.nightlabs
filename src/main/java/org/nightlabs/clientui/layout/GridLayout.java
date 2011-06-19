/**
 * 
 */
package org.nightlabs.clientui.layout;

import java.io.Serializable;

import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import org.nightlabs.clientui.layout.id.GridLayoutID;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;

/**
 * Instance of {@link GridLayout} can be used to store the layout of a widget 
 * of some client UI persistently on the JFire server.
 * It is highly inspired by the SWT GridLayout and reflects its fields and default values.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] -->
 * 
 * @jdo.persistence-capable
 *		identity-type="application"
 *		objectid-class="org.nightlabs.clientui.layout.id.GridLayoutID"
 *		detachable="true"
 *		table="NightLabsClientUI_GridLayout"
 *
 * @jdo.inheritance strategy="new-table"
 * @jdo.inheritance-discriminator strategy="class-name"
 *
 * @jdo.create-objectid-class field-order="gridLayoutID"
 */
@PersistenceCapable(
	objectIdClass=GridLayoutID.class,
	identityType=IdentityType.APPLICATION,
	detachable="true",
	table="NightLabsClientUI_GridLayout")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class GridLayout implements Serializable {

	private static final long serialVersionUID = 20090119L;

	/**
	 * @jdo.field primary-key="true"
	 */
	@PrimaryKey
	private long gridLayoutID;
	
 	/**
 	 * @jdo.field persistence-modifier="persistent"
 	 */
 	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private int numColumns;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private boolean makeColumnsEqualWidth;
	
	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
 	private int marginWidth;
 	
	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
 	private int marginHeight;

 	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
 	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private int marginLeft;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private int marginTop;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private int marginRight;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	private int marginBottom;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
 	private int horizontalSpacing;

	/**
 	 * @jdo.field persistence-modifier="persistent"
	 */
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
 	private int verticalSpacing;

 	/**
 	 * @deprecated Only for JDO.
 	 */
 	public GridLayout() {
	}
 	
 	/**
 	 * Constructs a new {@link GridLayout} with the given id
 	 * and default values for all fields.
 	 */
 	public GridLayout(long gridLayoutID) {
 		this.gridLayoutID = gridLayoutID;
 		this.numColumns = 1;
 		this.makeColumnsEqualWidth = false;
 	 	this.marginWidth = 5;
 	 	this.marginHeight = 5;
 		this.marginLeft = 0;
 		this.marginTop = 0;
 		this.marginRight = 0;
 		this.marginBottom = 0;
 	 	this.horizontalSpacing = 5;
 	 	this.verticalSpacing = 5;
 	}

	/**
 	 * numColumns specifies the number of cell columns in the layout.
 	 * If numColumns has a value less than 1, the layout will not
 	 * set the size and position of any controls.
 	 *
 	 * The default value is 1.
 	 * 
	 * @return the numColumns
	 */
	public int getNumColumns() {
		return numColumns;
	}

	/**
 	 * numColumns specifies the number of cell columns in the layout.
 	 * If numColumns has a value less than 1, the layout will not
 	 * set the size and position of any controls.
 	 *
 	 * The default value is 1.
 	 * 
	 * @param numColumns the numColumns to set
	 */
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	/**
	 * makeColumnsEqualWidth specifies whether all columns in the layout
	 * will be forced to have the same width.
	 *
	 * The default value is false.
	 * 
	 * @return the makeColumnsEqualWidth
	 */
	public boolean isMakeColumnsEqualWidth() {
		return makeColumnsEqualWidth;
	}

	/**
	 * makeColumnsEqualWidth specifies whether all columns in the layout
	 * will be forced to have the same width.
	 *
	 * The default value is false.
	 * 
	 * @param makeColumnsEqualWidth the makeColumnsEqualWidth to set
	 */
	public void setMakeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}

	/**
	 * marginWidth specifies the number of pixels of horizontal margin
	 * that will be placed along the left and right edges of the layout.
	 *
	 * The default value is 5.
	 * 
	 * @return the marginWidth
	 */
	public int getMarginWidth() {
		return marginWidth;
	}

	/**
	 * marginWidth specifies the number of pixels of horizontal margin
	 * that will be placed along the left and right edges of the layout.
	 *
	 * The default value is 5.
	 * 
	 * @param marginWidth the marginWidth to set
	 */
	public void setMarginWidth(int marginWidth) {
		this.marginWidth = marginWidth;
	}

	/**
	 * marginHeight specifies the number of pixels of vertical margin
	 * that will be placed along the top and bottom edges of the layout.
	 *
	 * The default value is 5.
	 * 
	 * @return the marginHeight
	 */
	public int getMarginHeight() {
		return marginHeight;
	}

	/**
	 * marginHeight specifies the number of pixels of vertical margin
	 * that will be placed along the top and bottom edges of the layout.
	 *
	 * The default value is 5.
	 * 
	 * @param marginHeight the marginHeight to set
	 */
	public void setMarginHeight(int marginHeight) {
		this.marginHeight = marginHeight;
	}

	/**
	 * marginLeft specifies the number of pixels of horizontal margin
	 * that will be placed along the left edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @return the marginLeft
	 */
	public int getMarginLeft() {
		return marginLeft;
	}

	/**
	 * marginLeft specifies the number of pixels of horizontal margin
	 * that will be placed along the left edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @param marginLeft the marginLeft to set
	 */
	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
	}

	/**
	 * marginTop specifies the number of pixels of vertical margin
	 * that will be placed along the top edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @return the marginTop
	 */
	public int getMarginTop() {
		return marginTop;
	}

	/**
	 * marginTop specifies the number of pixels of vertical margin
	 * that will be placed along the top edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @param marginTop the marginTop to set
	 */
	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
	}

	/**
	 * marginRight specifies the number of pixels of horizontal margin
	 * that will be placed along the right edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @return the marginRight
	 */
	public int getMarginRight() {
		return marginRight;
	}

	/**
	 * marginRight specifies the number of pixels of horizontal margin
	 * that will be placed along the right edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @param marginRight the marginRight to set
	 */
	public void setMarginRight(int marginRight) {
		this.marginRight = marginRight;
	}

	/**
	 * marginBottom specifies the number of pixels of vertical margin
	 * that will be placed along the bottom edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @return the marginBottom
	 */
	public int getMarginBottom() {
		return marginBottom;
	}

	/**
	 * marginBottom specifies the number of pixels of vertical margin
	 * that will be placed along the bottom edge of the layout.
	 *
	 * The default value is 0.
	 * 
	 * @param marginBottom the marginBottom to set
	 */
	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}

	/**
	 * horizontalSpacing specifies the number of pixels between the right
	 * edge of one cell and the left edge of its neighbouring cell to
	 * the right.
	 *
	 * The default value is 5.
	 * 
	 * @return the horizontalSpacing
	 */
	public int getHorizontalSpacing() {
		return horizontalSpacing;
	}

	/**
	 * horizontalSpacing specifies the number of pixels between the right
	 * edge of one cell and the left edge of its neighbouring cell to
	 * the right.
	 *
	 * The default value is 5.
	 * 
	 * @param horizontalSpacing the horizontalSpacing to set
	 */
	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}

	/**
	 * verticalSpacing specifies the number of pixels between the bottom
	 * edge of one cell and the top edge of its neighbouring cell underneath.
	 *
	 * The default value is 5.
	 * 
	 * @return the verticalSpacing
	 */
	public int getVerticalSpacing() {
		return verticalSpacing;
	}

	/**
	 * verticalSpacing specifies the number of pixels between the bottom
	 * edge of one cell and the top edge of its neighbouring cell underneath.
	 *
	 * The default value is 5.
	 * 
	 * @param verticalSpacing the verticalSpacing to set
	 */
	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}
 	
	/**
	 * @return The gridLayoutID of this {@link GridLayout}.
	 */
	public long getGridLayoutID() {
		return gridLayoutID;
	}
}
