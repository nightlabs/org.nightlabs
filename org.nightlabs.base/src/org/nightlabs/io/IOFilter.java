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
package org.nightlabs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nightlabs.i18n.I18nText;

/**
 * The base interface for all classes which need to load or save data
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IOFilter
{
	/**
	 * reads data from an {@link InputStream} and returns the read object
	 * 
	 * @param in the InpuStream to read
	 * @return the Object to read
	 * @throws IOException if an error occurs during the read
	 */
  public Object read(InputStream in) throws IOException;
  
  /**
   * writes the given object to the {@link OutputStream}
   * 
   * @param o the Object to write
   * @param out the OutputStream to write to
   * @throws IOException if an error occurs during the write
   */
  public void write(Object o, OutputStream out) throws IOException;
  
  /**
   * returns the multilanguage name
   * 
   * @return the multilanguage name
   */
  public I18nText getName();
  
  /**
   * sets the multilanguage name
   * @param name the multilanguage name to set
   */
  public void setName(I18nText name);
  
  /**
   * returns the multilanguage description
   * 
   * @return the multilanguage description
   */
  public I18nText getDescription();
  
  /**
   * sets the multilanguage description
   * 
   * @param desciption the multilanguage description to set
   */
  public void setDescription(I18nText desciption);
  
  /**
   * returns the supported file extensions of the file to read/write
   * 
   * @return the supported file extensions
   */
  public String[] getFileExtensions();
  
  /**
   * sets the file extensions for all supported formats
   * 
   * @param fileExtensions the file extensions for all supported formats
   */
  public void setFileExtensions(String[] fileExtensions);
  
  /**
   * returns the description for the given fileExtension
   * 
   * @return the description for the given fileExtension
   */
  public I18nText getFileExtensionDescription(String fileExtension);
  
  /**
   * sets the description for the given fileExtension
   * 
   * @param fileExtension the fileExtension to add the description for
   * @param text the description of the ioFilter
   */
  public void setFileExtensionDescription(String fileExtension, I18nText text);
  
  /**
   * returns true if the IOFilter can read the fileFormat with the given fileExtension
   *
   * @param fileExtension the fileExtension to check
   * @return true if the IOFilter can read the fileFormat with the given fileExtension
   * otherwise false
   */
  public boolean supportsRead(String fileExtension);
  
//  public void setSupportsRead(String fileExtension, boolean canRead);
  
  /**
   * returns true if the IOFilter can write the fileFormat with the given fileExtension
   *
   * @param fileExtension the fileExtension to check
   * @return true if the IOFilter can write the fileFormat with the given fileExtension
   * otherwise false
   */
  public boolean supportsWrite(String fileExtension);
  
//  public void setSupportsWrite(String fileExtension, boolean canWrite);
  
  /**
   * sets an optional {@link IOFilterInformationProvider} which can provide
   * the IOFilter which additional data
   * 
   * @param provider the {@link IOFilterInformationProvider} to set
   */
  public void setInformationProvider(IOFilterInformationProvider provider);
  
  /**
   * returns the {@link IOFilterInformationProvider} for the IOFilter
   * 
   * @return the {@link IOFilterInformationProvider} for the IOFilter, can be null
   * if no one was set before
   */
  public IOFilterInformationProvider getInformationProvider();
}
