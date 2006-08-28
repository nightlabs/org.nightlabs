/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.entity.tree;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.part.ControllablePart;
import org.nightlabs.base.part.PartController;

/**
 * The tree view for entity tree categories and their
 * entity elements. This view displays all categories
 * registered via extension point and opens the registered
 * editor for them upon user request.
 * 
 * This class is intended to be used in two ways.
 * <ol>
 * <li>Register this class as is as a View. Then it will display all categories registered
 * to the view-id you have assigned to it, but will not be controlled (see {@link #getPartController()}, {@link PartController})</li>
 * <li>Create a subclass and override {@link #getPartController()} and {@link #canDisplayPart()} in order to have the new View
 * correctly controlled by a {@link PartController}. Then proceed as above and register the new class as view.</li>
 * </ol>
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber - <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityTreeView 
extends ViewPart 
implements ControllablePart
{

	private EntityTree entityTree;
	
	/**
	 * Default constructor registers this view to 
	 * the controller returned by {@link #getPartController()}
	 */
	public EntityTreeView()
	{
		PartController controller = getPartController();
		// Regsiter to the PartControler if available
		if (controller != null)
			controller.registerPart(this);
	}

	/**
	 * The default implementation of createPartControl first checks the
	 * {@link PartController} of this View by {@link #getPartController()}.
	 * If it is not null, work will be delegated to the controller, 
	 * otherwise the contents will be created directly by {@link #createPartContents(Composite)}.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		PartController controller = getPartController();
		if (controller != null)
			// Delegate this to the view-controller, to let him decide what to display
			controller.createPartControl(this, parent);
		else
			// Directly create contents
			createPartContents(parent);
	}

	/**
	 * This method is cunsulted in the constructor
	 * and creatPartControl, to delegate the work 
	 * a {@link PartController} if neccessary.
	 * The default implementation returns null, so
	 * the View will not be controlled. If you want
	 * it to be controlled override and return 
	 * the appropriate controller.
	 */
	protected PartController getPartController() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartContents(Composite parent)
	{
		entityTree = new EntityTree(parent, getViewSite().getId());
	}

	/**
	 * The default implementation returns true.
	 * If you use this with a {@link PartController}, you have
	 * to override and return the appropriate value.
	 * @see #getPartController()
	 * @see org.nightlabs.base.part.ControllablePart#canDisplayPart()
	 */
	public boolean canDisplayPart()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * Returns the entity tree Composite ceated
	 * for this view.
	 * 
	 * @return The entity tree Composite ceated
	 * for this view.
	 */
	public EntityTree getEntityTree() {
		return entityTree;
	}
}
