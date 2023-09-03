package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private long id = 1;
    private final Map<Long, User> userStorage;

    public InMemoryUserStorage() {
        userStorage = new HashMap<>();
    }

    @Override
    public User create(User user) {
        user.setId(id++);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> read(Long id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public User update(User user) {
        if (userStorage.replace(user.getId(), user) == null) {
            return null;
        }
        return user;
    }

    @Override
    public User delete(User user) {
        if (userStorage.remove(user.getId()) == null) {
            return null;
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }
}
