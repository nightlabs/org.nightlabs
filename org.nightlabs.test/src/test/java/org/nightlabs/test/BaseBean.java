package org.nightlabs.test;

public class BaseBean {
	@SuppressWarnings("unused")
	private float fieldWithoutGetter;

	public void setFieldWithoutGetter(float fieldWithoutGetter) {
		this.fieldWithoutGetter = fieldWithoutGetter;
	}

	private double fieldDouble;

	public void setFieldDouble(double fieldDouble) {
		this.fieldDouble = fieldDouble;
	}

	public double getFieldDouble() {
		return fieldDouble;
	}

	protected int fieldWithoutSetter;

	public int getFieldWithoutSetter() {
		return fieldWithoutSetter;
	}
}
