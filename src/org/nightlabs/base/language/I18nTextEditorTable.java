package org.nightlabs.base.language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.language.I18nTextEditor.EditMode;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.language.LanguageCf;

/**
 * Editor Table Composite for {@link I18nText}s. This will provide (or use) a
 * language chooser and make an i18n text editable table. The editor table will
 * either operate directly on the {@link I18nText} passed in the
 * {@link #setI18nText(I18nText)} or work with an own buffer, so editing will
 * not affect the original {@link I18nText}. This behaviour can be controlled
 * by
 * {@link #setEditMode(org.nightlabs.base.language.yo.I18nTextEditorTable.EditMode)}.
 * 
 * Use {@link I18nText#copyFrom(I18nText)} with {@link #getI18nText()} as
 * paramteer to reflect the changes in your {@link I18nText}. You can also call
 * {@link #copyToOriginal()} to let that be done for you.
 * 
 * @author Chairat Kongarayawetchakun - Chairat at nightlabs dot de
 */
public class I18nTextEditorTable extends XComposite implements II18nTextEditor {
	private I18nText original;
	private I18nText work;
	private I18nTextBuffer buffer = null;

	private Table table;
	private TableViewer tableViewer;

	private String[] columnNames;
	private boolean[] editableColumns;

	private I18nTextTableItemList i18nTextTableItemList;
	
	private I18nTextEditorTable() {
		super(null, 0);
	}

	public I18nTextEditorTable(Composite parent, int style,
			I18nTextTableItemList i18nTextTableItemList) {
		super(parent, style);
		this.i18nTextTableItemList = i18nTextTableItemList;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);

		GridData gd = new GridData(GridData.FILL_BOTH);

