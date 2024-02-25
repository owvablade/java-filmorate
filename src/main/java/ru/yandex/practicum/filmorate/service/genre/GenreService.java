package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.interfaces.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenre(Integer id) {
        return genreStorage.get(id).orElseThrow(() -> new GenreNotFoundException(
                String.format("Genre with id = %d not found", id)));
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAll();
    }
}
