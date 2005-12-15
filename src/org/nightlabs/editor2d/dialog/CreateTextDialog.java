/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.dialog;



import java.awt.Font;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.request.TextCreateRequest;
import org.nightlabs.util.FontUtil;

public class CreateTextDialog 
extends Dialog 
{
  protected TextCreateRequest request;
  
  public CreateTextDialog(Shell parentShell, TextCreateRequest request) 
  {
    super(parentShell);
    this.request = request;
  }
   
  protected String defaultFontName = new String("Arial");
  protected int defaultSize = 24;
  
  protected int style = SWT.NONE;
  protected Combo fontCombo;
  protected Text text;
  protected Combo sizeCombo;
  protected Button italicButton; 
  protected Button boldButton;
  
  protected int fontStyle = Font.PLAIN;
//  protected int[] sizeArray = new int[] {8,10,12,14,16,18,24,30,36};
  protected String[] fonts;
  
  protected Composite dialogComp;
  protected Control createDialogArea(Composite parent) 
  {
    getShell().setText(EditorPlugin.getResourceString("dialog.createText.title"));
    
    dialogComp = new Composite(parent, style);    
    GridLayout layout = new GridLayout();
    dialogComp.setLayout(layout);    

    Composite nameComp = new Composite(dialogComp, style);
    nameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    GridLayout nameLayout = new GridLayout();
    nameLayout.numColumns = 2;
    nameLayout.marginWidth = 0;
    nameComp.setLayout(nameLayout);    
    
    Label nameLabel = new Label(nameComp, style);
    nameLabel.setText(EditorPlugin.getResourceString("label.name"));
    
    // TODO: Why is this text so short ????? 
    text = new Text(nameComp, style | SWT.BORDER);
//    GridData textData = new GridData(GridData.FILL_BOTH);
    GridData textData = new GridData();
    textData.horizontalAlignment = GridData.FILL;
    textData.grabExcessHorizontalSpace = true;
    text.setLayoutData(textData);
    
    Composite detailComp = new Composite(dialogComp, style);
    GridLayout detailLayout = new GridLayout();
    detailLayout.numColumns = 4;
    detailLayout.marginWidth = 0;
    detailComp.setLayout(detailLayout);    
    
    createFontCombo(detailComp);
    createSizeCombo(detailComp);
    
    boldButton = new Button(detailComp, style | SWT.TOGGLE);
    boldButton.setText("B");
    
    italicButton = new Button(detailComp, style | SWT.TOGGLE);
    italicButton.setText("I");
    
    return dialogArea;
  }  
    
  
  protected void createFontCombo(Composite parent) 
  {
    fontCombo = new Combo(parent, style | SWT.READ_ONLY);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.grabExcessHorizontalSpace = true;
    fontCombo.setLayoutData(gridData);    
    
    fonts = FontUtil.getSystemFonts();
    fontCombo.setItems(fonts);  
    
    for (int i=0; i<fonts.length; i++) {
      String f = fonts[i];
      if (f.equals(defaultFontName)) {
        fontCombo.select(i);
      }
    }    
  }
  
  protected void createSizeCombo(Composite parent) 
  {
    sizeCombo = new Combo(parent, style | SWT.READ_ONLY);
    String[] sizes = FontUtil.getFontSizes();
    sizeCombo.setItems(sizes);
    
    for (int i=0; i<sizes.length; i++) {
      String f = sizes[i];
      if (f.equals(Integer.toString(defaultSize))) {
        sizeCombo.select(i);
      }
    }    
  }
  
//  protected String[] createSizeArray() 
//  {
//    String[] stringSizes = new String[sizeArray.length];
//    for (int i=0; i<sizeArray.length; i++) {
//      stringSizes[i] = Integer.toString(sizeArray[i]);
//    }
//    return stringSizes;
//  }
  
  protected void okPressed() 
  {
    if (boldButton.getSelection())
      fontStyle = Font.BOLD;
    
    if (italicButton.getSelection())
      fontStyle = fontStyle | Font.ITALIC;
    
    int sizeIndex = sizeCombo.getSelectionIndex();
    int size = Integer.parseInt(sizeCombo.getItem(sizeIndex));
    
    int fontIndex = fontCombo.getSelectionIndex();
    String fontName = fontCombo.getItem(fontIndex);
    
    request.setText(text.getText());
    request.setFontName(fontName);
    request.setFontSize(size);
    request.setFontStyle(fontStyle);
    
    super.okPressed();
  }  
}
