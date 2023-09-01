package ru.yandex.practicum.filmorate.storage.user.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User read(Long id);

    User update(User user);

    User delete(User user);

    List<User> getAll();
}