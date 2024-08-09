package com.kadir.abdul.Twitter_App.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull
@Min(1)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface NotNullOrMin {
    String message() default "The field must not be null and must be greater than or equal to 1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
