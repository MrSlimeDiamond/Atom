package net.slimediamond.atom.telegram.event;

import net.slimediamond.telegram.Listener;
import net.slimediamond.telegram.events.MessageReceivedEvent;

public class TelegramMessageListener implements Listener {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        if (event.getText().toLowerCase().startsWith("ping")) {
            event.getChat().sendMessage("pong");
        }
    }
}
