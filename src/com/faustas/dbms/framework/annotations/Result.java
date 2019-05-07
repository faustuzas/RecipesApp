package com.faustas.dbms.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Result {
    String column();
    String property();

    Exec exec() default @Exec;
}
