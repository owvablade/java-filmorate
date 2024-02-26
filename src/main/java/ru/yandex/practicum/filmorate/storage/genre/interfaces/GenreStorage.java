package ru.yandex.practicum.filmorate.storage.genre.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> get(Integer id);

    List<Genre> getAll();
}
