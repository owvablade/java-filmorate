package ru.yandex.practicum.filmorate.storage.event.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void addEvent(Event event);

    List<Event> findUserEvent(Long userId);
}
