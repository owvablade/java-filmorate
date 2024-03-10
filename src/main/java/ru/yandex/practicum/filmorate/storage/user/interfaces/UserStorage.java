package ru.yandex.practicum.filmorate.storage.user.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User create(User user);

    Optional<User> read(Long id);

    User update(User user);

    boolean delete(Long id);

    List<User> getAll();
    Set<Long> getRecommendedFilmsForUser(Long userId);
}
