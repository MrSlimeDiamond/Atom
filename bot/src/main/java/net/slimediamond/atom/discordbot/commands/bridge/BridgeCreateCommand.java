package net.slimediamond.atom.discordbot.commands.bridge;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.concurrent.atomic.AtomicReference;

public class BridgeCreateCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        AtomicReference<String> name = new AtomicReference<>();
        context.getArguments().get("name").ifPresentOrElse(arg -> name.set(arg.getAsString()), () -> name.set(context.getGuild().getName()));

        int chatId = database.insertBridgedRoom(name.get());

        // failed to get data, must have failed to insert
        if (chatId == -1) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Failed to insert chat into the database (ChatID return value was -1)"));
        } else {
            // isEnabled to true because we just made it
            BridgeStore.getChats().put(chatId, new BridgedChat(true, chatId, name.get()));

            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Chat was created with an ID of " + chatId));
        }
    }
}
