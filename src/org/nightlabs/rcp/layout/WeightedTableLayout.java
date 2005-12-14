package org.nightlabs.rcp.layout;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * @author Nicklas Shiffler
 *
 */

public class WeightedTableLayout extends TableLayout 
{
  private int[] weights;
  
  public WeightedTableLayout(int[] weights) 
  {
    if (weights != null) 
      this.weights = weights;
    else
      this.weights = new int[0];
  }
  
  public void layout(Composite c, boolean flush) 
  {
    Table table = (Table)c;
    int columnCount = table.getColumnCount();
    

    int width = table.getBounds().width;
    ScrollBar sb = table.getVerticalBar();
    if(sb.isEnabled() && sb.isVisible()) 
      width -= sb.getSize().x;
    
    int[] _weights = new int[columnCount];
    int totalWeight = 0;
    
    for(int i = 0; i < columnCount; i++) 
    {
      if (i >= _weights.length) 
      {
        _weights[i] = 1;
        totalWeight++;
      }
      else 
      {
        _weights[i] = weights[i];
        totalWeight += weights[i];
      }
    }
    
    for(int i = 0; i < columnCount; i++) 
    {
      TableColumn tc = table.getColumn(i);
      tc.setWidth((width * _weights[i]) / totalWeight);
    }
  }
}

