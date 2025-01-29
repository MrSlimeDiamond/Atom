package net.slimediamond.telegram;

import net.slimediamond.telegram.events.MessageReceivedEvent;

public interface Listener {
    default void onMessage(MessageReceivedEvent event) {};
}
