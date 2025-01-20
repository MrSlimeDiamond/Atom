package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.slimediamond.atom.command.*;
import net.slimediamond.atom.command.discord.args.ArgumentList;
import net.slimediamond.atom.command.discord.args.DiscordArgumentMetadata;
import net.slimediamond.atom.command.discord.args.UserArgument;

import java.util.ArrayList;
import java.util.Arrays;

public class DiscordCommandContext implements CommandContext {
    private AtomDiscordCommandEvent interactionEvent;
    private CommandMetadata metadata;
    private String[] args;
    private CommandManager commandManager;
    private ArgumentList userArguments;

    public DiscordCommandContext(AtomDiscordCommandEvent interactionEvent, CommandMetadata metadata, String[] args, CommandManager commandManager) {
        this.interactionEvent = interactionEvent;
        this.metadata = metadata;
        this.args = args;
        this.commandManager = commandManager;
        this.userArguments = new ArgumentList();

        ArrayList<DiscordArgumentMetadata> arguments = metadata.getDiscordCommand().getArgs();
        if (interactionEvent.isTextCommand()) {
            if (arguments.size() > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object value = getValue(args[i]);
                    userArguments.add(new UserArgument(value, arguments.get(i)));
                }
            } // otherwise, it's probably a command without args
        } else {
            for (OptionMapping option : interactionEvent.getSlashCommandInteractionEvent().getOptions()) {
                userArguments.add(new UserArgument(getValue(option.getAsString()), arguments.get(interactionEvent.getSlashCommandInteractionEvent().getOptions().indexOf(option)))); // TODO: this is shit
            }
        }
    }

    private static Object getValue(String arg) {
        if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(arg);
        } else {
            try {
                return Integer.parseInt(arg);
            } catch (NumberFormatException ignored) {}

            try {
                return Double.parseDouble(arg);
            } catch (NumberFormatException ignored) {}
        }

        return arg;
    }

    @Override
    public CommandSender getSender() {
        return new DiscordCommandSender(interactionEvent.getUser().getEffectiveName(), interactionEvent.getUser());
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public CommandMetadata getCommandMetadata() {
        return this.metadata;
    }

    @Override
    public void reply(String message) {
        interactionEvent.reply(message);
    }

    public void replyEmbeds(MessageEmbed... embeds) {
        interactionEvent.replyEmbeds(embeds);
    }

    public void deferReply() {
        // do nothing on text commands
        if (!interactionEvent.isTextCommand()) {
            interactionEvent.getSlashCommandInteractionEvent().deferReply().queue();
            interactionEvent.setDeferred(true);
        }
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public String getDesiredCommandUsername() {
        // TODO
        return getSender().getName();
    }

    public ArgumentList getArguments() {
        return this.userArguments;
    }

}
