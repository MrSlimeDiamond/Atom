package net.slimediamond.telegram;

import net.slimediamond.telegram.events.MessageReceivedEvent;

import java.sql.SQLException;

public interface Listener {
    default void onMessage(MessageReceivedEvent event) throws Exception {};
}
