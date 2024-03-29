package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserCannotBefriendHimselfException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.friends.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FilmService filmService;
    private final EventService eventService;
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
            Event event = new Event(userId, EventType.FRIEND, EventOperation.ADD, friendId);
            eventService.addEvent(event);
        } catch (DuplicateKeyException e) {
            throw new UsersAreAlreadyFriendsException(
                    String.format("User with id %d already has friend with id %d.", userId, friendId), e);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(String.format("User with id %d or %d not found.", userId, friendId), e);
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        friendStorage.removeFriend(userId, friendId);
        Event event = new Event(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
        eventService.addEvent(event);
    }

    public List<User> getUserFriends(Long userId) {
        List<User> result = friendStorage.getFriends(userId);
        if (result == null) {
            throw new UserNotFoundException(String.format("User with id = %d not found", userId));
        }
        return result;
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

    public Set<Film> getRecommendedFilmsForUser(Long id) {
        Set<Long> recommendedFilmsIds = userStorage.getRecommendedFilmsForUser(id);
        return recommendedFilmsIds.stream()
                .map(filmService::readFilm)
                .collect(Collectors.toSet());
    }
}
