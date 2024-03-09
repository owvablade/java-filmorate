package ru.yandex.practicum.filmorate.storage.director.interfaces;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director create(Director director);

    Optional<Director> read(Integer id);

    Director update(Director director);

    boolean delete(Integer id);

    List<Director> getAll();
}
