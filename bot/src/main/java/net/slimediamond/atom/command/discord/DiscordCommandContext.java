package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.slimediamond.atom.command.*;
import net.slimediamond.atom.command.discord.args.ArgumentList;
import net.slimediamond.atom.command.discord.args.DiscordArgumentMetadata;
import net.slimediamond.atom.command.discord.args.UserArgument;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordCommandContext  implements CommandContext {
    private AtomDiscordCommandEvent interactionEvent;
    private CommandMetadata metadata;
    private String[] args;
    private CommandManager commandManager;
    private ArgumentList userArguments;
    private JDA jda;

    public DiscordCommandContext(AtomDiscordCommandEvent interactionEvent, CommandMetadata metadata, String[] args, CommandManager commandManager, JDA jda) {
        this.interactionEvent = interactionEvent;
        this.metadata = metadata;
        this.args = args;
        this.commandManager = commandManager;
        this.userArguments = new ArgumentList();
        this.jda = jda;

        ArrayList<DiscordArgumentMetadata> arguments = metadata.getDiscordCommand().getArgs();
        if (interactionEvent.isTextCommand()) {
            if (!arguments.isEmpty()) {
                for (int i = 0; i < args.length; i++) {
                    Object value = getValue(args[i], interactionEvent);
                    userArguments.add(new UserArgument(value, arguments.get(i)));
                }
            } // otherwise, it's probably a command without args
        } else {
            // FIXME: getOptions() can invoke an NPE, which could fix something that I already fixed
            // FIXME: but it might be made slightly nicer
            // FIXME: this is for getting options. See the top of a Discord command (like MCO lastseen)
            for (OptionMapping option : interactionEvent.getSlashCommandInteractionEvent().getOptions()) {
                userArguments.add(new UserArgument(getValue(option.getAsString(), interactionEvent),
                        arguments.get(interactionEvent.getSlashCommandInteractionEvent().getOptions().indexOf(option)))
                ); // TODO: maybe make this a little nicer
            }
        }
    }

    private static Object getValue(String arg, AtomDiscordCommandEvent interactionEvent) {
        if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(arg);
        } else {
            if (interactionEvent.getMessage() != null) {
                List<GuildChannel> channels = interactionEvent.getMessage().getMentions().getChannels();
                if (!channels.isEmpty()) {
                    return channels.get(0); // FIXME
                }
            } else {
                // FIXME: This doesn't work btw
                // FIXME: getOption(name) <-- we don't have the name
                //return interactionEvent.getSlashCommandInteractionEvent().getOption(arg).getAsChannel();
            }

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
    public DiscordCommandSender getSender() {
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

    public JDA getJDA() {
        return this.jda;
    }

    public Guild getGuild() {
        return interactionEvent.getGuild();
    }

    public MessageChannel getChannel() {
        return interactionEvent.getChannel();
    }

    public AtomDiscordCommandEvent getInteractionEvent() {
        return this.interactionEvent;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public void sendUsage() {
        this.replyEmbeds(new EmbedBuilder()
                .setDescription("Usage: " + metadata.getCommandUsage())
                .setColor(Color.RED)
                .build());
    }

    public ArgumentList getArguments() {
        return this.userArguments;
    }

}
