package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDaoImpl filmDao;
    private Film firstFilm;
    private Film secondFilm;

    @BeforeEach
    void setUp() {
        filmDao = new FilmDaoImpl(jdbcTemplate);
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
    void createValidFilms() {
        int expectedFirstFilmGenresCount = 0;
        int expectedSecondFilmGenresCount = 1;

        Film createdFirstFilm = filmDao.create(firstFilm);
        Film createdSecondFilm = filmDao.create(secondFilm);

        assertNotNull(createdFirstFilm);
        assertNotNull(createdSecondFilm);
        assertAll(
                () -> assertNotNull(createdFirstFilm.getId()),
                () -> assertEquals(firstFilm.getName(), createdFirstFilm.getName()),
                () -> assertEquals(firstFilm.getDescription(), createdFirstFilm.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), createdFirstFilm.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), createdFirstFilm.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), createdFirstFilm.getMpa()),
                () -> assertEquals(expectedFirstFilmGenresCount, createdFirstFilm.getGenres().size()),
                () -> assertNotNull(createdSecondFilm.getId()),
                () -> assertEquals(secondFilm.getName(), createdSecondFilm.getName()),
                () -> assertEquals(secondFilm.getDescription(), createdSecondFilm.getDescription()),
                () -> assertEquals(secondFilm.getReleaseDate(), createdSecondFilm.getReleaseDate()),
                () -> assertEquals(secondFilm.getDuration(), createdSecondFilm.getDuration()),
                () -> assertEquals(secondFilm.getMpa(), createdSecondFilm.getMpa()),
                () -> assertEquals(expectedSecondFilmGenresCount, createdSecondFilm.getGenres().size())
        );
    }

    @Test
    void readFilmWithValidIdWithNoGenres() {
        long expectedId = filmDao.create(firstFilm).getId();
        int expectedFilmGenresCount = 0;

        Film actualFilm = filmDao.read(expectedId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertNotNull(actualFilm.getId()),
                () -> assertEquals(firstFilm.getName(), actualFilm.getName()),
                () -> assertEquals(firstFilm.getDescription(), actualFilm.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), actualFilm.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), actualFilm.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size())
        );
    }

    @Test
    void readFilmWithValidIdWithGenres() {
        long expectedId = filmDao.create(secondFilm).getId();
        int expectedFilmGenresCount = 1;

        Film actualFilm = filmDao.read(expectedId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertNotNull(actualFilm.getId()),
                () -> assertEquals(secondFilm.getName(), actualFilm.getName()),
                () -> assertEquals(secondFilm.getDescription(), actualFilm.getDescription()),
                () -> assertEquals(secondFilm.getReleaseDate(), actualFilm.getReleaseDate()),
                () -> assertEquals(secondFilm.getDuration(), actualFilm.getDuration()),
                () -> assertEquals(secondFilm.getMpa(), actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size())
        );
    }

    @Test
    void readFilmWithInvalidId() {
        filmDao.create(firstFilm);

        assertNull(filmDao.read(Long.MIN_VALUE).orElse(null));
        assertNull(filmDao.read(Long.MAX_VALUE).orElse(null));
    }

    @Test
    void updateFilmWithNoGenre() {
        final String expectedName = "Updated Film 1";
        final String expectedDescription = "updated description";
        final LocalDate expectedReleaseDate = LocalDate.of(2020, 1, 1);
        final int expectedDuration = 300;
        final Mpa expectedMpa = new Mpa(5, "NC-17");
        final int expectedFilmGenresCount = 0;
        long filmToUpdateId = filmDao.create(firstFilm).getId();
        firstFilm.setId(filmToUpdateId)
                .setName(expectedName)
                .setDescription(expectedDescription)
                .setReleaseDate(expectedReleaseDate)
                .setDuration(expectedDuration)
                .setMpa(expectedMpa);
        filmDao.update(firstFilm);
        Film actualFilm = filmDao.read(filmToUpdateId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertEquals(filmToUpdateId, actualFilm.getId()),
                () -> assertEquals(expectedName, actualFilm.getName()),
                () -> assertEquals(expectedDescription, actualFilm.getDescription()),
                () -> assertEquals(expectedReleaseDate, actualFilm.getReleaseDate()),
                () -> assertEquals(expectedDuration, actualFilm.getDuration()),
                () -> assertEquals(expectedMpa, actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size())
        );
    }

    @Test
    void updateFilmWithZeroGenreToOne() {
        final int expectedFilmGenresCount = 1;

        long filmToUpdateId = filmDao.create(firstFilm).getId();
        secondFilm.setId(filmToUpdateId);
        filmDao.update(secondFilm);
        Film actualFilm = filmDao.read(filmToUpdateId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertNotNull(actualFilm.getId()),
                () -> assertEquals(secondFilm.getName(), actualFilm.getName()),
                () -> assertEquals(secondFilm.getDescription(), actualFilm.getDescription()),
                () -> assertEquals(secondFilm.getReleaseDate(), actualFilm.getReleaseDate()),
                () -> assertEquals(secondFilm.getDuration(), actualFilm.getDuration()),
                () -> assertEquals(secondFilm.getMpa(), actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size())
        );
    }

    @Test
    void updateFilmWithOneGenreToZero() {
        final int expectedFilmGenresCount = 0;

        long filmToUpdateId = filmDao.create(secondFilm).getId();
        firstFilm.setId(filmToUpdateId);
        filmDao.update(firstFilm);
        Film actualFilm = filmDao.read(filmToUpdateId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertNotNull(actualFilm.getId()),
                () -> assertEquals(firstFilm.getName(), actualFilm.getName()),
                () -> assertEquals(firstFilm.getDescription(), actualFilm.getDescription()),
                () -> assertEquals(firstFilm.getReleaseDate(), actualFilm.getReleaseDate()),
                () -> assertEquals(firstFilm.getDuration(), actualFilm.getDuration()),
                () -> assertEquals(firstFilm.getMpa(), actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size())
        );
    }

    @Test
    void updateFilmWithDuplicateGenre() {
        final int expectedFilmGenresCount = 2;
        final Genre firstExpectedGenre = new Genre(4, "Триллер");
        final Genre secondExpectedGenre = new Genre(6, "Боевик");

        long filmToUpdateId = filmDao.create(secondFilm).getId();
        secondFilm.setId(filmToUpdateId);
        secondFilm.addGenre(firstExpectedGenre);
        secondFilm.addGenre(secondExpectedGenre);
        filmDao.update(secondFilm);
        Film actualFilm = filmDao.read(filmToUpdateId).orElse(null);

        assertNotNull(actualFilm);
        assertAll(
                () -> assertNotNull(actualFilm.getId()),
                () -> assertEquals(secondFilm.getName(), actualFilm.getName()),
                () -> assertEquals(secondFilm.getDescription(), actualFilm.getDescription()),
                () -> assertEquals(secondFilm.getReleaseDate(), actualFilm.getReleaseDate()),
                () -> assertEquals(secondFilm.getDuration(), actualFilm.getDuration()),
                () -> assertEquals(secondFilm.getMpa(), actualFilm.getMpa()),
                () -> assertEquals(expectedFilmGenresCount, actualFilm.getGenres().size()),
                () -> assertEquals(firstExpectedGenre.getId(), actualFilm.getGenres().get(0).getId()),
                () -> assertEquals(secondExpectedGenre.getId(), actualFilm.getGenres().get(1).getId()),
                () -> assertEquals(firstExpectedGenre.getName(),
                        new String(actualFilm.getGenres().get(0).getName().getBytes(), StandardCharsets.UTF_8)),
                () -> assertEquals(secondExpectedGenre.getName(),
                        new String(actualFilm.getGenres().get(1).getName().getBytes(), StandardCharsets.UTF_8))
        );
    }

    @Test
    void deleteValidFilm() {
        Film filmToBeDeletedWithoutGenre = filmDao.create(firstFilm);
        Film filmToBeDeletedWithGenre = filmDao.create(secondFilm);

        boolean deletedFilmWithoutGenre = filmDao.delete(filmToBeDeletedWithoutGenre.getId());
        boolean deletedFilmWithGenre = filmDao.delete(filmToBeDeletedWithGenre.getId());

        assertAll(
                () -> assertTrue(deletedFilmWithoutGenre),
                () -> assertTrue(deletedFilmWithGenre),
                () -> assertNull(filmDao.read(filmToBeDeletedWithoutGenre.getId()).orElse(null)),
                () -> assertNull(filmDao.read(filmToBeDeletedWithGenre.getId()).orElse(null))
        );
    }

    @Test
    void deleteInvalidFilm() {
        filmDao.create(firstFilm);
        assertAll(
                () -> assertFalse(filmDao.delete(Long.MIN_VALUE)),
                () -> assertFalse(filmDao.delete(Long.MAX_VALUE))
        );
    }

    @Test
    void getAll() {
        assertEquals(0, filmDao.getAll().size());

        List<Film> expectedResult = new ArrayList<>();
        expectedResult.add(filmDao.create(firstFilm));
        expectedResult.add(filmDao.create(secondFilm));

        assertEquals(expectedResult.size(), filmDao.getAll().size());
    }
}