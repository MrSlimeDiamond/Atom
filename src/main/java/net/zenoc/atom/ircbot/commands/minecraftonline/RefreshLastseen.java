package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.Atom;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.MinecraftOnlineAPI;
import net.zenoc.atom.util.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class RefreshLastseen {
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
                        Atom.database.setMCOLastseenByUUID(uuid, lastseen);
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
