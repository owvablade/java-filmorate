package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserStorage storage;

    public UserController() {
        storage = new InMemoryUserStorage();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Invoke add user method at resource POST /users = {}", user);
        return storage.add(user);
    }

    @PutMapping
    @ResponseBody
    public User update(@Valid @RequestBody User user, HttpServletResponse response) {
        log.info("Invoke update user method at resource PUT /users = {}", user);
        if (storage.update(user) == null) {
            log.info("Cannot update because user with id={} not found", user.getId());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        return storage.getAll();
    }
}
