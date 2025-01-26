package net.slimediamond.atom.command.discord.args;

import java.util.ArrayList;
import java.util.Optional;

public class ArgumentList extends ArrayList<UserArgument> {
    public Optional<UserArgument> get(String name) {
        for (UserArgument userArgument : this) {
            if (userArgument.getMetadata().getName().equalsIgnoreCase(name)) {
                return Optional.of(userArgument);
            }
        }
        return Optional.empty();
    }
}
