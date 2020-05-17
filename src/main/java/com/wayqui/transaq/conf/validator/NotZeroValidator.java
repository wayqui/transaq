package com.wayqui.transaq.conf.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class NotZeroValidator implements ConstraintValidator<NotZero, BigDecimal>
{
    @Override
    public void initialize(NotZero cons) {}

    @Override
    public boolean isValid(BigDecimal d, ConstraintValidatorContext cxt) {
        if (d != null) {
            return d.compareTo(new BigDecimal(0)) != 0;
        }
        return false;
    }
}