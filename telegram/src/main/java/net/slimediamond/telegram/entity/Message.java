package net.slimediamond.telegram.entity;

public class Message {
    File photo;
    private final String content;
    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }
}
