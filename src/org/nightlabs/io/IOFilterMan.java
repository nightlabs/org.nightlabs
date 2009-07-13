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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a Manager-Class for handling different types of IOFilters
 */
public class IOFilterMan
{
	/**
	 * contains all registered IOFilters
	 */
  private List<IOFilter> ioFilters = new ArrayList<IOFilter>();
    
  /**
   * key: FileExtension (String)
   * value: IOFilter Instance
   */
  private Map<String, IOFilter> fileExtension2IOFilter = new HashMap<String, IOFilter>();
  
  /**
   * The default ioFilter for reading
   */
  private IOFilter defaultReadIOFilter = null;

  /**
   * The default ioFilter for writing
   */
  private IOFilter defaultWriteIOFilter = null;
  
  public IOFilterMan() {
    super();
  }
  
	/**
	 * Adds / Registers an IOFilter
	 */
  public void addIOFilter(IOFilter ioFilter)
  {
    if (ioFilter== null)
      throw new NullPointerException("Param ioFilter must not be null!");

    ioFilters.add(ioFilter);
    for (int i=0; i<ioFilter.getFileExtensions().length; i++) {
      fileExtension2IOFilter.put(ioFilter.getFileExtensions()[i], ioFilter);
    }
  }
    
  /**
   * Removes/unregisters an IOFilter
   */
  public void removeIOFilter(IOFilter ioFilter)
  {
    if (ioFilters.contains(ioFilter))
    {
      ioFilters.remove(ioFilter);
      for (int i=0; i<ioFilter.getFileExtensions().length; i++) {
        fileExtension2IOFilter.remove(ioFilter.getFileExtensions()[i]);
      }
    }
  }
  
  /**
   * returns the IOFilter for the given file
   * 
   * @param file the File to get a IOFilter for
   * @return the IOFilter for the given file
   */
  public IOFilter getIOFilter(File file)
	{
    if (file == null)
      throw new IllegalArgumentException("Param file must not be null!");
    
    String fileExtension = getFileExtension(file);
    if (fileExtension2IOFilter.containsKey(fileExtension))
      return fileExtension2IOFilter.get(fileExtension);
    else
      return null;
  }
  
  /**
   * returns the IOFilter for the given fileExtension
   * 
   * @param fileExtension The FileExtension
   * @return the IOFilter for the given FileExtension
   */
  public IOFilter getIOFilter(String fileExtension)
	{
    if (fileExtension == null)
      throw new IllegalArgumentException("Param fileExtension must not be null!");
    
    if (fileExtension2IOFilter.containsKey(fileExtension))
      return fileExtension2IOFilter.get(fileExtension);
    else
      return null;
  }
  
  /**
   * 
   * @param file The File
   * @return the FileExtention of the File
   */
  protected String getFileExtension(File file)
  {
    if (file == null)
      throw new IllegalArgumentException("Param file must not be null!");
    
  	StringBuffer sb = new StringBuffer(file.getName());
  	int index = sb.lastIndexOf(".");
  	if (index == -1) {
  		return null;
  	}
  	return sb.substring(index+1);
  }
  
//  /**
//   * @return A Collection of Strings which contains all fileExtensions,
//   * for all registered IOFilters
//   */
//  public Collection<String> getAvailableFileExtensions()
//  {
//  	return fileExtension2IOFilter.keySet();
//  }
//
//  /**
//   * @return String[] which contains all fileExtensions,
//   * for all registered IOFilters
//   */
//  public String[] getAvailableFileExtensionsAsStrings(boolean concatWildcard)
//  {
//  	String[] fileExtensions = null;
//  	int counter = 0;
//  	for (Iterator it = fileExtension2IOFilter.keySet().iterator(); it.hasNext(); )
//  	{
//  		if (fileExtensions == null)
//  			fileExtensions = new String[fileExtension2IOFilter.keySet().size()];
//
//  		String fileExtension = (String) it.next();
//  		if (concatWildcard)
//  			fileExtension = concatWildcard(fileExtension);
//  		fileExtensions[counter] = fileExtension;
//  		counter++;
//  	}
//  	return fileExtensions;
//  }
//
//  public String[] getAvailableFileExtensionsAsStrings() {
//  	return getAvailableFileExtensionsAsStrings(true);
//  }
//
//	protected String concatWildcard(String s) {
//		return "*." + s;
//	}
	
	public Collection<IOFilter> getReadFilter()
	{
		Set<IOFilter> readFilter = new HashSet<IOFilter>();
		for (IOFilter ioFilter : ioFilters) {
			for (String fileExtension : ioFilter.getFileExtensions()) {
				if (ioFilter.supportsRead(fileExtension)) {
					readFilter.add(ioFilter);
				}
			}
		}
		return readFilter;
	}
	
