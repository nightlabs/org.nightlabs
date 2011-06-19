package org.nightlabs.jdo.timepattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.nightlabs.test.jdo.PersistenceCapableTestBase;

public class TimePatternJDOImplTest extends PersistenceCapableTestBase<TimePatternJDOImpl> {
	private TimePatternJDOImpl bean;

	public TimePatternJDOImplTest() {
		super(TimePatternJDOImpl.class);
	}

	@Override
	protected TimePatternJDOImpl createEmptyInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		bean = super.createEmptyInstance();
		return bean;
	}

	@Override
	protected Object createValue(Type type, String propertyName) {
		if (type == TimePatternSetJDOImpl.class) {
			return new TimePatternSetJDOImpl((String)createValue(String.class, propertyName + "-constr1"));
		} else if (type == TimePatternJDOImpl.class) {
			return bean;
			//		} else if (type == CondensedTimePatternFieldPeriod.class) {
			//			return new CondensedTimePatternFieldPeriod(
			//					(Date)createValue(Date.class, propertyName + "-constr1"),
			//					(Date)createValue(Date.class, propertyName + "-constr2"));
		} else if (type == String.class && "year".equals(propertyName)) {
			return String.valueOf(1970 + (Integer)createValue(Integer.class, "year-add"));
		} else if (type == String.class && "dayOfWeek".equals(propertyName)) {
			return String.valueOf(((Integer)createValue(Integer.class, "year-add") % 7) + 1);
		}
		return super.createValue(type, propertyName);
	}
}
