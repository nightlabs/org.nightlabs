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

import org.nightlabs.base.app.AbstractApplication;
import org.nightlabs.base.app.AbstractWorkbenchAdvisor;
import org.nightlabs.editor2d.EditorPerspective;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Editor2DWorkbenchAdvisor 
extends AbstractWorkbenchAdvisor 
{

	public Editor2DWorkbenchAdvisor() {
		super();		
	}

	/**
	 * @see org.nightlabs.base.app.AbstractWorkbenchAdvisor#initApplication()
	 */
	protected AbstractApplication initApplication() {
		return new Editor2DApplication(); // TODO Why do you return a NEW application here? There should already be an instance of your application. shouldn't it? Marco.
	}

	/**
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	public String getInitialWindowPerspectiveId() {
		return EditorPerspective.ID_PERSPECTIVE;
	}

//	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) 
//	{
//		return new DefaultActionBuilder(configurer, true, true, true, true, true, true, true, 
//				false, true, true, true, true, true, true, true, true);
//	}
}
