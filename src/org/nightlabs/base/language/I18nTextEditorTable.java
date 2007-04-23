package org.nightlabs.base.language;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.language.I18nTextEditor.EditMode;
import org.nightlabs.base.table.TableContentProvider;
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
public class I18nTextEditorTable extends XComposite implements II18nTextEditor
{
	// Marco: constants should be final.
	private static final String COLUMN_FLAG_NAME     = "Flag"; //$NON-NLS-1$
	private static final String COLUMN_LANGUAGE_NAME = "Language"; //$NON-NLS-1$
	private static final String COLUMN_VALUE_NAME    = "Value"; //$NON-NLS-1$

	private static final int COLUMN_FLAG_INDEX = 0;
	private static final int COLUMN_LANGUAGE_INDEX = 1;
	private static final int COLUMN_VALUE_INDEX = 2;

	private static final String[] COLUMN_NAMES = { COLUMN_FLAG_NAME, COLUMN_LANGUAGE_NAME, COLUMN_VALUE_NAME };

	private I18nText original;
	private I18nText work;
	private I18nTextBuffer buffer = null;

	private Table table;
	private TableViewer tableViewer;

//	//Set column names
//	private String[] columnNames = new String[] {FLAG_COLUMN,
//			LANGUAGE_COLUMN,
//			VALUE_COLUMN
//	};
//	private boolean[] editableColumns = new boolean[]{false, false, true};

	// Marco: Why do we need this constructor?
//	private I18nTextEditorTable() {
//		super(null, 0);
//	}

	public I18nTextEditorTable(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		this.i18nTextTableItemList = i18nTextTableItemList;

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
		tableViewer.setUseHashlookup(true); // TODO Marco: Do we need this? Isn't this true by default? Maybe we should simply extend our AbstraceTableComposite and then activate it there, if it makes always sense.

// Marco: Why separating the methods here?
//		generateI18nTableTextEditor();
//	}
//
//	public void generateI18nTableTextEditor() {

		// set Columns
		tableViewer.setColumnProperties(COLUMN_NAMES);

		new TableColumn(table, SWT.LEFT).setText("F"); // TODO Externalise: Eclipse Menu => Source => Externalize Strings...
		new TableColumn(table, SWT.LEFT).setText("Language");
		new TableColumn(table, SWT.LEFT).setText("Text");

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnPixelData(24, false));
		tableLayout.addColumnData(new ColumnWeightData(20));
		tableLayout.addColumnData(new ColumnWeightData(80));
		table.setLayout(tableLayout);

		// Marco: We don't set the width of a column! We use a Layout for this purpose!
//		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };
//
//		for (int i = 0; i < columnNames.length; i++) {
//			TableColumn tableColumn = new TableColumn(table,
//					columnAlignments[i], i);
//			tableColumn.setText(columnNames[i]);
//			tableColumn.setWidth(150);
//		}// for

// Marco: I think we need only one cell-editor, because we have only one column (with one type) to edit
//		// Create the cell editors
//		CellEditor[] editors = new CellEditor[columnNames.length];
//
//		if (editableColumns == null)
//			editableColumns = new boolean[columnNames.length];
//		else {
//			for (int i = 0; i < columnNames.length; i++) {
//				if (editableColumns[i] == true) {
//					TextCellEditor textEditor = new TextCellEditor(table);
//					((Text) textEditor.getControl()).setTextLimit(60); // Marco: why this?
//					editors[i] = textEditor;
//				}// if
//			}// for
//		}// else

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(new CellEditor[] { null, null, new TextCellEditor(table) });
		tableViewer.setCellModifier(new I18nTextEditorTableCellModifier());

		// Set the cell modifier for the viewer
//		tableViewer.setCellModifier(new I18nTextCellModifier(Arrays.asList(columnNames),i18nTextTableItemList)); // Marco: commented this out temporarily in order to get rid of the I18nTextEditorTableContentProvider
		tableViewer.setContentProvider(new I18nTextEditorTableContentProvider()); // Marco: removed the parameters - a ContentProvider never gets its content immutable this way - it gets it via the callback-method inputChanged(...) 
		tableViewer.setLabelProvider(new I18nTextLabelProvider());
//		tableViewer.setInput(i18nTextTableItemList); // Marco: there is no input yet!
	}

