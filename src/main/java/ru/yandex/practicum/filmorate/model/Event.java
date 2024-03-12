package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Event {
    @JsonProperty("timestamp")
    private long timeAdd;
    private long userId;
    private EventType eventType;
    @JsonProperty("operation")
    private EventOperation eventOperation;
    @JsonProperty("eventId")
    private long id;
    private long entityId;

    public Event(long timeAdd, long userId, EventType eventType, EventOperation eventOperation, long id, long entityId) {
        this.timeAdd = timeAdd;
        this.userId = userId;
        this.eventType = eventType;
        this.eventOperation = eventOperation;
        this.id = id;
        this.entityId = entityId;
    }
}
