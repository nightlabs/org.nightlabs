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
package org.nightlabs.editor2d.app;

import org.nightlabs.base.app.AbstractApplicationThread;
import org.nightlabs.base.app.AbstractWorkbenchAdvisor;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Editor2DApplicationThread 
extends AbstractApplicationThread 
{

	/**
	 * 
	 */
	public Editor2DApplicationThread() {
		super();
	}

	/**
	 * @param arg0
	 */
	public Editor2DApplicationThread(Runnable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Editor2DApplicationThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public Editor2DApplicationThread(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Editor2DApplicationThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Editor2DApplicationThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public Editor2DApplicationThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public Editor2DApplicationThread(ThreadGroup arg0, Runnable arg1,
			String arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @see org.nightlabs.base.app.AbstractApplicationThread#initWorkbenchAdvisor()
	 */
	public AbstractWorkbenchAdvisor initWorkbenchAdvisor() {
		return new Editor2DWorkbenchAdvisor();
	}

}
