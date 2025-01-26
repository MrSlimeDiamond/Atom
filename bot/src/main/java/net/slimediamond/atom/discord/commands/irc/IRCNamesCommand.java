package net.slimediamond.atom.discord.commands.irc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class IRCNamesCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (database.isChannelBridged(context.getChannel().getIdLong())) {
            IRC.client.getChannel(database.getIRCBridgeChannel(context.getChannel().getIdLong())).ifPresent(channel -> {
                StringBuilder stringBuilder = new StringBuilder();
                AtomicInteger ops = new AtomicInteger();
                AtomicInteger voice = new AtomicInteger();
                AtomicInteger total = new AtomicInteger();
                channel.getUsers().forEach(user -> {
                    total.getAndIncrement();
                    if (channel.getUserModes(user).isPresent()) {
                        channel.getUserModes(user).get().forEach(usermode -> {
                            if (usermode.getNickPrefix() == '@') {
                                stringBuilder.append("@");
                                ops.getAndIncrement();
                            } else if (usermode.getNickPrefix() == '+') {
                                stringBuilder.append("+");
                                voice.getAndIncrement();
                            }
                        });
                    }
                    stringBuilder.append(user.getNick().replace("_", "\\_")).append(", ");
                });
                MessageEmbed embed = new EmbedBuilder()
                        .setAuthor("Users in " + channel.getName())
                        .setTitle(ops.get() + " ops, " + voice.get() + " voiced, " + total.get() + " total")
                        .setDescription(stringBuilder.toString())
                        .build();
                context.replyEmbeds(embed);
            });
        } else {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("This channel is not bridged"));
        }
    }
}
