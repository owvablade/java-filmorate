package ru.yandex.practicum.filmorate.exception.film;

public class FilmAlreadyLikedByUserException extends RuntimeException {

    public FilmAlreadyLikedByUserException(String message) {
        super(message);
    }
}
