package net.slimediamond.atom.discord.annotations;

public @interface Subcommand {
    /**
     * Name of the command
     */
    String name();

    /**
     * Command description
     */
    String description();

    /**
     * If false, the command will NOT be a slash command. Defaults to true
     */
    boolean slashCommand() default true;

    /**
     * Additional aliases for the command
     * Name is used as an alias
     */
    String[] aliases() default {};

    /**
     * Command options / arguments
     */
    Option[] options() default {};

    /**
     * How the command should be used
     */
    String usage();

    /**
     * If true, the command will be restricted to administrators only
     */
    boolean adminOnly() default false;
}
