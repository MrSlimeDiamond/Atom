package net.slimediamond.atom.command.discord.args;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public interface DiscordArgumentMetadata {
    OptionType getOptionType();

    int getId();

    String getName();

    String getDescription();

    boolean isRequired();
}
