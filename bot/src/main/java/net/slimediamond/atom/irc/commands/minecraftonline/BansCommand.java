package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.util.MinecraftOnlineAPI;

import java.io.IOException;

public class BansCommand implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws IOException {
        MinecraftOnlineAPI.getBanCount().ifPresentOrElse(bans -> {
            ctx.reply(bans + " players have been banished from Freedonia!");
        }, () -> {
            ctx.reply("MinecraftOnlineAPI::getBanCount Optional was not present! What the fuck happened? Tell an admin!");
        });
    }
}
