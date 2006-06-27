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
package org.nightlabs.editor2d.page;

import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.editor2d.page.resolution.DPIResolutionUnit;
import org.nightlabs.editor2d.page.resolution.IResolutionUnit;
import org.nightlabs.editor2d.page.resolution.Resolution;
import org.nightlabs.editor2d.page.resolution.ResolutionImpl;
import org.nightlabs.editor2d.page.resolution.ResolutionUnit;
import org.nightlabs.editor2d.page.unit.PixelUnit;
import org.nightlabs.i18n.IUnit;
import org.nightlabs.i18n.Unit;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PageRegistryEP 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.editor2d.pageRegistry";
	public static final String ELEMENT_UNIT = "Unit";
	public static final String ELEMENT_RESOLUTION_UNIT = "ResolutionUnit";
	public static final String ELEMENT_PREDEFINED_PAGE = "PredefinedPage";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_FACTOR = "factor";
	public static final String ATTRIBUTE_UNIT_ID = "unitID";
	public static final String ATTRIBUTE_UNIT_SYMBOL = "symbol";	
	public static final String ATTRIBUTE_RESOLUTION_ID = "resolutionID";
	public static final String ATTRIBUTE_PAGE_ID = "pageID";
	
	protected static PageRegistryEP sharedInstance = null;
	public static PageRegistryEP sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new PageRegistryEP(); 
			sharedInstance.checkProcessing();
		}
		return sharedInstance;
	}
	
	protected PageRegistryEP() {
		super();
	}

	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_UNIT)) 
		{
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new EPProcessorException("unit name must not be null nor empty! for element "+element);
			
			double factor = 1;
			try {
				factor = Double.parseDouble(element.getAttribute(ATTRIBUTE_FACTOR));				
			} catch (NumberFormatException e) {
				throw new EPProcessorException("unit "+name+" has no valid factor ("+element.getAttribute(ATTRIBUTE_FACTOR)+")!", e);				
			}
			
			String unitID = element.getAttribute(ATTRIBUTE_UNIT_ID);
			if (!checkString(unitID))
				throw new EPProcessorException("unitID must not be null nor empty! for element "+element);

			String symbol = element.getAttribute(ATTRIBUTE_UNIT_SYMBOL);
			
			IUnit unit = new Unit();
			unit.setName(Locale.getDefault().getLanguage(), name);
			unit.setFactor(factor);
			unit.setUnitID(unitID);
			if (checkString(symbol))
				unit.setUnitSymbol(symbol);
			
//			registry.addUnit(unit);
			getPageRegistry().addUnit(unit);
		}
		else if (element.getName().equalsIgnoreCase(ELEMENT_RESOLUTION_UNIT)) 
		{
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new EPProcessorException("unit name must not be null nor empty! for element "+element);
			
			String resolutionID = element.getAttribute(ATTRIBUTE_RESOLUTION_ID);
			if (!checkString(resolutionID))
				throw new EPProcessorException("resolutionID must not be null nor empty! for element "+element);
			
			IResolutionUnit resUnit = new ResolutionUnit();
			try {
				IUnit unit = (IUnit) element.createExecutableExtension("unit");
				resUnit.setName(Locale.getDefault().getLanguage(), name);
				resUnit.setResolutionID(resolutionID);
				resUnit.setUnit(unit);
//				registry.addResolutionUnit(resUnit);
				getPageRegistry().addResolutionUnit(resUnit);								
			} catch (CoreException ce) {
				throw new EPProcessorException(ce); 
			}			
		}
		else if (element.getName().equalsIgnoreCase(ELEMENT_PREDEFINED_PAGE)) 
		{
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new IllegalArgumentException("unit name must not be null nor empty!");
			
			String pageID = element.getAttribute(ATTRIBUTE_PAGE_ID);
			if (!checkString(pageID))
				throw new IllegalArgumentException("pageID must not be null nor empty!");
				
			try {
				IPredefinedPage page = (IPredefinedPage) element.createExecutableExtension("page");
				page.setName(Locale.getDefault().getLanguage(), name);
				page.setPageID(pageID);
//				registry.addPredefinedPage(page);
				getPageRegistry().addPredefinedPage(page);				
			} catch (CoreException ce) {
				throw new EPProcessorException(ce); 
			}
			 
		}
	}

//	protected PageRegistry registry = PageRegistry.sharedInstance();
//	public PageRegistry getPageRegistry() 
//	{
//		return registry;
//	}

	protected PageRegistry registry = null;
	public PageRegistry getPageRegistry() 
	{
		if (registry == null) {
			registry = PageRegistry.sharedInstance();
			addPixelUnit(registry);
		}
		return registry;
	}
	
	protected void addPixelUnit(PageRegistry pageRegistry) 
	{
//		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		int dpi = 72;		
		Resolution screenResolution = new ResolutionImpl(new DPIResolutionUnit(), dpi);
		PixelUnit pixelUnit = new PixelUnit(screenResolution);
		pageRegistry.addUnit(pixelUnit);
	}
}
