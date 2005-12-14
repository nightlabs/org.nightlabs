/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import com.nightlabs.editor2d.EditorPlugin;

/**
* If the current object on the default GEF {@link org.eclipse.gef.ui.actions.Clipboard}
* is a template and the current viewer is a graphical viewer, this action will paste the
* template to the viewer.
* @author Eric Bordeau
*/


public abstract class PasteTemplateAction 
extends SelectionAction 
{

 /**
  * Constructor for PasteTemplateAction.
  * @param editor
  */
 public PasteTemplateAction(IWorkbenchPart editor) {
 	super(editor);
 }

 /**
  * Returns <code>true</code> if the {@link Clipboard clipboard's} contents are not
  * <code>null</code> and the command returned by {@link #createPasteCommand()} can
  * execute.
  */
 protected boolean calculateEnabled() {
 	Command command = null;
 	if (getClipboardContents() != null)
 		command = createPasteCommand();
 	return command != null && command.canExecute();
 }

 /**
  * Creates and returns a command (which may be <code>null</code>) to create a new EditPart
  * based on the template on the clipboard.
  * @return the paste command
  */
 protected Command createPasteCommand() {
 	if (getSelectedObjects() == null || getSelectedObjects().isEmpty())
 		return null;
 	CreateRequest request = new CreateRequest();
 	request.setFactory(getFactory(getClipboardContents()));
 	request.setLocation(getPasteLocation());
 	Object obj = getSelectedObjects().get(0);
 	if (obj instanceof EditPart)
 		return ((EditPart)obj).getCommand(request);
 	return null;
 }

 /**
  * Returns the contents of the default GEF {@link Clipboard}.
  * @return the clipboard's contents
  */
 protected Object getClipboardContents() {
 	return Clipboard.getDefault().getContents();
 }

 /**
  * Returns the appropriate Factory object to be used for the specified template. This
  * Factory is used on the CreateRequest that is sent to the target EditPart.
  * @param template the template Object
  * @return a Factory
  */
 protected abstract CreationFactory getFactory(Object template);

 protected abstract Point getPasteLocation();

 /**
  * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
  */
 protected void init() {
 	setId(ActionFactory.PASTE.getId());
 	setText(EditorPlugin.getResourceString("action_paste_label"));
 }

 /**
  * Executes the command returned by {@link #createPasteCommand()}.
  */
 public void run() {
 	execute(createPasteCommand());
 }
}
