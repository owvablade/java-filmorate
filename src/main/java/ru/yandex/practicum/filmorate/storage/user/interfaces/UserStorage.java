package ru.yandex.practicum.filmorate.storage.user.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    Optional<User> read(Long id);

    User update(User user);

    User delete(User user);

    List<User> getAll();
}
