package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDaoImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDaoImpl userDao;
    private FriendDaoImpl friendDao;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(jdbcTemplate);
        friendDao = new FriendDaoImpl(jdbcTemplate);
        firstUser = new User()
                .setEmail("tempArtem@yandex.ru")
                .setLogin("artem123")
                .setName("Artem")
                .setBirthday(LocalDate.of(2001, 1, 31));
        secondUser = new User()
                .setEmail("tempMisha@gmail.com")
                .setLogin("misha321")
                .setBirthday(LocalDate.of(2001, 5, 16));
        thirdUser = new User()
                .setEmail("somemail@mail.ru")
                .setLogin("ilovetesting")
                .setName("ireallylovetesting")
                .setBirthday(LocalDate.of(2010, 12, 10));
    }

    @Test
    void addFriend() {
        final int expectedFriendSize = 1;

        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        friendDao.addFriend(createdFirstUser.getId(), createdSecondUser.getId());

        assertEquals(expectedFriendSize, friendDao.getFriends(createdFirstUser.getId()).size());
    }

    @Test
    void addNonexistentFriend() {
        assertThrows(DataIntegrityViolationException.class, () -> friendDao.addFriend(Long.MIN_VALUE, Long.MAX_VALUE));

        User createdFirstUser = userDao.create(firstUser);
        assertAll(
                () -> assertThrows(DataIntegrityViolationException.class,
                        () -> friendDao.addFriend(createdFirstUser.getId(), Long.MAX_VALUE)),
                () -> assertNull(friendDao.getFriends(createdFirstUser.getId()))
        );
    }

    @Test
    void removeFriend() {
        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        friendDao.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        friendDao.removeFriend(createdFirstUser.getId(), createdSecondUser.getId());

        assertNull(friendDao.getFriends(createdFirstUser.getId()));
    }

    @Test
    void removeNonexistentFriend() {
        assertDoesNotThrow(() -> friendDao.removeFriend(Long.MIN_VALUE, Long.MAX_VALUE));

        User createdFirstUser = userDao.create(firstUser);
        assertAll(
                () -> assertDoesNotThrow(() -> friendDao.removeFriend(createdFirstUser.getId(), Long.MAX_VALUE)),
                () -> assertNull(friendDao.getFriends(createdFirstUser.getId()))
        );
    }

    @Test
    void getFriends() {
        assertAll(
                () -> assertNull(friendDao.getFriends(Long.MIN_VALUE)),
                () -> assertNull(friendDao.getFriends(Long.MAX_VALUE))
        );

        final int expectedFriendSize = 2;
        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        User createdThirdUser = userDao.create(thirdUser);
        friendDao.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        friendDao.addFriend(createdFirstUser.getId(), createdThirdUser.getId());
        List<User> actualResult = friendDao.getFriends(createdFirstUser.getId());
        User actualSecondUser = actualResult.get(0);
        User actualThirdUser = actualResult.get(1);

        assertAll(
                () -> assertEquals(expectedFriendSize, actualResult.size()),
                () -> assertNotNull(actualSecondUser.getId()),
                () -> assertEquals(secondUser.getEmail(), actualSecondUser.getEmail()),
                () -> assertEquals(secondUser.getLogin(), actualSecondUser.getLogin()),
                () -> assertEquals(secondUser.getName(), actualSecondUser.getName()),
                () -> assertEquals(secondUser.getBirthday(), actualSecondUser.getBirthday()),
                () -> assertNotNull(actualThirdUser.getId()),
                () -> assertEquals(thirdUser.getEmail(), actualThirdUser.getEmail()),
                () -> assertEquals(thirdUser.getLogin(), actualThirdUser.getLogin()),
                () -> assertEquals(thirdUser.getName(), actualThirdUser.getName()),
                () -> assertEquals(thirdUser.getBirthday(), actualThirdUser.getBirthday())
        );
    }

    @Test
    void getCommonFriends() {
        assertEquals(0, friendDao.getCommonFriends(Long.MIN_VALUE, Long.MAX_VALUE).size());

        final int expectedFriendSize = 1;
        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        User createdThirdUser = userDao.create(thirdUser);
        friendDao.addFriend(createdFirstUser.getId(), createdThirdUser.getId());
        friendDao.addFriend(createdSecondUser.getId(), createdThirdUser.getId());
        List<User> actualResult = friendDao.getCommonFriends(createdFirstUser.getId(), createdSecondUser.getId());
        User actualCommonFriend = actualResult.get(0);

        assertAll(
                () -> assertEquals(expectedFriendSize, actualResult.size()),
                () -> assertNotNull(actualCommonFriend.getId()),
                () -> assertEquals(thirdUser.getEmail(), actualCommonFriend.getEmail()),
                () -> assertEquals(thirdUser.getLogin(), actualCommonFriend.getLogin()),
                () -> assertEquals(thirdUser.getName(), actualCommonFriend.getName()),
                () -> assertEquals(thirdUser.getBirthday(), actualCommonFriend.getBirthday())
        );
    }

    @Test
    void getCommonFriendsWithFriend() {
        assertEquals(0, friendDao.getCommonFriends(Long.MIN_VALUE, Long.MAX_VALUE).size());

        final int expectedFriendSize = 1;
        User createdFirstUser = userDao.create(firstUser);
        User createdSecondUser = userDao.create(secondUser);
        User createdThirdUser = userDao.create(thirdUser);
        friendDao.addFriend(createdFirstUser.getId(), createdThirdUser.getId());
        friendDao.addFriend(createdSecondUser.getId(), createdThirdUser.getId());
        friendDao.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        friendDao.addFriend(createdSecondUser.getId(), createdFirstUser.getId());
        List<User> actualResult = friendDao.getCommonFriends(createdFirstUser.getId(), createdSecondUser.getId());
        User actualCommonFriend = actualResult.get(0);

        assertAll(
                () -> assertEquals(expectedFriendSize, actualResult.size()),
                () -> assertNotNull(actualCommonFriend.getId()),
                () -> assertEquals(thirdUser.getEmail(), actualCommonFriend.getEmail()),
                () -> assertEquals(thirdUser.getLogin(), actualCommonFriend.getLogin()),
                () -> assertEquals(thirdUser.getName(), actualCommonFriend.getName()),
                () -> assertEquals(thirdUser.getBirthday(), actualCommonFriend.getBirthday())
        );
    }
}