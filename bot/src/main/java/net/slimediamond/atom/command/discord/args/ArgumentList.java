package net.slimediamond.atom.command.discord.args;

import java.util.ArrayList;

public class ArgumentList extends ArrayList<UserArgument> {
    public UserArgument get(String name) {
        for (UserArgument userArgument : this) {
            if (userArgument.getMetadata().getName().equalsIgnoreCase(name)) {
                return userArgument;
            }
        }
        return null;
    }
}
