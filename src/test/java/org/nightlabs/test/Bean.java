package org.nightlabs.test;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bean extends BaseBean {
	private long anotherField;

	public void setAnotherField(long anotherField) {
		this.anotherField = anotherField;
	}

	public long getAnotherField() {
		return anotherField;
	}

	@SuppressWarnings("unused")
	private char anotherFieldWithoutGetter;

	public void setAnotherFieldWithoutGetter(char anotherFieldWithoutGetter) {
		this.anotherFieldWithoutGetter = anotherFieldWithoutGetter;
	}

	private List<String> x;

	public List<String> getX() {
		return x;
	}

	public void setX(List<String> x) {
		this.x = x;
	}

	private Set<Date> dates;

	public Set<Date> getDates() {
		return dates;
	}

	public void setDates(Set<Date> dates) {
		this.dates = dates;
	}

	private float myFloat;

	public float getMyFloat() {
		return myFloat;
	}

	public void setMyFloat(float myFloat) {
		this.myFloat = myFloat;
	}

	private short s;

	public short getS() {
		return s;
	}

	public void setS(short s) {
		this.s = s;
	}

	private byte b;

	public byte getB() {
		return b;
	}

	public void setB(byte b) {
		this.b = b;
	}

	private Collection<Boolean> collection;

	public Collection<Boolean> getCollection() {
		return collection;
	}

	public void setCollection(Collection<Boolean> collection) {
		this.collection = collection;
	}

	private String[] array;

	public String[] getArray() {
		return array;
	}

	public void setArray(String[] array) {
		this.array = array;
	}

	private Map<Integer, Character> map;

	public Map<Integer, Character> getMap() {
		return map;
	}

	public void setMap(Map<Integer, Character> map) {
		this.map = map;
	}
}
