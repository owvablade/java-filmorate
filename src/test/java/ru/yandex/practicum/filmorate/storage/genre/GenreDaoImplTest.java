package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private Genre thriller;
    private GenreDaoImpl genreDao;

    @BeforeEach
    void setUp() {
        thriller = new Genre(4, "Триллер");
        genreDao = new GenreDaoImpl(jdbcTemplate);
    }

    @Test
    void getGenreWithValidId() {
        Genre actualGenre = genreDao.get(4).orElse(null);

        assertAll(
                () -> assertEquals(thriller.getId(), actualGenre == null ? null : actualGenre.getId()),
                () -> assertEquals(thriller.getName(), actualGenre == null ? null :
                        new String(actualGenre.getName().getBytes(), StandardCharsets.UTF_8))
        );
    }

    @Test
    void getGenreWithInvalidId() {
        Genre firstActualGenre = genreDao.get(Integer.MIN_VALUE).orElse(null);
        Genre secondActualGenre = genreDao.get(Integer.MAX_VALUE).orElse(null);

        assertAll(
                () -> assertNull(firstActualGenre),
                () -> assertNull(secondActualGenre)
        );
    }

    @Test
    void getAllGenres() {
        int expectedSize = 6;

        List<Genre> actualGenres = genreDao.getAll();
        Genre actualFourthGenre = null;
        try {
            actualFourthGenre = actualGenres.get(3);
        } catch (IndexOutOfBoundsException ignored) {
        }

        Genre finalActualFourthGenre = actualFourthGenre;
        assertAll(
                () -> assertEquals(expectedSize, actualGenres.size()),
                () -> assertEquals(thriller.getId(), finalActualFourthGenre == null ?
                        null : finalActualFourthGenre.getId()),
                () -> assertEquals(thriller.getName(), finalActualFourthGenre == null ?
                        null : new String(finalActualFourthGenre.getName().getBytes(), StandardCharsets.UTF_8))
        );
    }
}