// Marco: These methods should not be public - actually we don't need them at all
//	public void setTableViewer(TableViewer tableViewer) {
//		this.tableViewer = tableViewer;
//	}
//
//	/**
//	 * Return the TableViewer
//	 * 
//	 * @return TableViewer
//	 */
//	public TableViewer getTableViewer() {
//		return tableViewer;
//	}

//	/**
//	 * Return the I18nTableItemList
//	 * 
//	 * @return I18nTableItemList of I18nText items
//	 */
//	protected I18nTextTableItemList getI18nTableItemList() {
//		return null;//i18nTableItemList;
//	}

//	/**
//	 * Return the array of booleans
//	 * 
//	 * @return Array of booleans
//	 */
//	protected boolean[] getEditableColumns() {
//		return editableColumns;
//	}
//
//	public void setEditableColumns(boolean[] editableColumns) {
//		this.editableColumns = editableColumns;
//	}

//	/**
//	 * Return the column names in a collection
//	 * 
//	 * @return List containing column names
//	 */
//	protected java.util.List getColumnNames() {
//		return Arrays.asList(columnNames);
//	}
//
//	public void setColumnNames(String[] columnNames) {
//		this.columnNames = columnNames;
//	}

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

	/**
	 * If the {@link EditMode} is {@link EditMode#DIRECT}, this editor will
	 * work directly with the {@link I18nText} that has been passed to
	 * {@link #setI18nText(I18nText)}. That means, every modification is
	 * directly forwarded to the original {@link I18nText} object (on
	 * focus lost or when calling {@link #getI18nText()}). This behaviour is
	 * useful when you create a new object (e.g. in a wizard page). In this case,
	 * it's not necessary to call {@link #copyToOriginal()}.
	 * <p>
	 * If you edit an existing object, it is (in most cases) not desired to modify it
	 * directly, but instead to transfer all data only from the UI to the object, if
	 * the user explicitely applies his changes (in order to transfer them to the server
	 * as well). Therefore, you can use the {@link EditMode#BUFFERED}, which will
	 * cause an internal {@link I18nTextBuffer} to be created. 
	 * </p>
	 *
	 * @param editMode The new {@link EditMode}.
	 */
	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;

		switch (editMode) {
		case DIRECT:
			work = original;
			break;
		case BUFFERED:
			if (buffer == null)
				buffer = new I18nTextBuffer();

			buffer.clear();
			work = buffer;
			break;
		default:
			throw new IllegalArgumentException("Unknown editMode: " + editMode);
		}

		if (work != original && work != null && original != null)
			work.copyFrom(original);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
//		text.setEditable(editable);
	}

	protected void _setI18nText(I18nText i18nText, EditMode editMode)
	{
		original = i18nText;

		if (editMode != null)
			setEditMode(editMode);
		else {
			if (original instanceof I18nTextBuffer)
				setEditMode(EditMode.DIRECT);
			else
				setEditMode(EditMode.BUFFERED);
		}
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
		storeText();
		_setI18nText(i18nText, editMode);
		loadText();
	}

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
		// Marco: Probably we don't need to do anything in this method, because we can implement the CellModifier in a way that it updates this.work directly.
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
		tableViewer.setInput(work);

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

	private class I18nTextEditorTableCellModifier
	implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			return COLUMN_VALUE_NAME.equals(property);
		}

		@SuppressWarnings("unchecked")
		public Object getValue(Object element, String property)
		{
			return ((Map.Entry<String, String>)element).getValue();
		}

		@SuppressWarnings("unchecked")
		public void modify(Object element, String property, Object value)
		{
			if (COLUMN_VALUE_NAME.equals(property)) {
				TableItem tableItem = (TableItem) element;
				Map.Entry<String, String> mapEntry = (Entry<String, String>) tableItem.getData();
				String languageID = mapEntry.getKey();
				String newText = (String) value;
				work.setText(languageID, newText);
				tableItem.setText(COLUMN_VALUE_INDEX, newText);
				mapEntry.setValue(newText);
				tableViewer.update(element, new String[] { property });

				if (modifyListeners != null) {
					Object[] listeners = modifyListeners.getListeners();
					Event e = new Event();
					e.widget = I18nTextEditorTable.this;
					e.item = e.widget;
					e.display = I18nTextEditorTable.this.getDisplay();
					e.text = newText;
					ModifyEvent event = new ModifyEvent(e);
					for (Object l : listeners)
						((ModifyListener)l).modifyText(event);
				}
			}
		}
	}