		// Initial TableViewer
		int tableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
		| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = new Table(this, tableStyle);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
	}

	public void generateI18nTableTextEditor() {
		// Set Columns
		if (columnNames == null)
			columnNames = new String[] { "Column1", "Column2", "Column3" };
		tableViewer.setColumnProperties(columnNames);

		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(table,
					columnAlignments[i], i);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(150);
		}// for

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		if (editableColumns == null)
			editableColumns = new boolean[columnNames.length];
		else {
			for (int i = 0; i < columnNames.length; i++) {
				if (editableColumns[i] == true) {
					TextCellEditor textEditor = new TextCellEditor(table);
					((Text) textEditor.getControl()).setTextLimit(60);
					editors[i] = textEditor;
				}// if
			}// for
		}// else

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);

		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new I18nTextCellModifier(Arrays.asList(columnNames),i18nTextTableItemList));
		tableViewer.setContentProvider(new I18nTextTableContentProvider(i18nTextTableItemList, tableViewer));
		tableViewer.setLabelProvider(new I18nTextLabelProvider());
		tableViewer.setInput(i18nTextTableItemList);
	}


	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	/**
	 * Return the I18nTableItemList
	 * 
	 * @return I18nTableItemList of I18nText items
	 */
	protected I18nTextTableItemList getI18nTableItemList() {
		return null;//i18nTableItemList;
	}

	/**
	 * Return the array of booleans
	 * 
	 * @return Array of booleans
	 */
	protected boolean[] getEditableColumns() {
		return editableColumns;
	}

	/**
	 * Return the TableViewer
	 * 
	 * @return TableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * Return the column names in a collection
	 * 
	 * @return List containing column names
	 */
	protected java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public void setEditableColumns(boolean[] editableColumns) {
		this.editableColumns = editableColumns;
	}

	protected void setI18nTableItemList(I18nTextTableItemList tableItemList) {
//		i18nTableItemList = tableItemList;
	}

	protected void setCellModifier(ICellModifier cellModifier) {
		//	this.cellModifier = cellModifier;
	}

	/** ************************************************************************** */
	/**
	 * List of modifyListeners. They will only be triggered if the text is
	 * actually modified by the user, not when the edit changes its text.
	 */
	private ListenerList modifyListeners = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			modifyListeners = new ListenerList();

		modifyListeners.add(l);
	}

	public void copyToOriginal() {
		if (original != null && work != original)
			work.copyTo(original);
	}

	private EditMode editMode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getEditMode()
	 */
	public EditMode getEditMode() {
		return editMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getI18nText()
	 */
	public I18nText getI18nText() {
		storeText();
		return work;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getOriginal()
	 */
	public I18nText getOriginal() {
		storeText(); // if we work in buffered mode, this method doesn't make
		// much sense, but we can call it without harm.
		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#refresh()
	 */
	public void refresh() {
		loadText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			return;

		modifyListeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#reset()
	 */
	public void reset() {
		setI18nText(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
//		text.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText)
	 */
	public void setI18nText(I18nText i18nText) {
		setI18nText(i18nText, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText,
	 *      org.nightlabs.base.language.I18nTextEditor.EditMode)
	 */
	public void setI18nText(I18nText i18nText, EditMode editMode) {
//		storeText();
//		_setI18nText(i18nText, editMode);
//		loadText();
	}

	/*********************************************************************
	/**
	 * This method stores the currently edited text into the "backend" {@link I18nText}
	 * object. This method is automatically called whenever the <tt>I18nTextEditor</tt>
	 * is changed by the user. This means, the I18nText is kept synchronous with the
	 * displayed and edited text. 
	 * <p>
	 * This method has been changed from public to private.
	 */
	private void storeText()
	{
//		String newText = text.getText();
//		if (!newText.equals(orgText)) {
//		if (work == null) {
//		if (original == null) {
//		_setI18nText(new I18nTextBuffer(), null);
//		}
//		else
//		throw new IllegalStateException("work == null, but original != null - how's that possible?!");
//		}

//		if (textLanguage == null)
//		textLanguage = languageChooser.getLanguage();

//		work.setText(textLanguage.getLanguageID(), newText);
//		orgText = newText;
//		}
	}

	/**
	 * Loads the text out of the buffer and displays it in the text field.
	 * ModifyListeners registered will not be triggered when this happens.
	 */
	private void loadText()
	{
//		loadingText = true;
//		try {
//		String txt = null;

//		if (work != null) {
//		textLanguage = languageChooser.getLanguage();
//		txt = work.getText(textLanguage.getLanguageID());
//		}
//		if (txt == null) txt = "";
//		text.setText(txt);
//		orgText = txt;
//		} finally {
//		loadingText = false;
//		}
	}

	private class I18nTextCellModifier implements ICellModifier {
		private List<String> i18ntexts;
		private I18nTextTableItemList i18nTextTableItemList;

		/**
		 * Constructor 
		 * @param TableViewerExample an instance of a TableViewerExample 
		 */
		public I18nTextCellModifier(List i18ntexts, I18nTextTableItemList i18nTextTableItemList) {
			super();
			this.i18ntexts = i18ntexts;
			this.i18nTextTableItemList = i18nTextTableItemList;
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			return true;
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property) {

			// Find the index of the column
			int columnIndex = i18ntexts.indexOf(property);

			Object result = null;
			I18nText i18nText = (I18nText) element;

			switch (columnIndex) {
			case 0 : break;
			case 1 : break;
			case 2 : // VALUE_COLUMN 
				String stringValue = i18nText.getText();
				result = stringValue;					
				break;
			default :
				result = "";
			}
			return result;	
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {	

			// Find the index of the column 
			int columnIndex	= i18ntexts.indexOf(property);

			TableItem item = (TableItem) element;
			I18nText i18nText = (I18nText) item.getData();
			String valueString = "";
			String languageID = item.getText(1);
			switch (columnIndex) {
			case 2 : // VALUE_COLUMN 
				valueString = ((String)value).trim();

				i18nText.setText("", value.toString());

				Map<Object, Object> newI18nTextMap = new HashMap<Object, Object>();
				newI18nTextMap.put(languageID, valueString);
				i18nTextTableItemList.createI18nTextMap(newI18nTextMap);
				break;
			default :
			}

			i18nTextTableItemList.i18nTextChanged(i18nText);
//			updateTemplateData(i18nTextEditorTableItemList);
		}

		private void updateTemplateData(I18nTextTableItemList i18nTextTableItemList){
//			try {
//				GeographyTemplateDataAdmin geoAdmin = new GeographyTemplateDataAdmin();
//
//				String rootOrganisationID = SecurityReflector.getUserDescriptor().getOrganisationID(); //TODO Change to root orgID
//				if(geographyNameList.getGeographyObject() instanceof Country){
//					Country country = (Country)geographyNameList.getGeographyObject();

//					Collection<GeographyName> geographyNames = geographyNameList.getGeographyNames().values();
//					for(GeographyName geographyName : geographyNames){
//					country.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//					}//for
//					geoAdmin.storeGeographyTemplateCountryData(country);
//				}//if
//				else if(geographyNameList.getGeographyObject() instanceof Region){
//					Region region = (Region)geographyNameList.getGeographyObject();
//
//					Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//					for(GeographyName geographyName : geographyNames){
//						region.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//					}//for
//					geoAdmin.storeGeographyTemplateRegionData(region);
//				}//if
//				else if(geographyNameList.getGeographyObject() instanceof City){
//					City city = (City)geographyNameList.getGeographyObject();
//
//					Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//					for(GeographyName geographyName : geographyNames){
//						city.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//					}//for
//					geoAdmin.storeGeographyTemplateCityData(city);
//				}//if
//				else if(geographyNameList.getGeographyObject() instanceof Location){
//					Location location = (Location)geographyNameList.getGeographyObject();
//
//					Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//					for(GeographyName geographyName : geographyNames){
//						location.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//					}//for
//					geoAdmin.storeGeographyTemplateLocationData(location);
//				}//if

//			}//try
//			finally {
//			}//finally
		}
	}

	/////////////////////////////////////////////////
	public class I18nTextLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {

//		Names of images used to represent checkboxes
//		public static final String CHECKED_IMAGE 	= "checked";
//		public static final String UNCHECKED_IMAGE  = "unchecked";

//		For the checkbox images
//		private static ImageRegistry imageRegistry = new ImageRegistry();


		/**
		 * Returns the image with the given key, or <code>null</code> if not found.
		 */
//		private Image getImage(boolean isSelected) {
//		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
//		return  null/*imageRegistry.get(key)*/;
//		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			I18nText item = (I18nText) element;
			switch (columnIndex) {
			case 0: 	// FLAG_COLUMN
				result = "FLAG";
				break;
			case 1 :	// LANGUAGE_NAME_COLUMN
//				result = item.getLanguageID();
				break;
			case 2 :	// VALUE_COLUMN
				result = item.getText();
				break;
			default :
				break; 	
			}
			return result;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return /*(columnIndex == 0) ?   // COMPLETED_COLUMN?
					getImage(((Object) element).isCompleted()) :*/
			null;
		}
	}
}
