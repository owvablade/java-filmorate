package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserCannotBefriendHimselfException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public User createUser(User user) {
        checkUserName(user);
        return userStorage.create(user);
    }

    public User readUser(Long userId) {
        return userStorage.read(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id = %d not found", userId)));
    }

    public User updateUser(User user) {
        checkUserName(user);
        if (userStorage.update(user) == null) {
            throw new UserNotFoundException(String.format("User with id = %d not found", user.getId()));
        }
        return user;
    }

    public void deleteUser(Long userId) {
        if (!userStorage.delete(userId)) {
            throw new UserNotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new UserCannotBefriendHimselfException("User cannot befriend himself");
        }
        try {
            friendStorage.addFriend(userId, friendId);
        } catch (DuplicateKeyException e) {
            throw new UsersAreAlreadyFriendsException(
                    String.format("User with id %d already has friend with id %d.", userId, friendId), e);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(String.format("User with id %d or %d not found.", userId, friendId), e);
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        readUser(userId);
        return new ArrayList<>(friendStorage.getFriends(userId));
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return new ArrayList<>(friendStorage.getCommonFriends(userId, otherId));
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
