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
package org.nightlabs.editor2d.actions.print;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.dialog.PrintModeDialog;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintAction
extends AbstractEditorAction
{
	public static final String ID = ActionFactory.PRINT.getId();
//	private IFile selectedFile;

	public EditorPrintAction(AbstractEditor editor, int style) 
	{
		super(editor, style);
	}

	public EditorPrintAction(AbstractEditor editor) 
	{
		super(editor);
	}
		
//	private Object contents;	
//	protected Object getContents() {
//		return contents;
//	}
//	protected void setContents(Object o) {
//		contents = o;
//	}	
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() 
	{
		int style = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getStyle();
		Shell shell = new Shell((style & SWT.MIRRORED) != 0 ? SWT.RIGHT_TO_LEFT : SWT.NONE);
//		GraphicalViewer viewer = new J2DScrollingGraphicalViewer();
//		viewer.createControl(shell);
//		viewer.setEditDomain(new DefaultEditDomain(null));
//		viewer.setRootEditPart(new J2DScalableFreeformRootEditPart());
//		viewer.setEditPartFactory(new GraphicalEditPartFactory());
//		viewer.setContents(getContents());
//		viewer.flush();
		GraphicalViewer viewer = getEditor().getOutlineGraphicalViewer();
		String fileName = getEditor().getTitle();
		
		int printMode = new PrintModeDialog(shell).open();
		if (printMode == -1)
			return;
		PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
		PrinterData data = dialog.open();
		if (data != null) {
			PrintGraphicalViewerOperation op = 
						new PrintGraphicalViewerOperation(new Printer(data), viewer);
			op.setPrintMode(printMode);
			op.run(fileName);
		}
	}

//	/**
//	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
//	 */
//	public void selectionChanged(IAction action, ISelection selection) 
//	{
//		if (!(selection instanceof IStructuredSelection))
//			return;
//		IStructuredSelection sel = (IStructuredSelection)selection;
//		if (sel.size() != 1)
//			return;
//		selectedFile = (IFile)sel.getFirstElement();
//		try 
//		{
//			if (getEditor() != null) {
//				IOFilter ioFilter = getEditor().getIOFilterMan().getIOFilter(selectedFile.getFileExtension());
//				Object content = ioFilter.read(selectedFile.getContents(false));
//				setContents(content);
//			}
////			InputStream is = selectedFile.getContents(false);
////			ObjectInputStream ois = new ObjectInputStream(is);
////			setContents(ois.readObject());
////			ois.close();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}		
//	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		PrinterData[] printers = Printer.getPrinterList();
		return printers != null && printers.length > 0;
	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
	 */
	protected void init() 
	{
		setText(GEFMessages.PrintAction_Label);
		setToolTipText(GEFMessages.PrintAction_Tooltip);
		setId(ID);
		setActionDefinitionId(ID);
	}	
}
