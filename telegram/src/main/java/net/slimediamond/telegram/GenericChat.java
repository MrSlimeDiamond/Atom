package net.slimediamond.telegram;

import java.io.IOException;

public class GenericChat implements Chat {
    private TelegramClient client;
    private long id;

    public GenericChat(TelegramClient client, long id) {
        this.client = client;
        this.id = id;
    }

    @Override
    public void sendMessage(String message) {
        try {
            client.sendMessage(id, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
