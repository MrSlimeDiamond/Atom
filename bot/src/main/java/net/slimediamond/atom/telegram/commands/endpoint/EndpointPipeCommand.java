package net.slimediamond.atom.telegram.commands.endpoint;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;

import java.util.Arrays;

public class EndpointPipeCommand implements TelegramCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(TelegramCommandContext context) throws Exception {
        boolean status = false;

        String[] yesOptions = {"true", "on", "enabled"};
        if (Arrays.asList(yesOptions).contains(context.getArgs()[0])) {
            status = true;
        }

        BridgeEndpoint endpoint = BridgeStore.getChats().values().stream()
                .flatMap(chat -> chat.getEndpoints().stream()) // Flatten the list of endpoints
                .filter(ep -> ep.getUniqueIdentifier().equals(String.valueOf(context.getChat().getId())))
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
