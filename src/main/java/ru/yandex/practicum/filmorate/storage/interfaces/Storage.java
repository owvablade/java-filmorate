package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

public interface Storage<T> {

    T add(T item);

    T update(T item);

    List<T> getAll();
}
