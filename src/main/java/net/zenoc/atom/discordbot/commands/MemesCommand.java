package net.zenoc.atom.discordbot.commands;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.util.EmbedUtil;

import java.sql.SQLException;

public class MemesCommand {
    @Command(name = "memes", aliases = {"memevoting"}, slashCommand = false, description = "Manage memes voting service", adminOnly = true, usage = "memes channel <set|unset> [channel]")
    public void memesCommand(CommandEvent event) throws SQLException {
        String[] args = event.getCommandArgs();
        if (args[0].equalsIgnoreCase("channel")) {
            if (args[1].equalsIgnoreCase("set")) {
                // try to parse a channel
                GuildChannel channel = event.getMessage().getMentions().getChannels().get(0);
                Atom.database.setServerMemesChannel(channel.getIdLong(), event.getGuild());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server memes channel"));
            }
        } else {
            event.sendUsage();
        }
    }
}
