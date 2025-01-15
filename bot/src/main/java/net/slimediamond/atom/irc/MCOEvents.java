package net.slimediamond.atom.irc;

import net.slimediamond.atom.Atom;
import net.slimediamond.atom.database.Database;
import net.slimediamond.util.minecraft.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class MCOEvents {
    private static Database database = Atom.getServiceManager().getInstance(Database.class);
    
    public static void onMCOJoin(String username) throws IOException, SQLException {
        insertAndSetLastseen(username);
    }

    public static void onMCOLeave(String username) throws IOException, SQLException {
        insertAndSetLastseen(username);
    }

    private static void insertAndSetLastseen(String username) throws SQLException, IOException {
        if (!database.isMCOUserInDatabaseByUsername(username)) {
            MinecraftUtils.getPlayerUUID(username).ifPresent(uuid -> {
                try {
                    // Insert the user
                    database.insertMCOUser(username, uuid);

                    // Set lastseen date
                    database.setMCOLastseenByUUID(uuid, new Date());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            // we can grab it locally, the user is in the db
            database.getMCOuuid(username).ifPresent(uuid -> {
                try {
                    database.setMCOLastseenByUUID(uuid, new Date());
                } catch (SQLException e) {
                    // I hate Java.
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
