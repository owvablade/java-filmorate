package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;

import java.util.*;

@Component
@Deprecated
public class InMemoryFilmStorage implements FilmStorage {

    private long id = 1;
    private final Map<Long, Film> filmStorage;

    public InMemoryFilmStorage() {
        filmStorage = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        film.setId(id++);
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> read(Long id) {
        return Optional.ofNullable(filmStorage.get(id));
    }

    @Override
    public Film update(Film film) {
        if (filmStorage.replace(film.getId(), film) == null) {
            return null;
        }
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (filmStorage.remove(film.getId()) == null) {
            return null;
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public List<Film> getTopNPopular(int n) {
        return null;
    }

    @Override
    public List<Film> getAllByDirector(Integer directorId, String sort) {
        return null;
    }
}
