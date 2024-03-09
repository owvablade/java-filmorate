package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @GetMapping("/{id}")
    public Director read(@PathVariable Integer id) {
        return directorService.readDirector(id);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director newDirector) {
        return directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        directorService.deleteDirector(id);
    }

    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }
}
