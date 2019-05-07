package com.faustas.dbms.framework.annotations;

public @interface Exec {
    Class aClass() default void.class;

    String method() default "findById";
}
