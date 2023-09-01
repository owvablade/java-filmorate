package ru.yandex.practicum.filmorate.exception.film;

public class FilmHasNoLikeFromUserException extends RuntimeException {

    public FilmHasNoLikeFromUserException(String message) {
        super(message);
    }
}
