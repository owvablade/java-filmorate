package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.interfaces.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.create(director);
    }

    public Director readDirector(Integer directorId) {
        return directorStorage.read(directorId).orElseThrow(
                () -> new DirectorNotFoundException(String.format("Director with id = %d not found", directorId)));
    }

    public Director updateDirector(Director newDirector) {
        if (directorStorage.update(newDirector) == null) {
            throw new DirectorNotFoundException(String.format("Director with id = %d not found", newDirector.getId()));
        }
        return newDirector;
    }

    public void deleteDirector(Integer id) {
        if (!directorStorage.delete(id)) {
            throw new DirectorNotFoundException(String.format("Director with id = %d not found", id));
        }
    }

    public List<Director> getAll() {
        return directorStorage.getAll();
    }
}
