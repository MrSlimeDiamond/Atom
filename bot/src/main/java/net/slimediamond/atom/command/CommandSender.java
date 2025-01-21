package net.slimediamond.atom.command;

import java.sql.SQLException;

public interface CommandSender {
    /**
     * The name of the sender
     * @return sender name
     */
    String getName();

    boolean isAdmin() throws SQLException;
}
