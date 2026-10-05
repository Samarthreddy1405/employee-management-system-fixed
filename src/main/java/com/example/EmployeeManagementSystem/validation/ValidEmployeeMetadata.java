package com.example.employeemanagementsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MetadataValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmployeeMetadata {

    String message() default "Invalid employee metadata";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
