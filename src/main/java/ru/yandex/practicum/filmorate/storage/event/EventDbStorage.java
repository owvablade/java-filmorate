package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.event.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;

    public EventDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO USER_EVENT (TIME_ADD, USER_ID, EVENT_TYPE_ID, EVENT_OPERATION_ID, ENTITY_ID) " +
                " VALUES(? , ? , ? , ? , ?)";
        jdbcTemplate.update(sql,
                event.getTimeAdd(),
                event.getUserId(),
                event.getEventType().getId(),
                event.getEventOperation().getId(),
                event.getEntityId()
        );

    }

    @Override
    public List<Event> findUserEvent(Long userId) {
        userStorage.read(userId);
        String sql = "SELECT UE.EVENT_ID, UE.TIME_ADD, UE.USER_ID,UE.ENTITY_ID, ET.NAME AS EVENT_TYPE_NAME, EO.NAME AS EVENT_OPERATION_NAME " +
                "FROM USER_EVENT AS UE LEFT JOIN EVENT_TYPE AS ET ON UE.EVENT_TYPE_ID = ET.EVENT_TYPE_ID " +
                "LEFT JOIN EVENT_OPERATION AS EO " +
                "ON UE.EVENT_OPERATION_ID = EO.EVENT_OPERATION_ID " +
                "WHERE UE.USER_ID=?";
        return jdbcTemplate.query(sql, this::makeEvent, userId);
    }

    private Event makeEvent(ResultSet rs, int i) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("event_id"));
        event.setTimeAdd(rs.getLong("time_add"));
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type_name")));
        event.setEventOperation(EventOperation.valueOf(rs.getString("event_operation_name")));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}
