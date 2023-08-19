package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryFilmStorage storage;

    public FilmController() {
        storage = new InMemoryFilmStorage();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return storage.add(film);
    }

    @PutMapping
    @ResponseBody
    public Film update(@Valid @RequestBody Film film, HttpServletResponse response) {
        if (storage.update(film) == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        return storage.getAll();
    }
}
