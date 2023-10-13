package com.faesa.librarycli.shared.core.validators;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = UniqueValueValidator.class)
public @interface UniqueValue {

    Class<?> domainClass();

    String fieldName();

    String message() default "Value already exists";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
