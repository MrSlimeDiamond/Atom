package net.slimediamond.atom.util;

import net.dv8tion.jda.api.entities.User;

public abstract class UserUtil {
    public static String getUserName(User user) {
        String discriminator = user.getAsTag();
        if (discriminator.endsWith("#0000")) {
            // New username
            return discriminator.replace("#0000", "");
        } else {
            return discriminator;
        }
    }
}
