/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.command;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ImageDrawComponent;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;

public class CreateImageCommand 
extends CreateDrawComponentCommand 
{

  public CreateImageCommand() 
  {
    super();
    setLabel(EditorPlugin.getResourceString("command.create.image"));  
  }
  
  public ImageDrawComponent getImageDrawComponent() 
  {
    return (ImageDrawComponent) getChild();
  }
  
  protected BufferedImage image;
  
  protected String fileName;
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  protected String simpleFileName;    
  public void setSimpleFileName(String simpleFileName) {
    this.simpleFileName = simpleFileName;
  }
  
  public void execute() 
  {
    super.execute();    
    try {
      image = JPEGCodec.createJPEGDecoder(new FileInputStream(fileName)).decodeAsBufferedImage();
      getImageDrawComponent().setImage(image);
      getImageDrawComponent().setName(simpleFileName);
    } catch (ImageFormatException e) {
    	throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
    	throw new RuntimeException(e);    
    } catch (IOException e) {
    	throw new RuntimeException(e);    
    }
  }
  
  public void redo() 
  {
    super.redo();
  }
  
  public void undo() 
  {
    super.undo();
  }
  
  
}
