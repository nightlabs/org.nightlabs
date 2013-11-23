package org.nightlabs.jdo.timepattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.nightlabs.test.jdo.PersistenceCapableTestBase;

public class TimePatternSetJDOImplTest extends PersistenceCapableTestBase<TimePatternSetJDOImpl> {
	private TimePatternSetJDOImpl bean;

	public TimePatternSetJDOImplTest() {
		super(TimePatternSetJDOImpl.class);
	}

	@Override
	protected TimePatternSetJDOImpl createEmptyInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		bean = super.createEmptyInstance();
		return bean;
	}

	@Override
	protected Object createValue(Type type, String propertyName) {
		if (type == TimePatternJDOImpl.class) {
			return new TimePatternJDOImpl(bean);
		}
		return super.createValue(type, propertyName);
	}

	@Override
	protected String[] getIgnorePropertyNames() {
		return new String[] { "condensedPeriods" };
	}
}
