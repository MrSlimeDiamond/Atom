package com.minecraftonline.mcodata.api.model;

import java.util.Date;

/**
 * An immutable reference to a note on a player
 */
public class Note {

    private final String message;
    private final MCOPlayer player;
    private final MCOPlayer author;
    private final Date date;

    public Note(String message, MCOPlayer player, MCOPlayer author, Date date) {
        this.message = message;
        this.player = player;
        this.author = author;
        this.date = date;
    }

    /**
     * Get the ban reason - the message on the note
     *
     * @return Ban reason
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the banned player
     *
     * @return Banned player
     */
    public MCOPlayer getPlayer() {
        return player;
    }

    /**
     * Get the author who left the ban
     *
     * @return Ban author
     */
    public MCOPlayer getAuthor() {
        return author;
    }

    /**
     * Get the date that this note was added
     *
     * @return Note date
     */
    public Date getDate() {
        return date;
    }

}
