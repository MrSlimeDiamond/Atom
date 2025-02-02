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
                .setAuthor(event.getWhoisData().getNick() + " (" + event.getWhoisData().getUserString() + "@" + event.getWhoisData().getHost() + ")");

        StringBuilder stringBuilder = new StringBuilder();

        event.getWhoisData().getRealName().ifPresent(realname -> {
            stringBuilder.append("**Real name:** ").append(realname).append("\n");
        });

        event.getWhoisData().getServer().ifPresent(server -> {
            AtomicReference<String> serverDescription = new AtomicReference<>(null);
            event.getWhoisData().getServerDescription().ifPresent(serverDescription::set);

            stringBuilder.append("**Server**: ").append(server);
            if (!serverDescription.get().isEmpty()) {
                stringBuilder.append(" (*").append(serverDescription).append("*)");
            }

            stringBuilder.append("\n");
        });

        event.getWhoisData().getIdleTime().ifPresent(idletime -> stringBuilder.append("**Idle:** ").append(new Date(System.currentTimeMillis() - idletime)).append(" (").append(DateUtil.formatDuration(Duration.ofSeconds(idletime))).append(")\n"));

        event.getWhoisData().getAccount().ifPresent(account -> stringBuilder.append("**Account:** " + "Logged in as ").append(account).append("\n"));
        event.getWhoisData().getOperatorInformation().ifPresent(operatorInfo -> stringBuilder.append("**Operator:** ").append(operatorInfo).append("\n"));

        StringBuilder channelsBuilder = new StringBuilder();
        event.getWhoisData().getChannels().forEach(channel -> channelsBuilder.append(channel).append(" "));
        String channels = channelsBuilder.toString();

        stringBuilder.append("**Channels:** ").append(channels).append("\n");

        builder.setDescription(stringBuilder);

        context.replyEmbeds(builder.build());
    }
}
