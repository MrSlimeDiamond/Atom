package net.slimediamond.telegram;

public class MessageSender {
    private String firstName;
    private String lastName;
    private String username;
    private long id;

    public MessageSender(String firstName, String lastName, String username, long id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }
}
