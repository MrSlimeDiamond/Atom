package net.slimediamond.atom.telegram.commands.bridge;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;


public class BridgeCreateCommand implements TelegramCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(TelegramCommandContext context) throws Exception {
        String name;

        if (context.getArgs().length == 0) {
            name = context.getChat().getName();
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
