package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedByUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmHasNoLikeFromUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmStorage filmStorage,
                       @Qualifier("userDaoImpl") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film readFilm(Long filmId) {
        return filmStorage.read(filmId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Film with id = %d not found", filmId)));
    }

    public Film updateFilm(Film film) {
        if (filmStorage.update(film) == null) {
            throw new FilmNotFoundException(String.format("Film with id = %d not found", film.getId()));
        }
        return film;
    }

    public Film deleteFilm(Film film) {
        if (filmStorage.delete(film) == null) {
            throw new FilmNotFoundException(String.format("Film with id = %d not found", film.getId()));
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (userStorage.read(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("User with id = %d not found", userId));
        }
        Film film = readFilm(filmId);
        if (film.containsLike(userId)) {
            throw new FilmAlreadyLikedByUserException(String.format("Film with id = %d " +
                    "already has like from user with id = %d", filmId, userId));
        }
        film.addLike(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (userStorage.read(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("User with id = %d not found", userId));
        }
        Film film = readFilm(filmId);
        if (!film.containsLike(userId)) {
            throw new FilmHasNoLikeFromUserException(String.format("Film with id = %d " +
                    "does not contain like from user with id = %d", filmId, userId));
        }
        film.deleteLike(userId);
    }

    public List<Film> getTopNFilmsByLikes(int n) {
        return filmStorage.getAll()
                .stream()
                .sorted(Comparator.comparing(Film::getLikesCount, Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }
}
