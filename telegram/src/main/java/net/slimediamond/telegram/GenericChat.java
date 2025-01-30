package net.slimediamond.telegram;

import java.io.IOException;

public class GenericChat implements Chat {
    private TelegramClient client;
    private String name;
    private long id;
    private ChatType type;

    public GenericChat(TelegramClient client, String name, long id, ChatType type) {
        this.client = client;
        this.id = id;
        this.type = type;
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
}
