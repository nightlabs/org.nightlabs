/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.rulers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.command.CreateGuideCommand;
import org.nightlabs.editor2d.command.DeleteGuideCommand;
import org.nightlabs.editor2d.command.MoveGuideCommand;


public class EditorRulerProvider 
extends RulerProvider 
{
  private EditorRuler ruler;
  private PropertyChangeListener rulerListener = new PropertyChangeListener() {
  	public void propertyChange(PropertyChangeEvent evt) {
  		if (evt.getPropertyName().equals(EditorRuler.PROPERTY_CHILDREN)) {
  			EditorGuide guide = (EditorGuide)evt.getNewValue();
  			if (getGuides().contains(guide)) {
  				guide.addPropertyChangeListener(guideListener);
  			} else {
  				guide.removePropertyChangeListener(guideListener);
  			}
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyGuideReparented(guide);
  			}
  		} else {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyUnitsChanged(ruler.getUnit());
  			}
  		}
  	}
  };
  private PropertyChangeListener guideListener = new PropertyChangeListener() {
  	public void propertyChange(PropertyChangeEvent evt) {
  		if (evt.getPropertyName().equals(EditorGuide.PROPERTY_CHILDREN)) {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyPartAttachmentChanged(evt.getNewValue(), evt.getSource());
  			}
  		} else {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyGuideMoved(evt.getSource());
  			}
  		}
  	}
  };

  public EditorRulerProvider(EditorRuler ruler) {
  	this.ruler = ruler;
  	this.ruler.addPropertyChangeListener(rulerListener);
  	List guides = getGuides();
  	for (int i = 0; i < guides.size(); i++) {
  		((EditorGuide)guides.get(i)).addPropertyChangeListener(guideListener);
  	}
  }

  public List getAttachedModelObjects(Object guide) {
  	return new ArrayList(((EditorGuide)guide).getParts());
  }

  public Command getCreateGuideCommand(int position) {
  	return new CreateGuideCommand(ruler, position);
  }

  public Command getDeleteGuideCommand(Object guide) {
  	return new DeleteGuideCommand((EditorGuide)guide, ruler);
  }

  public Command getMoveGuideCommand(Object guide, int pDelta) {
  	return new MoveGuideCommand((EditorGuide)guide, pDelta);
  }

  public int[] getGuidePositions() {
  	List guides = getGuides();
  	int[] result = new int[guides.size()];
  	for (int i = 0; i < guides.size(); i++) {
  		result[i] = ((EditorGuide)guides.get(i)).getPosition();
  	}
  	return result;
  }

  public Object getRuler() {
  	return ruler;
  }

  public int getUnit() {
  	return ruler.getUnit();
  }

  public void setUnit(int newUnit) {
  	ruler.setUnit(newUnit);
  }

  public int getGuidePosition(Object guide) {
  	return ((EditorGuide)guide).getPosition();
  }

  public List getGuides() {
  	return ruler.getGuides();
  }
  
}
