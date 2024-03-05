package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDaoImpl userDao;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(jdbcTemplate);
        firstUser = new User()
                .setEmail("tempArtem@yandex.ru")
                .setLogin("artem123")
                .setName("Artem")
                .setBirthday(LocalDate.of(2001, 1, 31));
        secondUser = new User()
                .setEmail("tempMisha@gmail.com")
                .setLogin("misha321")
                .setBirthday(LocalDate.of(2001, 5, 16));
    }

    @Test
    void createValidUser() {
        User createdUser = userDao.create(firstUser);

        assertNotNull(createdUser);
        assertAll(
                () -> assertNotNull(createdUser.getId()),
                () -> assertEquals(createdUser.getEmail(), firstUser.getEmail()),
                () -> assertEquals(createdUser.getLogin(), firstUser.getLogin()),
                () -> assertEquals(createdUser.getName(), firstUser.getName()),
                () -> assertEquals(createdUser.getBirthday(), firstUser.getBirthday())
        );
    }

    @Test
    void readUserWithValidId() {
        long expectedId = userDao.create(firstUser).getId();

        User actualUser = userDao.read(expectedId).orElse(null);

        assertNotNull(actualUser);
        assertAll(
                () -> assertEquals(expectedId, actualUser.getId()),
                () -> assertEquals(firstUser.getEmail(), actualUser.getEmail()),
                () -> assertEquals(firstUser.getLogin(), actualUser.getLogin()),
                () -> assertEquals(firstUser.getName(), actualUser.getName()),
                () -> assertEquals(firstUser.getBirthday(), actualUser.getBirthday())
        );
    }

    @Test
    void readUserWithInvalidId() {
        userDao.create(firstUser);

        assertNull(userDao.read(Long.MIN_VALUE).orElse(null));
        assertNull(userDao.read(Long.MAX_VALUE).orElse(null));
    }

    @Test
    void updateValidUser() {
        long expectedId = userDao.create(firstUser).getId();

        secondUser.setId(expectedId);
        User actualUser = userDao.update(secondUser);

        assertNotNull(actualUser);
        assertAll(
                () -> assertEquals(expectedId, actualUser.getId()),
                () -> assertEquals(secondUser.getEmail(), actualUser.getEmail()),
                () -> assertEquals(secondUser.getLogin(), actualUser.getLogin()),
                () -> assertEquals(secondUser.getName(), actualUser.getName()),
                () -> assertEquals(secondUser.getBirthday(), actualUser.getBirthday())
        );
    }

    @Test
    void updateInvalidUser() {
        userDao.create(firstUser);

        firstUser.setId(Long.MIN_VALUE);
        assertNull(userDao.update(firstUser));

        firstUser.setId(Long.MAX_VALUE);
        assertNull(userDao.update(firstUser));
    }

    @Test
    void deleteValidUser() {
        User userToBeDeleted = userDao.create(firstUser);

        boolean deletedUser = userDao.delete(userToBeDeleted.getId());

        assertAll(
                () -> assertTrue(deletedUser),
                () -> assertNull(userDao.read(userToBeDeleted.getId()).orElse(null))
        );
    }

    @Test
    void deleteInvalidUser() {
        userDao.create(firstUser);

        assertAll(
                () -> assertFalse(userDao.delete(Long.MIN_VALUE)),
                () -> assertFalse(userDao.delete(Long.MAX_VALUE))
        );
    }

    @Test
    void getAllUsers() {
        assertEquals(0, userDao.getAll().size());

        List<User> expectedResult = new ArrayList<>();
        expectedResult.add(userDao.create(firstUser));
        expectedResult.add(userDao.create(secondUser));

        assertEquals(expectedResult.size(), userDao.getAll().size());
    }
}