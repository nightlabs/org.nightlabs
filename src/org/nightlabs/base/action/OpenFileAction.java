/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.action;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.config.RecentFileCfMod;
import org.nightlabs.base.io.IOUtil;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

public class OpenFileAction 
extends Action
{
	public static final String ID = OpenFileAction.class.getName();
	public static final String FILTER_ALL = "*";
	
	protected RecentFileCfMod historyConfig;
		
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	};
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	};
	
	public OpenFileAction() {
		super();
		init();
	}
	
	protected void init() 
	{
		setId(ID);
		setText(NLBasePlugin.getResourceString("action.openfile.text"));
		setToolTipText(NLBasePlugin.getResourceString("action.openfile.tooltip"));
				
		try {
			historyConfig = (RecentFileCfMod) Config.sharedInstance().createConfigModule(RecentFileCfMod.class);
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	protected List getRecentFileNames() {
		if (historyConfig != null)
			return historyConfig.getRecentFileNames();
		
		return null;
	}
	
	public void run() 
	{
		FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
		String[] fileExtensions = getFileExtensions(editorRegistry);
		dialog.setFilterExtensions(fileExtensions);
		String fullFileName = dialog.open();
		// Cancel pressed
		if (fullFileName == null)
			return;
		
		File file = new File(fullFileName);
		if (!file.exists()) {
			return;
		}
		
		try {
			boolean foundEditor = IOUtil.openFile(file);
			if (foundEditor)
				addFileToHistory(fullFileName);				
		} catch (PartInitException e) {
			e.printStackTrace();
			RCPUtil.showErrorDialog(
					NLBasePlugin.getResourceString("action.openfile.error.message1")
					+ " " + fullFileName + " " + 
					NLBasePlugin.getResourceString("action.openfile.error.message2")
			);			
		}		
	}
	
	public static final String HISTORY_FILE_ADDED = "history file added";
	protected void addFileToHistory(String fileName) 
	{
		if (getRecentFileNames() != null) 
		{
			// add file only if it is not already contained
			if (!getRecentFileNames().contains(fileName)) 
			{				
				getRecentFileNames().add(fileName);
				pcs.firePropertyChange(HISTORY_FILE_ADDED, null, fileName);
			}
			historyConfig.setChanged(true);			
		}
	}
		
	protected String[] getFileExtensions(IEditorRegistry editorRegistry) 
	{
		IFileEditorMapping[] mappings = editorRegistry.getFileEditorMappings();
		List extensions = new ArrayList(mappings.length);		
		for (int mapCount=0; mapCount<mappings.length; mapCount++) {
			IFileEditorMapping map = mappings[mapCount];
			String extension = map.getExtension();			
			IEditorDescriptor[] editorDescritors = map.getEditors();
			for (int descCount=0; descCount<editorDescritors.length; descCount++) {
				IEditorDescriptor descriptor = editorDescritors[descCount];
				String id = descriptor.getId();					
				if (EditorFileFilterRegistry.sharedInstance().doesMatchEditorID(id)) {
					if (isValid(extension)) {
						extensions.add(extension);
					}											
				}
			}
		}
		if (isUseFilterAll()) {
			extensions.add(0, FILTER_ALL);
		}
		String[] fileExtensions = new String[extensions.size()];
		for (int j=0; j<extensions.size(); j++) {
			String fileExtension = (String) extensions.get(j);
			fileExtensions[j] = concatExtension(fileExtension); 
		}
		return fileExtensions;
	}
	
	protected boolean isValid(String s) 
	{
		if (s == null)
			return false;
		if (s.equals("") || s.equals(" "))
			return false;
		
		return true;
	}
	
	protected String concatExtension(String s) {
		return "*." + s;
	}
		
	protected boolean useFilterAll = true;
	public boolean isUseFilterAll() {
		return useFilterAll;
	}
	public void setUseFilterAll(boolean useFilterAll) {
		this.useFilterAll = useFilterAll;
	}
		
}
