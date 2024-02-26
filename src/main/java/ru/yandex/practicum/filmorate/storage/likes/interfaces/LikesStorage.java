package ru.yandex.practicum.filmorate.storage.likes.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {

    void addLike(Long filmId, Long userId);

    int deleteLike(Long filmId, Long userId);

    List<Film> getNPopular(int n);
}
