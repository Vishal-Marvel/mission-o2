package com.lrc.missionO2.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddressValidator.class)
@NotNull
@ReportAsSingleViolation
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAddress {
    String message() default "Invalid address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}