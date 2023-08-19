package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserStorage implements Storage<User> {

    private static int id;
    private final Map<Integer, User> userStorage;

    public InMemoryUserStorage() {
        userStorage = new HashMap<>();
    }

    @Override
    public User add(User item) {
        item.setId(++id);
        userStorage.put(id, item);
        return item;
    }

    @Override
    public User update(User item) {
        if (userStorage.replace(item.getId(), item) == null) {
            return null;
        }
        return item;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }
}
