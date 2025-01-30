package net.slimediamond.telegram;

// TODO
public interface Chat {
    void sendMessage(String message);
    String getName();
    long getId();
    ChatType getType();
    File getPhoto();
}
