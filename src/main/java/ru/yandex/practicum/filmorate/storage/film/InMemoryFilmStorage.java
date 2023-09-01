package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static long id = 1;
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
    public Film read(Long id) {
        return filmStorage.get(id);
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
}
