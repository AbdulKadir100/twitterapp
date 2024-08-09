package com.kadir.abdul.Twitter_App.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@NotNullOrEmpty
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface NotNullOrEmpty {
    String message() default "The field must not be null or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
