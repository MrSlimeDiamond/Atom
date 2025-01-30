package net.slimediamond.telegram;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class GenericChat implements Chat {
    private TelegramClient client;
    private String name;
    private long id;
    private ChatType type;
    private JsonNode base;

    public GenericChat(TelegramClient client, String name, long id, ChatType type) {
        this.client = client;
        this.id = id;
        this.name = name;
        this.type = type;
        this.base = base;
    }

    @Override
    public void sendMessage(String message) {
        try {
            client.sendMessage(id, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public ChatType getType() {
        return this.type;
    }

    @Override
    public File getPhoto() {
        // lazy
        try {
            return client.getChatPhoto(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
