package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class MemesCommand {
    @GetService
    private Database database;
    @Command(name = "memes", aliases = {"memevoting"}, slashCommand = false, description = "Manage memes voting service", adminOnly = true, usage = "memes channel <set|unset> [channel]")
    public void memesCommand(CommandEvent event) throws SQLException {
        String[] args = event.getCommandArgs();
        if (args[0].equalsIgnoreCase("channel")) {
            if (args[1].equalsIgnoreCase("set")) {
                // try to parse a channel
                GuildChannel channel = event.getMessage().getMentions().getChannels().get(0);
                database.setServerMemesChannel(channel.getIdLong(), event.getGuild());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server memes channel"));
            } else if (args[1].equalsIgnoreCase("unset")) {
                database.unsetServerMemesChannel(event.getGuild());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset server memes channel"));
            } else {
                event.sendUsage();
            }
        } else {
            event.sendUsage();
        }
    }
}
