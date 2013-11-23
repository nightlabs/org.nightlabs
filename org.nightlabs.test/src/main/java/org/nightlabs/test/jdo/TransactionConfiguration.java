package org.nightlabs.test.jdo;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TransactionConfiguration {
	boolean defaultRollback() default false;
}
