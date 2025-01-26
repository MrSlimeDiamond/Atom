package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.util.MinecraftOnlineAPI;
import net.slimediamond.util.minecraft.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class RefreshLastseen implements IRCCommandExecutor {
    @GetService
    private Database database;

    public void execute(IRCCommandContext ctx) throws IOException {
        if (database == null) {
            ctx.reply("Database object is null, therefore data cannot be set. It is likely that the inject has failed. Please see the log");
            return;
        }

        String username = ctx.getDesiredCommandUsername();
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> ctx.reply("Could not find that player!"));

        if (correctname.get() == null) {
            return;
        }

        MinecraftOnlineAPI.getPlayerLastseenByName(correctname.get()).ifPresent(lastseen -> {
            try {
                MinecraftUtils.getPlayerUUID(correctname.get()).ifPresent(uuid -> {
                    try {
                        database.setMCOLastseenByUUID(uuid, lastseen);
                        ctx.reply("Refreshed lastseen date for " + correctname + " new date: " + lastseen);
                    } catch (SQLException e) {
                        ctx.reply("SQLException! Is the database down? Tell an admin!");
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                ctx.reply("IOException! something is fucked, tell SlimeDiamond");
                throw new RuntimeException(e);
            }
        });
    }
}
