/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.awt.geom.AffineTransform;

import org.eclipse.draw2d.PositionConstants;


public class ShearCommand 
extends AbstractTransformCommand
{

  public ShearCommand() 
  {
    super();
  }

  protected double shear;  
  public double getShear() {
    return shear;
  }
  public void setShear(double shear) {
    this.shear = shear;
  }
  
  protected int direction;  
  public int getDirection() {
    return direction;
  }
  public void setDirection(int direction) {
    this.direction = direction;
  }
    
  protected AffineTransform calcAffineTransform() 
  {
    AffineTransform at = new AffineTransform();
    switch (direction) 
    {    	
    	case(PositionConstants.EAST):
    	case(PositionConstants.WEST):    	  
    		at.shear(shear, 1.0d);
    		break;
    	case(PositionConstants.NORTH):
    	case(PositionConstants.SOUTH):
    		at.shear(1.0d, shear);
    		setAffineTransform(at);
    		break;    		
    }
		return at;
  }
  
  public void execute() 
  {
    setAffineTransform(calcAffineTransform());
    super.execute();
  }
  
}
