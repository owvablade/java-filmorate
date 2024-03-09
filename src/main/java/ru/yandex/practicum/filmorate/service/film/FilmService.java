package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedByUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.interfaces.LikesStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

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

    public void deleteFilm(Long id) {
        if (!filmStorage.delete(id)) {
            throw new FilmNotFoundException(String.format("Film with id = %d not found", id));
        }
    }

    public void addLike(Long filmId, Long userId) {
        try {
            likesStorage.addLike(filmId, userId);
        } catch (DuplicateKeyException e) {
            throw new FilmAlreadyLikedByUserException(
                    String.format("User with id %d has already liked film with id %d.", userId, filmId), e);
        } catch (DataIntegrityViolationException e) {
            throw new FilmNotFoundException(
                    String.format("User with id %d or film with id %d not found.", userId, filmId), e);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        if (likesStorage.deleteLike(filmId, userId) == 0) {
            throw new LikeNotFoundException(String.format(
                    "Like from user with id=%d was not found on film with id=%d. Or vice versa", userId, filmId));
        }
    }

    public List<Film> getTopNFilmsByLikes(int n) {
        return filmStorage.getTopNPopular(n);
    }

    public List<Film> getAllByDirector(Integer directorId, String sort) {
        return filmStorage.getAllByDirector(directorId, sort);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }
}
