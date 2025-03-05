package net.slimediamond.telegram.entity;

import net.slimediamond.telegram.TelegramClient;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private long id;
    private TelegramClient client;

    public User(String firstName, String lastName, String username, long id, TelegramClient client) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.id = id;
        this.client = client;
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

    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }

    public long getId() {
        return id;
    }

    public List<File> getProfilePhotos() {
        try {
            return client.getUserProfilePhotos(this.id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public File getProfilePhoto() {
        try {
            return client.getProfilePhoto(this.id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
