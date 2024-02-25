package ru.yandex.practicum.filmorate.exception.user;

public class UserCannotBefriendHimselfException extends RuntimeException {

    public UserCannotBefriendHimselfException(String message) {
        super(message);
    }
}
