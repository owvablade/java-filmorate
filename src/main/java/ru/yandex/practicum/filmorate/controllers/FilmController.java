package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        log.info("Invoke get film method at resource GET /films with id={}", id);
        return filmService.readFilm(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Invoke add film method at resource POST /films={}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Invoke update film method at resource PUT /films={}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke add like to film method at resource PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke delete like to film method at resource PUT /films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(
            @RequestParam(defaultValue = "10")
            @Positive(message = "Parameter count must be positive")
            Integer count) {
        log.info("Invoke get most n popular film method at resource GET /films/popular?count={}", count);
        return filmService.getTopNFilmsByLikes(count);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAllFilms();
    }
}
