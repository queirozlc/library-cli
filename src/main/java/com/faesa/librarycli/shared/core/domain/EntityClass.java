package com.faesa.librarycli.shared.core.domain;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityClass {

    String tableName();

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Column {

        String name();

        boolean primaryKey() default false;

        boolean nullable() default false;

        boolean unique() default false;
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface hasOne {
        String foreignKey();

        boolean nullable() default false;

        boolean unique() default false;
    }
}
