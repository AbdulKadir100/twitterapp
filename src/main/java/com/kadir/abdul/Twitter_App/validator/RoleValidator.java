package com.kadir.abdul.Twitter_App.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import com.kadir.abdul.Twitter_App.utils.AppUtils;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {


    @Override
    public void initialize(ValidRole constraintAnnotation) {
        // Not needed for this example
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && AppUtils.USER_ROLES.contains(value.toLowerCase());
    }

}
