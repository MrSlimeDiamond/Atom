package net.zenoc.atom.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Option;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.services.system.GetServiceProcessor;
import net.zenoc.atom.util.EmbedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CommandHandler extends ListenerAdapter {
    JDA jda;
    String prefix;
    ArrayList<AtomCommand> commands = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }
    public void registerCommand(Object command) {
        // normal
        Arrays.stream(command.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Command.class))
                .map(method -> {
                    GetServiceProcessor.processAnnotations(command);
                    return new AtomCommand(method.getAnnotation(Command.class), command, method);
                })
                .forEach(commands::add);
    }

    public void refreshSlashCommands() {
        // HACKHACK: Clear all existing commands to prevent duplicates
        jda.updateCommands().queue();
        jda.getGuilds().forEach(guild -> guild.updateCommands().queue());

        commands.forEach(cmd -> {
            if (cmd.getCommand().slashCommand()) {
                String name = cmd.getCommand().name();
                String desc = cmd.getCommand().description();
                log.debug("Registering slash command " + name);

                Option[] options = cmd.getCommand().options();
                OptionData[] optionData = new OptionData[options.length];
                for (int i = 0; i < options.length; i++) {
                    log.debug("Registering option");
                    Option option = options[i];
                    OptionData data = new OptionData(option.type(), option.name(), option.description(), option.required());
                    optionData[i] = data;
                }

                Subcommand[] subcommands = cmd.getCommand().subcommands();
                SubcommandData[] subcommandData = new SubcommandData[subcommands.length];
                for (int i = 0; i < subcommands.length; i++) {
                    Subcommand subcommand = subcommands[i];
                    if (!subcommand.slashCommand()) continue;
                    log.debug("Registering subcommand " + subcommand.name());

                    Option[] options1 = subcommand.options();
                    OptionData[] optionData1 = new OptionData[options1.length];

                    log.debug(String.valueOf(subcommand.options().length));

                    for (int n = 0; n < subcommand.options().length; n++) {
                        Option option = options1[n];
                        OptionData optData = new OptionData(option.type(), option.name(), option.description(), option.required());
                        optionData1[n] = optData;
                    }

                    SubcommandData data = new SubcommandData(subcommand.name(), subcommand.description())
                            .addOptions(optionData1);

                    subcommandData[i] = data;
                }

                CommandData commandData = new CommandDataImpl(name, desc)
                        .addOptions(optionData)
                        .addSubcommands(subcommandData);

                if (cmd.getCommand().whitelistedGuilds().length == 0) {
                    jda.upsertCommand(commandData).queue();
                } else {
                    for (long guildID : cmd.getCommand().whitelistedGuilds()) {
                        Guild guild = jda.getGuildById(guildID);
                        if (guild == null) {
                            continue;
                        }
                        guild.upsertCommand(commandData).queue();
                    }
                }
                //jda.getGuilds().forEach(guild -> guild.upsertCommand(commandData).queue());
            }
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //log.debug("Received message: " + event.getMessage().getContentDisplay());
        // catch 'bad messages'
        if (event.getAuthor().isBot() || event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong() || !event.getMessage().getContentDisplay().startsWith(prefix)) return;

        //log.debug("check pass");

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        AtomicReference<String> commandName = new AtomicReference<>();
        commandName.set(event.getMessage().getContentRaw().split(prefix)[1].split(" ")[0]); // Not scuffed at all

        if (args.length > 0) {
            commands.forEach(cmd -> {
                String name = cmd.getCommand().name();
                //log.debug(name);
                ArrayList<String> aliases = new ArrayList<>(Arrays.asList(cmd.getCommand().aliases()));
                if (name.equalsIgnoreCase(commandName.get()) || aliases.contains(commandName.get())) {
                    log.debug("Executing command");
                    try {
                        executeCommand(cmd, event, null);
                    } catch(SQLException e) {
                        event.getChannel().sendMessageEmbeds(EmbedUtil.expandedErrorEmbed("SQLException occured! Is the database down?")).queue();
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        event.getChannel().sendMessageEmbeds(EmbedUtil.genericErrorEmbed()).queue();
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.forEach(cmd -> {
            String name = cmd.getCommand().name();
            if (event.getName().equals(name)) {
                try {
                    executeCommand(cmd, null, event);
                } catch(SQLException e) {
                    event.getInteraction().replyEmbeds(EmbedUtil.expandedErrorEmbed("SQLException occured! Is the database down?")).queue();
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    event.replyEmbeds(EmbedUtil.genericErrorEmbed()).queue();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void executeCommand(AtomCommand command, MessageReceivedEvent msgEvent, SlashCommandInteractionEvent interaction) throws Exception {

        // FIXME: admin-only subcommands

        CommandEvent commandEvent = new CommandEvent(jda, this, command);
        if (msgEvent != null) {
            commandEvent.setMsgEvent(msgEvent);
            if (!Atom.database.isDiscordAdminByID(msgEvent.getAuthor().getIdLong()) && command.getCommand().adminOnly()) {
                msgEvent.getChannel().sendMessageEmbeds(EmbedUtil.genericPermissionDeniedError()).queue();
                return;
            }
            if (!msgEvent.isFromGuild() && command.getCommand().whitelistedGuilds().length > 1) return;
            boolean go = false;
            if (command.getCommand().whitelistedGuilds().length == 0) go = true;
            for (long guildID : command.getCommand().whitelistedGuilds()) {
                Guild guild = jda.getGuildById(guildID);
                if (guild == null) {
                    continue;
                }
                if (guild.getId().equals(commandEvent.getGuild().getId())) {
                    go = true;
                    break;
                }
            }
            if (!go) return;
        } else {
            commandEvent.setSlashEvent(interaction);
            if (!Atom.database.isDiscordAdminByID(interaction.getUser().getIdLong()) && command.getCommand().adminOnly()) {
                interaction.replyEmbeds(EmbedUtil.genericPermissionDeniedError()).queue();
                return;
            }
        }

        Method method = command.getMethod();

        log.debug("Invoking");

        new Thread(() -> {
            Thread.currentThread().setName("Discord Command Executor");
            try {
                method.invoke(command.getCaller(), commandEvent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public ArrayList<AtomCommand> getCommands() {
        return commands;
    }
}
