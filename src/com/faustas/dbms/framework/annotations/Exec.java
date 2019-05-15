package com.faustas.dbms.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
public @interface Exec {
    Class aClass() default void.class;

    String method() default "findById";
}
