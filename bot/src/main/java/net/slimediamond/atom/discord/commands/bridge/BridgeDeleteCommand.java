package net.slimediamond.atom.discord.commands.bridge;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class BridgeDeleteCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        BridgedChat chat;
        if (context.getArgs().length == 0) {
            try {
                chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(context.getChannel().getId())));

                if (chat == null) {
                    context.replyEmbeds(EmbedUtil.expandedErrorEmbed("It doesn't look like this channel is bridged. Try to delete with an ID or name instead."));
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
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find a chat with the name or id."));
            return;
        }

        // the meat and potatoes
        int chatId = chat.getId();

        // nuke it
        database.removeEndpointsInChat(chatId);
        database.removeBridgedRoom(chatId);

        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed data for this chat room, including any and all endpoints."));
    }
}
