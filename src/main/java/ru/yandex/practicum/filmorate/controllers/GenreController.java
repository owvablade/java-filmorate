package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("{id}")
    public Genre getGenre(@PathVariable Integer id) {
        log.info("Invoke get genre method at resource GET /genre with id={}", id);
        return genreService.getGenre(id);
    }

    @GetMapping
    public List<Genre> getAllGenre() {
        log.info("Invoke get genre method at resource GET /genre");
        return genreService.getAllGenre();
    }
}
