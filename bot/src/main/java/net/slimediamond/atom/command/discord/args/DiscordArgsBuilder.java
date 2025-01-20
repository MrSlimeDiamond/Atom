package net.slimediamond.atom.command.discord.args;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;

public class DiscordArgsBuilder {
    private OptionType optionType;
    private int id;
    private String description;
    private ArrayList<String> aliases = new ArrayList<>();
    private boolean required = false;


    public DiscordArgsBuilder setOptionType(OptionType optionType) {
        this.optionType = optionType;
        return this;
    }

    public DiscordArgsBuilder setId(int it) {
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
