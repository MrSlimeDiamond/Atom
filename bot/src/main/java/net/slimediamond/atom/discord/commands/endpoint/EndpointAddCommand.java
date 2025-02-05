package net.slimediamond.atom.discord.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.chatbridge.discord.DiscordBridgeEndpoint;
import net.slimediamond.atom.chatbridge.irc.IRCBridgeEndpoint;
import net.slimediamond.atom.chatbridge.telegram.TelegramBridgeEndpoint;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.telegram.Telegram;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.Map;

public class EndpointAddCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        // types right now: discord, irc, telegram
        // endpoint add <type> <uniqueIdentifier> [chatId]

        if (context.getArgs().length > 3) {
            context.sendUsage();
            return;
        }

        String type = context.getArgs()[0];
        String unique = context.getArgs()[1];

        // FIXME
        BridgedChat chat = null;

        // chatId was specified
        if (context.getArgs().length == 3) {
            System.out.println(context.getArgs()[2]);
            try {
                int id = Integer.parseInt(context.getArgs()[2]);
                chat = BridgeStore.getChats().get(id);
            } catch (NumberFormatException ignored) {
                // go by name
                Map.Entry<Integer, BridgedChat> entry = BridgeStore.getChats().entrySet().stream().filter(e -> e.getValue().getName().equals(context.getArgs()[2])).findFirst().orElse(null);
                if (entry.getValue() != null) {
                    chat = entry.getValue();
                }
            }

            if (chat == null) {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that chat."));
                return;
            }

        } else {
            chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(context.getChannel().getId())));

            if (chat == null) {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("It doesn't look like this channel is bridged. Try to add with an ID or name instead."));
                return;
            }
        }

        int endpointId = database.insertBridgeEndpoint(chat.getId(), type, unique, true);

        if (endpointId == -1) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("An error occurred when inserting the endpoint."));
            return;
        }

        switch (type.toLowerCase()) {
            case "discord" ->
                    chat.addEndpoint((new DiscordBridgeEndpoint(context.getJDA().getTextChannelById(unique), unique, endpointId, true)));
            case "irc" ->
                    chat.addEndpoint(new IRCBridgeEndpoint(IRC.client.getChannel(unique).get(), unique, endpointId, true));
            case "telegram" ->
                    chat.addEndpoint(new TelegramBridgeEndpoint(Telegram.getClient().getChatById(Long.parseLong(unique)), endpointId, true));
            default -> {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Can not find that type! (valid types: discord, irc, telegram)"));
                return;
            }
        }

        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added endpoint!"));
    }
}
