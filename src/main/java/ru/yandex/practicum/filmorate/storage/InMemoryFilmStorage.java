package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFilmStorage implements Storage<Film> {

    private static int id;
    private final Map<Integer, Film> filmStorage;

    public InMemoryFilmStorage() {
        filmStorage = new HashMap<>();
    }

    @Override
    public Film add(Film item) {
        item.setId(++id);
        filmStorage.put(id, item);
        return item;
    }

    @Override
    public Film update(Film item) {
        if (filmStorage.replace(item.getId(), item) == null) {
            return null;
        }
        return item;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.values());
    }
}
