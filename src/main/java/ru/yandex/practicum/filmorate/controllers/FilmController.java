package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryFilmStorage storage;

    public FilmController() {
        storage = new InMemoryFilmStorage();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Invoke add film method at resource POST /films = {}", film);
        return storage.add(film);
    }

    @PutMapping
    @ResponseBody
    public Film update(@Valid @RequestBody Film film, HttpServletResponse response) {
        log.info("Invoke update film method at resource PUT /films = {}", film);
        if (storage.update(film) == null) {
            log.info("Cannot update because film with id={} not found", film.getId());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        return storage.getAll();
    }
}
