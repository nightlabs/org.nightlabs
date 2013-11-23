/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.config;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision: 12386 $ - $Date: 2008-10-10 14:42:28 +0200 (Fr, 10 Okt 2008) $
 */
public class ConfigModuleBeanInfo
extends SimpleBeanInfo
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigModuleBeanInfo.class);

	Class<?> beanClass = ConfigModule.class;

	private static final int PROPERTY_identifier = 0;
	private static final int PROPERTY_searchClass = 1;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] properties = new PropertyDescriptor[2];

		try {
//  		properties[PROPERTY_identifier] = new PropertyDescriptor ( "identifier", beanClass, "getIdentifier", "setIdentifier" );
//  		properties[PROPERTY_identifier].setHidden(true);
//  		properties[PROPERTY_searchClass] = new PropertyDescriptor ( "searchClass", beanClass, "getSearchClass", "setSearchClass" );
//  		properties[PROPERTY_searchClass].setHidden(true);
			properties[PROPERTY_identifier] = new PropertyDescriptor ( "identifier", beanClass, null, null );
			properties[PROPERTY_searchClass] = new PropertyDescriptor ( "searchClass", beanClass, null, null );
		}
		catch( IntrospectionException e) {
			logger.error("", e);
		}

		// Here you can add code for customizing the properties array.
		return properties;
	}

}
