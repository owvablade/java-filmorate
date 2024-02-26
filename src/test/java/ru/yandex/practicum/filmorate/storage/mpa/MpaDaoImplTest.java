package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private Mpa mpaPg13;
    private MpaDaoImpl mpaDao;

    @BeforeEach
    void setUp() {
        mpaPg13 = new Mpa(3, "PG-13");
        mpaDao = new MpaDaoImpl(jdbcTemplate);
    }

    @Test
    void getMpaWithValidId() {
        Mpa actualMpa = mpaDao.get(3).orElse(null);

        assertAll(
                () -> assertEquals(mpaPg13.getId(), actualMpa == null ? null : actualMpa.getId()),
                () -> assertEquals(mpaPg13.getName(), actualMpa == null ? null :
                        new String(actualMpa.getName().getBytes(), StandardCharsets.UTF_8))
        );
    }

    @Test
    void getMpaWithInvalidId() {
        Mpa firstActualMpa = mpaDao.get(Integer.MIN_VALUE).orElse(null);
        Mpa secondActualMpa = mpaDao.get(Integer.MAX_VALUE).orElse(null);

        assertAll(
                () -> assertNull(firstActualMpa),
                () -> assertNull(secondActualMpa)
        );
    }

    @Test
    void getAllMpa() {
        int expectedSize = 5;

        List<Mpa> actualMpaRatings = mpaDao.getAll();
        Mpa actualThirdMpa = null;
        try {
            actualThirdMpa = actualMpaRatings.get(2);
        } catch (IndexOutOfBoundsException ignored) {
        }

        Mpa finalActualThirdMpa = actualThirdMpa;
        assertAll(
                () -> assertEquals(expectedSize, actualMpaRatings.size()),
                () -> assertEquals(mpaPg13.getId(), finalActualThirdMpa == null ? null : finalActualThirdMpa.getId()),
                () -> assertEquals(mpaPg13.getName(), finalActualThirdMpa == null ?
                        null : new String(finalActualThirdMpa.getName().getBytes(), StandardCharsets.UTF_8))
        );
    }
}