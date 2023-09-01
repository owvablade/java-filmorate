package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedByUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmHasNoLikeFromUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

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
        return new ErrorResponse(String.format("Invalid fields: %s", fieldErrors), 400);
    }

    @ExceptionHandler({UserNotFoundException.class,
            UsersAreNotFriendsException.class,
            UsersAreAlreadyFriendsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserException(final RuntimeException e) {
        log.error("User error", e);
        return new ErrorResponse(e.getMessage(), 404);
    }

    @ExceptionHandler({FilmNotFoundException.class,
            FilmHasNoLikeFromUserException.class,
            FilmAlreadyLikedByUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmException(final RuntimeException e) {
        log.error("Film error", e);
        return new ErrorResponse(e.getMessage(), 404);
    }
}