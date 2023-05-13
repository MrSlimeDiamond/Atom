package net.zenoc.atom.ircbot;

import net.zenoc.atom.Atom;
import net.zenoc.atom.util.MinecraftUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class MCOEvents {
    public static void onMCOJoin(String username) throws IOException, SQLException {
        MinecraftUtils.getPlayerUUID(username).ifPresent(uuid -> {
            try {
                if (!Atom.database.isMCOUserInDatabaseByUsername(username)) {
                    try {
                        Atom.database.insertMCOUser(username, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                Atom.database.setMCOLastseenByUUID(uuid, new Date());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void onMCOLeave(String username) throws IOException, SQLException {
        MinecraftUtils.getPlayerUUID(username).ifPresent(uuid -> {
            try {
                if (!Atom.database.isMCOUserInDatabaseByUsername(username)) {
                    try {
                        Atom.database.insertMCOUser(username, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                Atom.database.setMCOLastseenByUUID(uuid, new Date());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
