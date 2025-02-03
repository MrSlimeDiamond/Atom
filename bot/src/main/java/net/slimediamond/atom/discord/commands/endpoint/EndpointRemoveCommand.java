package net.slimediamond.atom.discord.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

// FIXME
public class EndpointRemoveCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        BridgedChat chat = null;
        BridgeEndpoint endpoint = null;
        int endpointId;

        // Remove the current endpoint
        try {
            // endpoint remove
            if (context.getArgs().length == 0) {
                endpointId = database.getBridgedEndpointId(context.getChannel().getId());

                // endpoint remove <uniqueIdentifier | id>
            } else {

                // Look up as a uniqueIdentifier first, then later try ID if nothing is found.
                endpointId = database.getBridgedEndpointId(context.getArgs()[0]);
                if (endpointId == -1) {
                    // okay, try by parseInt(uniqueIdentifier)
                    try {
                        endpoint = chat.getEndpoints().stream().filter(ep -> ep.getId() == Integer.parseInt(context.getArgs()[0])).findFirst().orElse(null);
                    } catch (NumberFormatException e) {
                        context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find an endpoint with that identifier."));
                    }
                } else {
                    endpoint = chat.getEndpoints().get(endpointId);
                }
            }

            if (endpointId != -1) {
                chat = BridgeStore.getChats().get(database.getBridgedChatID(endpointId));
                endpoint = chat.getEndpoints().stream().filter(ep -> ep.getId() == endpointId).findFirst().orElse(null);
            }

            if (chat == null) {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("It doesn't look like this channel is bridged. Try to delete with an ID or name instead."));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (endpoint == null || endpoint.getId() == -1) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find an endpoint with that identifier."));
        }

        database.removeBridgedEndpoint(endpoint.getId());
        chat.getEndpoints().remove(endpoint);

        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("A " + endpoint.getType() + " bridge with an id of " + endpoint.getId() + " has been removed."));
    }
}
