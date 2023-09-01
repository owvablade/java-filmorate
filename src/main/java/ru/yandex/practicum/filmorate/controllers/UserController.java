package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Invoke add user method at resource POST /users = {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Invoke update user method at resource PUT /users = {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }
}
