package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.util.MinecraftOnlineAPI;

import java.io.IOException;

public class BansCommand {
    @Command(
            name = "bans",
            aliases = {"bancount"},
            description = "Get MCO ban count",
            whitelistedChannels = {"#minecraftonline", "#slimediamond"},
            usage = "bans"
    )
    public void bansCommand(CommandEvent event) throws IOException {
        MinecraftOnlineAPI.getBanCount().ifPresentOrElse(bans -> {
            event.reply(bans + " players have been banished from Freedonia!");
        }, () -> {
            event.reply("MinecraftOnlineAPI::getBanCount Optional was not present! What the fuck happened? Tell an admin!");
        });
    }
}
