package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedByUserException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.interfaces.LikesStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    private final EventService eventService;

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
            Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
            eventService.addEvent(event);
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
        }else
            likesStorage.deleteLike(filmId, userId);
            Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
            eventService.addEvent(event);

    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public List<Film> getTopNFilmsByLikes(int n) {
        return filmStorage.getTopNPopular(n);
    }

    public List<Film> getAllByDirector(Integer directorId, String sort) {
        return filmStorage.getAllByDirector(directorId, sort);
    }

    public List<Film> getMostPopularByGenreAndYear(String year, String genreId, int limit) {
        var temp = filmStorage.getAll();

        if (year == null & genreId != null) {
            final int genId = Integer.parseInt(genreId);
            return temp.stream()
                    .filter(film ->
                            film.getGenres()
                                    .stream()
                                    .anyMatch(genre -> genre.getId() == genId))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if (year != null & genreId == null) {
            final int yearInt = Integer.parseInt(year);
            return temp.stream()
                    .filter(film -> film.getReleaseDate().getYear() == yearInt)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        final int genId = Integer.parseInt(genreId);
        final int yearInt = Integer.parseInt(year);

        return temp.stream()
                .filter(film -> film.getReleaseDate().getYear() == yearInt)
                .filter(film ->
                        film.getGenres()
                                .stream()
                                .anyMatch(genre -> genre.getId() == genId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