	public Collection<IOFilter> getWriteFilter()
	{
		Set<IOFilter> writeFilter = new HashSet<IOFilter>();
		for (IOFilter ioFilter : ioFilters) {
			for (String fileExtension : ioFilter.getFileExtensions()) {
				if (ioFilter.supportsWrite(fileExtension)) {
					writeFilter.add(ioFilter);
				}
			}
		}
		return writeFilter;
	}
	
	/**
	 * returns a String Array with the fileExtensions of all FileFormats which can be read
	 * @param concatWildcard determines if a "*." should be concated or not
	 * @return a String Array with the fileExtensions of all FileFormats which can be read
	 */
	public String[] getReadFileExtensions(boolean concatWildcard)
	{
		List<String> readFileExtensions = new ArrayList<String>();
		String defaultFileExtension = null;
		if (getDefaultWriteIOFilter() != null) {
			defaultFileExtension = getDefaultReadIOFilter().getFileExtensions()[0];
		}
		
		int indexDefaultFileExtension = 0;
		for (IOFilter ioFilter : ioFilters) {
			for (String fileExtension : ioFilter.getFileExtensions()) {
				if (ioFilter.supportsRead(fileExtension)) {
					if (defaultFileExtension != null && defaultFileExtension.equals(fileExtension)) {
						indexDefaultFileExtension = readFileExtensions.size();
					}
					
					if (concatWildcard)
						readFileExtensions.add("*."+fileExtension);
					else
						readFileExtensions.add(fileExtension);
				}
			}
		}
		
		if (indexDefaultFileExtension != 0) {
			String oldFirstFileExtension = readFileExtensions.get(0);
			defaultFileExtension = readFileExtensions.get(indexDefaultFileExtension);
			readFileExtensions.set(0, defaultFileExtension);
			readFileExtensions.set(indexDefaultFileExtension, oldFirstFileExtension);
		}
		return readFileExtensions.toArray(new String[readFileExtensions.size()]);
	}

	/**
	 * returns a String Array with the fileExtensions of all FileFormats which can be written
	 * @param concatWildcard determines if a "*." should be concated or not
	 * @return a String Array with the fileExtensions of all FileFormats which can be written
	 */
	public String[] getWriteFileExtensions(boolean concatWildcard)
	{
		List<String> writeFileExtensions = new ArrayList<String>();
		String defaultFileExtension = null;
		if (getDefaultWriteIOFilter() != null) {
			defaultFileExtension = getDefaultWriteIOFilter().getFileExtensions()[0];
		}
		
		int indexDefaultFileExtension = 0;
		for (IOFilter ioFilter : ioFilters) {
			for (String fileExtension : ioFilter.getFileExtensions()) {
				if (ioFilter.supportsWrite(fileExtension)) {
					if (defaultFileExtension != null && defaultFileExtension.equals(fileExtension)) {
						indexDefaultFileExtension = writeFileExtensions.size();
					}
					
					if (concatWildcard)
						writeFileExtensions.add("*."+fileExtension);
					else
						writeFileExtensions.add(fileExtension);
				}
			}
		}
		
		if (indexDefaultFileExtension != 0) {
			String oldFirstFileExtension = writeFileExtensions.get(0);
			defaultFileExtension = writeFileExtensions.get(indexDefaultFileExtension);
			writeFileExtensions.set(0, defaultFileExtension);
			writeFileExtensions.set(indexDefaultFileExtension, oldFirstFileExtension);
		}
		return writeFileExtensions.toArray(new String[writeFileExtensions.size()]);
	}

	/**
	 * returns the defaultReadIOFilter
	 * @return the defaultReadIOFilter
	 */
	public IOFilter getDefaultReadIOFilter() {
		return defaultReadIOFilter;
	}

	/**
	 * sets the field defaultReadIOFilter
	 * @param defaultReadIOFilter the defaultReadIOFilter to set
	 */
	public void setDefaultReadIOFilter(IOFilter defaultReadIOFilter) {
		this.defaultReadIOFilter = defaultReadIOFilter;
	}

	/**
	 * returns the defaultWriteIOFilter
	 * @return the defaultWriteIOFilter
	 */
	public IOFilter getDefaultWriteIOFilter() {
		return defaultWriteIOFilter;
	}

	/**
	 * sets the field defaultWriteIOFilter
	 * @param defaultWriteIOFilter the defaultWriteIOFilter to set
	 */
	public void setDefaultWriteIOFilter(IOFilter defaultWriteIOFilter) {
		this.defaultWriteIOFilter = defaultWriteIOFilter;
	}
	
	
}
