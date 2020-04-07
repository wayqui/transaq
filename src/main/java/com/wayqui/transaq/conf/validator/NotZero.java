package com.wayqui.transaq.conf.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotZeroValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotZero {
    String message() default "Double value is zero";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}