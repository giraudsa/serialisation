package com.actemium.marshall.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
	public TypeRelation type();
}
