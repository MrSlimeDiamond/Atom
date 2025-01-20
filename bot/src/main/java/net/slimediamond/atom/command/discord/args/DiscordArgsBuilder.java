package net.slimediamond.atom.command.discord.args;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.command.exceptions.ArgumentException;

import java.util.ArrayList;

public class DiscordArgsBuilder {
    private OptionType optionType;
    private int id = -1;
    private String description;
    private ArrayList<String> aliases = new ArrayList<>();
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

    public DiscordArgsBuilder addAliases(String... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias);
        }
        return this;
    }

    public DiscordArgumentMetadata build() {
        if (id == -1) {
            throw new ArgumentException("Cannot create an argument without an id");
        } else if (aliases.isEmpty()) {
            throw new ArgumentException("Cannot crate an argument without any aliases");
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
                return aliases.get(0);
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public ArrayList<String> getAliases() {
                return aliases;
            }

            @Override
            public boolean isRequired() {
                return isRequired();
            }
        };
    }
}
