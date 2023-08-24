package com.neoflex.application.validator;

import com.neoflex.application.validator.impl.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {
    String message() default "Age has to be greater than 18";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
