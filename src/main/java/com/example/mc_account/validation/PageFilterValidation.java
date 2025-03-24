package com.example.mc_account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PageFilterValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageFilterValidation {

    String message() default "Поля пагинации должны быть заполнены. Нумерация страниц начинается с ноля.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
