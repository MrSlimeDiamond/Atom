package net.slimediamond.atom.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.slimediamond.atom.discord.exceptions.InvalidOptionError;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.atom.util.EmbedUtil;
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
    private boolean sendIncorrectUsageForCommandArgs = true;

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
            getMessage().getChannel().sendMessage(content).queue();
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
            getMessage().getChannel().sendMessageEmbeds(embeds).queue();
        } else {
            // Slash command
            if (deferred) {
                slashEvent.getHook().sendMessageEmbeds(embeds).queue();
            } else {
                getInteraction().replyEmbeds(embeds).queue();
            }
        }
    }

    public void sendIncorrectUsageForCommandArgs(boolean bool) {
        this.sendIncorrectUsageForCommandArgs = bool;
    }

    public String[] getCommandArgs() {
        if (isSlashCommand()) {
            // HACKHACK: Slash commands have no args
            // Besides, we should check beforehand
            return new String[0];
        } else {
            String[] argz = getMessage().getContentRaw().split(getCommandHandler().prefix)[1].split(" ");
            String[] args = Arrays.copyOfRange(argz, 1, argz.length);
            if (args.length == 0) {
                if (this.sendIncorrectUsageForCommandArgs) {
                    this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage()));
                }
                return null;
            }
            return args;
        }
    }

    public boolean isSubCommand() {
        if (isSlashCommand()) {
            return slashEvent.getSubcommandName() != null;
        }
        if (getCommandArgs() == null || getCommandArgs().length == 0) {
            return false;
        }
        for (Subcommand subcommand : command.getCommand().subcommands()) {
            for (String alias : subcommand.aliases()) {
                if (getCommandArgs()[0].equals(alias)) {
                    return true;
                }
            }
            if (getCommandArgs()[0].equals(subcommand.name())) {
                return true;
            }
        }
        return false;
    }

    public String getStringOption(String name) {
        // FIXME: Normal command args
         if (isSlashCommand()) {
            return getInteraction().getOption(name).getAsString();
         } else {
             // pretty scuffed, it'll work though
             String[] args = getCommandArgs();
             if (args.length == 1) {
                 return null;
             }
             if (isSubCommand()) args = Arrays.copyOfRange(args, 1, args.length);
             int optID = -1;
             int length = -1;
             if (isSubCommand()) {
                 for (Subcommand subcommand : command.getCommand().subcommands()) {
                     for (String alias : subcommand.aliases()) {
                         if (alias.equals(getCommandArgs()[0])) {
                             length = subcommand.options().length;
                         }
                     }
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
                     for (String alias : subcommand.aliases()) {
                         if (alias.equals(getCommandArgs()[0])) {
                             cmd = subcommand;
                             break;
                         }
                     }
                     if (subcommand.name().equals(getCommandArgs()[0])) {
                         cmd = subcommand;
                         break;
                     }
                 }

                 if (cmd == null) {
                     this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage() + " (called from line 203)"));
                     return null;
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
                 this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage() + " (called from line 223)"));
                 return null;
             }

             return args[optID];
         }

    }

    public GuildChannel getChannelOption(String name) {
        if (isSlashCommand()) {
            return getInteraction().getOption(name).getAsChannel();
        } else {
            List<GuildChannel> channels = msgEvent.getMessage().getMentions().getChannels();
            return channels.get(0);
        }
    }

    public Optional<Boolean> getBooleanOption(String name) {
        if (isSlashCommand()) {
            return Optional.of(getInteraction().getOption(name).getAsBoolean());
        } else {
            String[] args = getCommandArgs();
            if (args.length == 1) {
                return null;
            }
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
                    this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage()));
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
                this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage()));
            }

            return Optional.of(args[optID].equals("true") || args[optID].equals("on") || args[optID].equals("yes"));
        }
    }

    public String getSubcommandName() {
        if (isSlashCommand()) {
            return slashEvent.getSubcommandName();
        } else {
            for (Subcommand subcommand : command.getCommand().subcommands()) {
                for (String alias : subcommand.aliases()) {
                    if (Objects.equals(alias, getCommandArgs()[0])) {
                        return subcommand.name();
                    }
                }
            }
            return getCommandArgs()[0];
        }
    }

    public void sendUsage() {
        this.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed(command.getCommand().usage()));
    }

}
