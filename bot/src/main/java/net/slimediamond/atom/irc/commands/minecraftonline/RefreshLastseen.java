package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.util.MinecraftOnlineAPI;
import net.slimediamond.util.minecraft.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class RefreshLastseen {
    @GetService
    private Database database;
    @Command(
            name = "refreshlastseen",
            description = "Refresh the lastseen date of a user",
            usage = "refreshlastseen [username]"
    )
    public void refreshUserLastseen(CommandEvent event) throws IOException {
        String username = event.getDesiredCommandUsername();
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> event.reply("Could not find that player!"));

        if (correctname.get() == null) {
            return;
        }

        MinecraftOnlineAPI.getPlayerLastseenByName(correctname.get()).ifPresent(lastseen -> {
            try {
                MinecraftUtils.getPlayerUUID(correctname.get()).ifPresent(uuid -> {
                    try {
                        database.setMCOLastseenByUUID(uuid, lastseen);
                        event.reply("Refreshed lastseen date for " + correctname + " new date: " + lastseen);
                    } catch (SQLException e) {
                        event.reply("SQLException! Is the database down? Tell an admin!");
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                event.reply("IOException! something is fucked, tell SlimeDiamond");
                throw new RuntimeException(e);
            }
        });
    }
}
