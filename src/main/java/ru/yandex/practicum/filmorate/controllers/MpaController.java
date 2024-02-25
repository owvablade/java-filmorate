package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable Integer id) {
        log.info("Invoke get mpa method at resource GET /mpa with id={}", id);
        return mpaService.getMpa(id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Invoke get all mpa method at resource GET /mpa");
        return mpaService.getAllMpa();
    }
}
