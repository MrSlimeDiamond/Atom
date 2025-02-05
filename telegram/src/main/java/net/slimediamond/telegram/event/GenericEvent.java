package net.slimediamond.telegram.event;

import net.slimediamond.telegram.TelegramClient;

public abstract class GenericEvent {
    protected TelegramClient client;
    public GenericEvent(TelegramClient client) {
        this.client = client;
    }
    public TelegramClient getClient() {
        return client;
    }
}
