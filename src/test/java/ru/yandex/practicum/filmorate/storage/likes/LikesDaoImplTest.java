package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDaoImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikesDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDaoImpl filmDao;
    private UserDaoImpl userDao;
    private LikesDaoImpl likesDao;
    private User firstUser;
    private User secondUser;
    private Film firstFilm;
    private Film secondFilm;

    @BeforeEach
    void setUp() {
        filmDao = new FilmDaoImpl(jdbcTemplate);
        userDao = new UserDaoImpl(jdbcTemplate);
        likesDao = new LikesDaoImpl(jdbcTemplate);
        firstUser = new User()
                .setEmail("tempArtem@yandex.ru")
                .setLogin("artem123")
                .setName("Artem")
                .setBirthday(LocalDate.of(2001, 1, 31));
        secondUser = new User()
                .setEmail("tempMisha@gmail.com")
                .setLogin("misha321")
                .setBirthday(LocalDate.of(2001, 5, 16));
        firstFilm = new Film()
                .setName("Film 1")
                .setDescription("first film desc")
                .setReleaseDate(LocalDate.of(2024, 2, 1))
                .setDuration(100)
                .setMpa(new Mpa(2, "PG"));
        secondFilm = new Film()
                .setName("Film 2")
                .setDescription("second film desc")
                .setReleaseDate(LocalDate.of(2023, 1, 1))
                .setDuration(200)
                .setMpa(new Mpa(3, "PG-13"));
        secondFilm.addGenre(new Genre(6, "Боевик"));
    }

    @Test
    void addLikeFromValidUser() {
        final int expectedResultSize = 2;

        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        Film createdFirstFilm = filmDao.create(firstFilm);
        filmDao.create(secondFilm);

        likesDao.addLike(createdFirstFilm.getId(), createdFirstUser.getId());
        likesDao.addLike(createdFirstFilm.getId(), createdSecondUser.getId());
        List<Film> actualResult = filmDao.getTopNPopular(2);
        Film mostPopular = actualResult.get(0);

        assertNotNull(actualResult);
        assertAll(
                () -> assertEquals(expectedResultSize, actualResult.size()),
                () -> assertNotNull(createdFirstFilm.getId()),
                () -> assertEquals(firstFilm.getName(), mostPopular.getName()),
                () -> assertEquals(firstFilm.getDescription(), mostPopular.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), mostPopular.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), mostPopular.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), mostPopular.getMpa())
        );
    }

    @Test
    void addLikeFromNonexistentUser() {
        Film createdFirstFilm = filmDao.create(firstFilm);
        assertAll(
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(createdFirstFilm.getId(), Long.MIN_VALUE)),
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(createdFirstFilm.getId(), Long.MAX_VALUE))
        );
    }

    @Test
    void addLikeToNonexistentFilm() {
        User createdFirstUser = userDao.create(firstUser);
        assertAll(
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(Long.MIN_VALUE, createdFirstUser.getId())),
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(Long.MAX_VALUE, createdFirstUser.getId()))
        );
    }

    @Test
    void deleteLikeFromFilm() {
        final int expectedResultSize = 2;

        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        Film createdFirstFilm = filmDao.create(firstFilm);
        Film createdSecondFilm = filmDao.create(secondFilm);

        likesDao.addLike(createdFirstFilm.getId(), createdFirstUser.getId());
        likesDao.addLike(createdSecondFilm.getId(), createdSecondUser.getId());
        likesDao.deleteLike(createdSecondFilm.getId(), createdSecondUser.getId());
        List<Film> actualResult = filmDao.getTopNPopular(2);
        Film mostPopular = actualResult.get(0);

        assertNotNull(actualResult);
        assertAll(
                () -> assertEquals(expectedResultSize, actualResult.size()),
                () -> assertNotNull(createdFirstFilm.getId()),
                () -> assertEquals(firstFilm.getName(), mostPopular.getName()),
                () -> assertEquals(firstFilm.getDescription(), mostPopular.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), mostPopular.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), mostPopular.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), mostPopular.getMpa())
        );
    }

    @Test
    void deleteLikeFromNonexistentUser() {
        final int expectedChangedRows = 0;
        Film createdFirstFilm = filmDao.create(firstFilm);
        assertAll(
                () -> assertEquals(expectedChangedRows, likesDao.deleteLike(createdFirstFilm.getId(), Long.MIN_VALUE)),
                () -> assertEquals(expectedChangedRows, likesDao.deleteLike(createdFirstFilm.getId(), Long.MAX_VALUE))
        );
    }

    @Test
    void deleteLikeToNonexistentFilm() {
        User createdFirstUser = userDao.create(firstUser);
        assertAll(
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(Long.MIN_VALUE, createdFirstUser.getId())),
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> likesDao.addLike(Long.MAX_VALUE, createdFirstUser.getId()))
        );
    }

    @Test
    void getTopNFilms() {
        assertEquals(0, filmDao.getTopNPopular(50).size());

        final int expectedResultSize = 2;
        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        Film createdFirstFilm = filmDao.create(firstFilm);
        Film createdSecondFilm = filmDao.create(secondFilm);
        likesDao.addLike(createdFirstFilm.getId(), createdFirstUser.getId());
        likesDao.addLike(createdFirstFilm.getId(), createdSecondUser.getId());
        likesDao.addLike(createdSecondFilm.getId(), createdSecondUser.getId());

        List<Film> actualResult = filmDao.getTopNPopular(2);
        Film mostPopular = actualResult.get(0);
        assertNotNull(actualResult);
        assertAll(
                () -> assertEquals(expectedResultSize, actualResult.size()),
                () -> assertNotNull(createdFirstFilm.getId()),
                () -> assertEquals(firstFilm.getName(), mostPopular.getName()),
                () -> assertEquals(firstFilm.getDescription(), mostPopular.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), mostPopular.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), mostPopular.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), mostPopular.getMpa())
        );
    }
}