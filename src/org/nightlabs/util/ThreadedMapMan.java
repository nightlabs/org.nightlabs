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
package org.nightlabs.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @deprecated I think this class is never used and actually, I think it's not such a big deal to implement these few lines specifically for a certain use case. This class will be deleted soon. Marco.
 */
@Deprecated
public class ThreadedMapMan {

	private static ThreadLocal<Map<String, Object>> m = new ThreadLocal<Map<String,Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};
	
	public static Map<String, Object> getMap() {
//		for (Map.Entry<String, Object> entry : m.get().entrySet()) {
//			System.out.println("Map entry: "+entry.getKey()+"="+entry.getValue().getClass().getName()+": "+entry.getValue());
//		}
		return m.get();
	}
	
}
