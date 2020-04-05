package com.wayqui.transaq.conf.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotZeroValidator implements ConstraintValidator<NotZero, Double>
{
    @Override
    public void initialize(NotZero cons) {}

    @Override
    public boolean isValid(Double d, ConstraintValidatorContext cxt)
    {
        if (d != null) {
            return Double.compare(d, 0.0d) != 0;
        }
        return false;
    }
}