package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedByUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmHasNoLikeFromUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Validation error", e);
        BindingResult result = e.getBindingResult();
        final List<String> fieldErrors = result.getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());
        return new ErrorResponse(String.format("Invalid fields: %s", fieldErrors), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.error("Path variable error", e);
        return new ErrorResponse(String.format("Value '%s' is invalid for parameter '%s'", e.getValue(), e.getName()),
                HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final ConstraintViolationException e) {
        log.error("Parameter error", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({UserNotFoundException.class,
            UsersAreNotFriendsException.class,
            UsersAreAlreadyFriendsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserException(final RuntimeException e) {
        log.error("User error", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({FilmNotFoundException.class,
            FilmHasNoLikeFromUserException.class,
            FilmAlreadyLikedByUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmException(final RuntimeException e) {
        log.error("Film error", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({GenreNotFoundException.class,
            LikeNotFoundException.class,
            MpaNotFoundException.class,
            DirectorNotFoundException.class,
            ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundInDbException(final RuntimeException e) {
        log.error("Object in database not found", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Error", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserDoesNotExistException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

}
