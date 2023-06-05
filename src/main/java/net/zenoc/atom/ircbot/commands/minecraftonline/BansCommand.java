package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.reference.EmbedReference;
import net.zenoc.atom.util.EmbedUtil;
import net.zenoc.atom.util.MinecraftOnlineAPI;

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
