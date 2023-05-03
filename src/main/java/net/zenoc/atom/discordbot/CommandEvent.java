package net.zenoc.atom.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.discordbot.errors.InvalidOptionError;
import net.zenoc.atom.discordbot.exceptions.IncorrectUsageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommandEvent {
    private static final Logger log = LoggerFactory.getLogger(CommandEvent.class);
    private JDA jda;
    private CommandHandler commandHandler;
    private MessageReceivedEvent msgEvent;
    private SlashCommandInteractionEvent slashEvent;
    private AtomCommand command;
    private boolean deferred = false;

    public CommandEvent(JDA jda, CommandHandler commandHandler, AtomCommand command) {
        this.jda = jda;
        this.commandHandler = commandHandler;
        this.command = command;
    }

    void setMsgEvent(MessageReceivedEvent event) {
        this.msgEvent = event;
    }

    void setSlashEvent(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
    }

    private boolean isSlashCommand() {
        return msgEvent == null;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public CommandHandler getCommandHandler(){
        return this.commandHandler;
    }

    public SlashCommandInteraction getInteraction() {
        if (isSlashCommand()) {
            return slashEvent.getInteraction();
        } else {
            return null;
        }
    }

    public Message getMessage() {
        if (isSlashCommand()) {
            return null;
        } else {
            return msgEvent.getMessage();
        }
    }

    public Channel getChannel() {
        if (isSlashCommand()) {
            return getInteraction().getChannel();
        } else {
            return msgEvent.getChannel();
        }
    }

    public Guild getGuild() {
        if (isSlashCommand()) {
            return getInteraction().getGuild();
        } else {
            return msgEvent.getGuild();
        }
    }

    public User getAuthor() {
        if (isSlashCommand()) {
            return getInteraction().getUser();
        } else {
            return msgEvent.getAuthor();
        }
    }

    public void deferReply() {
        if (isSlashCommand()) {
            deferred = true;
            slashEvent.deferReply().queue();
        }
    }

    public void reply(String content) {
        if (getInteraction() == null) {
            // Non slash command
            getMessage().reply(content).queue();
        } else {
            // Slash command
            if (deferred) {
                slashEvent.getHook().sendMessage(content).queue();
            } else {
                getInteraction().reply(content).queue();
            }
        }
    }

    public void replyEmbeds(MessageEmbed embed, MessageEmbed... other) {
        List<MessageEmbed> embeds = new ArrayList<>(1 + other.length);
        embeds.add(embed);
        Collections.addAll(embeds, other);
        if (getInteraction() == null) {
            // Non slash command
            getMessage().replyEmbeds(embeds).queue();
        } else {
            // Slash command
            if (deferred) {
                slashEvent.getHook().sendMessageEmbeds(embeds).queue();
            } else {
                getInteraction().replyEmbeds(embeds).queue();
            }
        }
    }

    public String[] getCommandArgs() throws IncorrectUsageException {
        if (isSlashCommand()) {
            // HACKHACK: Slash commands have no args
            // Besides, we should check beforehand
            return new String[0];
        } else {
            String[] argz = getMessage().getContentRaw().split(getCommandHandler().prefix)[1].split(" ");
            String[] args = Arrays.copyOfRange(argz, 1, argz.length);
            if (args.length == 0) {
                throw new IncorrectUsageException();
            }
            return args;
        }
    }

    public boolean isSubCommand() {
        return command.getCommand().subcommands().length != 0;
    }

    public String getStringOption(String name) throws IncorrectUsageException {
         if (isSlashCommand()) {
            return getInteraction().getOption(name).getAsString();
         } else {
             // pretty scuffed, it'll work though
             String[] args = getCommandArgs();
             if (isSubCommand()) args = Arrays.copyOfRange(args, 1, args.length);
             int optID = -1;
             int length = -1;
             if (isSubCommand()) {
                 for (Subcommand subcommand : command.getCommand().subcommands()) {
                     if (subcommand.name().equals(getCommandArgs()[0])) {
                         length = subcommand.options().length;
                     }
                 }
             } else {
                 length = command.getCommand().options().length;
             }

             if (isSubCommand()) {
                 Subcommand cmd = null;
                 for (Subcommand subcommand : command.getCommand().subcommands()) {
                     if (subcommand.name().equals(getCommandArgs()[0])) {
                         cmd = subcommand;
                     }
                 }

                 if (cmd == null) {
                     throw new IncorrectUsageException();
                 }

                 for (int i = 0; i < length; i++) {
                     if (!Objects.equals(cmd.options()[i].name(), name)) continue;
                     optID = i;
                 }
             } else {
                 for (int i = 0; i < length; i++) {
                     if (!Objects.equals(command.getCommand().options()[i].name(), name)) continue;
                     optID = i;
                 }
             }

             if (optID == -1) {
                 throw new InvalidOptionError();
             }

             if (optID > args.length) {
                 throw new IncorrectUsageException();
             }

             return args[optID];
         }

    }

    public GuildChannel getChannelOption(String name) throws IncorrectUsageException {
        if (isSlashCommand()) {
            return getInteraction().getOption(name).getAsChannel();
        } else {
            List<GuildChannel> channels = msgEvent.getMessage().getMentions().getChannels();
            return channels.get(0);
        }
    }

    public Optional<Boolean> getBooleanOption(String name) throws IncorrectUsageException {
        if (isSlashCommand()) {
            return Optional.of(getInteraction().getOption(name).getAsBoolean());
        } else {
            String[] args = getCommandArgs();
            if (isSubCommand()) args = Arrays.copyOfRange(args, 1, args.length);
            int optID = -1;
            int length = -1;
            if (isSubCommand()) {
                for (Subcommand subcommand : command.getCommand().subcommands()) {
                    if (subcommand.name().equals(getCommandArgs()[0])) {
                        length = subcommand.options().length;
                    }
                }
            } else {
                length = command.getCommand().options().length;
            }

            if (isSubCommand()) {
                Subcommand cmd = null;
                for (Subcommand subcommand : command.getCommand().subcommands()) {
                    if (subcommand.name().equals(getCommandArgs()[0])) {
                        cmd = subcommand;
                    }
                }

                if (cmd == null) {
                    throw new IncorrectUsageException();
                }

                for (int i = 0; i < length; i++) {
                    if (!Objects.equals(cmd.options()[i].name(), name)) continue;
                    optID = i;
                }
            } else {
                for (int i = 0; i < length; i++) {
                    if (!Objects.equals(command.getCommand().options()[i].name(), name)) continue;
                    optID = i;
                }
            }

            if (optID == -1) {
                throw new InvalidOptionError();
            }

            if (optID > args.length) {
                throw new IncorrectUsageException();
            }

            return Optional.of(args[optID].equals("true") || args[optID].equals("on") || args[optID].equals("yes"));
        }
    }

    public String getSubcommandName() {
        if (isSlashCommand()) {
            return slashEvent.getSubcommandName();
        } else {
            try {
                return getCommandArgs()[0];
            } catch (IncorrectUsageException e) {
                return null;
            }
        }
    }

}
