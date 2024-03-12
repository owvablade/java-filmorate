package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Invoke get user method at resource GET /users with id={}", id);
        return userService.readUser(id);
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Invoke add user method at resource POST /users={}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Invoke update user method at resource PUT /users={}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Invoke delete user method at resource DELETE /users with id={}", id);
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Invoke add friend method at resource PUT /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Invoke delete friend method at resource DELETE /users/{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        log.info("Invoke get all friends method at resource GET /users/{}/friends", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Invoke get all common friends method at resource GET /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendedFilmsForUser(@PathVariable Long id) {
        log.info("Invoke get recommendations of films for user.");
        return userService.getRecommendedFilmsForUser(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }


    @GetMapping("/{userId}/feed")
    public List<Event> getUserFeed(@NonNull @PathVariable long userId){
        return  eventService.findUserEvent(userId);
    }
}
