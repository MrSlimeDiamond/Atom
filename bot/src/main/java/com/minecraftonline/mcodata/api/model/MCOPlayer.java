package com.minecraftonline.mcodata.api.model;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface MCOPlayer {
    /**
     * Get the name of the player
     *
     * @return Player name
     */
    String getName();

    /**
     * Get the UUID of the player
     *
     * @return Player UUID
     */
    UUID getUUID();

    /**
     * Get the date that the player first joined the server
     *
     * @return Player first join date
     */
    Date getFirstseen();

    /**
     * Get the date the player last visited the server
     *
     * @return Lastseen date
     */
    Date getLastseen();

    /**
     * Get the amount of time the player has spent on the server, in <strong>seconds</strong>
     *
     * @return Player time played
     */
    int getTimeOnline();

    /**
     * Get whether the player is online currently
     *
     * @return Whether the player is online
     */
    boolean isOnline();

    /**
     * Get whether the player is banned
     *
     * @return Player ban status
     */
    boolean isBanned();

    /**
     * Get this player's ban reason
     *
     * @return Player ban reason
     */
    Optional<Note> getBanReason();

    /**
     * Get this player's avatar URL, showing their skin
     *
     * @param size The size for the avatar
     * @return The avatar URL for the player
     */
    default String getAvatarUrl(int size) {
        return "https://minecraftonline.com/cgi-bin/getplayerhead.sh?" + getName() + "&size=" + size;
    }

    /**
     * Get this player's avatar URL, showing their skin
     *
     * @return The avatar URL for the player
     */
    default String getAvatarUrl() {
        return getAvatarUrl(64);
    }

}