// Marco: I reimplemented this class above
//	private class I18nTextCellModifier implements ICellModifier {
//		private List<String> i18ntexts;
//		private I18nTextTableItemList i18nTextTableItemList;
//
//		/**
//		 * Constructor 
//		 * @param TableViewerExample an instance of a TableViewerExample 
//		 */
//		public I18nTextCellModifier(List i18ntexts, I18nTextTableItemList i18nTextTableItemList) {
//			super();
//			this.i18ntexts = i18ntexts;
//			this.i18nTextTableItemList = i18nTextTableItemList;
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
//		 */
//		public boolean canModify(Object element, String property) {
//			return true;
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
//		 */
//		public Object getValue(Object element, String property) {
//
//			// Find the index of the column
//			int columnIndex = i18ntexts.indexOf(property);
//
//			Object result = null;
//			I18nText i18nText = (I18nText) element;
//
//			switch (columnIndex) {
//			case 0 : break;
//			case 1 : break;
//			case 2 : // VALUE_COLUMN 
//				String stringValue = i18nText.getText();
//				result = stringValue;					
//				break;
//			default :
//				result = "";
//			}
//			return result;	
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
//		 */
//		public void modify(Object element, String property, Object value) {	
//
//			// Find the index of the column 
//			int columnIndex	= i18ntexts.indexOf(property);
//
//			TableItem item = (TableItem) element;
//			I18nText i18nText = (I18nText) item.getData();
//			String valueString = "";
//			String languageID = item.getText(1);
//			switch (columnIndex) {
//			case 2 : // VALUE_COLUMN 
//				valueString = ((String)value).trim();
//
//				i18nText.setText("", value.toString());
//
//				Map<Object, I18nText> newI18nTextMap = new HashMap<Object, I18nText>();
//				newI18nTextMap.get(languageID);
//				i18nTextTableItemList.createI18nTextMap(newI18nTextMap);
//				break;
//			default :
//			}
//
//			i18nTextTableItemList.i18nTextChanged(i18nText);
////			updateTemplateData(i18nTextEditorTableItemList);
//		}
//
////		private void updateTemplateData(I18nTextTableItemList i18nTextTableItemList){
//////			try {
//////			GeographyTemplateDataAdmin geoAdmin = new GeographyTemplateDataAdmin();
////
//////			String rootOrganisationID = SecurityReflector.getUserDescriptor().getOrganisationID(); //TODO Change to root orgID
//////			if(geographyNameList.getGeographyObject() instanceof Country){
//////			Country country = (Country)geographyNameList.getGeographyObject();
////
//////			Collection<GeographyName> geographyNames = geographyNameList.getGeographyNames().values();
//////			for(GeographyName geographyName : geographyNames){
//////			country.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//////			}//for
//////			geoAdmin.storeGeographyTemplateCountryData(country);
//////			}//if
//////			else if(geographyNameList.getGeographyObject() instanceof Region){
//////			Region region = (Region)geographyNameList.getGeographyObject();
////
//////			Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//////			for(GeographyName geographyName : geographyNames){
//////			region.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//////			}//for
//////			geoAdmin.storeGeographyTemplateRegionData(region);
//////			}//if
//////			else if(geographyNameList.getGeographyObject() instanceof City){
//////			City city = (City)geographyNameList.getGeographyObject();
////
//////			Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//////			for(GeographyName geographyName : geographyNames){
//////			city.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//////			}//for
//////			geoAdmin.storeGeographyTemplateCityData(city);
//////			}//if
//////			else if(geographyNameList.getGeographyObject() instanceof Location){
//////			Location location = (Location)geographyNameList.getGeographyObject();
////
//////			Collection<GeographyName> geographyNames = geographyNameList.getI18nTableItemMap().values();
//////			for(GeographyName geographyName : geographyNames){
//////			location.getName().setText(geographyName.getLanguageID(), geographyName.getValue());
//////			}//for
//////			geoAdmin.storeGeographyTemplateLocationData(location);
//////			}//if
////
//////			}//try
//////			finally {
//////			}//finally
////		}
//	}

	/////////////////////////////////////////////////
	private class I18nTextLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {

//		Names of images used to represent checkboxes
//		public static final String CHECKED_IMAGE 	= "checked";
//		public static final String UNCHECKED_IMAGE  = "unchecked";

//		For the checkbox images
//		private static ImageRegistry imageRegistry = new ImageRegistry();


//		/**
//		 * Returns the image with the given key, or <code>null</code> if not found.
//		 */
//		private Image getImage(boolean isSelected) {
//		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
//		return  null/*imageRegistry.get(key)*/;
//		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			Map.Entry<String, String> item = (Map.Entry<String, String>)element;
			switch (columnIndex) {
				case COLUMN_FLAG_INDEX:
					return ""; //$NON-NLS-1$
				case COLUMN_LANGUAGE_INDEX:
					return LanguageManager.sharedInstance().getLanguage(item.getKey(), true).getName().getText();
				case COLUMN_VALUE_INDEX:
					return item.getValue();
				default :
					return ""; 	 //$NON-NLS-1$
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@SuppressWarnings("unchecked")
		public Image getColumnImage(Object element, int columnIndex) {
			Map.Entry<String, String> item = (Map.Entry<String, String>)element;
			switch (columnIndex) {
				case COLUMN_FLAG_INDEX:
					return LanguageManager.sharedInstance().getFlag16x16Image(item.getKey());
				case COLUMN_LANGUAGE_INDEX:
				case COLUMN_VALUE_INDEX:
				default :
					return null; 	
			}

//			Image result = null;
//			I18nText item = (I18nText) element;
//			String[] languageIDs = item.getLanguageIDs().toArray(new String[0]);
//			if(languageIDs != null && languageIDs.length > 0){
//				String languageID = languageIDs[0];
//				if(languageID != null && !languageID.equals("")){
//					LanguageCf cf = new LanguageCf(languageID);
//					cf.init(null);
//					switch (columnIndex) {
//					case 0: 	// FLAG_COLUMN
//						result = new Image(Display.getCurrent(), new ImageData(new ByteArrayInputStream(Utils.decodeHexStr(cf.getFlagIcon16x16()))));
//						break;
//					case 1 :	// LANGUAGE_NAME_COLUMN
//						break;
//					case 2 :	// VALUE_COLUMN
//						break;
//					default :
//						break; 	
//					}
//				}//if
//			}//if
//			else{
//				result = null; 
//			}//else
//
//			return result;
		}
	}

	private static class I18nTextEditorTableContentProvider extends TableContentProvider
	{
		private I18nText i18nText = null;

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof I18nText)
				this.i18nText = (I18nText) newInput;
			else
				this.i18nText = null;
		}

		// Return the i18nText as an array of Objects
		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object parent) {
			if (i18nText == null)
				return new Object[0];

			Collection<LanguageCf> languageCfs = LanguageManager.sharedInstance().getLanguages();
			Map<String, String> es = new HashMap<String, String>(languageCfs.size());
			for (Map.Entry<String, String> me : i18nText.getTexts()) {
				String languageID = me.getKey();
				String text = me.getValue();
				es.put(languageID, text);
			}

			for (LanguageCf languageCf : languageCfs) {
				String languageID = languageCf.getLanguageID();
				if (!es.containsKey(languageID))
					es.put(languageID, ""); //$NON-NLS-1$
			}

			return es.entrySet().toArray();
		}
	}
}
