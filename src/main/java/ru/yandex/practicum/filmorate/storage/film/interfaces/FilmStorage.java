package ru.yandex.practicum.filmorate.storage.film.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Optional<Film> read(Long id);

    Film update(Film film);

    Film delete(Film film);

    List<Film> getAll();

}
