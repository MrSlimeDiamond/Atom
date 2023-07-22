package net.slimediamond.atom.irc.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Name of the command
     */
    String name();

    /**
     * Command description
     */
    String description();

    /**
     * Additional aliases for the command
     * Name is used as an alias
     */
    String[] aliases() default {};

    /**
     * How the command should be used
     */
    String usage();

    /**
     * If true, the command will be restricted to administrators only
     */
    boolean adminOnly() default false;

    /**
     * Channels the command works in, if none are specified, it will work in all channels
     */
    String[] whitelistedChannels() default {};
}
