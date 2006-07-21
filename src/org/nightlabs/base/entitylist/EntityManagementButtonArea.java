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

package org.nightlabs.base.entitylist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;

/**
 * Button area associated to one or more EntityManager.
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EntityManagementButtonArea extends XComposite {

	private Composite managementArea;	
	private Button buttonSave; 
	private List managers;
	
	private EntityDataChangedListener dataChangedListener = new EntityDataChangedListener() {
		public void entityDataChanged(EntityManager comp) {
			buttonSave.setEnabled(true);
		}
	};
	
	public EntityManagementButtonArea(Composite parent, int style, EntityManager manager) {
		this(parent, style, new EntityManager[]{manager});
	}

	/**
	 * Create a Composite with Buttons that manages the storage of Entities when
	 * their data changed.
	 *  
	 * @param parent Parent Composite
	 * @param style SWT style
	 * @param associatedManagers An array of all Managers associated to this area. (e.g. several ManagementComposites or one ManagementView). 
	 */
	public EntityManagementButtonArea(Composite parent, int style, EntityManager[] associatedManagers) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		managers = new ArrayList();
		if (associatedManagers != null) {
			for (int i = 0; i < associatedManagers.length; i++) {
				managers.add(associatedManagers[i]);
				associatedManagers[i].addDataChangedListener(dataChangedListener);
			}
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    managementArea = new Composite(this, SWT.NONE);
    this.setLayoutData(gd);
    RowLayout rl = new RowLayout();
    managementArea.setLayout(rl);
    buttonSave = new Button(managementArea,SWT.PUSH);
    buttonSave.setText("Save");
    buttonSave.setEnabled(false);
    buttonSave.addSelectionListener(
        new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
						if (managers != null) {
							for (Iterator iter = managers.iterator(); iter.hasNext();) {
								EntityManager manager = (EntityManager) iter.next();
								try {
									manager.save();
								} catch (Exception e1) {
									throw new RuntimeException(e1);
								}
							}
						}
          }
          public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
          }
        }
    );
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		for (Iterator iter = managers.iterator(); iter.hasNext();) {
			EntityManager manager = (EntityManager) iter.next();
			manager.removeDataChangedListener(dataChangedListener);
		}
		managers.clear();
		super.dispose();
	}
	
}
