package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static Validator validator;
    private User user;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @BeforeEach
    void beforeEach() {
        user = new User()
                .setEmail("artem@yandex.ru")
                .setLogin("login")
                .setBirthday(LocalDate.of(2001, 1, 31));
    }

    @Test
    void shouldValidateUserWithName() {
        user.setName("Artem");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertAll(
                () -> assertTrue(violations.isEmpty()),
                () -> assertEquals("login", user.getLogin()),
                () -> assertEquals("Artem", user.getName())
        );
    }

    @Test
    void shouldValidateUserWithoutName() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertAll(
                () -> assertTrue(violations.isEmpty()),
                () -> assertEquals(user.getName(), user.getLogin())
        );
    }

    @Test
    void shouldNotValidateUserWithBlankEmail() {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateUserWithoutAddressSignInEmail() {
        user.setEmail("artemyandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateUserWithWrongEmail() {
        user.setEmail("this-is-wrong?email@");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateUserWithBlankLogin() {
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateUserWithWhitespacesInLogin() {
        user.setLogin(" login login  ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateUserWithFutureBirthdayDate() {
        user.setBirthday(LocalDate.of(2049, 12, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }
}