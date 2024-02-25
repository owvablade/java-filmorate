package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.interfaces.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpa(Integer id) {
        return mpaStorage.get(id).orElseThrow(
                () -> new MpaNotFoundException(String.format("Mpa with id = %d not found", id)));
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAll();
    }
}
