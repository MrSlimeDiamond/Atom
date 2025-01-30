package net.slimediamond.atom.chatbridge;

public class UserEvent {
    private EventType eventType;
    private String username;

    public UserEvent(EventType eventType, String username) {
        this.eventType = eventType;
        this.username = username;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getUsername() {
        return username;
    }
}
