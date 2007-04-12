package org.nightlabs.base.language;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.i18n.I18nText;

/**
 * Editor Table Composite for {@link I18nText}s. This will provide (or use)
 * a language chooser and make an i18n text editable table. The editor
 * table will either operate directly on the {@link I18nText} passed in
 * the {@link #setI18nText(I18nText)} or work with an own buffer,
 * so editing will not affect the original {@link I18nText}. This
 * behaviour can be controlled by
 * {@link #setEditMode(org.nightlabs.base.language.yo.I18nTextEditorTable.EditMode)}.
 * 
 * Use {@link I18nText#copyFrom(I18nText)} with {@link #getI18nText()}
 * as paramteer to reflect the changes in your {@link I18nText}.
 * You can also call {@link #copyToOriginal()} to let that be done for you.
 * 
 * @author Chairat Kongarayawetchakun - yo at nightlabs dot de
 */
public class I18nTableTextEditor extends XComposite{
	
	private TableViewer tableViewer;
	
	private String[] columnNames;
	private boolean[] editableColumns;
	
	private ICellModifier cellModifier;
	private IStructuredContentProvider contentProvider;
	private ILabelProvider labelProvider;
	
	private I18nTableItemList i18nTableItemList;
	
	public I18nTableTextEditor(Composite parent, int style, I18nTableItemList i18nTableItemList) {
		super(parent, style);
		this.i18nTableItemList = i18nTableItemList;
	}
	
	public void generateI18nTableTextEditor(){
		GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;
	    gridLayout.horizontalSpacing = 1;
	    this.setLayout(gridLayout);

	    GridData gd = new GridData(GridData.FILL_BOTH);
	    
		//Initial TableViewer
		int tableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		Table table = new Table(getParent(), tableStyle);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);

		//Set Columns
		if(columnNames == null)
			columnNames = new String[]{"Column1", "Column2", "Column3"};
		tableViewer.setColumnProperties(columnNames);
		
		int columnWidth = getParent().getBounds().width / columnNames.length;
		int[] columnAlignments = new int[] {
				SWT.LEFT, SWT.LEFT, SWT.LEFT};

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn =
				new TableColumn(table, columnAlignments[i], i);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidth);
		}//for


		//Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];
		
		if(editableColumns == null)
			editableColumns = new boolean[columnNames.length];
		else{
			for(int i = 0; i < columnNames.length; i++){
				if(editableColumns[i] == true){
					TextCellEditor textEditor = new TextCellEditor(table);
					((Text) textEditor.getControl()).setTextLimit(60);
					editors[i] = textEditor;
				}//if
			}//for
		}//else
		

		//Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);

		//Set the cell modifier for the viewer
		tableViewer.setCellModifier(cellModifier);

		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setInput(i18nTableItemList);
	}
	
	/**
	 * Return the I18nTableItemList
	 * 
	 * @return I18nTableItemList of I18nText items
	 */
	public I18nTableItemList getI18nTableItemList() {
		return i18nTableItemList;
	}

	/**
	 * Return the array of booleans
	 * 
	 * @return Array of booleans
	 */
	public boolean[] getEditableColumns() {
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
	 * @return List  containing column names
	 */
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public void setContentProvider(IStructuredContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public void setEditableColumns(boolean[] editableColumns) {
		this.editableColumns = editableColumns;
	}

	public void setI18nTableItemList(I18nTableItemList tableItemList) {
		i18nTableItemList = tableItemList;
	}
	
	
	
}
