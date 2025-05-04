package com.greatorator.tolkienmobs.handler.annotation;

import java.lang.annotation.*;

@Retention (RetentionPolicy.CLASS)
@Repeatable (Requires.RequiresList.class)
public @interface Requires {

    String value();

    String minVersion() default "";

    String maxVersion() default "";

    String optional() default "";

    @Target (ElementType.TYPE)
    @Retention (RetentionPolicy.CLASS)
    @interface RequiresList {

        Requires[] value();
    }
}
