package com.nightlabs.rcp.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class FileEditorInputFactory implements IElementFactory {

	public static final String FILENAME_KEY = "fileName";
	
	public FileEditorInputFactory() {
		super();
	}

	public IAdaptable createElement(IMemento memento) {
		String fileName = memento.getString(FILENAME_KEY);
//		if (fileName != null) {
			File file = new File(fileName);
			FileEditorInput input = new FileEditorInput(file);
			return input;
//		}
//		// TODO: check if this may cause errors
//		return null;
	}

	public static void storeFileEditorInput(IMemento memento, FileEditorInput input) {
		if (input.getFile() == null) // || !input.getFile().exists())
			return;

		try {
			memento.putString(FILENAME_KEY, input.getFile().getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
