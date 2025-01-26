package net.slimediamond.atom.command.discord.args;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.command.exceptions.ArgumentException;

import java.util.ArrayList;

public class DiscordArgsBuilder {
    private OptionType optionType;
    private int id = -1;
    private String description;
    private String name;
    private boolean required = false;


    public DiscordArgsBuilder setOptionType(OptionType optionType) {
        this.optionType = optionType;
        return this;
    }

    public DiscordArgsBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public DiscordArgsBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DiscordArgsBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public DiscordArgsBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public DiscordArgumentMetadata build() {
        if (id == -1) {
            throw new ArgumentException("Cannot create an argument without an id");
        } else if (name == null) {
            throw new ArgumentException("Cannot crate an argument without a name");
        } else if (description == null) {
            throw new ArgumentException("Cannot create an argument without a description");
        } else if (optionType == null) {
            throw new ArgumentException("Cannot create an argument without a type");
        }

        return new DiscordArgumentMetadata() {
            @Override
            public OptionType getOptionType() {
                return optionType;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean isRequired() {
                return required;
            }
        };
    }
}
