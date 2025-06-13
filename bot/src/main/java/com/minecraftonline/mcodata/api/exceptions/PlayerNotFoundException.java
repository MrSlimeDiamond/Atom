package com.minecraftonline.mcodata.api.exceptions;

public class PlayerNotFoundException extends DataNotFoundException {

    public PlayerNotFoundException(String username) {
        super("Player: \"" + username + "\" not found.");
    }

}
