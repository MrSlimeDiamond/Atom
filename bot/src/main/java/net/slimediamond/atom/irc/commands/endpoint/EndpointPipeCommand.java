package net.slimediamond.atom.irc.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.Arrays;

public class EndpointPipeCommand implements IRCCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(IRCCommandContext context) throws Exception {
        boolean status = false;

        String[] yesOptions = {"true", "on", "enabled"};
        if (Arrays.asList(yesOptions).contains(context.getArgs()[0])) {
            status = true;
        }

        BridgeEndpoint endpoint = BridgeStore.getChats().values().stream()
                .flatMap(chat -> chat.getEndpoints().stream()) // Flatten the list of endpoints
                .filter(ep -> ep.getUniqueIdentifier().equals(context.getChannelName()))
                .findFirst()
                .orElse(null); // Returns null if no match is found

        endpoint.setEnabled(status);
        database.setEndpointPipeStatus(endpoint.getId(), status);

        if (status) {
            context.reply("Bridge pipe is now enabled.");
        } else {
            context.reply("Bridge pipe is now disabled.");
        }
    }
}
