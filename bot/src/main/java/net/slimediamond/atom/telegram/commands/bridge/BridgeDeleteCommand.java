package net.slimediamond.atom.telegram.commands.bridge;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;

import java.sql.SQLException;

public class BridgeDeleteCommand implements TelegramCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(TelegramCommandContext context) throws Exception {
        BridgedChat chat;
        if (context.getArgs().length == 0) {
            try {
                chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(String.valueOf(context.getChat().getId()))));

                if (chat == null) {
                    context.reply("It doesn't look like this channel is bridged. Try to delete with an ID or name instead.");
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            // get via name or id
            try {
                int id = Integer.parseInt(context.getArgs()[0]);
                chat = BridgeStore.getChats().get(database.getBridgedChatID(id));
            } catch (NumberFormatException ignored) {
                // go by name
                chat = BridgeStore.getChats().entrySet().stream().filter(e -> e.getValue().getName().equals(context.getArgs()[0])).findFirst().orElse(null).getValue();
            }
        }

        if (chat == null) {
            context.reply("Could not find a chat with the name or id.");
            return;
        }

        // the meat and potatoes
        int chatId = chat.getId();

        // nuke it
        database.removeEndpointsInChat(chatId);
        database.removeBridgedRoom(chatId);

        context.reply("Removed data for this chat room, including any and all endpoints.");
    }
}
