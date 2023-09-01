package ru.yandex.practicum.filmorate.exception.user;

public class UsersAreNotFriendsException extends RuntimeException {

    public UsersAreNotFriendsException(String message) {
        super(message);
    }
}
