package net.slimediamond.atom.discord.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;
import org.sk.PrettyTable;

public class EndpointListCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(context.getChannel().getId())));

        if (chat == null) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find a bridged chat for this channel. Are you sure it's bridged?"));
            return;
        }

        PrettyTable table = new PrettyTable("Endpoint ID", "Type", "Unique Identifier", "Enabled");
        chat.getEndpoints().forEach(endpoint -> {
            table.addRow(String.valueOf(endpoint.getId()), endpoint.getType(), endpoint.getUniqueIdentifier(), String.valueOf(endpoint.isEnabled()));
        });

        context.reply("**" + chat.getEndpoints().size() + "** endpoints in this chat:\n```\n" + table + "```");
    }
}
