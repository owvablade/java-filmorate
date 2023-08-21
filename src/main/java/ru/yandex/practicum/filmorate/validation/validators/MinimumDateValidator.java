package ru.yandex.practicum.filmorate.validation.validators;

import ru.yandex.practicum.filmorate.validation.annotations.MinimumDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MinimumDateValidator implements ConstraintValidator<MinimumDate, LocalDate> {

    private LocalDate minimumDate;

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(minimumDate) || localDate.isEqual(minimumDate);
    }

    @Override
    public void initialize(MinimumDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }
}
