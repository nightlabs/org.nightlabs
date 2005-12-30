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

package org.nightlabs.base.exceptionhandler.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Alexander Bieber
 * @deprecated is this still in use?? see org.nightlabs.base.exceptionhandler.DefaultErrorDialog
 */
public class ThrowableHandlerDetailArea extends Composite {

	private Text textAreaStackTrace = null;
	public ThrowableHandlerDetailArea(Composite parent, int style) {
		super(parent, style);
		initialize();
	}
	private void initialize() {
		GridData gridDataTextArea = new GridData(GridData.FILL_BOTH);
		textAreaStackTrace = new Text(this, SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
		this.setLayout(new GridLayout());
		gridDataTextArea.grabExcessHorizontalSpace = true;
		gridDataTextArea.grabExcessVerticalSpace = true;
		gridDataTextArea.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		textAreaStackTrace.setForeground(new org.eclipse.swt.graphics.Color(org.eclipse.swt.widgets.Display.getDefault(), 240, 0, 0));
		textAreaStackTrace.setLayoutData(gridDataTextArea);
		this.setSize(new org.eclipse.swt.graphics.Point(426,200));
	}	
	
	private Throwable error = null;
	public void setThrowable(Throwable err) {
		this.error = err;
		if (error != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			error.printStackTrace(pw);
			textAreaStackTrace.setText(sw.toString());
//			
//			StringBuffer stackTraceBuf = new StringBuffer();
//			err.printStackTrace();
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			StackTraceElement[] stackTrace = error.getStackTrace();
//			stackTraceBuf.append(err+" in:\n");
//			for(int i=0; i<stackTrace.length; i++) {
//				stackTraceBuf.append("  "+stackTrace[i].toString()+"\n");
//			}
//			Throwable cause = err.getCause();
//			while (cause != null) {
//				stackTraceBuf.append("Caused by "+cause+" in:\n");
//				stackTrace = cause.getStackTrace();
//				for(int i=0; (i<stackTrace.length) && (i<5); i++) {
//					stackTraceBuf.append("  "+stackTrace[i].toString()+"\n");
//				}
//				cause = cause.getCause();
//			}
//			textAreaStackTrace.setText(stackTraceBuf.toString());
		}
	}
}
