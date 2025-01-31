package net.slimediamond.telegram;

import net.slimediamond.telegram.event.MessageReceivedEvent;
import net.slimediamond.telegram.event.UserAddedToChatEvent;
import net.slimediamond.telegram.event.UserRemovedFromChatEvent;

// TODO: make this abstract
public interface Listener {
    default void onMessage(MessageReceivedEvent event) throws Exception {};
    default void onUserJoinChat(UserAddedToChatEvent event) throws Exception {};
    default void onUserLeaveChat(UserRemovedFromChatEvent event) throws Exception {};
}
