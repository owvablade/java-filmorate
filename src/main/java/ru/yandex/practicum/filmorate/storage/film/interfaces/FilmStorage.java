package ru.yandex.practicum.filmorate.storage.film.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Optional<Film> read(Long id);

    Film update(Film film);

    boolean delete(Long id);

    List<Film> getAll();

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getTopNPopular(int n);

    List<Film> getAllByDirector(Integer directorId, String sort);

    List<Film> getFilmBySearch(String query, String by);
}
