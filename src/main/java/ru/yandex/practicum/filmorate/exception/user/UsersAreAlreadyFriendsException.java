package ru.yandex.practicum.filmorate.exception.user;

public class UsersAreAlreadyFriendsException extends RuntimeException {

    public UsersAreAlreadyFriendsException(String message) {
        super(message);
    }
}
