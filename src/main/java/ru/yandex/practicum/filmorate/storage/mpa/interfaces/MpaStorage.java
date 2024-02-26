package ru.yandex.practicum.filmorate.storage.mpa.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Optional<Mpa> get(Integer id);

    List<Mpa> getAll();
}
