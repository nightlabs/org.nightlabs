package com.nightlabs.editor2d.model;

import java.util.ArrayList;
import java.util.List;

import com.nightlabs.editor2d.Layer;

public class LayerPropertySource 
extends DrawComponentPropertySource 
{
	public LayerPropertySource(Layer layer) {
		super(layer);
	}

	protected List createPropertyDescriptors() {
		return new ArrayList();
	}		
}
