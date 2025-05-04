package com.greatorator.tolkienmobs.handler.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention (RetentionPolicy.CLASS)
public @interface ReplaceWithExpr {

    String value();
}
