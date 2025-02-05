package net.slimediamond.atom.discord.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.concurrent.atomic.AtomicReference;

public class EndpointPipeCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        AtomicReference<Boolean> status = new AtomicReference<>();
        context.getArguments().get("status").ifPresent(arg -> status.set(arg.getAsBoolean()));

        BridgeEndpoint endpoint = BridgeStore.getChats().values().stream()
                .flatMap(chat -> chat.getEndpoints().stream()) // Flatten the list of endpoints
                .filter(ep -> ep.getUniqueIdentifier().equals(context.getChannel().getId()))
                .findFirst()
                .orElse(null); // Returns null if no match is found

        endpoint.setEnabled(status.get());
        database.setEndpointPipeStatus(endpoint.getId(), status.get());

        if (status.get()) {
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Bridge pipe is now enabled."));
        } else {
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Bridge pipe is now disabled."));
        }
    }
}
