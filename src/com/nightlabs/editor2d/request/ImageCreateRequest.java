/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;

import org.eclipse.gef.requests.CreateRequest;

public class ImageCreateRequest 
extends CreateRequest 
{

  public ImageCreateRequest() {
    super();
  }

  public ImageCreateRequest(Object type) {
    super(type);
  }

  protected String fileName;
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
    
}
