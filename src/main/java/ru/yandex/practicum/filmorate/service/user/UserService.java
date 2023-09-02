package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public User deleteUser(User user) {
        if (userStorage.delete(user) == null) {
            throw new UserNotFoundException(String.format("User with id = %d not found", user.getId()));
        }
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = readUser(userId);
        User friendUser = readUser(friendId);
        if (user.containsFriend(friendId) && friendUser.containsFriend(userId)) {
            throw new UsersAreAlreadyFriendsException(String.format(
                    "User with id = %d and id = %d are already friends",
                    userId,
                    friendId)
            );
        }
        user.addFriend(friendId);
        friendUser.addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = readUser(userId);
        User friendUser = readUser(friendId);
        if (!user.containsFriend(friendId) && !friendUser.containsFriend(userId)) {
            throw new UsersAreNotFriendsException(String.format(
                    "User with id = %d and id = %d are not friends",
                    userId,
                    friendId)
            );
        }
        user.removeFriend(friendId);
        friendUser.removeFriend(userId);
    }

    public List<User> getUserFriends(Long userId) {
        User user = readUser(userId);
        return new ArrayList<>(user.getFriends())
                .stream()
                .map(this::readUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long anotherUserId) {
        User user = readUser(userId);
        User anotherUser = readUser(anotherUserId);
        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(anotherUser.getFriends());
        return new ArrayList<>(commonFriends)
                .stream()
                .map(this::readUser)
                .collect(Collectors.toList());
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
