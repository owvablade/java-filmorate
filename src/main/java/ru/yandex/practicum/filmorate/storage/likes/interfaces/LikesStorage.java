package ru.yandex.practicum.filmorate.storage.likes.interfaces;

public interface LikesStorage {

    void addLike(Long filmId, Long userId);

    int deleteLike(Long filmId, Long userId);
}
