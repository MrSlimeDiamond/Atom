package net.zenoc.atom.services;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Service("discord message cache")
public class MessageCacheService extends ListenerAdapter {
    private static Logger log = LoggerFactory.getLogger(MessageCacheService.class);

    @Service.Start
    public void startService() throws Exception {
        DiscordBot.jda.addEventListener(this);
    }
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        // Don't cache messages that don't really need to be cached
        if (!event.isFromGuild() || event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;
        try {
            Atom.database.addMessage(event.getMessageIdLong(), event.getGuild().getIdLong(), event.getAuthor().getIdLong(), event.getMessage().getContentDisplay());
        } catch (SQLException e) {
            log.error("SQLException when caching message, is the database down?");
            e.printStackTrace();
        }
    }
}
