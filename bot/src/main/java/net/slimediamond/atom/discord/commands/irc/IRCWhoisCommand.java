package net.slimediamond.atom.discord.commands.irc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.discord.args.UserArgument;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.util.date.DateUtil;
import org.kitteh.irc.client.library.command.WhoisCommand;
import org.kitteh.irc.client.library.event.user.WhoisEvent;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class IRCWhoisCommand implements DiscordCommandExecutor {
    private DiscordCommandContext context;

    public IRCWhoisCommand() {
        IRC.client.getEventManager().registerEventListener(this);
    }

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        Optional<UserArgument> arg = context.getArguments().get("user");
        String name;
        if (arg.isPresent()) {
            name = arg.get().getAsString();
        } else {
            context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
            return;
        }

        context.deferReply();
        this.context = context;
        new WhoisCommand(IRC.client).target(name).execute();
    }

    @Handler
    public void whoisEvent(WhoisEvent event) {
        if (this.context == null) return;

        if (!event.getWhoisData().getServer().isPresent()) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("This user is not on " + IRCReference.host));
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(event.getWhoisData().getNick())
                .setTitle(event.getWhoisData().getName());

        StringBuilder stringBuilder = new StringBuilder();
        event.getWhoisData().getRealName().ifPresent(builder::setDescription);
        event.getWhoisData().getChannels().forEach(channel -> {
            stringBuilder.append(channel).append(" ");
        });
        event.getWhoisData().getServer().ifPresent(server -> {
            AtomicReference<String> serverDescription = new AtomicReference<>("*No server description*");
            event.getWhoisData().getServerDescription().ifPresent(serverDescription::set);
            builder.addField("Server", server + " : " + serverDescription, false);
        });
        event.getWhoisData().getAccount().ifPresent(account -> builder.addField("Account", "Logged in as " + account, true));
        event.getWhoisData().getIdleTime().ifPresent(idletime -> builder.addField("Idle", new Date(System.currentTimeMillis() - idletime) + " (" + DateUtil.formatDuration(Duration.ofSeconds(idletime)) + ")", true));
        event.getWhoisData().getOperatorInformation().ifPresent(operatorInfo -> builder.addField("Operator", operatorInfo, false));

        event.getWhoisData().getIdleTime().ifPresent(System.out::println);

        String channels = stringBuilder.toString();
        builder.addField("Channels", channels, false);

        context.replyEmbeds(builder.build());
    }
}
