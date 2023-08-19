package ru.yandex.practicum.filmorate.validation.annotations;

import ru.yandex.practicum.filmorate.validation.validators.MinimumDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MinimumDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumDate {

    String value() default "1895-12-28";
    String message() default "Date must not be before {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
