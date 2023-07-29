package net.slimediamond.atom.irc;

import net.slimediamond.atom.Atom;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class MCOEvents {
    private static Database database = Atom.getServiceManager().getInstance(Database.class);
    
    public static void onMCOJoin(String username) throws IOException, SQLException {
        MinecraftUtils.getPlayerUUID(username).ifPresent(uuid -> {
            try {
                if (!database.isMCOUserInDatabaseByUsername(username)) {
                    try {
                        database.insertMCOUser(username, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                database.setMCOLastseenByUUID(uuid, new Date());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void onMCOLeave(String username) throws IOException, SQLException {
        MinecraftUtils.getPlayerUUID(username).ifPresent(uuid -> {
            try {
                if (!database.isMCOUserInDatabaseByUsername(username)) {
                    try {
                        database.insertMCOUser(username, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                database.setMCOLastseenByUUID(uuid, new Date());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
