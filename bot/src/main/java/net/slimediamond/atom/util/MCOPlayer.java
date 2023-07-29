package net.slimediamond.atom.util;

import net.slimediamond.atom.Atom;
import net.slimediamond.atom.database.Database;
import net.slimediamond.util.minecraft.MinecraftUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

public class MCOPlayer {
    String username;
    String uuid;
    Date firstseen;
    Date lastseen;
    Long playtime;

    private Logger log = LoggerFactory.getLogger(MCOPlayer.class);

    private Database database = Atom.getServiceManager().getInstance(Database.class);

    /**
     * Creates a new MinecraftOnline player
     * @param username The username of the player
     * @throws UnknownPlayerException If the player does not exist
     */
    public MCOPlayer(String username) throws UnknownPlayerException {
        // Set the name ASAP
        this.username = username;

        try {
            Optional<String> uuid = MinecraftUtils.getPlayerUUID(username);
            Optional<String> playerCorrectName = MinecraftOnlineAPI.getCorrectUsername(username);
            playerCorrectName.ifPresent(correctname -> this.username = correctname);
            uuid.ifPresent(u -> this.uuid = u);
            if (!playerCorrectName.isPresent() || !uuid.isPresent()) {
                throw new UnknownPlayerException(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a user's first join date
     * @return First join date
     * @throws SQLException If the database goes crazy
     * @throws IOException If something goes crazy, IDK
     * @throws UnknownPlayerException If the player does not exist
     */
    public Optional<Date> getFirstseen() throws SQLException, IOException, UnknownPlayerException {
        if (this.firstseen != null) {
            return Optional.of(this.firstseen);
        } else {
            Optional<Date> mcoFirstseen = database.getMCOFirstseenByName(username);
            if (mcoFirstseen.isPresent()) {
                // in database
                this.firstseen = mcoFirstseen.get();
                return mcoFirstseen;
            } else {
                // We'll have to retrieve it first
                Optional<Date> firstseen = MinecraftOnlineAPI.getPlayerFirstseenByName(username);

                // This shouldn't happen, but just in case
                if (!firstseen.isPresent()) throw new UnknownPlayerException(this.username + " was not found");

                // If the player's firstseen is not in the database, insert it

                // Creating a new thread for the sake of efficiency,
                // we want to return a value as fast as possible
                new Thread(() -> {
                    try {
                        setFirstseen(firstseen.get());
                    } catch (SQLException e) {
                        this.firstseen = firstseen.get();
                        throw new RuntimeException(e);
                    }
                }).start();
                return firstseen;
            }
        }
    }

    /**
     * Get a user's last join date
     * @return Last join date
     * @throws SQLException If the database goes crazy
     * @throws IOException If something goes crazy, IDK
     * @throws UnknownPlayerException If the player does not exist
     */
    public Optional<Date> getLastseen() throws SQLException, IOException, UnknownPlayerException {
        if (this.lastseen != null) {
            return Optional.of(this.lastseen);
        } else {
            Optional<Date> mcoLastseen = database.getMCOLastseenByName(username);
            if (mcoLastseen.isPresent()) {
                // in database
                this.lastseen = mcoLastseen.get();
                return mcoLastseen;
            } else {
                // We'll have to retrieve it first
                Optional<Date> lastseen = MinecraftOnlineAPI.getPlayerLastseenByName(username);

                // This shouldn't happen, but just in case
                if (!lastseen.isPresent()) throw new UnknownPlayerException(this.username + " was not found");

                // If the player's lastseen is not in the database, insert it

                // Creating a new thread for the sake of efficiency,
                // we want to return a value as fast as possible
                new Thread(() -> {
                    try {
                        setLastseen(lastseen.get());
                    } catch (SQLException e) {
                        this.lastseen = lastseen.get();
                        throw new RuntimeException(e);
                    }
                }).start();
                return lastseen;
            }
        }
    }

    /**
     * Get a user's playtime
     * @return The user's playtime
     */
    public Optional<Long> getPlaytime() throws IOException {
        if (this.playtime == null) {
            MinecraftOnlineAPI.getPlayerPlaytime(this.username).ifPresent(playtime -> this.playtime = playtime);
        }
        return Optional.of(this.playtime);
    }

    /**
     * Set the user's first seen date in the database
     * @param date The date the player first joined
     * @throws SQLException If the database goes crazy
     */
    public void setFirstseen(Date date) throws SQLException {
        log.info(username + " hasn't got a firstseen record in the database, adding");
        if (!database.isMCOUserInDatabaseByUsername(username)) {
            log.info("Inserting user");
            database.insertMCOUser(username, this.uuid);
        }
        database.setMCOFirstseenByUUID(this.uuid, date);
        log.info("Set firstseen date!");
    }

    /**
     * Set the user's last seen date in the database
     * @param date The date the player last joined
     * @throws SQLException If the database goes crazy
     */
    public void setLastseen(Date date) throws SQLException {
        log.info(username + " hasn't got a lastseen record in the database, adding");
        if (!database.isMCOUserInDatabaseByUsername(username)) {
            log.info("Inserting user");
            database.insertMCOUser(username, this.uuid);
        }
        database.setMCOLastseenByUUID(this.uuid, date);
        log.info("Set lastseen date!");
    }

    /**
     * Get the player's username
     * @return The player's username
     */
    public String getName() {
        return this.username;
    }
}
