/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
