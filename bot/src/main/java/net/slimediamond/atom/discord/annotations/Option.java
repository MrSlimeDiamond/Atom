package net.slimediamond.atom.discord.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public @interface Option {
    /**
     * Type of option
     */
    OptionType type();

    /**
     * ID of the option (used to access)
     */
    int id();

    /**
     * Name of the option (probably gonna remove)
     */
    String name();

    /**
     * Option description
     */
    String description();

    /**
     * Aliases (non-slash command only)
     */
    String[] aliases() default {};

    /**
     * If true, the option will be required to execute the command
     */
    boolean required() default false;
}
