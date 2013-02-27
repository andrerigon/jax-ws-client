package org.jaxwsclient.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Body {

	String contentType() default "application/json";
}
