package net.slimediamond.atom.irc.commands.bridge;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;


public class BridgeCreateCommand implements IRCCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(IRCCommandContext context) throws Exception {
        String name;

        if (context.getArgs().length == 0) {
            name = context.getChannelName().replace("#", "");
        } else {
            name = context.getArgs()[0];
        }

        int chatId = database.insertBridgedRoom(name);

        // failed to get data, must have failed to insert
        if (chatId == -1) {
            context.reply("Failed to insert chat into the database (ChatID return value was -1)");
        } else {
            // isEnabled to true because we just made it
            BridgeStore.getChats().put(chatId, new BridgedChat(true, chatId, name));

            context.reply("Chat was created with an ID of " + chatId);
        }
    }
}
