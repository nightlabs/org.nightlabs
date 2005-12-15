package org.nightlabs.editor2d.model;

import java.util.ArrayList;
import java.util.List;

import org.nightlabs.editor2d.MultiLayerDrawComponent;

public class MultiLayerDrawComponentPropertySource 
extends DrawComponentPropertySource 
{
	public MultiLayerDrawComponentPropertySource(MultiLayerDrawComponent mldc) {
		super(mldc);
	}

	protected List createPropertyDescriptors() {
		return new ArrayList();
	}		
}
