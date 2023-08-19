package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserStorage storage;

    public UserController() {
        storage = new InMemoryUserStorage();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return storage.add(user);
    }

    @PutMapping
    @ResponseBody
    public User update(@Valid @RequestBody User user, HttpServletResponse response) {
        if (storage.update(user) == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        return storage.getAll();
    }
}
